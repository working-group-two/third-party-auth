package com.wgtwo.api.auth.jwt;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.TimeUnit;

public class JwtUtils {
    private static final String DOMAIN = "id.wgtwo.com";
    private static final String ISSUER = "https://id.wgtwo.com/";

    private JwkProvider provider = new JwkProviderBuilder(DOMAIN)
            .cached(10, 1, TimeUnit.HOURS)
            .build();

    public void verify(DecodedJWT jwt, String nonce) throws JwkException, JWTVerificationException {
        Jwk jwk = provider.get(jwt.getKeyId());
        Algorithm algorithm = getAlgorithm(jwk);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .withClaim("nonce", nonce)
                .build();
        verifier.verify(jwt);
    }

    private Algorithm getAlgorithm(Jwk jwk) throws InvalidPublicKeyException {
        switch (jwk.getAlgorithm()) {
            case "RS256":
                return Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            default:
                throw new IllegalArgumentException("Unsupported JWT algorithm");
        }
    }
}
