package sky.farmerBenificiary.serviceImpl;

import java.sql.Connection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.neml.sms.util.MessageConstants.LoggerConstant;

import jakarta.servlet.http.HttpServletRequest;
import sky.farmerBenificiary.jwt.ApplicationConfiguration;
import sky.farmerBenificiary.jwt.JwtUtil;
import sky.farmerBenificiary.payloads.ApiResponse;
import sky.farmerBenificiary.payloads.LoginBean;
import sky.farmerBenificiary.payloads.User;
import sky.farmerBenificiary.repository.LoginDao;
import sky.farmerBenificiary.service.LoginService;
import sky.farmerBenificiary.utils.Constants.CREATOR_MODE;
import sky.farmerBenificiary.utils.Constants.FLAG_VALUES;
import sky.farmerBenificiary.utils.ErrorConstants.LOGIN_ERROR_MESSAGE;
import sky.farmerBenificiary.utils.ErrorConstants.SUCCESS_FAILED_CODE;
import sky.farmerBenificiary.utils.ErrorConstants.SUCCESS_FAILED_STRING;
import sky.farmerBenificiary.utils.IPUtil;
import sky.farmerBenificiary.utils.LoggerUtil;
import sky.farmerBenificiary.utils.RedisKeys;
import sky.farmerBenificiary.utils.Utils;

@Service
public class LoginServiceImpl implements LoginService {
	
	@Autowired
	ApplicationConfiguration applicationConfiguration;

	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	@Qualifier("procprd")
	DataSource dataSource;
	
	@Autowired
	LoginDao loginDao;
	
	
	

	@Override
	public User userLogin(LoginBean bean, HttpServletRequest request, Logger log) {
		LoggerUtil.info(LoggerConstant.INSIDE.value, bean.getMobileNo(), null, log);
		User response = new User();
		int result1 = 0;
		Connection con = null;
		try {
			con = dataSource.getConnection();
			con.setAutoCommit(true);
			if (!Utils.isNeitherNullNorEmpty(bean.getMobileNo())) {
				response.setResponseMessage(LOGIN_ERROR_MESSAGE.MOBILE_NUMBER_IS_EMPTY.value);
				response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
				LoggerUtil.errorInfo(LOGIN_ERROR_MESSAGE.MOBILE_NUMBER_IS_EMPTY.value, null, bean.toString(), log);

				return response;
			}
			if (!Utils.isNeitherNullNorEmpty(bean.getPassword())) {
				response.setResponseMessage(LOGIN_ERROR_MESSAGE.PASSWORD_IS_EMPTY.value);
				response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
				LoggerUtil.errorInfo(LOGIN_ERROR_MESSAGE.PASSWORD_IS_EMPTY.value, null, bean.toString(), log);

				return response;
			}
//			if (Utils.isNeitherNullNorEmpty(bean.getMobileNo())) {
//				if (bean.getMobileNo().matches("\\d+")
//						&& (bean.getMobileNo().length() == 10 || bean.getMobileNo().length() == 12)) {
//					ApiResponse res = commonService.checkMobileNoValidOrNot(bean.getMobileNo(), log);
//					if (res.getResponseCode() == SUCCESS_FAILED_CODE.FAILED.value) {
//						response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
//						response.setResponseMessage(LOGIN_ERROR_MESSAGE.ERROR_INVALID_MOBILE_NO.value);
//						return response;
//					}
//				}
//			}

//			// check user role - start
//			if (Utils.isNeitherNullNorEmpty(bean.getIsFromAndroid()) && bean.getIsFromAndroid().equalsIgnoreCase("Y")) {
//				String userRole = otpDao.checkUserRole(bean, con, log);
//				if (!Utils.isNeitherNullNorEmpty(userRole) || !userRole.equalsIgnoreCase(USER_ROLES.SOCIETY.value)) {
//					response.setResponseMessage(LOGIN_ERROR_MESSAGE.ACCESS_DENIED_NOT_A_SOCIETY_USER.value);
//					response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
//					LoggerUtil.errorInfo(LOGIN_ERROR_MESSAGE.ACCESS_DENIED_NOT_A_SOCIETY_USER.value, null,
//							bean.toString(), log);
//					return response;
//				}
//			}

//			// Check if account is already locked before attempting login
//			boolean isAccountLocked = loginDao.checkAccountLocked(bean.getMobileNo(), con, log);
//			if (isAccountLocked) {
//				response.setResponseMessage(LOGIN_ERROR_MESSAGE.ACCOUNT_LOCKED.value);
//				response.setResponseCode(SUCCESS_FAILED_CODE.ACCOUNT_LOCKED.value);
//				// response.setAccountLocked(true);
//				LoggerUtil.errorInfo(LOGIN_ERROR_MESSAGE.ACCOUNT_LOCKED.value, null, bean.toString(), log);
//				return response;
//			}

			// check user role - end
			boolean passwordIsMandatory = true;
			String result = loginDao.checkIfUserExist(bean, passwordIsMandatory, con, log);
			if (Utils.isNeitherNullNorEmpty(result)) {
				if (result.equalsIgnoreCase(FLAG_VALUES.FLAG_N.value)) {
					response = loginDao.validateMobileNo(bean, con, log);

					// reset the count of attempts
					int resetlogin = loginDao.resetLoginAttempts(bean.getMobileNo(), con, log);
					if (resetlogin <= 0) {
						LoggerUtil.errorInfo("Failed to reset login attempts for user: " + bean.getMobileNo(), null,
								bean.toString(), log);
						response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
						response.setResponseMessage(LOGIN_ERROR_MESSAGE.ERROR_WHILE_RESETTING_LOGIN_ATTEMPTS.value);
						return response;
					}

					// Updating society biometric authentication status on login
//					if (Utils.isNeitherNullNorEmpty(response.getUserCategory())
//							&& response.getUserCategory().equalsIgnoreCase(Constants.USER_ROLES.SOCIETY.value)) {
//						int updateAuthentication = loginDao.updateSocAuthentication(bean, log,con);
//						if (updateAuthentication >= 0) {
//							response.setIsSoceityAadhaarVerified(Constants.YES_NO.NO.value);
//						} else {
//							response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
//							response.setResponseMessage(LOGIN_ERROR_MESSAGE.LOGIN_FAILED.value);
//							LoggerUtil.errorInfo(
//									LOGIN_ERROR_MESSAGE.ERROR_WHILE_UPDATING_SOCIETY_BIOMETRIC_AUTHENTICATION_FLAG.value,
//									null, bean.toString(), log);
//							return response;
//						}
//
//					}

					if (Utils.isNeitherNullNorEmpty(response)) {

						var jwtToken = jwtUtil.generateTokenFromUsername(bean.getMobileNo(), log);
						var jwtCookie = jwtUtil.generateJwtCookie(bean.getMobileNo(), request, jwtToken, log);

						var refreshToken = jwtUtil.createRefreshToken(bean.getMobileNo(), log);
						var jwtRefreshCookie = jwtUtil.generateRefreshJwtCookie(refreshToken.getRefreshToken(), request,
								log);

						response.setJwtToken(jwtCookie.getValue());
						response.setJwtRefreshToken(jwtRefreshCookie.getValue());

						if (!Utils.isNeitherNullNorEmpty(response.getUserId())) {
							response.setMobileNo(bean.getMobileNo());
							response.setUserId(bean.getMobileNo());
							response.setResponseCode(SUCCESS_FAILED_CODE.NEW_USER_LOGIN.value);
							LoggerUtil.errorInfo(SUCCESS_FAILED_CODE.NEW_USER_LOGIN.toString(), null, bean.toString(),
									log);

						}

						response.setIsFarmer(FLAG_VALUES.FLAG_N.value);
						response.setOwnerCode(applicationConfiguration.getOwnerCode());
						String appcode = "NA";
						if (!Utils.isNeitherNullNorEmpty(bean.getCreatorMode())) {
							response.setCreatorMode(CREATOR_MODE.NOT_CAPTURED.value);
						} else {
							if (bean.getCreatorMode().equalsIgnoreCase("A")) {
								response.setCreatorMode(CREATOR_MODE.ANDROID.value);
								appcode = "ANDROID";
							} else if (bean.getCreatorMode().equalsIgnoreCase("W")) {
								response.setCreatorMode(CREATOR_MODE.WEB.value);
								appcode = "WEB";
							}
						}

//						userSession.opsForValue().set(RedisKeys.IDLE_SESSION_DATA + bean.getMobileNo(),
//								new Gson().toJson(response));
//						userSession.expire(RedisKeys.IDLE_SESSION_DATA + bean.getMobileNo(),
//								applicationConfiguration.getSessionValidity(), TimeUnit.MINUTES);
//
//						jwtUserSession.opsForValue().set(RedisKeys.JWT_TOKEN + bean.getMobileNo(),
//								jwtCookie.getValue());
//						jwtUserSession.expire(RedisKeys.JWT_TOKEN + bean.getMobileNo(),
//								applicationConfiguration.getSessionValidity(), TimeUnit.MINUTES);
						
						Utils.userSession.put(RedisKeys.IDLE_SESSION_DATA +bean.getMobileNo(),new Gson().toJson(response));
						Utils.jwtUserSession.put(RedisKeys.JWT_TOKEN + bean.getMobileNo(),jwtCookie.getValue());
						
						response.setOwnerCode(applicationConfiguration.getOwnerCode());
						response.setResponseCode(SUCCESS_FAILED_CODE.SUCCESS.value);
						//response.setMenuList(moduleService.getModuleListV5(bean.getMobileNo(), log));
						// response.setClientIP(request.getHeader("X-Forwarded-For"));
						response.setClientIP(IPUtil.getClientIp(request));
						String serverIp = IPUtil.getServerIp(request);
						log.info(Utils.isNeitherNullNorEmpty(response.getUserId()) + " : Client IP Address : "
								+ response.getClientIP() + " : Server IP Address : " + serverIp);
						if (response.getResponseCode() == SUCCESS_FAILED_CODE.SUCCESS.value) {
							result1 = loginDao.insertintoapplicationClientIP(response, "LOGIN", serverIp, appcode, con,
									log);
							if (result1 <= 0) {
								response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
								response.setResponseMessage(LOGIN_ERROR_MESSAGE.ERROR_WHILE_INSERTING_CLIENTIP.value);
								LoggerUtil.errorInfo(LOGIN_ERROR_MESSAGE.ERROR_WHILE_INSERTING_CLIENTIP.value, null,
										bean.toString(), log);
								return response;
							}
						}

						return response;

					} else {
						response.setResponseMessage(LOGIN_ERROR_MESSAGE.FARMER_DATA_NOT_FOUND.value);
						response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
						LoggerUtil.errorInfo(LOGIN_ERROR_MESSAGE.FARMER_DATA_NOT_FOUND.value, null, bean.toString(),
								log);
						return response;
					}

				} else if (result.equalsIgnoreCase(FLAG_VALUES.FLAG_Y.value)) {
					response.setMobileNo(bean.getMobileNo());
					response.setResponseCode(SUCCESS_FAILED_CODE.FIRST_TIME_LOGIN.value);
					LoggerUtil.errorInfo(SUCCESS_FAILED_CODE.FIRST_TIME_LOGIN.toString(), null, bean.toString(), log);
					return response;
				}
			} else {
				 User userData = loginDao.checkIfOnlyUserIdExists(bean, con, log);
				if (Utils.isNeitherNullNorEmpty(userData.getUserId())) {
					int accountLocked = loginDao.incrementLoginAttempts(bean.getMobileNo(), userData.getLoginAttempt(), con, log);
					if (accountLocked <= 0) {
						response.setResponseMessage(LOGIN_ERROR_MESSAGE.ACCOUNT_UPDATION_FAILED_IN_USER_MASTER.value);
						response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
						LoggerUtil.errorInfo(LOGIN_ERROR_MESSAGE.ACCOUNT_UPDATION_FAILED_IN_USER_MASTER.value, null,
								bean.toString(), log);
						
					} else if (accountLocked == 3) {
						response.setResponseMessage(LOGIN_ERROR_MESSAGE.ACCOUNT_LOCKED_3_ATTEMPTS.value);
						response.setResponseCode(SUCCESS_FAILED_CODE.ACCOUNT_LOCKED.value);
						LoggerUtil.errorInfo(LOGIN_ERROR_MESSAGE.ACCOUNT_LOCKED_3_ATTEMPTS.value, null, bean.toString(),
								log);
					}else {
						response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
						response.setResponseMessage(LOGIN_ERROR_MESSAGE.INCORRECT_CREDS.value);
						LoggerUtil.errorInfo(LOGIN_ERROR_MESSAGE.INCORRECT_CREDS.value, null, bean.toString(), log);
					}
					

				} else {
					response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
					response.setResponseMessage(LOGIN_ERROR_MESSAGE.INCORRECT_CREDS.value);
					LoggerUtil.errorInfo(LOGIN_ERROR_MESSAGE.INCORRECT_CREDS.value, null, bean.toString(), log);
				}
				return response;
			}

		} catch (Exception ex) {
			LoggerUtil.exception(ex, bean.getMobileNo(), null, log);
			response.setResponseCode(SUCCESS_FAILED_CODE.FAILED.value);
			return response;
		} finally {
			try {
				if (response.getResponseCode() == SUCCESS_FAILED_CODE.SUCCESS.value) {
					response.setResponseMessage(SUCCESS_FAILED_STRING.SUCCESS.value);
				}
			} catch (Exception e) {
				LoggerUtil.exception(e, bean.getMobileNo(), null, log);
			}
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
			} catch (Exception e) {
				LoggerUtil.exception(e, bean.getMobileNo(), null, log);
			}
		}
		LoggerUtil.info(LoggerConstant.EXIT.value, bean.getMobileNo(), null, log);
		return response;
	}


	@Override
	public ApiResponse checkIfUserExist(LoginBean bean, Logger log) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponse logout(LoginBean bean, HttpServletRequest request, Logger log) {
		// TODO Auto-generated method stub
		return null;
	}

}
