package com.treasuremountain.datalake.dlapiservice.common.data.jwt;

/**
 * Created by ref.tian on 2018/3/1.
 */
public enum Authority {

    SYS_ADMIN(0),
    SYS_OPERATOR(1),
    //    TENANT(2),
    SYS_DATA_MANAGER(2),
    CUSTOMER(3),
    RESTAPI(4),
    REFRESH_TOKEN(10);


    private int code;

    Authority(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Authority parse(String value) {
        Authority authority = null;
        if (value != null && value.length() != 0) {
            for (Authority current : Authority.values()) {
                if (current.name().equalsIgnoreCase(value)) {
                    authority = current;
                    break;
                }
            }
        }
        return authority;
    }
}
