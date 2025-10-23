package sky.farmerBenificiary.utils;

import org.springframework.stereotype.Component;

@Component
public class RedisKeys {

    public static String IDLE_SESSION_DATA = "IDLE";
    public static String JWT_TOKEN = "JWT";
    public static String USER_SECRET_KEY = "SECRET_KEY";
    public static String REFRESH_JWT_TOKEN = "REFRESH_JWT";
    public static String STATE_TRANSCODE_MAP_KEY = "STATE_TRANSCODE_MAP";

}
