package sky.farmerBenificiary.payloads;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Password {
    private String userId;
    private String sha1Password;
    private String sha512Password;
    private String plainPassword;
    private String userMobile;
}
