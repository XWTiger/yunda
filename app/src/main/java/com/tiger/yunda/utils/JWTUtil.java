package com.tiger.yunda.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTUtil {

    public static String decoder(String token) {
        try {
            DecodedJWT jwt= JWT.decode(token);

            return  jwt.getClaim("PersonalName").asString();
        } catch (JWTDecodeException ex){
            return null;
        }
    }
}
