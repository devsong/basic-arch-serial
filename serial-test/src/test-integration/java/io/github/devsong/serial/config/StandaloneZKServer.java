package io.github.devsong.serial.config;


import java.io.File;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

/**
 * date:  2023/4/21
 * author:guanzhisong
 */
@Slf4j
public class StandaloneZKServer {
    /**
     * 启动单例zk server
     *
     * @param tickTime   Zookeeper中最小时间单元的长度
     * @param dataDir    Zookeeper服务器存储快照文件的目录
     * @param clientPort 当前服务器对外的服务端口
     * @param initLimit  Leader服务器等待Follower启动，并完成数据同步的时间
     * @param syncLimit  Leader服务器和Follower之间进行心跳检测的最大延时时间
     */
    private static ZooKeeperServerMain startStandaloneServer(String tickTime, String dataDir, int clientPort, String initLimit, String syncLimit) {
        System.setProperty("zookeeper.admin.enableServer", "false");
        Properties props = new Properties();
        props.setProperty("tickTime", tickTime);
        props.setProperty("dataDir", dataDir);
        props.setProperty("clientPort", clientPort + "");
        props.setProperty("initLimit", initLimit);
        props.setProperty("syncLimit", syncLimit);

        QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
        final ZooKeeperServerMain zkServer = new ZooKeeperServerMain();
        try {
            quorumConfig.parseProperties(props);
            final ServerConfig config = new ServerConfig();
            config.readFrom(quorumConfig);
            zkServer.runFromConfig(config);
        } catch (Exception e) {
            log.error("Start standalone server faile", e);
        }
        return zkServer;
    }

    public static ZooKeeperServerMain startEmbeddedZkServer(int port) {
        return startStandaloneServer("2000", new File(System.getProperty("java.io.tmpdir"), "zookeeper").getAbsolutePath(), port, "10", "5");
    }

}
