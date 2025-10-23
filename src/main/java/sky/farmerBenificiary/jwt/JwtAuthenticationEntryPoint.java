package sky.farmerBenificiary.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("Caught with Unauthorized error {}", authException.getMessage());

        boolean isSessionChanged = (boolean) request.getAttribute("isSessionChanged");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        final Map<String, Object> body = new HashMap<>();

        if(isSessionChanged){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            body.put("status", HttpServletResponse.SC_FORBIDDEN);
            body.put("error", "Session Expired");
            body.put("message", "Session Expired");
            body.put("path", request.getServletPath());
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            body.put("error", "Unauthorized");
            body.put("message", authException.getMessage());
            body.put("path", request.getServletPath());
        }
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
