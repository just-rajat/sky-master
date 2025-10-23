package sky.farmerBenificiary.payloads;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
/**
 * 
 */
@Data
@ToString
public class User implements UserDetails {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotBlank(message = "UserId is mandatory")
    private String userId;

    @NotBlank(message = "Password is mandatory")
    private String password;

    private String userName;
    private String userRole;
    private ArrayList<String> userRoles;
    private String userCategory;
    private String ownerCode;
    private String sessionId;
    private String redirectUrl;
    private String orgId;
    private String userScope;
    private String stateCode;
    private String districtCode;
    private String loginMode;
    private String userType;

    private String jwtToken;
    private String jwtRefreshToken;
    private String token;
    private String mobileNo;
    private String responseMessage;
    private String documentNo;
    private String userEmail;
    private String designation;
    private String dccbId;
    private String userAddress;
    private long responseCode;
    private String isFarmer;
    private String creatorMode;
    private String orgName;
    private String clientIP;
    private String loginDate;
    private String applicationName;
    private String userFirstTimeLogin;
    private String adminId;
    private String isSoceityAadhaarVerified;
    private String isDemographicUpdated;
    private String stateName;
    private String districtName;
    private String talukaCode;
    private String talukaName;
    private String villageCode;
    private String villageName;
    private String panchayatName;
    private String isAadhaarFlowReq;
    private String aadhaarAuthType;

    private int loginAttempt;
    
    //private List<ModulesMasterV5> menuList;

    private ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User that))
            return false;
        return userName.equals(that.userName) && userId.equals(that.userId) && password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, userId, password);
    }
}
