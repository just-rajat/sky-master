package sky.farmerBenificiary.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import sky.farmerBenificiary.payloads.ApiResponse;
import sky.farmerBenificiary.payloads.FarmerPayload;
import sky.farmerBenificiary.service.FarmerBeneficiaryService;

@RestController
@RequestMapping("/beneficiary")
@Slf4j
public class FarmerBeneficiaryController {
	
	@Autowired
	FarmerBeneficiaryService oService;
	
    @PostMapping("/getBenfDetails")
    public ResponseEntity<List<FarmerPayload>> getBenfDetails(@RequestBody FarmerPayload requestBean ) {
        List<FarmerPayload> result = new ArrayList<>();
        try {
        	result =oService.getStatewiseFarmerDetails(requestBean);
		} catch (Exception e) {
			e.printStackTrace();e.getMessage();
		}
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @PostMapping("/saveBenfDetails")
    public ResponseEntity<ApiResponse> saveBenfDetails(@RequestBody FarmerPayload requestBean ) {
    	ApiResponse response = new ApiResponse();
        try {
        	response =oService.InsertBenfDetails(requestBean);
		} catch (Exception e) {
			e.printStackTrace();e.getMessage();
		}
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping("/modifyBenfDetails")
    public ResponseEntity<ApiResponse> modifyFarmerDetails(@RequestBody FarmerPayload requestBean ) {
    	ApiResponse response = new ApiResponse();
        try {
        	response =oService.modifyBenfDetails(requestBean);
		} catch (Exception e) {
			e.printStackTrace();e.getMessage();
		}
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    
    
    @PostMapping("/approveBenfDetails")
    public ResponseEntity<ApiResponse> approvePaymentDetails(@RequestBody List<FarmerPayload> requestBean ) {
    	ApiResponse response = new ApiResponse();
        try {
        	response =oService.approveBenfDetails(requestBean);
		} catch (Exception e) {
			e.printStackTrace();e.getMessage();
		}
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping("/uploadFarmerDetails")
    public ResponseEntity<ApiResponse> uploadFarmerDetails(@RequestParam("file") MultipartFile file) {
    	ApiResponse response = new ApiResponse();
        try {
        	response =oService.uploadFarmerDetails(file);
		} catch (Exception e) {
			e.printStackTrace();e.getMessage();
		}
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
