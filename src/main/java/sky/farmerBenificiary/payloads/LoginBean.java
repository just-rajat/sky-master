/**
 * 
 */
package sky.farmerBenificiary.payloads;

/**
 * 
 */
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class LoginBean {
	
    private String mobileNo;
    private String otp;
    private String password;
    private Timestamp otpExpiryTime;
    private String isFromAndroid;
    private String creatorMode;
    private String userId;
    private Integer passwordExpiryDays;
    private String ownerCode;
    private String isActive;
    private String orgId;
    private String orgName;
    private String userName;
    private String orgRole;
    private String isFirstLogin;
    private String userSsoToken;
    private String hashedToken;
    private String orgState;
    private List<String> userRoles;
    
}
