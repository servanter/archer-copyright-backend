package com.archer.admin.web.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

public class TokenUtils {

    private static final long EXPIRE_TIME = 60 * 60 * 1000;

    /**
     * token私钥
     */
    private static final String TOKEN_SECRET = "1234567890";

    /**
     * 生成签名,15分钟后过期
     *
     * @param username
     * @param userId
     * @return
     */
    public static String sign(String username, Integer userId) {
        //过期时间
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        //私钥及加密算法
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        //设置头信息
        HashMap<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        //附带username和userID生成签名
        return JWT.create().withHeader(header)
                .withClaim("loginName", username)
                .withClaim("userId", userId)
                .withExpiresAt(date)
                .sign(algorithm);
    }


    /**
     * 校验token是否正确
     *
     * @param token
     * @return
     */
    public static boolean verity(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            // 此处简化，直接返回boolean值
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 校验token是否正确
     *
     * @param token
     * @return
     */
    public static UserInfo getUserInfo(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String loginName = jwt.getClaim("loginName").asString();
            Integer userId = jwt.getClaim("userId").asInt();
            return UserInfo.builder()
                    .userId(Optional.ofNullable(userId).orElse(-1))
                    .loginName(Optional.ofNullable(loginName).orElse(""))
                    .build();
        } catch (Exception e) {
            return UserInfo.EMPTY;
        }
    }

    @Getter
    @Builder
    public static class UserInfo {

        public static final UserInfo EMPTY = UserInfo.builder().build();

        @Default
        private int userId = -1;

        @Default
        private String loginName = "";


    }
}
