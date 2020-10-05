package com.wgtwo.api.auth.scribejava.apis.examples;

import com.github.scribejava.apis.openid.OpenIdOAuth2AccessToken;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.wgtwo.api.auth.scribejava.apis.WgTwoApi;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collector;

public class WgTwoApiExample {

    private static final String CLIENT_ID = System.getenv("WGTWO_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("WGTWO_CLIENT_SECRET");

    public static void main(String[] args) throws Exception {
        if (CLIENT_ID == null || CLIENT_SECRET == null) {
            System.err.println("Environment variables required: WGTWO_CLIENT_ID and WGTWO_CLIENT_SECRET");
            return;
        }

        // Configure OAuth 2.0 service, note that callback must match client configuration
        OAuth20Service service = new ServiceBuilder(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .callback("https://example.com/oauth/callback")
                .build(WgTwoApi.instance());

        // Obtain the Authorization URL
        System.out.println("Creating authorization URL...");
        String state = randomAlphanumeric();

        String authorizationUrl = service.createAuthorizationUrlBuilder()
                .scope("offline_access openid phone")
                .state(state)
                .additionalParams(ImmutableMap.of(
                        "nonce", randomAlphanumeric(),
                        "prompt", "login"
                ))
                .build();
        System.out.println("Got the Authorization URL!");

        System.out.println("Open the auth url in your browser: " + authorizationUrl);
        System.out.println("Paste the callback url here, including all query parameters");
        System.out.print(">> ");
        final Scanner in = new Scanner(System.in, "UTF-8");
        String result = in.nextLine();
        System.out.println();

        System.out.println("Extracting these query parameters....");
        ReturnedValues returned = ReturnedValues.parse(result);
        System.out.println("code=" + returned.code);
        System.out.println("scope=" + returned.scope);
        System.out.println("state=" + returned.state);
        System.out.println();

        if (Objects.equals(state, returned.state)) {
            System.out.println("State value matches");
        } else {
            System.out.println("State value does not match! Quitting...");
            System.out.println("Expected = " + state);
            System.out.println("Got      = " + returned.state);
            System.out.println();
            return;
        }

        System.out.println("Trading the Authorization Code for an Access Token...");
        OAuth2AccessToken accessToken = service.getAccessToken(returned.code);
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        System.out.println();

        if (accessToken instanceof OpenIdOAuth2AccessToken) {
            OpenIdOAuth2AccessToken openIdOAuth2AccessToken = (OpenIdOAuth2AccessToken) accessToken;
            System.out.println("The response contains a openid token: " + openIdOAuth2AccessToken.getOpenIdToken());
        }

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://id.wgtwo.com/userinfo");
        service.signRequest(accessToken, request);
        try (Response response = service.execute(request)) {
            System.out.println("Got it! Lets see what we found...");
            System.out.println();
            System.out.println("Status code: " + response.getCode());
            System.out.println("Body: " + response.getBody());
        }
        System.out.println();

        System.out.println("Nice job! It works!");
    }

    private static String randomAlphanumeric() {
        long length = 20;
        String alphabet = "0123456789abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return ThreadLocalRandom.current().ints(length, 0, alphabet.length())
                .mapToObj(alphabet::charAt)
                .collect(Collector.of(StringBuilder::new, StringBuilder::append, StringBuilder::append, StringBuilder::toString));
    }

    private static class ReturnedValues {
        final String code;
        final String scope;
        final String state;

        private ReturnedValues(String code, String scope, String state) {
            this.code = code;
            this.scope = scope;
            this.state = state;
        }

        public static ReturnedValues parse(String url) {
            int index = url.lastIndexOf("?");
            String query = index > 0 ? url.substring(index + 1) : url;
            Map<String, String> queryParameters = Splitter.on('&').withKeyValueSeparator('=').split(query);
            return new ReturnedValues(queryParameters.get("code"), queryParameters.get("scope"), queryParameters.get("state"));
        }
    }
}
