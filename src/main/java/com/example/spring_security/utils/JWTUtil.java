package com.example.spring_security.utils;

public class JWTUtil {
    public static final String SECRET = "mySecret1234";
    public static final String AUTH_HEADER = "Authorization";
    public static final String PREFIX = "Bearer ";
    public static final String ENDPOINT_REFRESH_TOKEN = "/refreshToken";
    public static final long EXPIRE_ACCESS_TOKEN = 2 * 60 * 1000;
    public static final long EXPIRE_REFRESH_TOKEN = 15 * 60 * 1000;

}
