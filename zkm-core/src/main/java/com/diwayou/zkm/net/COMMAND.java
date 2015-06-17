package com.diwayou.zkm.net;

/**
 * Created by cn40387 on 15/6/16.
 */
public enum COMMAND {

    CONF("conf"),

    CONS("cons"),

    CRST("crst"),

    DUMP("dump"),

    ENVI("envi"),

    RUOK("ruok"),

    SRST("srst"),

    SRVR("srvr"),

    STAT("stat"),

    WCHS("wchs"),

    WCHC("wchc"),

    WCHP("wchp"),

    ;

    private String comm;

    COMMAND(String comm) {
        this.comm = comm;
    }

    public String getComm() {
        return comm;
    }
}
