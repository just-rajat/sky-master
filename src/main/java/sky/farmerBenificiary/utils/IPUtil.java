/**
 * 
 */
package sky.farmerBenificiary.utils;
import java.net.InetAddress;
import java.net.UnknownHostException;

import jakarta.servlet.http.HttpServletRequest;
/**
 * @author Neml10345
 *
 */
public class IPUtil {
	public static String getClientIp(HttpServletRequest request) {
		//System.out.println("Remote IP " +request.getRemoteAddr());
        String clientIp;
        // Check for X-Forwarded-For header
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For may contain multiple IPs, so take the first one
            clientIp = xForwardedFor.split(",")[0].trim();
        } else {
            // Fallback to getRemoteAddr() if there's no proxy
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
	
	public static String getServerIp(HttpServletRequest request) {
	    try {
	        // This gives the server IP where app is running
	        String serverIp = InetAddress.getLocalHost().getHostAddress();
	      //  System.out.println("Server IP: " + serverIp);
	        return serverIp;
	    } catch (UnknownHostException e) {
	        e.printStackTrace();
	        return "UNKNOWN";
	    }
	}
}