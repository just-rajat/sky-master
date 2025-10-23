package sky.farmerBenificiary.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import sky.farmerBenificiary.payloads.ApiResponse;
import sky.farmerBenificiary.payloads.FarmerPayload;

public interface FarmerBeneficiaryService {

	List<FarmerPayload> getStatewiseFarmerDetails(FarmerPayload requestBean);

	ApiResponse modifyBenfDetails(FarmerPayload requestBean);
	
	ApiResponse InsertBenfDetails(FarmerPayload requestBean);

	ApiResponse approveBenfDetails(List<FarmerPayload> requestBean);

	ApiResponse uploadFarmerDetails(MultipartFile file);

}
