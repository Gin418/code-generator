package com.code.maker.meta;

/**
 * packageName com.code.maker.meta
 *
 * @author Gin
 * @version 1.0.0
 * @title MetaException
 * @date 2024/11/18 22:52 周一
 * @desreciption 元信息异常
 */
public class MetaException extends RuntimeException {

    public MetaException(String message) {
        super(message);
    }

    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }
}
