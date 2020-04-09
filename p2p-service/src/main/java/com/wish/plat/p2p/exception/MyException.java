package com.wish.plat.p2p.exception;

/**
 * @author ： yangxd
 * @date ：Created in 2019/12/20 11:17
 * @description ：自定义异常
 * @modified By：
 * @version: v1.0
 */
public class MyException extends RuntimeException{

    private String msg;
    private String code = "500";

    public MyException(String msg){
        super(msg);
        this.msg = msg;
    }

    public MyException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public MyException(String code, String msg) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public MyException(String code, String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
