package com.github.zackhotte.pasithea.controller;

public class Response {

    private String message;
    private String error;
    private int code;

    public Response(String message, String error, int code) {
        this.message = message;
        this.error = error;
        this.code = code;
    }

    public static Response ok(String message) {
        return new Response(message, "", 200);
    }

    public String getError() {
        return error;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
