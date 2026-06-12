package com.hillstone.simulator.entity.http;

/**
 * @author: xjhuang
 * @date: create in 11:20 2021/11/19
 * @description:
 */
public class ErrorMessage {

    /**
     * 错误码
     */
    private int errCode;

    /**
     * 错误信息
     */
    private String errMessage;

    public ErrorMessage(){

    }

    public ErrorMessage(int errCode){
        this.errCode = errCode;
    }

    public ErrorMessage(int errCode, String errMessage){
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public ErrorMessage(ErrorMessage error){
        this.errCode = error.getErrCode();
        this.errMessage = error.getErrMessage();
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}

