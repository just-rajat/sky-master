/**
 * 
 */
package sky.farmerBenificiary.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 */
@ConfigurationProperties(prefix = "app")
@Validated
@Getter
@Setter
public class ApplicationConfiguration{
    private String secret;
    private String corsOrigin;
    private String redisCluster;
    private String redisPrefix;
    private String domain;
    private String ownerCode;
    private int otpExpiryTime;
    private long jwtExpiry;
    private long sessionValidity;
    private long refreshTokenValidity;
    private long portalJwtExpiry;
}
