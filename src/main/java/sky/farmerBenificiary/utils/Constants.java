package sky.farmerBenificiary.utils;

public class Constants {

	public enum Active_InActive {
		Active("A"), InActive("I");

		public final String value;

		Active_InActive(String value) {
			this.value = value;
		}
	}

	public enum SUCCESS_FAILEDS {
		SUCCESS("Success"), FAILED("Failed");

		public final String value;

		SUCCESS_FAILEDS(String value) {
			this.value = value;
		}
	}

	public enum YES_NO {
		YES("Y"), NO("N");

		public final String value;

		YES_NO(String value) {
			this.value = value;
		}
	}

	public enum STATUS {
		ACCEPTED("A"), PENDING("P"), REVERTED("R"), COMPLETED("C"), MODIFIED("M"), FLOWERING_STAGE_PENDING("FSP");

		public final String value;

		STATUS(String value) {
			this.value = value;
		}
	}

	public enum LOGIN_FLAG {
		FIRST_LOGIN_FLAG_YES("Y"), FIRST_LOGIN_FLAG_NO("N"), RESET_PASSWORD("Y"), RESET_USER_PASSWORD("N"),
		USER_IS_ACTIVE("T"), USER_IS_LOCK("L"), USER_IS_PENDING("P");

		public final String value;

		LOGIN_FLAG(String value) {
			this.value = value;
		}
	}

	public enum FLAG_VALUES {
		FLAG_Y("Y"), FLAG_N("N");

		public final String value;

		FLAG_VALUES(String value) {
			this.value = value;
		}
	}

	public enum CREATOR_MODE {
		WEB("WEB"), ANDROID("ANDROID"), NOT_CAPTURED("NA");

		public final String value;

		CREATOR_MODE(String value) {
			this.value = value;
		}
	}

}
