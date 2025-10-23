/**
 * 
 */
package sky.farmerBenificiary.utils;

/**
 * 
 */
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class LoggerUtil {

    // Log method for Inside and Exit the Method
    public static void info(String status, String userId, String param, Logger log) {
        String logString;
        String user;
        if (Utils.isNeitherNullNorEmpty(userId)) {
            user = " for User ID " + userId;
        } else {
            user = "";
        }
        if (!Utils.isNeitherNullNorEmpty(param)) {
            logString = status + " " + Thread.currentThread().getStackTrace()[2].getClassName() + " --> "
                    + Thread.currentThread().getStackTrace()[2].getMethodName() + " method " + user;
        } else {
            logString = status + " " + Thread.currentThread().getStackTrace()[2].getClassName() + " --> "
                    + Thread.currentThread().getStackTrace()[2].getMethodName() + " method " + user + " for parameter "
                    + param;
        }
        log.info(logString);
    }

    // Log method for Any Exception
    public static void exception(Exception exception, String userId, String param, Logger log) {
        String logString;
        String user;
        if (Utils.isNeitherNullNorEmpty(userId)) {
            user = " for User ID " + userId;
        } else {
            user = "";
        }
        if (!Utils.isNeitherNullNorEmpty(param)) {
            logString = "Exception in " + Thread.currentThread().getStackTrace()[2].getClassName() + " --> "
                    + Thread.currentThread().getStackTrace()[2].getMethodName() + " method " + user + " "
                    + exception.getMessage() + " " + exception.getStackTrace() + " at Line Number --> "
                    + exception.getStackTrace()[0].getLineNumber() + " Caught Exception at -->" + exception;
        } else {
            logString = "Exception in " + Thread.currentThread().getStackTrace()[2].getClassName() + " --> "
                    + Thread.currentThread().getStackTrace()[2].getMethodName() + " method " + user + " "
                    + exception.getMessage() + " " + exception.getStackTrace() + " at Line Number --> "
                    + exception.getStackTrace()[0].getLineNumber() + " for parameter " + param;
        }
        log.error(logString);
    }

    // Log Method for Custom Error
    public static void errorInfo(String errorMessage, String userId, String param, Logger log) {
        String logString;
        String user;
        if (Utils.isNeitherNullNorEmpty(userId)) {
            user = " for User ID " + userId;
        } else {
            user = "";
        }
        if (!Utils.isNeitherNullNorEmpty(param)) {
            logString = "Error in " + Thread.currentThread().getStackTrace()[2].getClassName() + " --> "
                    + Thread.currentThread().getStackTrace()[2].getMethodName() + " method " + user + " "
                    + errorMessage;
        } else {
            logString = "Error in " + Thread.currentThread().getStackTrace()[2].getClassName() + " --> "
                    + Thread.currentThread().getStackTrace()[2].getMethodName() + " method " + user + " " + errorMessage
                    + " for parameter " + param;
        }
        log.error(logString);
    }
}
