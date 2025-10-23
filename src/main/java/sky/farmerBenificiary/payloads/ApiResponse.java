package sky.farmerBenificiary.payloads;

import lombok.Data;

@Data
public class ApiResponse {
	private long responseCode;
 	private String responseMessage;
}
