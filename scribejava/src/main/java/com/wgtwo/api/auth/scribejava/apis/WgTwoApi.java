package com.wgtwo.api.auth.scribejava.apis;

import com.github.scribejava.apis.openid.OpenIdJsonTokenExtractor;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;

public class WgTwoApi extends DefaultApi20 {
    protected WgTwoApi() {
    }

    private static class InstanceHolder {
        private static final WgTwoApi INSTANCE = new WgTwoApi();
    }

    public static WgTwoApi instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://id.wgtwo.com/oauth2/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://id.wgtwo.com/oauth2/auth";
    }

    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return OpenIdJsonTokenExtractor.instance();
    }
}
