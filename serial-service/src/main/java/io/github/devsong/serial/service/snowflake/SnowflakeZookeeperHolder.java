package io.github.devsong.serial.service.snowflake;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

import io.github.devsong.base.common.util.IpUtil;
import io.github.devsong.base.common.util.JsonUtil;
import io.github.devsong.serial.config.properties.SerialProperties;
import io.github.devsong.serial.exception.SerialException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhisong.guan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SnowflakeZookeeperHolder implements InitializingBean {
    private String notifyZk;
    private String propPath;
    private String prefixZkPath;
    /**
     * 保存所有数据持久的节点
     */
    private String pathForever;
    /**
     * 保存自身的key ip:port-000000001
     */
    private String zkAddressNode = null;

    private int dataCenterId;
    private int workerId;
    private String appId;
    private String appIp;
    private Integer appPort;
    private long lastUpdateTime;

    private final SerialProperties serialProperties;

    private final ScheduledExecutorService schedulePool = new ScheduledThreadPoolExecutor(1, r -> {
        Thread t = new Thread(r);
        t.setName("schedule-upload-time");
        t.setDaemon(true);
        return t;
    });

    @Override
    public void afterPropertiesSet() {
        this.dataCenterId = serialProperties.getDataCenterId();
        if (dataCenterId > SnowflakeConst.MAX_DATA_CENTER_ID) {
            throw new Error("dataCenterId must no more than:" + SnowflakeConst.MAX_DATA_CENTER_ID);
        }
        this.appPort = serialProperties.getServerPort();
        this.appIp = IpUtil.getLocalIp();
        this.appId = appIp + ":" + appPort;
        this.notifyZk = serialProperties.getZk();
        this.prefixZkPath = "/snowflake/" + serialProperties.getName();
        this.pathForever = prefixZkPath + "/forever";
        this.propPath = System.getProperty("java.io.tmpdir") + File.separator + serialProperties.getName()
                + "/serialconf/{port}/workerID.properties";

        init();
    }

    public boolean init() {
        try {
            CuratorFramework curator = createWithOptions(notifyZk, new RetryUntilElapsed(1000, 4));
            curator.start();
            Stat stat = curator.checkExists().forPath(pathForever);
            if (stat == null) {
                // 不存在根节点,机器第一次启动,创建/snowflake/ip:port-000000000,并上传数据
                zkAddressNode = createNode(curator);
                // worker id 默认是0
                updateLocalWorkerID(workerId);
                // 定时上报本机时间给forever节点
                scheduledUploadData(curator, zkAddressNode);
                return true;
            } else {
                // ip:port->00001
                Map<String, Integer> nodeMap = Maps.newHashMap();
                // ip:port->(ipport-000001)
                Map<String, String> realNode = Maps.newHashMap();
                // 存在根节点,先检查是否有属于自己的根节点
                List<String> keys = curator.getChildren().forPath(pathForever);
                for (String key : keys) {
                    String[] nodeKey = key.split("-");
                    realNode.put(nodeKey[0], key);
                    nodeMap.put(nodeKey[0], Integer.parseInt(nodeKey[1]));
                }
                Integer workerid = nodeMap.get(appId);
                if (workerid != null) {
                    // 有自己的节点,zkAddressNode=ip:port
                    zkAddressNode = pathForever + "/" + realNode.get(appId);
                    // 启动worker时使用会使用
                    workerId = workerid;
                    if (!checkInitTimeStamp(curator, zkAddressNode)) {
                        throw new SerialException("init timestamp check error,forever node timestamp gt this node time");
                    }
                    // 准备创建临时节点
                    doService(curator);
                    updateLocalWorkerID(workerId);
                    log.info("endpoint ip {} port {} worker id {} children node and start success", appIp, appPort, workerId);
                } else {
                    // 表示新启动的节点,创建持久节点 ,不用check时间
                    String newNode = createNode(curator);
                    zkAddressNode = newNode;
                    String[] nodeKey = newNode.split("-");
                    workerId = Integer.parseInt(nodeKey[1]);
                    doService(curator);
                    updateLocalWorkerID(workerId);
                    log.info("endpoint ip {} port {} worker id {},create own node on forever node and start success ", appIp, appPort,
                            workerId);
                }
            }
        } catch (Exception e) {
            log.error("Start node ERROR", e);
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(propPath.replace("{port}", appPort + "")));
                workerId = Integer.parseInt(properties.getProperty("workerID"));
                log.warn("START FAILED ,use local node file properties workerID-{}", workerId);
            } catch (Exception e1) {
                log.error("Read file error ", e1);
                return false;
            }
        }
        return true;
    }


    private void doService(CuratorFramework curator) {
        // /snowflake_forever/ip:port-000000001
        scheduledUploadData(curator, zkAddressNode);
    }

    private void scheduledUploadData(final CuratorFramework curator, final String zkNode) {
        // 每3s上报数据
        schedulePool.scheduleWithFixedDelay(() -> updateNewData(curator, zkNode), 1L, 3L, TimeUnit.SECONDS);
    }

    private boolean checkInitTimeStamp(CuratorFramework curator, String zkNode) throws Exception {
        byte[] bytes = curator.getData().forPath(zkNode);
        Endpoint endPoint = deBuildData(new String(bytes));
        // 该节点的时间不能小于最后一次上报的时间
        return endPoint.getTimestamp() <= System.currentTimeMillis();
    }

    /**
     * 创建持久顺序节点 ,并把节点数据放入 value
     */
    private String createNode(CuratorFramework curator) throws Exception {
        try {
            return curator.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                    .forPath(pathForever + "/" + appId + "-", buildData().getBytes());
        } catch (Exception e) {
            log.error("create node error msg {} ", e.getMessage());
            throw e;
        }
    }

    private void updateNewData(CuratorFramework curator, String path) {
        try {
            if (System.currentTimeMillis() < lastUpdateTime) {
                return;
            }
            curator.setData().forPath(path, buildData().getBytes());
            lastUpdateTime = System.currentTimeMillis();
        } catch (Exception e) {
            log.info("update init data error path is {} error is {}", path, e);
        }
    }

    /**
     * 构建需要上传的数据
     */
    private String buildData() {
        Endpoint endpoint = new Endpoint(appIp, appPort + "", System.currentTimeMillis());
        return JsonUtil.toJSONString(endpoint);
    }

    private Endpoint deBuildData(String json) {
        return JsonUtil.parseObject(json, Endpoint.class);
    }

    /**
     * 在节点文件系统上缓存一个worker id值,zk失效,机器重启时保证能够正常启动
     *
     * @param workerID workerId
     */
    private void updateLocalWorkerID(int workerID) {
        File leafConfFile = new File(propPath.replace("{port}", appPort + ""));
        boolean exists = leafConfFile.exists();
        log.info("file exists status is {}", exists);
        if (exists) {
            try {
                FileUtils.writeStringToFile(leafConfFile, "workerID=" + workerID, Charsets.UTF_8, false);
                log.info("update file cache workerID is {}", workerID);
            } catch (IOException e) {
                log.error("update file cache error ", e);
            }
        } else {
            // 不存在文件,父目录也肯定不存在
            try {
                boolean parentExists = leafConfFile.getParentFile().exists();
                boolean mkdirs = leafConfFile.getParentFile().mkdirs();
                log.info("init local file cache create parent dir status is {}, worker id is {}", mkdirs, workerID);
                if (mkdirs) {
                    if (leafConfFile.createNewFile()) {
                        FileUtils.writeStringToFile(leafConfFile, "workerID=" + workerID, Charsets.UTF_8, false);
                        log.info("local file cache workerID is {}", workerID);
                    }
                } else {
                    if (parentExists) {
                        // 父目录存在
                        if (leafConfFile.createNewFile()) {
                            FileUtils.writeStringToFile(leafConfFile, "workerID=" + workerID, Charsets.UTF_8, false);
                            log.info("local file cache workerID is {}", workerID);
                        }
                    } else {
                        log.warn("create parent dir error");
                    }
                }
            } catch (IOException e) {
                log.warn("craete workerID conf file error", e);
            }
        }
    }

    private CuratorFramework createWithOptions(String notifyZk, RetryPolicy retryPolicy) {
        return CuratorFrameworkFactory.builder()
                .connectString(notifyZk)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(6000)
                .build();
    }


    /**
     * 上报数据结构
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Endpoint {
        private String ip;
        private String port;
        private long timestamp;
    }

    public int getWorkerId() {
        return workerId;
    }

}
