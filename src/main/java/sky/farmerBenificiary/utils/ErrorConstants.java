package sky.farmerBenificiary.utils;

public class ErrorConstants {
	
	public enum SUCCESS_FAILED_CODE {
		SUCCESS(200), FAILED(201), UPLAOD(202), EXIST(203), FIRST_TIME_LOGIN(204), NOT_MATCHED(205),
		ALREADY_TOTAL_AREA_USED(206), NEW_USER_LOGIN(207), MULTIPLE_MOBILE_EXISTS(208),ACCOUNT_LOCKED(405);

		public final long value;

		SUCCESS_FAILED_CODE(long value) {
			this.value = value;
		}
	}
	
	public enum SUCCESS_FAILED_STRING {
		SUCCESS("Success"), FAILED("Failed");

		public final String value;

		SUCCESS_FAILED_STRING(String value) {
			this.value = value;
		}
	}
	

	public enum LOGIN_ERROR_MESSAGE {
		MOBILE_NUMBER_IS_EMPTY("Mobile number cannot be null"),
		INCORRECT_CREDENTIALS("Entered mobile number or password is incorrect"),
		INCORRECT_CREDS("Entered User Id/ Mobile Number or password is incorrect"),
		PASSWORD_IS_EMPTY("Password cannot be null"), FARMER_DATA_NOT_FOUND("Farmer Data not Found"),
		FARMER_USER_MASTER_ARCHIVAL_FAILED("Password is incorrect"), CHANGING_PASSWORD("Changing password"),
		FARMER_UNLOCK_ARCHIVAL_FAILED("Archival failed while unlocking the user"),
		YOUR_PASSWORD_HAS_BEEN_CHANGED_SUCCESSFULLY("Your Password has been changed successfully"),
		ERROR_WHILE_CHANGING_PASSWORD("Error while changing password"),
//        WRONG_CURRENT_PASSWORD("Current password is wrong"),
		WRONG_CURRENT_PASSWORD("Old password is wrong"), USER_DOES_NOT_EXIST("User does not exist"),
		TOKEN_GENERATION_FAILED("Token generation failed"), AADHAAR_NUMBER_NOT_VERFIED("Aadhaar number not verified"),
		AADHAAR_NUMBER_CANNOT_BE_NULL("Aadhaar number cannot be null"),
		LOGOUT("You've been securely logged out. Until next time!"),
		ACCESS_DENIED_NOT_A_SOCIETY_USER("Access Denied!! Entered User is not a PAC User."),
		USER_ID_IS_EMPTY("User ID cannot be null"),
		ERROR_WHILE_RESETING_PASSWORD("Error while reseting password"),
		FORGOT_PASSWORD("Forgot Password"),
		UNLOCK_USER("Unlock User"),
		INVALID_OTP("OTP does not matched"),
		MOBILE_NO_NOT_FOUND("User Mobile Number not found"),
		SOMETHING_WENT_WRONG_WRONG_PLEASE_TRY_AGAIN("Something went wrong please try again"),
		CREATOR_MODE_IS_NULL("Creator mode is null"),
		ERROR_WHILE_INSERTING_CLIENTIP("ERROR WHILE INSERTING CLIENTIP"),
		LOGOUT_UNSUCESSFULL("LOGOUT UNSUCESSFULL"),
		FIRST_LOGIN_USER("First login User"),
		OTP_VERIFIED_SUCESSFULLY("OTP VERIFIED SUCESSFULLY"),
		USER_DATA_NOT_FOUND("User Data not Found"),
		USER_PENDING_FOR_APPROVAL("User Pending for Approval"),
		ERROR_WHILE_DEACTIVATING_THE_USER("Error while deactivating the user"),
		USER_DEACTIVATED_SUCCESSFULLY("User Deactivated Successfully"),
		ARCHIVAL_FAILED("Archival Failed"),
		ERROR_INVALID_MOBILE_NO("Invalid mobile number, Please enter valid mobile number"),
		ACCESS_DENIED_NOT_A_WAREHOUSE_USER("Access denied: The user is not a Warehouse Service Provider or Assayer."),
		USER_IS_NOT_ACTIVE("USER IS NOT ACTIVE"),
		INCORRECT_CURRENT_PASSWORD("Entered current password is incorrect"),
		LOGIN_SUCESSFULL("LOGIN SUCESSFULL"),MOBILE_NO_IS_EMPTY("MOBILE NO IS EMPTY"),
		LOGIN_FAILED("LOGIN FAILED"),
		ERROR_WHILE_UPDATING_SOCIETY_BIOMETRIC_AUTHENTICATION_FLAG("Error updating society biometric authentication flag"),
		ACCOUNT_LOCKED_3_ATTEMPTS("Your account has been locked due to multiple failed login attempts"),
		ACCOUNT_UPDATION_FAILED_IN_USER_MASTER("Account Updation failed while doing account update in User master"),
		ACCOUNT_NOT_LOCKED("User is not Locked"),
		ACCOUNT_LOCKED("User is Locked"),
		ERROR_WHILE_RESETTING_LOGIN_ATTEMPTS("Error while resetting login attempts"),
		ACCOUNT_UNLOCKED_SUCCESSFULLY("Your Account Unlocked Successfully"),
		FAILED_TO_UNLOCK_ACCOUNT("Failure while Unlocking Account");

		public final String value;

		LOGIN_ERROR_MESSAGE(String value) {
			this.value = value;
		}
	}
	
	public enum USER_CATEGORY {
		HOST("HST"), STATE_OWNER("OWS"), NAFED_STATE_ADMIN("SBM"), STATE_AGENCY("AGS"), DISTRICT_AGENCY("AGD"),
		SOCIETY("SOC"), NAFED_HQ_ADMIN("NSA"), WSP("WSP"),ASSAYER("ASY");

		public final String value;

		USER_CATEGORY(String value) {
			this.value = value;
		}
	}
}
