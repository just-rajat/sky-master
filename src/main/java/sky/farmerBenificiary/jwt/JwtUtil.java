package sky.farmerBenificiary.jwt;


import java.security.Key;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.WebUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.DatatypeConverter;
import sky.farmerBenificiary.payloads.RefreshToken;

@Component
@Validated
public class JwtUtil {

    @Autowired
    ApplicationConfiguration applicationConfiguration;

    public ResponseCookie generateJwtCookie(String userName, HttpServletRequest request, String accessToken, Logger log) {
        log.info("Entering into and {} generateJwtCookie() ", JwtUtil.class.getName());
        return generateCookie("jwtCookie", request, accessToken, log);
    }


    public ResponseCookie generateRefreshJwtCookie(String refreshToken, HttpServletRequest request, Logger log) {
        log.info("Entering into {} and generateRefreshJwtCookie()", JwtUtil.class.getName());
        return generateCookie("jwtRefreshCookie", request, refreshToken, log);
    }

    public String getJwtFromCookies(HttpServletRequest request, Logger log) {
        log.info("Entering into {} getJwtFromCookies()", JwtUtil.class.getName());
        // TODO : remove new method when login from header not working instead use getCookiesValueByName method
        //return getCookieValueByName(request, "jwtCookie", log);
        return getCookieValueFromHeader(request, "Jwttoken", log);
    }

    public String getJwtRefreshFromCookies(HttpServletRequest request, Logger log) {
        log.info("Entering into {} getJwtRefreshFromCookies()", JwtUtil.class.getName());
        return getCookieValueByName(request, "jwtRefreshCookie", log);
    }

    public ResponseCookie getCleanJwtCookie(Logger log) {
        log.info("Entering into {} and getCleanJwtCookie() ", JwtUtil.class.getName());
        return ResponseCookie.from("jwtCookie", null).path("/api").build();
    }

    public ResponseCookie getCleanJwtRefreshCookie(Logger log) {
        log.info("Entering into {} and getCleanJwtRefreshCookie()  ", JwtUtil.class.getName());
        return ResponseCookie.from("jwtRefreshCookie", null).path("/auth/refreshToken").build();
    }

    public String getUserNameFromJwtToken(String token, Logger log) {

        log.info("Entering into {} and getUserNameFromJwtToken() ", JwtUtil.class.getName());
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(applicationConfiguration.getSecret());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        log.info("Exiting from {} and getUserNameFromJwtToken() ", JwtUtil.class.getName());
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(@NotBlank String authToken, Logger log) {

        log.info("Entering into {} and validateJwtToken()  ", JwtUtil.class.getName());
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(applicationConfiguration.getSecret());
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(authToken);

            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }finally {
            log.info("Exiting from {} and validateJwtToken()  ", JwtUtil.class.getName());

        }
        return false;
    }

    public String generateTokenFromUsername(String username, Logger log) {

        log.info("Entering into {} and generateTokenFromUsername()", JwtUtil.class.getName());
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(applicationConfiguration.getSecret());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        log.info("Exiting from {} and generateTokenFromUsername() ", JwtUtil.class.getName());
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date((new Date()).getTime() + 1000 * 60 * applicationConfiguration.getJwtExpiry()))
                .signWith(signingKey).compact();

    }

    public String generateTokenForPortalLogin(String username, Logger log) {

        log.info("Entering into {} and generateTokenFromUsername()", JwtUtil.class.getName());
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(applicationConfiguration.getSecret());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        log.info("Exiting from {} and generateTokenFromUsername() ", JwtUtil.class.getName());
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date((new Date()).getTime() + 1000 * 60 * applicationConfiguration.getPortalJwtExpiry()))
                .signWith(signingKey).compact();

    }

    private ResponseCookie generateCookie(String name, HttpServletRequest request, String value, Logger log) {
        log.info("Entering into {} and generateCookie() ", JwtUtil.class.getName());
        log.info("Domain is {} ", request.getHeader("host"));
        var domain = request.getHeader("host");
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, value)
                .path("/")
                .maxAge(24 * 60 * 60)
                // TODO: check http only cookies
                .httpOnly(true);

        if (request.getHeader("host").contains("localhost")) {
            log.info("Domain is localhost {}", request.getHeader("host").contains("localhost"));
        }
//        else {
//            cookieBuilder = cookieBuilder.domain(applicationConfiguration.domain());
//        }
        //cookieBuilder=cookieBuilder.sameSite("none");
        //cookieBuilder=cookieBuilder.secure(true);
        log.info("Exiting from {} and generateCookie() ", JwtUtil.class.getName());
        return cookieBuilder.build();

    }

    private String getCookieValueByName(HttpServletRequest request, String name, Logger log) {
        log.info("Entering into {} and getCookieValueByName()", JwtUtil.class.getName());
        Cookie cookie = WebUtils.getCookie(request, name);
        log.info("Exiting from {} and getCookieValueByName() and cookieName is {} ", JwtUtil.class.getName(), name);
        return cookie == null ? null : Objects.requireNonNull(cookie).getValue();
    }

    private String getCookieValueFromHeader(HttpServletRequest request, String name, Logger log) {
        log.info("Entering into {} and getCookieValueByName()", JwtUtil.class.getName());
        String cookie = request.getHeader("Jwttoken");
        log.info("Exiting from {} and getCookieValueByName() and cookieName is {} ", JwtUtil.class.getName(), name);
        return cookie == null ? null : Objects.requireNonNull(cookie);
    }

    public RefreshToken createRefreshToken(String userId, Logger log) {

        log.info("Entering into {} and createRefreshToken() with userId {}", JwtUtil.class.getName(),
                userId);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(Timestamp.valueOf(
                new Timestamp(System.currentTimeMillis()).toLocalDateTime().plusDays(applicationConfiguration.getRefreshTokenValidity())));
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
 
        log.info("Exiting from {} and createRefreshToken() with refreshToken {}", JwtUtil.class.getName(),
                refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token, Logger log) {

        log.info("Entering into {} and verifyExpiration() with RefreshToken {}", JwtUtil.class.getName(),
                token);
        if (token.getExpiryDate().compareTo(Timestamp.from(Instant.now())) < 0) {
            throw new TokenRefreshException(token.getRefreshToken(),
                    "Refresh token was expired. Please make a new signin request");
        }
        log.info("Exiting from {} and verifyExpiration() with RefreshToken {}", JwtUtil.class.getName(),
                token);
        return token;
    }
}
