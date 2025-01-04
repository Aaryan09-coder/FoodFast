package com.jplProject.config;

public class JwtConstant {
    // SECRET_KEY is a long, randomly generated string used as the key for signing JWTs.
    public static final String SECRET_KEY="efkhwbefkbwekgbkwejerbngjkerwnkjgnerjgnwegkwernsdkgvjerbgrkvrehgjredjsabnjkfq";

    // JWT_HEADER represents the HTTP header name where the JWT is expected to be included in requests.
    // Typically, JWTs are sent in the "Authorization" header in the format "Bearer <token>".
    public static final String JWT_HEADER="Authorization";

}
