package com.jozibear247_cab.parse;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.jozibear247_cab.R;
import com.jozibear247_cab.db.DBHelper;
import com.jozibear247_cab.maputils.PolyLineUtils;
import com.jozibear247_cab.models.ApplicationPages;
import com.jozibear247_cab.models.Bill;
import com.jozibear247_cab.models.Card;
import com.jozibear247_cab.models.Driver;
import com.jozibear247_cab.models.DriverLocation;
import com.jozibear247_cab.models.History;
import com.jozibear247_cab.models.MyThings;
import com.jozibear247_cab.models.Referral;
import com.jozibear247_cab.models.Route;
import com.jozibear247_cab.models.Step;
import com.jozibear247_cab.models.User;
import com.jozibear247_cab.models.VehicalType;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.AppLog;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;
import com.jozibear247_cab.utils.ReadFiles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Hardik A Bhalodi
 */
public class ParseContent {
	private static ParseContent instance = null;
	private Activity activity = null;
//	private PreferenceHelper preferenceHelper;
	private final String KEY_SUCCESS = "success";
	private final String KEY_ERROR = "error";
	private final String NAME = "name";
	private final String AGE = "age";
	private final String TYPE = "type";
	private final String NOTES = "notes";
	private final String IMAGE_URL = "image_url";
	private final String THINGS_ID = "thing_id";
	private final String KEY_ERROR_CODE = "error_code";
	private final String KEY_WALKER = "walker";
	private final String BILL = "bill";
	private final String KEY_BILL = "bill";

	private final String IS_WALKER_STARTED = "is_walker_started";
	private final String IS_WALKER_ARRIVED = "is_walker_arrived";
	private final String IS_WALK_STARTED = "is_walk_started";
	private final String IS_WALKER_RATED = "is_walker_rated";
	private final String IS_COMPLETED = "is_completed";
	private final String STATUS = "status";
	private final String CONFIRMED_WALKER = "confirmed_walker";

	private final String TIME = "time";
	private final String BASE_PRICE = "base_price";

	private final String PAYMENT_MODE = "payment_mode";

	private final String DISTANCE_COST = "distance_cost";
	private final String DISTANCE = "distance";
	private final String CURRENCY = "currency";
	private final String UNIT = "unit";
	private final String TIME_COST = "time_cost";
	private final String TOTAL = "total";
	private final String IS_PAID = "is_paid";
	private final String START_TIME = "start_time";

	public static final String DATE = "date";

	private final String TYPES = "types";

	private final String ID = "id";

	private final String ICON = "icon";
	private final String IS_DEFAULT = "is_default";
	private final String PRICE_PER_UNIT_TIME = "price_per_unit_time";
	private final String PRICE_PER_UNIT_DISTANCE = "price_per_unit_distance";

	private final String STRIPE_TOKEN = "stripe_token";
	private final String LAST_FOUR = "last_four";
	private final String CREATED_AT = "created_at";
	private final String UPDATED_AT = "updated_at";
	private final String OWNER_ID = "owner_id";
	private final String CARD_ID = "card_id";

	private final String PAYMENTS = "payments";

	private final String REQUESTS = "requests";
	private final String WALKER = "walker";
	private final String CUSTOMER_ID = "customer_id";

	private final String REFERRAL_CODE = "referral_code";
	private final String TOTAL_REFERRALS = "total_referrals";
	private final String AMOUNT_EARNED = "total_referrals";
	private final String AMOUNT_SPENT = "total_referrals";
	private final String BALANCE_AMOUNT = "balance_amount";

	public ParseContent(Activity activity) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
//		preferenceHelper = new PreferenceHelper(activity);
	}

//	public static synchronized ParseContent getInstance(Activity activity) {
//		if(instance == null) {
//			instance = new ParseContent();
//		}
//		if(instance.activity == null || !instance.activity.equals(activity)) {
//			instance.activity = activity;
//		}
//
//		return instance;
//	}

	public Route parseRoute(String response, Route routeBean) {

		try {
			Step stepBean;
			JSONObject jObject = new JSONObject(response);
			JSONArray jArray = jObject.getJSONArray("routes");
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject innerjObject = jArray.getJSONObject(i);
				if (innerjObject != null) {
					JSONArray innerJarry = innerjObject.getJSONArray("legs");
					for (int j = 0; j < innerJarry.length(); j++) {

						JSONObject jObjectLegs = innerJarry.getJSONObject(j);
						routeBean.setDistanceText(jObjectLegs.getJSONObject(
								"distance").getString("text"));
						routeBean.setDistanceValue(jObjectLegs.getJSONObject(
								"distance").getInt("value"));

						routeBean.setDurationText(jObjectLegs.getJSONObject(
								"duration").getString("text"));
						routeBean.setDurationValue(jObjectLegs.getJSONObject(
								"duration").getInt("value"));

						routeBean.setStartAddress(jObjectLegs
								.getString("start_address"));
						routeBean.setEndAddress(jObjectLegs
								.getString("end_address"));

						routeBean.setStartLat(jObjectLegs.getJSONObject(
								"start_location").getDouble("lat"));
						routeBean.setStartLon(jObjectLegs.getJSONObject(
								"start_location").getDouble("lng"));

						routeBean.setEndLat(jObjectLegs.getJSONObject(
								"end_location").getDouble("lat"));
						routeBean.setEndLon(jObjectLegs.getJSONObject(
								"end_location").getDouble("lng"));

						JSONArray jstepArray = jObjectLegs
								.getJSONArray("steps");
						if (jstepArray != null) {
							for (int k = 0; k < jstepArray.length(); k++) {
								stepBean = new Step();
								JSONObject jStepObject = jstepArray
										.getJSONObject(k);
								if (jStepObject != null) {

									stepBean.setHtml_instructions(jStepObject
											.getString("html_instructions"));
									stepBean.setStrPoint(jStepObject
											.getJSONObject("polyline")
											.getString("points"));
									stepBean.setStartLat(jStepObject
											.getJSONObject("start_location")
											.getDouble("lat"));
									stepBean.setStartLon(jStepObject
											.getJSONObject("start_location")
											.getDouble("lng"));
									stepBean.setEndLat(jStepObject
											.getJSONObject("end_location")
											.getDouble("lat"));
									stepBean.setEndLong(jStepObject
											.getJSONObject("end_location")
											.getDouble("lng"));

									stepBean.setListPoints(new PolyLineUtils()
											.decodePoly(stepBean.getStrPoint()));
									routeBean.getListStep().add(stepBean);
								}

							}
						}
					}

				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return routeBean;
	}

	public boolean isSuccessWithStoreId(String response) {
		AppLog.Log(Const.TAG, response);
		if (TextUtils.isEmpty(response))
			return false;
		try {
			JSONObject jsonObject = new JSONObject(response);
			boolean result = true;
			try {
				result = jsonObject.getInt(KEY_SUCCESS) == 1 ? true : false;
			} catch (Exception e) {
				result = jsonObject.getBoolean(KEY_SUCCESS);
			}
			if (result) {
				PreferenceHelper.getInstance(activity).putUserId(jsonObject
						.getString(Const.Params.ID));
				PreferenceHelper.getInstance(activity).putSessionToken(jsonObject
						.getString(Const.Params.TOKEN));
				PreferenceHelper.getInstance(activity).putEmail(jsonObject
						.optString(Const.Params.EMAIL));
				PreferenceHelper.getInstance(activity).putLoginBy(jsonObject
						.getString(Const.Params.LOGIN_BY));
				if (!PreferenceHelper.getInstance(activity).getLoginBy().equalsIgnoreCase(
						Const.MANUAL)) {
					PreferenceHelper.getInstance(activity).putSocialId(jsonObject
							.getString(Const.Params.SOCIAL_UNIQUE_ID));
				}
				if(jsonObject.has(Const.Params.EMAIL_ACTIVATION)) {
					PreferenceHelper.getInstance(activity).putEmailActivation(jsonObject
							.getInt(Const.Params.EMAIL_ACTIVATION));
				}

				return true;
			} else {
				AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
				// AndyUtils.showErrorToast(jsonObject.getInt(KEY_ERROR_CODE),
				// activity);
				return false;
				// AndyUtils.showToast(jsonObject.getString(KEY_ERROR),
				// activity);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public User parseUserAndStoreToDb(String response) {
		User user = null;
		try {
			JSONObject jsonObject = new JSONObject(response);
			boolean result = true;
			try {
				result = jsonObject.getInt(KEY_SUCCESS) == 1 ? true : false;
			} catch (Exception e) {
				result = jsonObject.getBoolean(KEY_SUCCESS);
			}
			if (result) {
				user = new User();
				DBHelper dbHelper = new DBHelper(activity);
				user.setUserId(jsonObject.getInt(Const.Params.ID));
				user.setEmail(jsonObject.optString(Const.Params.EMAIL));
				user.setFname(jsonObject.getString(Const.Params.FIRSTNAME));
				user.setLname(jsonObject.getString(Const.Params.LAST_NAME));

				user.setAddress(jsonObject.getString(Const.Params.ADDRESS));
				user.setBio(jsonObject.getString(Const.Params.BIO));
				user.setZipcode(jsonObject.getString(Const.Params.ZIPCODE));
				user.setPicture(jsonObject.getString(Const.Params.PICTURE));
				user.setContact(jsonObject.getString(Const.Params.PHONE));
				user.setTimezone(jsonObject.getString(Const.Params.TIMEZONE));
				dbHelper.createUser(user);

			} else {
				// AndyUtils.showToast(jsonObject.getString(KEY_ERROR),
				// activity);

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}

	public boolean isSuccess(String response) {
		if (TextUtils.isEmpty(response))
			return false;
		try {

			AppLog.Log(Const.TAG, response);

			Log.d("[JSON isSuccess]", response);

			JSONObject jsonObject = null;
			boolean result = true;
			try {
				jsonObject = new JSONObject(response);
				result = jsonObject.getInt(KEY_SUCCESS) == 1 ? true : false;
			} catch (Exception e) {
				try {
					result = jsonObject.getBoolean(KEY_SUCCESS);
				} catch (Exception ee) {
					result = false;
				}
			}
			if (result) {
				return true;
			} else {
				try {
					if(!jsonObject.has(KEY_ERROR_CODE)) return false;
				} catch (Exception ee) {
					return false;
				}
				int error_code = jsonObject.getInt(KEY_ERROR_CODE);
				if (error_code == 415)
					AndyUtils.showToast("No Driver found in the vicinity",
							activity);
				else if (error_code == 416) {
					AndyUtils.removeCustomProgressDialog();
					AndyUtils
							.showToast(
									"No Driver for the selected service found in the vicinity",
									activity);
				} else if (error_code == 417) {

				} else
					AndyUtils.showToast(jsonObject.getString(KEY_ERROR),
							activity);
				// AndyUtils.showErrorToast(jsonObject.getInt(KEY_ERROR_CODE),
				// activity);
				return false;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public DriverLocation getDriverLocation(String response) {
		DriverLocation driverLocation = null;
		LatLng latLng = null;
		if (TextUtils.isEmpty(response))
			return null;
		AppLog.Log(Const.TAG, response);
		try {
			JSONObject jsonObject = new JSONObject(response);
			driverLocation = new DriverLocation();
			latLng = new LatLng(jsonObject.getDouble(Const.Params.LATITUDE),
					jsonObject.getDouble(Const.Params.LONGITUDE));
			driverLocation.setLatLng(latLng);
			driverLocation.setDistance(new DecimalFormat("0.00").format(Double
					.parseDouble(jsonObject.getString(DISTANCE))));
			driverLocation.setUnit(jsonObject.getString(UNIT));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return driverLocation;
	}

	public int getErrorCode(String response) {
		if (TextUtils.isEmpty(response))
			return 0;
		try {
			AppLog.Log(Const.TAG, response);
			JSONObject jsonObject = new JSONObject(response);
			if(jsonObject.has(KEY_ERROR_CODE)) return jsonObject.getInt(KEY_ERROR_CODE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public int checkRequestStatus(String response) {
		int status = Const.NO_REQUEST;
		try {

			AppLog.Log(Const.TAG, response);
			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.getInt(CONFIRMED_WALKER) == 0
					&& jsonObject.getInt(STATUS) == 0) {

				return Const.IS_REQEUST_CREATED;

			} else if (jsonObject.getInt(CONFIRMED_WALKER) == 0
					&& jsonObject.getInt(STATUS) == 1) {
				return Const.NO_REQUEST;
			} else if (jsonObject.getInt(CONFIRMED_WALKER) != 0
					&& jsonObject.getInt(STATUS) == 1) {

				if (jsonObject.getInt(IS_WALKER_STARTED) == 0) {
					status = Const.IS_WALKER_STARTED;
				} else if (jsonObject.getInt(IS_WALKER_ARRIVED) == 0) {
					status = Const.IS_WALKER_ARRIVED;
				} else if (jsonObject.getInt(IS_WALK_STARTED) == 0) {
					status = Const.IS_WALK_STARTED;
				} else if (jsonObject.getInt(IS_COMPLETED) == 0) {
					status = Const.IS_COMPLETED;
				} else if (jsonObject.getInt(IS_WALKER_RATED) == 0) {
					status = Const.IS_WALKER_RATED;
				}
			}

			String time = jsonObject.optString(START_TIME);
			// "start_time": "2014-11-20 03:27:37"
			if (!TextUtils.isEmpty(time)) {
				try {

					TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
					Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
							Locale.ENGLISH).parse(time);
					AppLog.Log("TAG", "START DATE---->" + date.toString()
							+ " month:" + date.getMonth());
					PreferenceHelper.getInstance(activity).putRequestTime(date.getTime());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}

	public Bill parseBllingInfo(String response) {
		Log.d("mahi", "bill response" + response);
		Bill bill = null;
		try {
			JSONObject jsonObject = new JSONObject(response).getJSONObject(KEY_BILL);
			bill = new Bill();
			bill.setBasePrice(jsonObject.getString(BASE_PRICE));
			double distance = Double.parseDouble(jsonObject.getString(DISTANCE));
			// bill.setDistance(jsonObject.getString(DISTANCE));
			bill.setUnit(jsonObject.getString(UNIT));
			if (bill.getUnit().equalsIgnoreCase("kms")) {
				distance = distance * 0.62137;

			}

			bill.setPayment_mode(jsonObject.getString(PAYMENT_MODE));
			if (jsonObject.getString(PAYMENT_MODE).equals("2")) {
				JSONObject ja_primary = jsonObject.getJSONObject("walker");
				bill.setPrimary_id(ja_primary.getString("email"));
				bill.setPrimary_amount(ja_primary.getString("amount"));

				JSONObject ja_secoundry = jsonObject.getJSONObject("admin");
				bill.setSecoundry_id(ja_secoundry.getString("email"));
				bill.setSecoundry_amount(ja_secoundry.getString("amount"));
			}

			bill.setDistance(new DecimalFormat("0.00").format(distance));
			bill.setDistanceCost(jsonObject.getString(DISTANCE_COST));
			bill.setTime(jsonObject.getString(TIME));
			bill.setTimeCost(jsonObject.getString(TIME_COST));
			bill.setIsPaid(jsonObject.getString(IS_PAID));
			bill.setTotal(jsonObject.getString(TOTAL));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			bill = null;
			e.printStackTrace();
		}
		return bill;
	}

	public Referral parseReffrelCode(String response) {
		Referral referral;
		try {
			referral = new Referral();
			JSONObject jsonObject = new JSONObject(response);
			referral.setReferralCode(jsonObject.getString(REFERRAL_CODE));
			referral.setAmountSpent(jsonObject.getString(AMOUNT_SPENT));
			referral.setBalanceAmount(jsonObject.getString(BALANCE_AMOUNT));
			referral.setTotalReferrals(jsonObject.getString(TOTAL_REFERRALS));
			referral.setAmountEarned(jsonObject.getString(AMOUNT_EARNED));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			referral = null;
			e.printStackTrace();
		}
		return referral;
	}

	public Driver getDriverDetail(String response) {
		Driver driver = null;
		try {
			driver = new Driver();
			AppLog.Log(Const.TAG, response);
			JSONObject jsonObject = new JSONObject(response).getJSONObject(KEY_WALKER);
			driver.setBio(jsonObject.getString(Const.Params.BIO));
			driver.setFirstName(jsonObject.getString(Const.Params.FIRSTNAME));
			driver.setLastName(jsonObject.getString(Const.Params.LAST_NAME));
			driver.setPhone(jsonObject.getString(Const.Params.PHONE));
			driver.setPicture(jsonObject.getString(Const.Params.PICTURE));
			driver.setLatitude(jsonObject.getDouble(Const.Params.LATITUDE));
			driver.setLongitude(jsonObject.getDouble(Const.Params.LONGITUDE));
			if(jsonObject.has(Const.Params.DEST_LATITUDE)) {
				driver.setD_latitude(jsonObject.getDouble(Const.Params.DEST_LATITUDE));
			}
			if(jsonObject.has(Const.Params.DEST_LONGITUDE)) {
				driver.setD_longitude(jsonObject.getDouble(Const.Params.DEST_LONGITUDE));
			}
			Log.d("getDriverDetail", "[DST latD:" + driver.getD_latitude() + ", longD:"
					+ driver.getD_longitude() + "] [SRC lat:" + driver.getLatitude() + ", long:"
					+ driver.getLongitude() + "]");

			driver.setRating(jsonObject.getDouble(Const.Params.RATING));
			JSONObject jsonObjectBill = new JSONObject(response)
					.optJSONObject(BILL);
			if (jsonObjectBill != null && jsonObjectBill.has(CURRENCY)) {
				Bill bill = new Bill();
				bill.setCurrency(jsonObjectBill.getString(CURRENCY));
				bill.setDistance(new DecimalFormat("0.00").format(Double
						.parseDouble(jsonObjectBill.getString(DISTANCE))));
				bill.setTime(jsonObjectBill.getString(TIME));
				bill.setBasePrice(jsonObjectBill.getString(BASE_PRICE));
				bill.setPayment_mode(jsonObjectBill.getString(PAYMENT_MODE));

				if (jsonObjectBill.getString(PAYMENT_MODE).equals("2")) {
					JSONObject ja_primary = jsonObjectBill.getJSONObject("walker");
					bill.setPrimary_id(ja_primary.getString("email"));
					bill.setPrimary_amount(ja_primary.getString("amount"));

					JSONObject ja_secoundry = jsonObjectBill.getJSONObject("admin");
					bill.setSecoundry_id(ja_secoundry.getString("email"));
					bill.setSecoundry_amount(ja_secoundry.getString("amount"));
				}
				bill.setActual_total(jsonObjectBill.getDouble("actual_total"));
				bill.setDiscounted_amount(jsonObjectBill.getDouble("promo_discount"));
				bill.setTimeCost(jsonObjectBill.getString(TIME_COST));
				bill.setDistanceCost(jsonObjectBill.getString(DISTANCE_COST));
				bill.setTotal(jsonObjectBill.getString(TOTAL));
				bill.setIsPaid(jsonObjectBill.getString(IS_PAID));
				bill.setUnit(jsonObjectBill.getString(UNIT));
				double distance = Double.parseDouble(jsonObjectBill.getString(DISTANCE));
				if (bill.getUnit().equalsIgnoreCase("kms")) {
					distance = distance * 0.62137;
					bill.setDistance(new DecimalFormat("0.00").format(distance));
				}

				driver.setBill(bill);
			}
			// driver.getBill().setUnit(object.getString(UNIT));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return driver;
	}

	public ArrayList<LatLng> parsePathRequest(String response,
			ArrayList<LatLng> points) {
		// TODO Auto-generated method stub
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			boolean result = true;
			try {
				result = jsonObject.getInt(KEY_SUCCESS) == 1 ? true : false;
			} catch (Exception e) {
				result = jsonObject.getBoolean(KEY_SUCCESS);
			}
			if (result) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(Const.Params.LOCATION_DATA);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject json = jsonArray.getJSONObject(i);
					points.add(new LatLng(Double.parseDouble(json.getString(Const.Params.LATITUDE)),
							Double.parseDouble(json.getString(Const.Params.LONGITUDE))));

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return points;
	}

	public int getRequestInProgress(String response) {
		if (TextUtils.isEmpty(response))
			return Const.NO_REQUEST;
		try {
			AppLog.Log(Const.TAG, response);
			JSONObject jsonObject = new JSONObject(response);
			boolean result = true;
			try {
				result = jsonObject.getInt(KEY_SUCCESS) == 1 ? true : false;
			} catch (Exception e) {
				result = jsonObject.getBoolean(KEY_SUCCESS);
			}
			if (result) {
				int requestId = jsonObject.getInt(Const.Params.REQUEST_ID);
				PreferenceHelper.getInstance(activity).putRequestId(requestId);
				return requestId;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Const.NO_REQUEST;
	}

	public int getRequestId(String response) {
		if (TextUtils.isEmpty(response))
			return Const.NO_REQUEST;
		try {
			AppLog.Log(Const.TAG, response);
			int index = response.indexOf("}{");
			if(index > 0) {
				response = response.substring(index + 1);
			}
			JSONObject jsonObject = new JSONObject(response);
			boolean result = true;
			try {
				result = jsonObject.getInt(KEY_SUCCESS) == 1 ? true : false;
			} catch (Exception e) {
				result = jsonObject.getBoolean(KEY_SUCCESS);
			}
			if (result && jsonObject.has(Const.Params.REQUEST_ID)) {
				int requestId = jsonObject.getInt(Const.Params.REQUEST_ID);
				PreferenceHelper.getInstance(activity).putRequestId(requestId);
				return requestId;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Const.NO_REQUEST;
	}

	public ArrayList<String> parseCountryCodes() {
		String response = "";
		ArrayList<String> list = new ArrayList<String>();
		try {
			response = ReadFiles.readRawFileAsString(activity,
					R.raw.countrycodes);

			JSONArray array = new JSONArray(response);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				list.add(object.getString("phone-code") + " "
						+ object.getString("name"));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public MyThings parseThings(String response) {
		MyThings things = null;
		try {
			JSONObject jObject = new JSONObject(response);
			things = new MyThings();
			things.setAge(jObject.getString(AGE));
			// things.setName(jObject.getString(NAME));
			things.setType(jObject.getString(TYPE));
			things.setNotes(jObject.getString(NOTES));
			things.setImgUrl(jObject.getString(IMAGE_URL));
			things.setThingId(jObject.getString(THINGS_ID));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			things = null;
		}
		return things;
	}

	public ArrayList<VehicalType> parseTypes(String response,
			ArrayList<VehicalType> list) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			boolean result = true;
			try {
				result = jsonObject.getInt(KEY_SUCCESS) == 1 ? true : false;
			} catch (Exception e) {
				result = jsonObject.getBoolean(KEY_SUCCESS);
			}
			if (result) {
				JSONArray jsonArray = jsonObject.getJSONArray(TYPES);
				for (int i = 0; i < jsonArray.length(); i++) {
					VehicalType type = new VehicalType();
					JSONObject typeJson = jsonArray.getJSONObject(i);
					type.setBasePrice(typeJson.getString(BASE_PRICE));
					type.setIcon(typeJson.getString(ICON));
					type.setId(typeJson.getInt(ID));
					type.setName(typeJson.getString(NAME));
					type.setPricePerUnitDistance(typeJson
							.getString(PRICE_PER_UNIT_DISTANCE));
					type.setPricePerUnitTime(typeJson
							.getString(PRICE_PER_UNIT_TIME));
					type.setCurrency(typeJson.getString(CURRENCY));
					type.setUnit(typeJson.getString(UNIT));

					list.add(type);

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

	public ArrayList<ApplicationPages> parsePages(
			ArrayList<ApplicationPages> list, String response) {
		list.clear();
		ApplicationPages applicationPages = new ApplicationPages();
		applicationPages.setId(-1);
		applicationPages.setTitle("Profile");
		applicationPages.setData("");
		list.add(applicationPages);

		applicationPages = new ApplicationPages();
		applicationPages.setId(-2);
		applicationPages.setTitle("Payment");
		applicationPages.setData("");
		list.add(applicationPages);

		applicationPages = new ApplicationPages();
		applicationPages.setId(-3);
		applicationPages.setTitle("History");
		applicationPages.setData("");
		list.add(applicationPages);

		applicationPages = new ApplicationPages();
		applicationPages.setId(-4);
		applicationPages.setTitle("Free Rides");
		applicationPages.setData("");
		list.add(applicationPages);

		applicationPages = new ApplicationPages();
		applicationPages.setId(-5);
		applicationPages.setTitle("Promotions");
		applicationPages.setData("");
		list.add(applicationPages);

		if (TextUtils.isEmpty(response)) {
			return list;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			boolean result = true;
			try {
				result = jsonObject.getInt(KEY_SUCCESS) == 1 ? true : false;
			} catch (Exception e) {
				result = jsonObject.getBoolean(KEY_SUCCESS);
			}
			if (result) {
				JSONArray jsonArray = jsonObject
						.getJSONArray(Const.Params.INFORMATIONS);
				if (jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						applicationPages = new ApplicationPages();
						JSONObject object = jsonArray.getJSONObject(i);
						applicationPages.setId(object.getInt(Const.Params.ID));
						applicationPages.setTitle(object
								.getString(Const.Params.TITLE));
						applicationPages.setData(object
								.getString(Const.Params.CONTENT));
						applicationPages.setIcon(object
								.getString(Const.Params.ICON));
						list.add(applicationPages);
					}
				}

			}
			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<Card> parseCards(String response, ArrayList<Card> listCards) {

		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			boolean result = true;
			try {
				result = jsonObject.getInt(KEY_SUCCESS) == 1 ? true : false;
			} catch (Exception e) {
				result = jsonObject.getBoolean(KEY_SUCCESS);
			}
			if (result) {
				JSONArray jsonArray = jsonObject.getJSONArray(PAYMENTS);
				for (int i = 0; i < jsonArray.length(); i++) {
					Card card = new Card();
					JSONObject cardJson = jsonArray.getJSONObject(i);
					// card.setStripeToken(cardJson.getString(STRIPE_TOKEN));
					card.setLastFour(cardJson.getString(LAST_FOUR));
					// card.setLastFour(cardJson.getString(CUSTOMER_ID));
					card.setId(cardJson.getInt(ID));
					card.setCreatedAt(cardJson.getString(CREATED_AT));
					card.setUpdatedAt(cardJson.getString(UPDATED_AT));
					card.setOwnerId(cardJson.getString(OWNER_ID));
					card.setCustomer_id(cardJson.getString(CUSTOMER_ID));
					card.setCard_id(cardJson.getString(CARD_ID));
					listCards.add(card);

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listCards;
	}

	public ArrayList<History> parseHistory(String response,
			ArrayList<History> list) {
		list.clear();

		if (TextUtils.isEmpty(response)) {
			return list;
		}
		try {
			JSONObject jsonObject = new JSONObject(response);
			boolean result = true;
			try {
				result = jsonObject.getInt(KEY_SUCCESS) == 1 ? true : false;
			} catch (Exception e) {
				result = jsonObject.getBoolean(KEY_SUCCESS);
			}
			if (result) {
				JSONArray jsonArray = jsonObject.getJSONArray(REQUESTS);
				if (jsonArray.length() > 0) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						History history = new History();
						history.setId(object.getInt(ID));
						history.setDate(object.getString(DATE));

						double distance = Double.parseDouble(object
								.getString(DISTANCE));
						// bill.setDistance(jsonObject.getString(DISTANCE));
						history.setUnit(object.getString(UNIT));
						if (history.getUnit().equalsIgnoreCase("kms")) {
							distance = distance * 0.62137;

						}
						history.setDistance(new DecimalFormat("0.00")
								.format(distance));

						history.setUnit(object.getString(UNIT));
						history.setTime(object.getString(TIME));
						history.setDistanceCost(object.getString(DISTANCE_COST));
						history.setTimecost(object.getString(TIME_COST));
						history.setBasePrice(object.getString(BASE_PRICE));
						history.setCurrency(object.getString(CURRENCY));
						history.setTotal(new DecimalFormat("0.00")
								.format(Double.parseDouble(object
										.getString(TOTAL))));
						history.setActual_total(object
								.getDouble("actual_total"));
						history.setType(object.getString(TYPE));
						JSONObject userObject = object.getJSONObject(WALKER);
						history.setFirstName(userObject
								.getString(Const.Params.FIRSTNAME));
						history.setLastName(userObject
								.getString(Const.Params.LAST_NAME));
						history.setPhone(userObject
								.getString(Const.Params.PHONE));
						history.setPicture(userObject
								.getString(Const.Params.PICTURE));
						history.setEmail(userObject
								.getString(Const.Params.EMAIL));
						history.setBio(userObject.getString(Const.Params.BIO));
						list.add(history);
					}
				}

			}
			// else {
			// AndyUtils.showToast(jsonObject.getString(KEY_ERROR), activity);
			// }
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
}
