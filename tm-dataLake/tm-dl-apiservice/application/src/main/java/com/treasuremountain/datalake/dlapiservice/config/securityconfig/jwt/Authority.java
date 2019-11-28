package com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt;

/**
 * Created by ref.tian on 2018/3/1.
 */
public enum Authority {

    SYS_ADMIN(0),
    TENANT_ADMIN(1),
    CUSTOMER_USER(2),
    SYS_USER(3),
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
