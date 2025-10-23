/**
 * 
 */
package sky.farmerBenificiary.service;

import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import sky.farmerBenificiary.payloads.ApiResponse;
import sky.farmerBenificiary.payloads.LoginBean;
import sky.farmerBenificiary.payloads.User;

/**
 * 
 */
public interface LoginService {
	// ResponseMessage forgotPassword(ForgotPassword requestModel, Logger log);
	// ResponseMessage changePassword(ChangePassword requestModel, Logger log);
	// ResponseMessage submitNewPassword(ChangePassword requestModel, Logger log);
	User userLogin(LoginBean bean, HttpServletRequest request, Logger log);

	ApiResponse checkIfUserExist(LoginBean bean, Logger log);

	ApiResponse logout(LoginBean bean, HttpServletRequest request, Logger log);
	// ResponseMessage userForgotPassword(ForgotPassword requestModel, Logger log);

	// ResponseMessage userDeactivate(LoginBean oUser, Logger log);

}
