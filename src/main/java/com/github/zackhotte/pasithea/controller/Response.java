package com.github.zackhotte.pasithea.controller;

public class Response {

    private String message;
    private String error;
    private int code;

    private Response(String message, String error, int code) {
        this.message = message;
        this.error = error;
        this.code = code;
    }

    private Response(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public static Response ok(String message) {
        return new Response(message, 200);
    }

    public static Response error(String message, String error, int code) {
        return new Response(message, error, code);
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
