package sky.farmerBenificiary.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
//import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import sky.farmerBenificiary.payloads.Password;

@Slf4j
public class Utils {

	private static int intSeqNo = 0;
	
	private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static ConcurrentHashMap<String, String> userSession =new  ConcurrentHashMap<String, String>();

	public static ConcurrentHashMap<String, String>  jwtUserSession =new  ConcurrentHashMap<String, String>();;

	public static Map<String, Object> beanToMap(Object obj) throws IllegalAccessException {
		Map<String, Object> map = new HashMap<>();
		Class<?> clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			field.setAccessible(true);
			Object value = field.get(obj);
			if (value instanceof MultipartFile) {
				continue;
			}
			map.put("$" + field.getName() + "$", value);
		}

		return map;
	}

	public static double compareTwoStrings(String first, String second) {
		first = first.trim().replaceAll(" ", "");
		second = second.trim().replaceAll(" ", "");

		if (first.equalsIgnoreCase(second)) {
			return 100; // identical or empty
		}
		if (first.length() < 2 || second.length() < 2) {
			return 0; // if either is a 0-letter or 1-letter string
		}

		Map<String, Integer> firstBigrams = new HashMap<>();
		for (int i = 0; i < first.length() - 1; i++) {
			String bigram = first.substring(i, i + 2);
			int count = firstBigrams.containsKey(bigram) ? firstBigrams.get(bigram) + 1 : 1;

			firstBigrams.put(bigram, count);
		}
		;

		int intersectionSize = 0;
		for (int i = 0; i < second.length() - 1; i++) {
			String bigram = second.substring(i, i + 2);
			int count = firstBigrams.containsKey(bigram) ? firstBigrams.get(bigram) : 0;

			if (count > 0) {
				firstBigrams.put(bigram, count - 1);
				intersectionSize++;
			}
		}
		DecimalFormat df = new DecimalFormat("#.##");
		double similarty = (2.0 * intersectionSize) / (first.length() + second.length() - 2);

		return Double.parseDouble(df.format(similarty * 100));
	}

	public static synchronized String encrypt(String plaintext) throws Exception {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1"); // step 2
		} catch (NoSuchAlgorithmException e) {
			throw new Exception(e.getMessage());
		}
		try {
			md.update(plaintext.getBytes("UTF-8")); // step 3
		} catch (UnsupportedEncodingException e) {
			throw new Exception(e.getMessage());
		}

		byte raw[] = md.digest(); // step 4
		//byte[] enocoded = Base64.getEncoder().encode(raw); // step 5
		byte[] enocoded = Base64.encodeBase64(raw); // step 5
		String result = new String(enocoded, StandardCharsets.UTF_8);
		return result.replace("=", "".trim()); // step 6
	}

	public static String getDateTimeForImage() {
		String pattern = "ddMMyyHHmmss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		// System.out.println(date);
		return date;
	}

	public static String getExtension(String FileName) {

		String fileExtension = null;
		int lastIndex = FileName.lastIndexOf('.');
		if (lastIndex > 0) {
			fileExtension = FileName.substring(lastIndex + 1);
		}

		return fileExtension;
	}

	public static String getHexCode() {
		Random random = new Random();

		// Initialize a StringBuilder to build the hex code
		StringBuilder hexCode = new StringBuilder();

		// Generate four random parts of the hex code
		for (int i = 0; i < 4; i++) {
			// Generate a random 16-bit hexadecimal value (0x0000 to 0xFFFF)
			int part = random.nextInt(0xFFFF + 1);

			// Format the part as a 4-digit hexadecimal string
			String hexPart = String.format("%04X", part);

			// Append the part to the hexCode
			hexCode.append(hexPart);

			// Add a hyphen except for the last part
			if (i < 3) {
				hexCode.append("-");
			}
		}
		return hexCode.toString();

	}

	public static String getOTP(int size) {
		StringBuilder generatedToken = new StringBuilder();
		try {
			SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
			// Generate 20 integers 0..20
			for (int i = 0; i < size; i++) {
				generatedToken.append(number.nextInt(9));
			}
		} catch (NoSuchAlgorithmException e) {
			//log.error("Exception", e);
		}
		return generatedToken.toString();
	}

	public static Timestamp getOTPExpiryTimestamp(int minutes) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis() + minutes * 60 * 1000);
		return timestamp;
	}

	public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate EndDate) {
		return !date.isBefore(startDate) && !date.isAfter(EndDate);
	}

	public static boolean isNeitherNullNorEmpty(Object obj) {
		boolean isNeitherNullNorEmpty = true;
		if (obj == null || "".equals(obj.toString().trim())) {
			isNeitherNullNorEmpty = false;
		}
		return isNeitherNullNorEmpty;
	}

	public static boolean isNeitherNullNorEmptyNorZero(Object obj) {
		boolean isNeitherNullNorEmptyNorZero = true;

		if (obj == null || "".equals(obj.toString().trim())) {
			isNeitherNullNorEmptyNorZero = false;
		} else if (obj instanceof BigDecimal) {
			if (BigDecimal.ZERO.compareTo((BigDecimal) obj) == 0) {
				isNeitherNullNorEmptyNorZero = false;
			}
		} else if (obj instanceof Integer) {
			if (Integer.valueOf(0).equals(obj)) {
				isNeitherNullNorEmptyNorZero = false;
			}
		}
		return isNeitherNullNorEmptyNorZero;
	}

	public static boolean isNullOrEmpty(String str) {

		return str == null || str.isEmpty();
	}

	public static BigDecimal quintalToMT(BigDecimal quintal) {
		if (quintal == null) {
			return BigDecimal.ZERO;
		}
		return quintal.divide(BigDecimal.TEN, 3, RoundingMode.HALF_UP);
	}

	public static String getPaddedString(String stringTobePadded, int expectedLength) {
		String paddedString = null;
		if (!Utils.isNeitherNullNorEmpty(stringTobePadded)) {
			stringTobePadded = "0";
		}
		paddedString = String.format("%0" + expectedLength + "d", Integer.parseInt(stringTobePadded));
		return paddedString;
	}

	public static String generateRandomString() {

		double random = (double) Math.random() * 1000000;
		String str = new Double(random).toString();

		String password = "";
		for (int i = 0; i < 8; i++) {
			// log.info(str.charAt(i));
			int j = new Integer(str.charAt(i));
			// log.info(j);

			char k = (char) (63 + j);
			if (j % 2 == 0) {
				k = Character.toUpperCase(k);
			}
			password = password + k;

		}

		return password;
	}

	public static long generateRandomNumber(int digits) {
		Random random = new Random();
		if (digits == 1) {
			return random.nextInt(9) + 1;
		}

		long min = (long) Math.pow(10, digits - 1);
		long max = (long) Math.pow(10, digits) - 1;

		return min + (long) (random.nextDouble() * (max - min + 1));
	}

    public static Password getEncryptedPassword(Password model) {
        try {
            String plainPassword=createPassword();
            String sha1Password = encryptPassword(plainPassword);
            model.setSha1Password(sha1Password);
            model.setPlainPassword(plainPassword);
            model.setSha512Password(toHexString(getSHA(model.getUserId().toUpperCase().concat("#").concat(sha1Password))));
        } catch (Exception e) {
            log.error("Exception", e);
        }
        return model;

    }
    private static String createPassword() {
        int count = 7;
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();

    }

    public static String encryptPassword(String plaintext) throws Exception {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA1"); // step 2
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(e.getMessage());
        }
        md.update(plaintext.getBytes(StandardCharsets.UTF_8)); // step 3

        byte[] raw = md.digest(); // step 4
//		String hash = (new BAS64Encoder()).encode(raw); // step 5
        String hash =  new String( Base64.encodeBase64(raw));
        return hash.replace("=", "".trim()); // step 6
    }

    public static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);
        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));
        // Pad with leading zeros
        while (hexString.length() < 128) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        // digest() method called to calculate message digest of an input and return
        // array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }


}
