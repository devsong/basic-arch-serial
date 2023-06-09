package io.github.devsong.serial;

import io.github.devsong.base.entity.GlobalConstant;

public interface SerialConstants {
    String SYSTEM_PREFIX = GlobalConstant.SYSTEM_PACKAGE_PREFIX+".serial";

    long MAX_RECORDS_LIMIT = 5000L;

    String MYBATIS_HANDLER_PACKAGE = GlobalConstant.SYSTEM_PACKAGE_PREFIX+".base.common.convert.mybatis";

    public static class SysMajorCode {
        public static final int MAJOR_5XX = 500000;
        public static final int MAJOR_4XX = 400000;
        public static final int MAJOR_3XX = 300000;
        public static final int MAJOR_2XX = 200000;
        public static final int MAJOR_1XX = 100000;
    }

    public static class SysMinorCode {
        public static final int START_UP_CODE = 0;
        public static final int ILLEGAL_ARGUMENT = 1;
        public static final int USER_NOT_FOUND = 2;
        public static final int DUPLICATE_REQUEST = 3;
    }
}
