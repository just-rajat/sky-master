package sky.farmerBenificiary.serviceImpl;


import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sky.farmerBenificiary.payloads.Password;
import sky.farmerBenificiary.payloads.User;
import sky.farmerBenificiary.repository.LoginDao;
import sky.farmerBenificiary.utils.Utils;

@Slf4j
@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    LoginDao loginDao;

    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Entering into {} and loadUserByUsername() with username {}",UserInfoService.class.getName(), username);

        Optional<User> user = Optional.empty();
        user = loginDao.getUserByNamePassword(username, log);

        if (user.isPresent()) {
            User userBean = user.get();
            log.info("Exiting from {} and loadUserByUsername() with UserModel {} "
                    , UserInfoService.class.getName(),userBean);
            return userBean;
        } else {
            log.error("User Not Found in {} and loadUserByUsername() ", UserInfoService.class.getName());
            throw new UsernameNotFoundException("User Not Found");
        }
    }

    public UserDetails getUserByNameRefreshToken(String username) throws UsernameNotFoundException {

        log.info("Entering into {} and getUserByNameRefreshToken() with username {}",UserInfoService.class.getName(), username);
        Optional<User> user = loginDao.getUserByNameRefreshToken(username, log);
        if (user.isPresent()) {
            User userBean = user.get();
            log.info("Exiting from {} and getUserByNameRefreshToken() with UserModel {} ",
                    UserInfoService.class.getName(), userBean);
            return userBean;
        } else {
            log.error("User Not Found in {} and getUserByNameRefreshToken() ", UserInfoService.class.getName());
            throw new UsernameNotFoundException("User Not Found ");
        }
    }

    public int updateDefaultPassword(Password passwordDetails) {
        log.info("Entering into {} and updateDefaultPassword() with PasswordDTO {}", UserInfoService.class.getName(),
                passwordDetails);
        passwordDetails = Utils.getEncryptedPassword(passwordDetails);
        return loginDao.updateDefaultPassword(passwordDetails, log);
    }

    public int updateToken(String token, String userId, Logger log) {
        log.info("Entering into {} and updateToken() with token {} and userId {}", UserInfoService.class.getName(),
                token, userId);
        return loginDao.updateToken(token, userId, log);
    }

}
