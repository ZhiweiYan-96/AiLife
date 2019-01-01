package com.record.bean;

public class RequestFailExeption extends Exception {
    private static final long serialVersionUID = -5639300588465949745L;
    int statusCode;

    public RequestFailExeption(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
