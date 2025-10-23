package sky.farmerBenificiary.payloads;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class RefreshToken {
    private String userId;
    private String refreshToken;
    private Timestamp expiryDate;
}
