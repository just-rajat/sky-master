/**
 * 
 */
package sky.farmerBenificiary.repository;

import java.sql.Connection;
import java.util.Optional;

import org.slf4j.Logger;

import sky.farmerBenificiary.payloads.LoginBean;
import sky.farmerBenificiary.payloads.Password;
import sky.farmerBenificiary.payloads.User;

/**
 * 
 */
public interface LoginDao {

	String checkIfUserExist(LoginBean bean, boolean passwordIsMandatory, Connection con, Logger log);

	User validateMobileNo(LoginBean bean, Connection con, Logger log);
	
	int resetLoginAttempts(String mobileNo, Connection con, Logger log);
	
	int insertintoapplicationClientIP(User bean,String loginStage,String serverIp,String appcode, Connection con, Logger log) ;
	
	User checkIfOnlyUserIdExists(LoginBean bean, Connection con, Logger log);
	
	int incrementLoginAttempts(String mobileNo,int currentAttempts, Connection con, Logger log);
	
	Optional<User> getUserByNamePassword(String username, Logger log);
	
	Optional<User> getUserByNameRefreshToken(String username, Logger log);

	int updateDefaultPassword(Password passwordDetails, Logger log);

	int updateToken(String token, String userId, Logger log);
}

