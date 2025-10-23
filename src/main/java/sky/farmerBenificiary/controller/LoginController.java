/**
 * 
 */
package sky.farmerBenificiary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neml.sms.util.MessageConstants.LoggerConstant;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import sky.farmerBenificiary.payloads.LoginBean;
import sky.farmerBenificiary.payloads.User;
import sky.farmerBenificiary.service.LoginService;
import sky.farmerBenificiary.utils.ErrorConstants;
/**
 * 
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginController {
	
	@Autowired
    LoginService loginService;
	
	@PostMapping("/login")
    public ResponseEntity<User> userLogin(@RequestBody LoginBean bean, HttpServletRequest request){
        log.info(LoggerConstant.INSIDE.value, null, null, log);
        User response = loginService.userLogin(bean, request, log);
        if(response.getResponseCode() == ErrorConstants.SUCCESS_FAILED_CODE.SUCCESS.value){
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, response.getJwtToken())
                    .header(HttpHeaders.SET_COOKIE, response.getJwtRefreshToken()).body(response);
        }
        log.info(LoggerConstant.EXIT.value, null, null, log);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
