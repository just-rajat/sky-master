/**
 * 
 */
package sky.farmerBenificiary.repositotyImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import com.neml.sms.util.MessageConstants.LoggerConstant;

import sky.farmerBenificiary.payloads.LoginBean;
import sky.farmerBenificiary.payloads.Password;
import sky.farmerBenificiary.payloads.User;
import sky.farmerBenificiary.repository.LoginDao;
import sky.farmerBenificiary.utils.Constants;
import sky.farmerBenificiary.utils.Constants.LOGIN_FLAG;
import sky.farmerBenificiary.utils.LoggerUtil;
import sky.farmerBenificiary.utils.QueryMaster;
import sky.farmerBenificiary.utils.Utils;

/**
 * 
 */
@Repository
public class LoginDaoImpl implements LoginDao {
	
	@Autowired
	@Qualifier("procprd")
	DataSource dataSource;

	@Override
	public String checkIfUserExist(LoginBean bean, boolean passwordIsMandatory, Connection con, Logger log) {
		LoggerUtil.info(LoggerConstant.INSIDE.value, bean.getMobileNo(), null, log);
		String Result = "";
		QueryMaster qm = new QueryMaster();
		List<Object> param = new ArrayList<Object>();
		ResultSet rs = null;
		StringBuilder query = new StringBuilder();
		try {
			query.append(
					"select user_first_login, user_id,password_expiry_date from user_mstr where upper(user_id) = upper(?) and user_is_active = ? ");
			param.add(bean.getMobileNo());
			param.add(LOGIN_FLAG.USER_IS_ACTIVE.value);
			if (Utils.isNeitherNullNorEmpty(bean.getPassword()) || passwordIsMandatory) {
				query.append(" and user_password=? ");
				param.add(bean.getPassword());
			}
			rs = qm.select(query.toString(), param, con);

			while (rs.next()) {
				String userFirstLogin = rs.getString("user_first_login");
				if (userFirstLogin.equalsIgnoreCase(Constants.YES_NO.YES.value)) {
					Result = userFirstLogin;
					return Result;
				} else if (userFirstLogin.equalsIgnoreCase(Constants.YES_NO.NO.value)) {
					Timestamp passwordExpiryDate = rs.getTimestamp("password_expiry_date");
					Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
					if (passwordExpiryDate != null && !passwordExpiryDate.after(currentTimestamp)) {
						Result = Constants.YES_NO.YES.value;
					} else {
						Result = userFirstLogin;
					}
					return Result;
				}
			}

		} catch (Exception e) {
			LoggerUtil.exception(e, bean.getMobileNo(), null, log);
		}
		LoggerUtil.info(LoggerConstant.EXIT.value, bean.getMobileNo(), null, log);
		return Result;
	}
	
	@Override
	public User validateMobileNo(LoginBean bean, Connection con, Logger log) {
		LoggerUtil.info(LoggerConstant.INSIDE.value, bean.getMobileNo(), null, log);
		ResultSet rs = null;
		QueryMaster qm = new QueryMaster();
		StringBuilder query = new StringBuilder();
		List<Object> param = new ArrayList<>();
		User userDetails = new User();
		try {

//			query.append("select u.user_category, u.org_id, u.user_id, u.user_name, u.user_category, u.user_password,"
//					+ " string_agg(r.role_id,',') as user_role, user_sso_token, user_sso_md5, org_user_scope,"
//					+ " (case when u.user_category='SOC' then  rm.org_state else osd.state_code  end ) as state_code, "
//					+ " (case when u.user_category='SOC' then  rm.org_district else osd.district_code  end )  as district_code,  "
//					+ " user_login_mode,u.user_address,designation,u.user_doc_type,"
//					+ " u.user_email,u.dccb_id,user_type,user_mobile, rm.org_name, u.user_first_login,coalesce(rm.admin_id,'') as admin_id ,"
//					+ " coalesce(uba.is_authenticated,'N') as is_authenticated" +
//					", u.is_demographic_updated , user_state_name,user_district_name,user_taluka_name,user_village_name,user_panchayat_name,user_state_code, " +
//					" user_district_code,user_taluka_code,user_village_code " +
//					" from user_mstr u "
//					+ " inner join user_role_mapping r on (u.user_id = r.user_id and r.is_active ='T') "
//					+ " inner join registration_mstr rm on (rm.org_id = u.org_id and rm.org_request_status = 'COMPLETED') "
//					+ " left join (select org_id, string_agg(distinct state_code,',')  as state_code,"
//					+ " string_agg(distinct district_code,',') as district_code from org_state_district_mapping "
//					+ " group by org_id) osd on (osd.org_id = u.org_id) "
//					+ " left join user_biometric_authentication uba on (uba.user_id = u.user_id) "
//					+ " where upper(u.user_id) = upper(?) " + " group by u.org_id, u.user_id, "
//					+ " u.user_name, u.user_category, u.user_password,11,12, user_login_mode,user_type,"
//					+ " u.dccb_id, user_mobile,rm.org_name,rm.admin_id,uba.is_authenticated limit 1");

			query.append("SELECT " + "    u.user_category," + "    u.org_id," + "    u.user_id," + "    u.user_name,"
					+ "    u.user_password," + "    r.user_roles," + "    u.user_sso_token," + "    u.user_sso_md5,"
					+ "    u.org_user_scope," + "    coalesce(osd.state_code, RM.org_state) as state_code,"
					+ "    COALESCE(rm.org_district, osd.district_code) AS district_code," + "    u.user_login_mode,"
					+ "    u.user_address," + "    u.designation," + "    u.user_doc_type," + "    u.user_email,"
					+ "    u.dccb_id," + "    u.user_type," + "    u.user_mobile," + "    rm.org_name,"
					+ "    u.user_first_login," + "    COALESCE(rm.admin_id, '') AS admin_id,"
					+ "    rm.is_authenticated," + "    u.is_demographic_updated,"
//					+ "    u.user_state_name,"
//					+ "    u.user_district_name,"
//					+ "    u.user_taluka_name,"
//					+ "    u.user_village_name,"
//					+ "    u.user_panchayat_name,"
//					+ "    u.user_state_code,"
//					+ "    u.user_district_code,"
//					+ "    u.user_taluka_code,"
//					+ "    u.user_village_code,"
					+ "    coalesce(rm.is_aadhaar_flow_req,'N') as is_aadhaar_flow_req, "
					+ "    coalesce(rm.aadhaar_auth_type,'NA') as aadhaar_auth_type " + " FROM user_mstr u "
					+ " JOIN registration_mstr rm " + "    ON rm.org_id = u.org_id "
					+ "   AND rm.org_request_status = 'COMPLETED' " + " LEFT JOIN LATERAL(" + "    SELECT "
					+ "        org_id, " + "        string_agg(DISTINCT state_code, ',')   AS state_code, "
					+ "        string_agg(DISTINCT district_code, ',') AS district_code "
					+ "    FROM org_state_district_mapping g where g.org_id=u.org_id " + "    GROUP BY org_id"
					+ " ) osd ON true "
					// + "-- Aggregate roles separately per user_id"
					+ " LEFT JOIN LATERAL (" + "    SELECT string_agg(role_id, ',') AS user_roles "
					+ "    FROM user_role_mapping r" + "    WHERE r.user_id = u.user_id AND r.is_active = 'T' "
					+ " ) r ON true" + " WHERE upper(u.user_id) = upper(?) ");

			param.add(bean.getMobileNo());
			rs = qm.select(query.toString(), param, con);
			while (rs.next()) {
				userDetails.setPassword(rs.getString("user_password"));
				userDetails.setUserId(rs.getString("user_id"));
				userDetails.setUserName(rs.getString("user_name"));
				// userDetails.setUserRole(rs.getString("user_role"));
				userDetails.setUserRole(rs.getString("user_roles"));
				// userDetails.setUserRoles((ArrayList<String>) rs.getArray("user_roles"));
				userDetails.setToken(rs.getString("user_sso_token"));
				userDetails.setSessionId(rs.getString("user_sso_md5"));
				userDetails.setUserCategory(rs.getString("user_category"));
				userDetails.setOrgId(rs.getString("org_id"));
				userDetails.setUserScope(rs.getString("org_user_scope"));
				userDetails.setStateCode(rs.getString("state_code"));
				userDetails.setDistrictCode(rs.getString("district_code"));
				userDetails.setLoginMode(rs.getString("user_login_mode"));
				userDetails.setDocumentNo(rs.getString("user_doc_type"));
				userDetails.setUserEmail(rs.getString("user_email"));
				userDetails.setDesignation(rs.getString("designation"));
				userDetails.setDccbId(rs.getString("dccb_id"));
				userDetails.setUserType(rs.getString("user_type"));
				userDetails.setUserAddress(rs.getString("user_address"));
				userDetails.setMobileNo(rs.getString("user_mobile"));
				userDetails.setOrgName(rs.getString("org_name"));
				userDetails.setUserFirstTimeLogin(rs.getString("user_first_login"));
				userDetails.setAdminId(rs.getString("admin_id"));
				userDetails.setIsSoceityAadhaarVerified(rs.getString("is_authenticated"));
				userDetails.setIsDemographicUpdated(rs.getString("is_demographic_updated"));
				userDetails.setIsAadhaarFlowReq(rs.getString("is_aadhaar_flow_req"));
				userDetails.setAadhaarAuthType(rs.getString("aadhaar_auth_type"));

//				userDetails.setStateName( rs.getString("user_state_name"));
//				userDetails.setDistrictName( rs.getString("user_district_name"));
//				userDetails.setTalukaName( rs.getString("user_taluka_name"));
//				userDetails.setVillageName( rs.getString("user_village_name"));
//				userDetails.setPanchayatName( rs.getString("user_panchayat_name"));
//				userDetails.setTalukaCode( rs.getString("user_taluka_code"));
//				userDetails.setVillageCode( rs.getString("user_village_code"));
			}
		} catch (Exception e) {
			LoggerUtil.exception(e, bean.getMobileNo(), null, log);
		}
		LoggerUtil.info(LoggerConstant.EXIT.value, bean.getMobileNo(), null, log);
		return userDetails;
	}
	@Override
	public int resetLoginAttempts(String mobileNo, Connection con, Logger log) {
		LoggerUtil.info(LoggerConstant.INSIDE.value, mobileNo, null, log);
		int result = 0;
		QueryMaster qm = new QueryMaster();
		List<Object> param = new ArrayList<Object>();
		StringBuilder updateQuery = new StringBuilder();

		try {
			updateQuery.append(
					"UPDATE user_mstr SET login_attempt = 0 , user_modified_on = current_timestamp, user_modified_by = ? WHERE upper(user_id) = upper(?)");
			param.add(mobileNo);
			param.add(mobileNo);
			result = qm.updateInsert(updateQuery.toString(), param, con);
		} catch (Exception e) {
			LoggerUtil.exception(e, mobileNo, null, log);
		}
		LoggerUtil.info(LoggerConstant.EXIT.value, mobileNo, null, log);
		return result;
	}
	
	@Override
	public int insertintoapplicationClientIP(User bean, String loginStage, String serverIp, String appcode,
			Connection con, Logger log) {
		LoggerUtil.info(LoggerConstant.INSIDE.value, null, null, log);
		QueryMaster qm = new QueryMaster();
		List<Object> param = new ArrayList<Object>();
		StringBuilder query = new StringBuilder();
		int result = 0;
		// Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		try {

			query.append(
					"INSERT INTO application_clientip (userid,clientip,logindate,applicationname,ipcapturestage,serverip,appcode)"
							+ "VALUES(?,?,now(),?,?,?,?)");
			param.add(bean.getUserId());
			param.add(bean.getClientIP());
			// param.add(currentTimestamp);
			param.add(bean.getOwnerCode());
			param.add(loginStage);
			param.add(serverIp);
			param.add(appcode);
			result = qm.updateInsert(query.toString(), param, con, false);
		} catch (Exception ex) {
			LoggerUtil.exception(ex, null, null, log);
		}
		LoggerUtil.info(LoggerConstant.EXIT.value, null, null, log);
		return result;
	}
	@Override
	public User checkIfOnlyUserIdExists(LoginBean bean, Connection con, Logger log) {
		LoggerUtil.info(LoggerConstant.INSIDE.value, bean.getMobileNo(), null, log);
		User userObj = new User();
		try {
			QueryMaster qm = new QueryMaster();
			List<Object> param = new ArrayList<Object>();
			StringBuilder query = new StringBuilder();
			query.append("select user_id, login_attempt from user_mstr where upper(user_id) = upper(?) and user_is_active in (?, ?) ");

			param.add(bean.getMobileNo());
			param.add(LOGIN_FLAG.USER_IS_ACTIVE.value);
			param.add(LOGIN_FLAG.USER_IS_LOCK.value);
			ResultSet rs = qm.select(query.toString(), param, con);
			if (rs.next()) {
				userObj.setUserId(rs.getString("user_id"));
				userObj.setLoginAttempt(rs.getInt("login_attempt"));
			}

		} catch (Exception e) {
			LoggerUtil.exception(e, bean.getMobileNo(), null, log);
		}
		LoggerUtil.info(LoggerConstant.EXIT.value, bean.getMobileNo(), null, log);
		return userObj;
	}
	
	@Override
	public int incrementLoginAttempts(String mobileNo, int currentAttempts, Connection con, Logger log) {
		LoggerUtil.info(LoggerConstant.INSIDE.value, mobileNo, null, log);
		int result = 0;
		QueryMaster qm = new QueryMaster();
		List<Object> param = new ArrayList<Object>();
		StringBuilder updateQuery = new StringBuilder();

		try {
			currentAttempts++;

			if (currentAttempts >= 3) {
				updateQuery.append(
						"UPDATE user_mstr SET login_attempt = ?, user_is_active = ?, user_modified_on = current_timestamp, user_modified_by = ? WHERE upper(user_id) = upper(?)");
				param.add(currentAttempts);
				param.add("L");
				param.add(mobileNo);
				param.add(mobileNo);
			} else {
				updateQuery.append(
						"UPDATE user_mstr SET login_attempt = ?, user_modified_on = current_timestamp, user_modified_by = ? WHERE upper(user_id) = upper(?)");
				param.add(currentAttempts);
				param.add(mobileNo);
				param.add(mobileNo);
			}

			result = qm.updateInsert(updateQuery.toString(), param, con);
			if (result > 0) {
				result = currentAttempts;
			}

		} catch (Exception e) {
			LoggerUtil.exception(e, mobileNo, null, log);
			result = -1;
		}

		LoggerUtil.info(LoggerConstant.EXIT.value, mobileNo, null, log);
		return result;
	}
	
	@Override
	public Optional<User> getUserByNamePassword(String username, Logger log) {
		log.info("Entering into {} and getUserByNamePassword()", LoginDaoImpl.class.getName());
		StringBuilder sql = new StringBuilder();
		ArrayList<Object> param = new ArrayList<>();
		Connection con = null;
		ResultSet rs;
		QueryMaster queryMaster = new QueryMaster();
		try {
			con = dataSource.getConnection();
			sql.append(
					"""
							select u.user_category, u.org_id, u.user_id, u.user_name, u.user_category, u.user_password,
							string_agg(r.role_id,',') as user_role, user_sso_token, user_sso_md5, org_user_scope,u.user_doc_type,u.user_email,
							osd.state_code, osd.district_code, user_login_mode,u.designation from user_mstr u
							inner join user_role_mapping r on (u.user_id = r.user_id and r.is_active ='T')
							left join (select org_id, state_code, string_agg(district_code,',') as district_code from org_state_district_mapping
							group by org_id, state_code) osd on (osd.org_id = u.org_id)
							where upper(u.user_id) = upper(?)
							group by u.user_category, u.org_id, u.user_id,
							u.user_name, u.user_category, u.user_password,osd.state_code, osd.district_code, user_login_mode""");
			param.add(username);
			rs = queryMaster.select(sql.toString(), param, con);
			User bean = null;
			while (rs.next()) {
				bean = new User();
				bean.setPassword(rs.getString("user_password"));
				bean.setUserId(rs.getString("user_id"));
				bean.setUserName(rs.getString("user_name"));
				bean.setUserRole(rs.getString("user_role"));
				bean.setToken(rs.getString("user_sso_token"));
				bean.setSessionId(rs.getString("user_sso_md5"));
				bean.setUserCategory(rs.getString("user_category"));
				bean.setOrgId(rs.getString("org_id"));
				bean.setUserScope(rs.getString("org_user_scope"));
				bean.setStateCode(rs.getString("state_code"));
				bean.setDistrictCode(rs.getString("district_code"));
				bean.setLoginMode(rs.getString("user_login_mode"));

				ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(bean.getUserRole().split(",")));
				bean.setUserRoles(arrayList);
				ArrayList<SimpleGrantedAuthority> collect = arrayList.stream().map(SimpleGrantedAuthority::new)
						.collect(Collectors.toCollection(ArrayList::new));
				bean.setAuthorities(collect);
			}
			return Optional.ofNullable(bean);
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				if (Objects.nonNull(con))
					con.close();
			} catch (SQLException e) {
				log.error(e.getMessage());
			}
			log.info("Exiting from {} and getUserByNamePassword()", LoginDaoImpl.class.getName());
		}
		return Optional.empty();
	}

	@Override
	public Optional<User> getUserByNameRefreshToken(String username, Logger log) {
		return Optional.empty();
	}

	@Override
	public int updateDefaultPassword(Password passwordDetails, Logger log) {
		return 0;
	}

	@Override
	public int updateToken(String token, String userId, Logger log) {
		return 0;
	}

}
