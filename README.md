# Libraries and examples for using Working Group Two's OAuth 2.0

The libraries and examples are so far in Java, as most of our customers are using that or Kotlin.

These may be used directly in your projects or used as inspiration.

This repo includes
- API implementation for ScribeJava (https://github.com/scribejava/scribejava/)
- A wrapper around Auth0's library for decoding and validating JWTs, configured for our JWKS endpoint
  - https://github.com/auth0/java-jwt
  - https://github.com/auth0/jwks-rsa-java

## Install as a dependency

Currently this must be done via [JitPack](https://jitpack.io/)

### Add the JitPack repository to your build file
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Add dependencies
You may replace `main-SNAPSHOT` with the short commit hash.

```
<dependencies>
    <dependency>
        <groupId>com.github.working-group-two.third-party-auth</groupId>
        <artifactId>jwt</artifactId>
        <version>main-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>com.github.working-group-two.third-party-auth</groupId>
        <artifactId>scribejava</artifactId>
        <version>main-SNAPSHOT</version>
    </dependency>
</depenencies>

```
