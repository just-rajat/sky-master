package sky.farmerBenificiary.filter;

import java.io.IOException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.google.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import sky.farmerBenificiary.jwt.ApplicationConfiguration;
import sky.farmerBenificiary.jwt.JwtUtil;
import sky.farmerBenificiary.payloads.User;
import sky.farmerBenificiary.serviceImpl.UserInfoService;
import sky.farmerBenificiary.utils.RedisKeys;
import sky.farmerBenificiary.utils.Utils;

@Slf4j
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final UserInfoService userDetailsService;

    @Autowired
    ApplicationConfiguration applicationConfiguration;

//    @Resource(name = "userSession")
//    private RedisTemplate<String, String> userSession;
//
//    @Resource(name = "jwtUserSession")
//    private RedisTemplate<String, String> jwtUserSession;


    public AuthenticationFilter(JwtUtil jwtUtil, UserInfoService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("Entering into {} and doFilterInternal()", AuthenticationFilter.class.getName());
            log.info("In {} with Source {}", AuthenticationFilter.class.getName(), request.getHeader("source"));
            var jwt = parseJwt(request,log);
            log.info("JWT token is {}", jwt);
            request.setAttribute("isSessionChanged", false);
            if (jwt != null && jwtUtil.validateJwtToken(jwt, log)) {
                var username = jwtUtil.getUserNameFromJwtToken(jwt, log);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    var userDetailsJson = Utils.userSession.get(RedisKeys.IDLE_SESSION_DATA + username);
                    var userDetails = Objects.nonNull(userDetailsJson)
                            ? new Gson().fromJson(userDetailsJson, User.class)
                            : null;
                    var actualToken = Utils.jwtUserSession.get(RedisKeys.JWT_TOKEN + username);
                    if (actualToken != null && actualToken.equals(jwt)) {
                    	 Utils.jwtUserSession.remove(RedisKeys.JWT_TOKEN + username);
                        if (userDetails != null && username.equalsIgnoreCase(userDetails.getUserId())) {
                            //long expiryTime = Utils.userSession.remove(RedisKeys.IDLE_SESSION_DATA + username);
                            log.info("Session Expiry Time Left for userName {} is {} ", username, null);
                            Utils.jwtUserSession.remove(RedisKeys.IDLE_SESSION_DATA + username);
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                            //for logging
                            putDataIntoMdc();
                        } else {
                            log.error("User Session has been Expired.");
                        }
                    } else {
                        log.info("Session has been changed with Actual Token {} ", actualToken);
                        request.setAttribute("isSessionChanged", true);
                    }

                }
            }
        } catch (Exception e) {
            log.error("In {} with Exception {} ", AuthenticationFilter.class.getName(), e);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("userid");
        }
    }

    private String parseJwt(HttpServletRequest request, Logger log) {
        log.info("Entering into {} and parseJwt ()", AuthenticationFilter.class.getName());
        var jwt = jwtUtil.getJwtFromCookies(request, AuthenticationFilter.log);
        log.info("Cookie Found in request{} ", jwt);
        return jwt;
    }

    private void putDataIntoMdc() {
        String userid;
        User userModel = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userid = userModel.getUserId();
        MDC.put("userid", userid);
    }
}
