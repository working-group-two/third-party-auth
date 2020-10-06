package com.wgtwo.api.auth.jwt;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;

/**
 * This is a wrapper around https://github.com/auth0/jwks-rsa-java for verifying JWTs from https://id.wgtwo.com
 */
public final class JwtUtils {
    private static final String DOMAIN = "id.wgtwo.com";
    private static final String ISSUER = "https://id.wgtwo.com/";

    private JwtUtils() {}

    private final JwkProvider provider = new JwkProviderBuilder(DOMAIN)
            .cached(10, 1, TimeUnit.HOURS)
            .build();

    public DecodedJWT decode(String token) throws JWTDecodeException {
        return JWT.decode(token);
    }

    /**
     * Validates the provided JWT using Working Group Two's public keys.
     * <p>
     * Each of the exceptions thrown has more specific subclasses.
     * You may want to catch com.auth0.jwk.NetworkException, as that may occur during timeouts and connection issues.
     *
     * @param jwt   The JWT to verify
     * @param nonce The nonce specified in the auth call to https://id.wgtwo.com
     * @return The given JWT
     * @throws JwkException             If key cannot be loaded
     * @throws JWTVerificationException If verification of the JWT fails
     */
    public DecodedJWT verify(String jwt, String nonce) throws JwkException, JWTVerificationException {
        DecodedJWT decoded = decode(jwt);
        return verify(decoded, nonce);
    }

    /**
     * Validates the provided JWT using Working Group Two's public keys.
     * <p>
     * Each of the exceptions thrown has more specific subclasses.
     * You may want to catch com.auth0.jwk.NetworkException, as that may occur during timeouts and connection issues.
     *
     * @param jwt   The JWT to verify
     * @param nonce The nonce specified in the auth call to https://id.wgtwo.com
     * @return The given JWT
     * @throws JwkException             If key cannot be loaded
     * @throws JWTVerificationException If verification of the JWT fails
     */
    public DecodedJWT verify(DecodedJWT jwt, String nonce) throws JwkException, JWTVerificationException {
        Jwk jwk = provider.get(jwt.getKeyId());
        Algorithm algorithm = getAlgorithm(jwk);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .withClaim("nonce", nonce)
                .build();
        return verifier.verify(jwt);
    }

    private Algorithm getAlgorithm(Jwk jwk) throws InvalidPublicKeyException {
        if ("RS256".equals(jwk.getAlgorithm())) {
            return Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
        }
        throw new IllegalArgumentException("Unsupported JWT algorithm");
    }
}
