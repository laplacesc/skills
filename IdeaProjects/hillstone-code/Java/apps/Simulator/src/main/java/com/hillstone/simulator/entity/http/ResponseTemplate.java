package com.hillstone.simulator.entity.http;


/**
 * @author: xjhuang
 * @date: create in 11:20 2021/11/19
 * @description:
 */
public class ResponseTemplate<T> {

    private int status;

    private T data;

    private ErrorMessage error;

    public ResponseTemplate() {

    }

    public ResponseTemplate(int status) {
        this.status = status;
    }

    public ResponseTemplate(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public ResponseTemplate(int status, T data, ErrorMessage error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorMessage getError() {
        return error;
    }

    public void setError(ErrorMessage error) {
        this.error = error;
    }
}
