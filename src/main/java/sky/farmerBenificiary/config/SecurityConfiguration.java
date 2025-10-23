package sky.farmerBenificiary.config;


import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import sky.farmerBenificiary.encoder.CustomSha256Encoder;
import sky.farmerBenificiary.filter.AuthenticationFilter;
import sky.farmerBenificiary.jwt.ApplicationConfiguration;
import sky.farmerBenificiary.jwt.JwtAuthenticationEntryPoint;
import sky.farmerBenificiary.serviceImpl.UserInfoService;

@Configuration
public class SecurityConfiguration {
 

    @Autowired
    CustomSha256Encoder customsha256encoder;

    @Autowired
    AuthenticationFilter jwtFilter;

    @Autowired
    JwtAuthenticationEntryPoint JwtAuthenticationEntryPoint;

    @Autowired
    ApplicationConfiguration applicationConfiguration;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .cors()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/swagger-resources",
                        "/auth-server/auth/userLogin",
                        "/auth/checkUserAndSendOTP",
                        "/auth/saveSignUpData",
                        "/auth/resentOTP",
                        "/auth/generateAccessToken",
                        "/auth/sendOTPUserId",
                        "/auth/resendOTPUserId",
                        "/auth/verifyOTP",
                        "/auth/forgotPassword",
                        "/redis/put",
                        "/auth-server/otp/verifyOtp",
                        "/auth-server/otp/sendOtp",
                        "/auth-server/auth/checkIfUserExist",
                        "/auth-server/auth/forgotPassword",
                        "/auth-server/auth/changePassword",
                        "/auth-server/auth/submitNewPassword",
                        "/schedular/esamridhiHighlights/getEsamridhiHighlights",
                        "/schedular/esamridhiHighlights/getOngoingSchemeDetails",
                        "/actuator/health",
                        "/auth-server/otp/otpSend",
                        "/auth-server/auth/**",
                        "/auth-server/modules/getModuleListV5",
                        "/auth-server/otp/otpSendOnChangePassword",
                        "/auth-server/otp/verifyUserOtp",
                        "/auth-server/otp/sendUserOtp",
                        "/auth-server/eWhrAuth",
                        "/auth-server/eWhrAuth/userLogin",
                        "/auth-server/eWhrAuth/sendOTP",
                        "/auth-server/eWhrAuth/verifyOtp",
                        "/auth-server/eWhrAuth/forgotPassword",
                        "/auth-server/eWhrAuth/changePassword",
                        "/auth-server/auth/portalAuthenticationCheck",
                        "/auth-server/auth/unlockUser",
                        "/auth-server/otp/sendOtpToFarmer",
                        "/auth-server/otp/verifyOtpToFarmer",
                        "/auth-server/otp/checkMobileAlreadyExists",
                        "/auth-server/otp/authenticateVerifiedUser"
                ).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).exceptionHandling()
                .authenticationEntryPoint(JwtAuthenticationEntryPoint);

        return httpSecurity.build();

    }

    @Bean
    @Primary
    public UserDetailsService UserService() {
        return new UserInfoService();
    }

    private CookieCsrfTokenRepository getCsrfTokenRepository() {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        tokenRepository.setCookiePath("/");
        return tokenRepository;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(UserService());
        authenticationProvider.setPasswordEncoder(customsha256encoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.applyPermitDefaultValues();
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.addAllowedHeader("*");
        configuration.setExposedHeaders(List.of("X-XSRF-TOKEN"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

    }

}