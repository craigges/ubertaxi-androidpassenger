package com.jozibear247_cab.utils;

/**
 * @author Hardik A Bhalodi
 */
public class Const {
	// map
	public static final String TAG = "UBER FOR X";

	public static final String PLACES_AUTOCOMPLETE_API_KEY = "AIzaSyC72wvYWYT3lI8B9Z0aPU8rRpwucJqE0uQ";

	public static final int MAP_ZOOM = 17;

	// card io
	// public static final String PUBLISHABLE_KEY = "pk_test_cVsZQmsqJYwAxWhEqDyifcaL";
	public static final String PUBLISHABLE_KEY = "pk_test_AXOWbW77nqnft02AfhkhOBtK";
	public static final String MY_CARDIO_APP_TOKEN = "c15fa417f757415c9d750d1ef5ee5fd8";

	// PayPal app configuration
	public static final String PAYPAL_CLIENT_ID = "APP-80W284485P519543T";
	public static final String PAYPAL_CLIENT_SECRET = "EOqGRn6WUXmZv6GpEPrargvLW0TfSViFnO35ZU5iy0HPIuKlrWD7rnoMyehXFV8heT-fywP9YyatiCEv";
	public static final String CURRENCY_TYPE = "USD";

	// web services
	public class ServiceType {
		// private static final String HOST_URL =
		// "http://uberforxapi.provenlogic.com/";
		// private static final String HOST_URL =
		// "http://192.168.0.19/uber_auto/public/";

		//private static final String HOST_URL = "http://52.25.104.97/";
//		public static final String HOST_URL = "http://52.49.122.63/";
		public static final String HOST_URL = "http://www.jozibear247.com/";

		private static final String BASE_URL = HOST_URL + "user/";
		public static final String LOGIN = BASE_URL + "login";
		public static final String REGISTER = BASE_URL + "register";
		public static final String ADD_CARD = BASE_URL + "addcardtoken";
		public static final String REMOVE_CARD = BASE_URL + "deletecardtoken";
		public static final String PAYGATE_URL = BASE_URL + "paygate";

		public static final String PAYMENT_OPTIONS = BASE_URL
				+ "payment_options?";
		public static final String CREATE_REQUEST = BASE_URL + "createrequest";
		public static final String PAY_DEBT = BASE_URL + "paydebt";

		public static final String GET_REQUEST_LOCATION = BASE_URL
				+ "getrequestlocation?";
		public static final String GET_REQUEST_STATUS = BASE_URL
				+ "getrequest?";
		public static final String SEND_ETA = BASE_URL + "send_eta?";

		public static final String REGISTER_MYTHING = BASE_URL + "thing?";
		public static final String REQUEST_IN_PROGRESS = BASE_URL
				+ "requestinprogress?";
		public static final String RATING = BASE_URL + "rating";
		public static final String CANCEL_REQUEST = BASE_URL + "cancelrequest";
		public static final String GET_PAGES = HOST_URL + "application/pages";
		public static final String GET_PAGES_DETAIL = HOST_URL
				+ "application/page/";
		public static final String GET_VEHICAL_TYPES = HOST_URL
				+ "application/types";
		public static final String FORGET_PASSWORD = HOST_URL
				+ "application/forgot-password";
		public static final String UPDATE_PROFILE = BASE_URL + "update";
		public static final String GET_CARDS = BASE_URL + "cards?";
		public static final String HISTORY = BASE_URL + "history?";
		public static final String GET_PATH = BASE_URL + "requestpath?";
		public static final String GET_REFERRAL = BASE_URL + "referral?";
		public static final String APPLY_REFFRAL_CODE = BASE_URL
				+ "apply-referral";
		public static final String GOOGLE_MAP_API = "http://maps.googleapis.com/maps/api/distancematrix/json?";
		public static final String GOOGLE_LOCATION = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
		public static final String GOOGLE_ADDRESS = "http://maps.googleapis.com/maps/api/geocode/json?address=";

		public static final String FARE_CALCULATOR = BASE_URL + "fare";
		public static final String GETPROVIDER_ALL = BASE_URL + "getnearbyproviders";
		public static final String GET_PROMO_REQUEST = BASE_URL + "check_promo_code?";

		public static final String SEND_PAYPAL_RESPONSE = BASE_URL + "paybypaypal";
		public static final String SEND_CASH_RESPONSE = BASE_URL + "paybycash";
		public static final String GET_CREDITS = BASE_URL + "credits?";
		// http://uberforxapi.provenlogic.com/provider/rating";

	}

	// fragments tag
	public static String FRAGMENT_REGISTER = "FRAGMENT_REGISTER";
	public static String FRAGMENT_MAIN = "FRAGMENT_MAIN";
	public static String FRAGMENT_MYTHING_REGISTER = "FRAGMENT_MYTHING_REGISTER";
	public static String FRAGMENT_SIGNIN = "FRAGMENT_SIGNIN";
	public static String FRAGMENT_PAYMENT_REGISTER = "ADD_FRAGMENT_PAYMENT_REGISTER";
	public static String FRAGMENT_PAYMENT_ADD = "FRAGMENT_PAYMENT_ADD";
	public static String FRAGMENT_REFFREAL = "FRAGMENT_REFFREAL";
	public static String FRAGMENT_MYTHING_ADD = "FRAGMENT_MYTHING_ADD";
	public static String FRAGMENT_MAP = "FRAGMENT_MAP";
	public static String FRAGMENT_TRIP = "FRAGMENT_TRIP";
	public static final String FOREGETPASS_FRAGMENT_TAG = "FOEGETPASSFRAGMENT";
	public static String FRAGMENT_FEEDBACK = "FRAGMENT_FEEDBACK";

	// service codes
	public class ServiceCode {
		public static final int REGISTER = 1;
		public static final int LOGIN = 2;
		public static final int GET_ROUTE = 3;
		public static final int REGISTER_MYTHING = 4;
		public static final int GET_MYTHING = 5;
		public static final int ADD_CARD = 6;
		public static final int PICK_ME_UP = 7;
		public static final int CREATE_REQUEST = 8;
		public static final int GET_REQUEST_STATUS = 9;
		public static final int GET_REQUEST_LOCATION = 10;
		public static final int GET_REQUEST_IN_PROGRESS = 11;
		public static final int RATING = 12;
		public static final int CANCEL_REQUEST = 13;
		public static final int GET_PAGES = 14;
		public static final int GET_PAGES_DETAILS = 15;
		public static final int GET_VEHICAL_TYPES = 16;
		public static final int FORGET_PASSWORD = 18;
		public static final int UPDATE_PROFILE = 19;
		public static final int GET_CARDS = 20;
		public static final int HISTORY = 21;
		public static final int GET_PATH = 22;
		public static final int GET_REFERREL = 23;
		public static final int APPLY_REFFRAL_CODE = 24;

		public static final int PAY_DEBT = 25;
		public static final int PAYMENT_OPTIONS = 26;
		public static final int GET_MAP_DETAILS = 27;
		public static final int FARE_CALCULATOR = 28;
		public static final int UPDATE_REFFRAL_CODE = 29;
		public static final int UPDATE_PAYPAL_ID = 30;

		public static final int GETPROVIDER_ALL = 31;
		public static final int GET_MAP_TIME = 32;
		public static final int GET_PROMO_REQUEST = 33;
		public static final int REMOVE_CARD = 34;
		public static final int GET_ADDRESS = 35;
		public static final int SEND_ETA = 36;
		public static final int GET_CREDITS = 37;
		public static final int GET_LOCATION = 38;
		public static final int PAYGATE_URL = 39;

		public static final int UPDATE_PAYMENT = 40;
		public static final int FINISH_PAYMENT = 41;
	}

	// service parameters
	public class Params {
		public static final String TITLE = "title";
		public static final String CONTENT = "content";
		public static final String INFORMATIONS = "informations";
		public static final String EMAIL = "email";
		public static final String REFERRAL_CODE = "referral_code";

		public static final String PASSWORD = "password";
		public static final String FIRSTNAME = "first_name";
		public static final String LAST_NAME = "last_name";
		public static final String PHONE = "phone";
		public static final String DEVICE_TOKEN = "device_token";
		public static final String ICON = "icon";
		public static final String DEVICE_TYPE = "device_type";
		public static final String LOCATION_DATA = "locationdata";
		public static final String BIO = "bio";
		public static final String ADDRESS = "address";
		public static final String STATE = "state";
		public static final String COUNTRY = "country";
		public static final String ZIPCODE = "zipcode";
		public static final String LOGIN_BY = "login_by";
		public static final String ID = "id";
		public static final String REQUEST_ID2 = "";
		public static final String TOKEN = "token";
		public static final String COMMENT = "comment";
		public static final String RATING = "rating";
		public static final String NUM_RATING = "num_rating";
		public static final String SOCIAL_UNIQUE_ID = "social_unique_id";
		public static final String EMAIL_ACTIVATION = "email_activation";
		public static final String PICTURE = "picture";
		public static final String NAME = "name";
		public static final String AGE = "age";
		public static final String TYPE = "type";
		public static final String OWNER = "2";
		public static final String NOTES = "notes";
		public static final String STRIPE_TOKEN = "payment_token";
		public static final String LAST_FOUR = "last_four";
		public static final String LONGITUDE = "longitude";
		public static final String LATITUDE = "latitude";
		public static final String DISTANCE = "distance";
		public static final String DEST_LONGITUDE = "d_longitude";
		public static final String DEST_LATITUDE = "d_latitude";
		public static final String SCHEDULE_ID = "schedule_id";
		public static final String REQUEST_ID = "request_id";
		public static final String TIMEZONE = "timezone";

		public static final String COD = "payment_mode";
		public static final String PAYPAL_DATA = "paypal_id";
		public static final String TIME = "time";
		public static final String MAP_ORIGINS = "origins";
		public static final String MAP_DESTINATIONS = "destinations";

		public static final String PROMO_CODE = "promo_code";
		public static final String CARD_ID = "card_id";
		public static final String ETA = "eta";

	}

	// general
	public static final int CHOOSE_PHOTO = 112;
	public static final int TAKE_PHOTO = 113;
	public static final String URL = "url";
	public static final String DEVICE_TYPE_ANDROID = "android";
	public static final String SOCIAL_FACEBOOK = "facebook";
	public static final String SOCIAL_GOOGLE = "google";
	public static final String MANUAL = "manual";

	// used for request status
	public static final int IS_WALKER_STARTED = 2;
	public static final int IS_WALKER_ARRIVED = 3;
	public static final int IS_WALK_STARTED = 4;
	public static final int IS_COMPLETED = 5;
	public static final int IS_WALKER_RATED = 6;
	public static final int IS_REQEUST_CREATED = 1;

	// used for sending model in to bundle
	public static final String DRIVER = "driver";
	public static final String USER = "user";
	public static final String THINGS = "things";
	// used for schedule request
	public static final long TIME_SCHEDULE = 20 * 1000;
	public static final long DELAY = 0 * 1000;

	// no request id
	public static final int NO_REQUEST = -1;
	public static final int NO_TIME = -1;

	// error code
	public static final int INVALID_TOKEN = 406;
	public static final int REQUEST_ID_NOT_FOUND = 408;
	public static final int REQUEST_CANCEL = 483;

	// notification
	public static final String INTENT_WALKER_STATUS = "walker_status";
	public static final String EXTRA_WALKER_STATUS = "walker_status_extra";

	public static final String INTENT_PAYMENT_RESULT = "pay_result";
	public static final String EXTRA_PAYMENT_RESULT = "pay_result_extra";

}
