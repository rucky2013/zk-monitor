package com.diwayou.zkm.exception;

/**
 * Created by cn40387 on 15/6/16.
 */
public class ClusterAlreadyExistException extends RuntimeException {
    public ClusterAlreadyExistException() {
    }

    public ClusterAlreadyExistException(String message) {
        super(message);
    }
}
