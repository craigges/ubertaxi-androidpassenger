package com.jozibear247_cab.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jozibear247_cab.R;
import com.jozibear247_cab.component.MyFontButton;
import com.jozibear247_cab.component.MyFontPopUpTextView;
import com.jozibear247_cab.component.MyFontTextView;
import com.jozibear247_cab.models.Driver;
import com.jozibear247_cab.models.DriverLocation;
import com.jozibear247_cab.models.Route;
import com.jozibear247_cab.models.Step;
import com.jozibear247_cab.parse.HttpRequester;
import com.jozibear247_cab.parse.ParseContent;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.AppLog;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.LocationHelper;
import com.jozibear247_cab.utils.LocationHelper.OnLocationReceived;
import com.jozibear247_cab.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Hardik A Bhalodi
 */
public class UberTripFragment extends UberBaseFragment {
	private static final int PICK_CONTACT = 1, PICK_MULTIPLE_CONTACT = 2;
	private GoogleMap map;
	private PolylineOptions lineOptions;
	private Route route;
	ArrayList<LatLng> points;
	private ParseContent parseContent;
	private TextView tvTime, tvDist, tvDriverName, tvDriverPhone, tvRate,
			tvStatus;
	private Driver driver;
	private Marker myMarker, markerDriver, destinationmarker;
	private ImageView ivDriverPhoto;
	private LocationHelper locHelper;
	private boolean isContinueStatusRequest;
	private boolean isContinueDriverRequest;
	private Timer timer, timerDriverLocation;
	private LocationClient client;
	private final int LOCATION_SCHEDULE = 10 * 1000;
//	private final int LOCATION_SCHEDULE = 5 * 1000;
	private String strDistance;
	private Polyline polyLine;
	private AlertDialog alertnew;
	private LatLng myLatLng;
	private Location myLocation;
	private boolean isTripStarted = false;

	private final int DRAW_TIME = 5 * 1000;
	private String lastTime;
	private String lastDistance;
	private WalkerStatusReceiver walkerReceiver;
	private boolean isAllLocationReceived = false;
	WakeLock wakeLock;
	private PopupWindow notificationWindow, driverStatusWindow;
	private MyFontPopUpTextView tvPopupMsg, tvJobAccepted, tvDriverStarted,
			tvDriverArrvied, tvTripStarted, tvTripCompleted;
	private ImageView ivJobAccepted, ivDriverStarted, ivDriverArrvied,
			ivTripStarted, ivTripCompleted;
	private boolean isNotificationArrievd = false;
	private RatingBar ratingBarTrip;

	private Uri notification;
	private MediaPlayer r;
	private TextView tripcancel;
	int selector = 2;
	private ArrayList<String> selectedContacts, phonesarray;
	String duration = "";
	MyFontButton shareeta;
	private EtaCountDownTimer etaCountDownTimer = null;
	private View tripFragmentView = null;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.fragments.UberBaseFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		phonesarray = new ArrayList<String>();
		PowerManager powerManager = (PowerManager) activity
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				Const.TAG);
		wakeLock.acquire();
		driver = (Driver) getArguments().getParcelable(Const.DRIVER);
		points = new ArrayList<LatLng>();
		route = new Route();
		IntentFilter filter = new IntentFilter(Const.INTENT_WALKER_STATUS);
		walkerReceiver = new WalkerStatusReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				walkerReceiver, filter);
		isAllLocationReceived = false;
		parseContent = new ParseContent(activity);

		try {
			notification = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			r = MediaPlayer.create(getActivity(), notification);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity.setTitle(getString(R.string.app_name));
		try {
			tripFragmentView = inflater.inflate(R.layout.fragment_trip, container, false);
		} catch (InflateException ie) {
			ie.printStackTrace();
		}
		tripFragmentView.findViewById(R.id.btnCall).setOnClickListener(this);
		shareeta = (MyFontButton) tripFragmentView.findViewById(R.id.btnshareeta);
		shareeta.setOnClickListener(this);
		tvTime = (MyFontTextView) tripFragmentView.findViewById(R.id.tvJobTime);
		tvDist = (MyFontTextView) tripFragmentView.findViewById(R.id.tvJobDistance);
		tvDriverName = (MyFontTextView) tripFragmentView.findViewById(R.id.tvDriverName);
		tvDriverPhone = (MyFontTextView) tripFragmentView.findViewById(R.id.tvDriverPhone);
		ivDriverPhoto = (ImageView) tripFragmentView.findViewById(R.id.ivDriverPhoto);
		tripcancel = (TextView) tripFragmentView.findViewById(R.id.tripcancel);
		tripcancel.setOnClickListener(this);
		// tvRate = (TextView) view.findViewById(R.id.tvRate);
		// tvRate.setText(new DecimalFormat("0.0").format(driver.getRating()));
		ratingBarTrip = (RatingBar) tripFragmentView.findViewById(R.id.ratingBarTrip);
		ratingBarTrip.setRating((float) driver.getRating());

		tvDriverPhone.setText(driver.getPhone());
		tvDriverName
				.setText(driver.getFirstName() + " " + driver.getLastName());
		tvStatus = (TextView) tripFragmentView.findViewById(R.id.tvStatus);
		if (driver.getD_latitude() == 0.0 && driver.getD_longitude() == 0.0) {
			shareeta.setVisibility(View.GONE);
		}
		setUpMap();
		return tripFragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// tvDist.setText(strDistance + "");
		new AQuery(activity).id(ivDriverPhoto).progress(R.id.pBar)
				.image(driver.getPicture(), true, true);
		locHelper = new LocationHelper(activity);
		locHelper.setLocationReceivedLister(new OnLocationReceived() {

			@Override
			public void onLocationReceived(LatLng latlong) {
				// TODO Auto-generated method stub
				if (isTripStarted && isAllLocationReceived) {
					// drawTrip(latlong);
					myLocation.setLatitude(latlong.latitude);
					myLocation.setLongitude(latlong.longitude);
					setMarker(latlong);
				}
			}
		});
		locHelper.onStart();
		// PopUp Window
		LayoutInflater inflate = LayoutInflater.from(activity);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(
				R.layout.popup_notification_window, null);
		tvPopupMsg = (MyFontPopUpTextView) layout.findViewById(R.id.tvPopupMsg);

		notificationWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		layout.setOnClickListener(this);
		activity.btnNotification.setOnClickListener(this);

		// Big PopUp Window
		RelativeLayout bigPopupLayout = (RelativeLayout) inflate.inflate(
				R.layout.popup_notification_status_window, null);
		tvJobAccepted = (MyFontPopUpTextView) bigPopupLayout
				.findViewById(R.id.tvJobAccepted);
		tvDriverStarted = (MyFontPopUpTextView) bigPopupLayout
				.findViewById(R.id.tvDriverStarted);
		tvDriverArrvied = (MyFontPopUpTextView) bigPopupLayout
				.findViewById(R.id.tvDriverArrvied);
		tvTripStarted = (MyFontPopUpTextView) bigPopupLayout
				.findViewById(R.id.tvTripStarted);
		tvTripCompleted = (MyFontPopUpTextView) bigPopupLayout
				.findViewById(R.id.tvTripCompleted);

		ivJobAccepted = (ImageView) bigPopupLayout
				.findViewById(R.id.ivJobAccepted);
		ivDriverStarted = (ImageView) bigPopupLayout
				.findViewById(R.id.ivDriverStarted);
		ivDriverArrvied = (ImageView) bigPopupLayout
				.findViewById(R.id.ivDriverArrvied);
		ivTripStarted = (ImageView) bigPopupLayout
				.findViewById(R.id.ivTripStarted);
		ivTripCompleted = (ImageView) bigPopupLayout
				.findViewById(R.id.ivTripCompleted);
		driverStatusWindow = new PopupWindow(bigPopupLayout,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		driverStatusWindow.setBackgroundDrawable(new BitmapDrawable());
		// driverStatusWindow.setFocusable(false);
		// driverStatusWindow.setTouchable(true);
		driverStatusWindow.setOutsideTouchable(true);
		showNotificationPopUp(getString(R.string.text_job_accepted));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCall:
			if (driver != null) {
				String number = driver.getPhone();
				if (!TextUtils.isEmpty(number)) {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:" + number));
					startActivity(callIntent);
				}
			}
			break;
		case R.id.tripcancel:
			cancelTrip();
			break;
		case R.id.rlPopupWindow:
			notificationWindow.dismiss();
			activity.setIcon(R.drawable.notification_box);
			break;
		case R.id.btnActionNotification:
			showDriverStatusNotification();
			break;
		case R.id.btnshareeta:
//			if (driver.getD_latitude() == 0.0 && driver.getD_longitude() == 0.0) {
//				Toast.makeText(activity, "Destination not set",
//						Toast.LENGTH_LONG).show();
//			} else {
//				getTime();
//				Intent intent = new Intent(activity, ContactListActivity.class);
//				activity.startActivityForResult(intent, PICK_MULTIPLE_CONTACT,
//						Const.FRAGMENT_TRIP);
//			}
			/*
			 * Intent intent = new Intent(Intent.ACTION_PICK,
			 * Contacts.CONTENT_URI); activity.startActivityForResult(intent,
			 * PICK_CONTACT,Const.FRAGMENT_TRIP);
			 */
			break;

		default:
			// if(driverStatusWindow.isShowing())
			// driverStatusWindow.dismiss();
			break;
		}
	}

	private void cancelTrip() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(getActivity())
		.setTitle("Cancel Trip")
		.setMessage("Are you sure? you want to cancel your trip?")
		.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialog,
							int which) {
						// continue with delete
						requestTripCancel();
						activity.gotoMapFragment();	
						PreferenceHelper.getInstance(activity).clearRequestData();
						

					}
				})
		.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
			
					public void onClick(
							DialogInterface dialog,
							int which) {
						// do nothing
						dialog.cancel();
					}
				})
		.setIcon(android.R.drawable.ic_dialog_alert).show()
		.setCancelable(false);

        if(etaCountDownTimer != null) {
			etaCountDownTimer.cancel();
		}
	}

	public void showDriverStatusNotification() {
		activity.setIcon(R.drawable.notification_box);
		if (driverStatusWindow.isShowing())
			driverStatusWindow.dismiss();
		else {
			if (notificationWindow.isShowing())
				notificationWindow.dismiss();
			else
				driverStatusWindow.showAsDropDown(activity.btnNotification);
		}

	}

	public void showNotificationPopUp(String text) {
		tvPopupMsg.setText(text);
		if (!driverStatusWindow.isShowing()) {

			if (!notificationWindow.isShowing()) {
				activity.setIcon(R.drawable.notification_box_arrived);
				notificationWindow.showAsDropDown(activity.btnNotification);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// if (PreferenceHelper.getInstance(activity).getRequestTime() == Const.NO_TIME)
		// setRequestTime(SystemClock.e);
		activity.btnNotification.setVisibility(View.VISIBLE);
		startUpdateDriverLocation();
		startCheckingStatusUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		stopUpdateDriverLoaction();
		stopCheckingStatusUpdate();

		super.onPause();
	}

	private void setUpMap() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (map == null) {
			map = ((SupportMapFragment) getActivity()
					.getSupportFragmentManager().findFragmentById(R.id.maptrip))
					.getMap();
			// map.setOnMyLocationChangeListener(new
			// OnMyLocationChangeListener() {
			//
			// @Override
			// public void onMyLocationChange(Location arg0) {
			// // TODO Auto-generated method stub
			// drawTrip(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
			// }
			// });
			map.setInfoWindowAdapter(new InfoWindowAdapter() {

				// Use default InfoWindow frame
				@Override
				public View getInfoWindow(Marker marker) {
					View v = activity.getLayoutInflater().inflate(
							R.layout.info_window_layout, null);
					MyFontTextView title = (MyFontTextView) v
							.findViewById(R.id.locationtitle);
					MyFontTextView content = (MyFontTextView) v
							.findViewById(R.id.infoaddress);
					title.setText(marker.getTitle());

					getAddressFromLocation(marker.getPosition(), content);

					// ((MyFontTextView) v).setText(marker.getTitle());
					return v;
				}

				// Defines the contents of the InfoWindow
				@Override
				public View getInfoContents(Marker marker) {

					// Getting view from the layout file info_window_layout View

					// Getting reference to the TextView to set title TextView

					// Returning the view containing InfoWindow contents return
					return null;
				}
			});

			map.setOnMarkerClickListener(new OnMarkerClickListener() {
				@Override
				public boolean onMarkerClick(Marker marker) {
					marker.showInfoWindow();
					return true;
				}
			});
		}
		initPreviousDrawPath();
		client = new LocationClient(activity, new ConnectionCallbacks() {

			@Override
			public void onDisconnected() {
				// TODO Auto-generated method stub
			}

			@Override
			public void onConnected(Bundle connectionHint) {
				// TODO Auto-generated method stub

				Location loc = client.getLastLocation();

				if (loc != null) {
					myLocation = loc;
					myLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
					setMarkers(myLatLng);
				}

			}
		}, new OnConnectionFailedListener() {

			@Override
			public void onConnectionFailed(ConnectionResult result) {
				// TODO Auto-generated method stub

			}
		});
		client.connect();
	}

	private void setMarkers(LatLng latLang) {
		LatLng latLngDriver = new LatLng(driver.getLatitude(),
				driver.getLongitude());

		setMarker(latLang);
		setDriverMarker(latLngDriver);
		if (driver.getD_latitude() == 0.0 && driver.getD_longitude() == 0.0) {

		} else {
			LatLng latlngdestination = new LatLng(driver.getD_latitude(),
					driver.getD_longitude());
			setdestinationmarker(latlngdestination);
		}
		animateCameraToMarkerWithZoom(latLngDriver);

		// showDirection(latLang, latLngDriver);
		// Location locDriver = new Location("");
		// locDriver.setLatitude(driver.getLatitude());
		// locDriver.setLongitude(driver.getLongitude());
		// strDistance = convertMilesFromMeters(loc
		// .distanceTo(locDriver));
		// animateCameraToMarker(latLang);
	}

	private void showDirection(LatLng source, LatLng destination) {

		Map<String, String> hashMap = new HashMap<String, String>();

		final String url = "http://maps.googleapis.com/maps/api/directions/json?origin="
				+ source.latitude
				+ ","
				+ source.longitude
				+ "&destination="
				+ destination.latitude
				+ ","
				+ destination.longitude
				+ "&sensor=false";
		// hashMap.put("url", url);
		new HttpRequester(activity, hashMap, Const.ServiceCode.GET_ROUTE, true,
				this);
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_getting_direction), false, null);
	}

	public void onDestroyView() {
		super.onDestroyView();
		wakeLock.release();
		if(tripFragmentView != null) {
			ViewGroup parent = (ViewGroup) tripFragmentView.getParent();
			if(parent != null) {
				parent.removeView(tripFragmentView);
			}
		}
		SupportMapFragment f = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.maptrip);
		if (f != null) {
			try {
				getFragmentManager().beginTransaction().remove(f).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		map = null;
	}

	@SuppressLint("NewApi")
	@Override
	public void onTaskCompleted(final String response, int serviceCode) {
		// TODO Auto-generated method stub
		if (!this.isVisible())
			return;
		switch (serviceCode) {
		case Const.ServiceCode.GET_ROUTE:
			AndyUtils.removeCustomProgressDialog();
			if (!TextUtils.isEmpty(response)) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						parseContent.parseRoute(response, route);
						final ArrayList<Step> step = route.getListStep();
						points = new ArrayList<LatLng>();
						lineOptions = new PolylineOptions();
						for (int i = 0; i < step.size(); i++) {
							List<LatLng> path = step.get(i).getListPoints();
							// System.out.println("step =====> " + i + " and "
							// + path.size());
							points.addAll(path);
						}
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (polyLine != null)
									polyLine.remove();
								lineOptions.addAll(points);
								lineOptions.width(15);
								lineOptions.color(getResources().getColor(
										R.color.skyblue));
								polyLine = map.addPolyline(lineOptions);
								LatLngBounds.Builder bld = new LatLngBounds.Builder();
								bld.include(myMarker.getPosition());
								bld.include(markerDriver.getPosition());
								LatLngBounds latLngBounds = bld.build();
								map.moveCamera(CameraUpdateFactory
										.newLatLngBounds(latLngBounds, 50));
//								 tvDist.setText(route.getDistanceText());
//								 tvTime.setText(route.getDurationText());
								// tvDist.setText(0 + " KM");
								// tvTime.setText(0 + " MINS");
							}
						});
					}
				}).start();
			}
		case Const.ServiceCode.GET_REQUEST_LOCATION:
			if (activity.pContent.isSuccess(response)) {
				DriverLocation driverLocation = activity.pContent
						.getDriverLocation(response);
				if (driverLocation == null || !this.isVisible())
					return;
				setDriverMarker(driverLocation.getLatLng());
				drawTrip(driverLocation.getLatLng());
				if (isTripStarted) {
					long startTime = Const.NO_TIME;
					if (PreferenceHelper.getInstance(activity).getRequestTime() == Const.NO_TIME) {
						startTime = System.currentTimeMillis();
						PreferenceHelper.getInstance(activity).putRequestTime(startTime);
					} else {
						startTime = PreferenceHelper.getInstance(activity).getRequestTime();
					}
					// distance = distance / 1625;
					// tvDist.setText(new DecimalFormat("0.00").format(distance)
					// + " " + driverLocation.getUnit());
					tvDist.setText(driverLocation.getDistance()
							+ " " + driverLocation.getUnit());
					long elapsedTime = System.currentTimeMillis() - startTime;
					lastTime = elapsedTime / (1000 * 60) + " "
							+ getResources().getString(R.string.text_mins);
					tvTime.setText(lastTime);
					// tvTime.setText("0" + " MINS");
					// tvDist.setText("0" + " KM");
				}
			}
			isContinueDriverRequest = true;
			// setMarker(latLng);
			break;
		case Const.ServiceCode.GET_REQUEST_STATUS:
			Log.d("hey", response);
			if (activity.pContent.isSuccess(response)) {
//				if (selector == activity.pContent.checkRequestStatus(response)) {
//					selector++;
					switch (activity.pContent.checkRequestStatus(response)) {
					case Const.IS_WALK_STARTED:
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_driver_arrvied)));
						// showNotificationPopUp(getString(R.string.text_driver_arrvied));
						changeNotificationPopUpUI(3);
						isContinueStatusRequest = true;
						isTripStarted = false;
						myMarker.remove();
//						GCMIntentService.generateNotification(getActivity(),
//								getString(R.string.text_driver_arrvied));
						break;
					case Const.ServiceCode.CANCEL_REQUEST:
						Log.d("mahi", "response in cancel request" + response);
						AndyUtils.removeCustomProgressDialog();
						break;
					case Const.IS_COMPLETED:
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_trip_started)));
						// showNotificationPopUp(getString(R.string.text_trip_started));
						changeNotificationPopUpUI(4);
						if (!isAllLocationReceived) {
							isAllLocationReceived = true;
							getPath(String.valueOf(PreferenceHelper.getInstance(activity)
									.getRequestId()));
						}
						isContinueStatusRequest = true;
						isTripStarted = true;
//						GCMIntentService.generateNotification(getActivity(),
//								getString(R.string.text_trip_started));
						break;
					case Const.IS_WALKER_ARRIVED:
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_driver_started)));
						// showNotificationPopUp(getString(R.string.text_driver_started));
						changeNotificationPopUpUI(2);
						isContinueStatusRequest = true;
//						GCMIntentService.generateNotification(getActivity(),
//								getString(R.string.text_driver_started));
					case Const.IS_WALKER_STARTED:
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_job_accepted)));
						// showNotificationPopUp(getString(R.string.text_job_accepted));
						changeNotificationPopUpUI(1);
						isContinueStatusRequest = true;
//						GCMIntentService.generateNotification(getActivity(),
//								getString(R.string.text_job_accepted));
						break;
					case Const.IS_WALKER_RATED:
						stopCheckingStatusUpdate();
						isTripStarted = false;
						if (notificationWindow.isShowing())
							notificationWindow.dismiss();
						if (driverStatusWindow.isShowing())
							driverStatusWindow.dismiss();
						Log.d("hey", response);
						driver = activity.pContent.getDriverDetail(response);
						Log.d("hey", "trip" + response);
						driver.setLastDistance(lastDistance);
						driver.setLastTime(lastTime);
						activity.gotoRateFragment(driver);
						break;
					default:
						break;
					}
		//		}
			} else {
				isContinueStatusRequest = true;
			}
			break;
		case Const.ServiceCode.GET_PATH:
			AndyUtils.removeCustomProgressDialog();
			activity.pContent.parsePathRequest(response, points);
			initPreviousDrawPath();
			AppLog.Log(Const.TAG, "Path====>" + response + "");
			break;

		case Const.ServiceCode.SEND_ETA:
			Log.d("xxx", "response from share eta  " + response);
			selectedContacts.clear();
			phonesarray.clear();
			if (activity.pContent.isSuccess(response)) {
				Toast.makeText(activity, getString(R.string.text_eta_success),
						Toast.LENGTH_LONG).show();
			} else
				Toast.makeText(activity, getString(R.string.text_eta_fail),
						Toast.LENGTH_LONG).show();
			break;

		case Const.ServiceCode.GET_MAP_TIME:
			if (response != null) {
				try {
					JSONObject jObject = new JSONObject(response);
					if (jObject.getString("status").equals("OK")) {
						JSONArray jaArray = jObject.getJSONArray("rows");
						for (int i = 0; i < jaArray.length(); i++) {
							JSONObject jobj = jaArray.getJSONObject(i);
							JSONArray jaArray2 = jobj.getJSONArray("elements");
							for (int j = 0; j < jaArray2.length(); j++) {
								JSONObject jobj1 = jaArray2.getJSONObject(j);
								if(jobj1.has("distance")) {
									JSONObject jobj_distance = jobj1.getJSONObject("distance");
									strDistance = jobj_distance.getString("text");
								} else {
									strDistance = "0";
								}
								if(jobj1.has("duration")) {
									JSONObject jobj_duration = jobj1.getJSONObject("duration");
									duration = jobj_duration.getString("text");
								} else {
									duration = "0";
								}
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!(driver.getD_latitude() == 0.0 && driver.getD_longitude() == 0.0) && !isTripStarted) {
					shareeta.setText("Share ETA " + "(" + duration + ")");

					if(etaCountDownTimer == null) {
						long nDuration = 0;
						try {
							if(!duration.equals("0")) {
								nDuration = Long.parseLong(duration.split(" ")[0]);
							}
						} catch (Exception e) {
							nDuration = 0;
						}
						if(nDuration > 0) {
							etaCountDownTimer = new EtaCountDownTimer(nDuration * 60000, 60000);
							etaCountDownTimer.start();
						}
					}
				}
//				tvDist.setText(strDistance);
			}
			AndyUtils.removeCustomProgressDialog();
			break;
		}
	}

	private void changeNotificationPopUpUI(int i) {
		// TODO Auto-generated method stub
		switch (i) {
		case 1:
			ivJobAccepted.setImageResource(R.drawable.checkbox);
			tvJobAccepted.setTextColor(getResources().getColor(
					R.color.color_text));
			break;
		case 2:
			ivJobAccepted.setImageResource(R.drawable.checkbox);
			tvJobAccepted.setTextColor(getResources().getColor(
					R.color.color_text));
			ivDriverStarted.setImageResource(R.drawable.checkbox);
			tvDriverStarted.setTextColor(getResources().getColor(
					R.color.color_text));
			break;
		case 3:
			ivJobAccepted.setImageResource(R.drawable.checkbox);
			tvJobAccepted.setTextColor(getResources().getColor(
					R.color.color_text));
			ivDriverStarted.setImageResource(R.drawable.checkbox);
			tvDriverStarted.setTextColor(getResources().getColor(
					R.color.color_text));
			ivDriverArrvied.setImageResource(R.drawable.checkbox);
			tvDriverArrvied.setTextColor(getResources().getColor(
					R.color.color_text));
			break;
		case 4:
			ivJobAccepted.setImageResource(R.drawable.checkbox);
			tvJobAccepted.setTextColor(getResources().getColor(
					R.color.color_text));
			ivDriverStarted.setImageResource(R.drawable.checkbox);
			tvDriverStarted.setTextColor(getResources().getColor(
					R.color.color_text));
			ivDriverArrvied.setImageResource(R.drawable.checkbox);
			tvDriverArrvied.setTextColor(getResources().getColor(
					R.color.color_text));
			ivTripStarted.setImageResource(R.drawable.checkbox);
			tvTripStarted.setTextColor(getResources().getColor(
					R.color.color_text));
			break;

		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.fragments.BaseFragment#isValidate()
	 */
	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	class TrackLocation extends TimerTask {

		public void run() {

			if (isContinueDriverRequest) {
				isContinueDriverRequest = false;
				getDriverLocation();
			}
		}
	}

	private void getDriverLocation() {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_REQUEST_LOCATION + Const.Params.ID + "="
						+ PreferenceHelper.getInstance(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ PreferenceHelper.getInstance(activity).getSessionToken()
						+ "&" + Const.Params.REQUEST_ID + "="
						+ PreferenceHelper.getInstance(activity).getRequestId());
		AppLog.Log("TAG",
				Const.ServiceType.GET_REQUEST_LOCATION + Const.Params.ID + "="
						+ PreferenceHelper.getInstance(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ PreferenceHelper.getInstance(activity).getSessionToken()
						+ "&" + Const.Params.REQUEST_ID + "="
						+ PreferenceHelper.getInstance(activity).getRequestId());
		new HttpRequester(activity, map,
				Const.ServiceCode.GET_REQUEST_LOCATION, true, this);

	}

	private void setdestinationmarker(LatLng latlng) {
		if (latlng != null) {
			if (map != null && this.isVisible()) {

				if (destinationmarker == null) {
					MarkerOptions opt = new MarkerOptions();
					opt.position(latlng);
					opt.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.pin_client_org));
					opt.title(getString(R.string.text_my_destination));

					destinationmarker = map.addMarker(opt);
					// animateCameraToMarkerWithZoom(latLng);

				} else {
					destinationmarker.setPosition(latlng);
					// animateCameraToMarker(latLng);
				}

			}

		}
	}

	private void setMarker(LatLng latLng) {
		if (latLng != null) {

			if (map != null && this.isVisible()) {

				if (myMarker == null) {
					MarkerOptions opt = new MarkerOptions();
					opt.position(latLng);
					opt.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.pin_client_org));
					opt.title(getString(R.string.text_my_location));

					myMarker = map.addMarker(opt);
					// animateCameraToMarkerWithZoom(latLng);

				} else {
					myMarker.setPosition(latLng);
					// animateCameraToMarker(latLng);
				}

			}

			if (!(driver.getD_latitude() == 0.0 && driver.getD_longitude() == 0.0)) {
				getTime();
			}

		}

	}

	private void setDriverMarker(LatLng latLng) {
		if (latLng != null) {
			if (map != null && this.isVisible()) {

				if (markerDriver == null) {

					MarkerOptions opt = new MarkerOptions();
					opt.position(latLng);
					opt.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.pin_driver));
					opt.title(getString(R.string.text_drive_location));
					markerDriver = map.addMarker(opt);

				} else {
					markerDriver.setPosition(latLng);

				}
				// animateCameraToMarker(latLng);
			}

		}

	}

	private void startUpdateDriverLocation() {
		isContinueDriverRequest = true;
		timerDriverLocation = new Timer();
		timerDriverLocation.scheduleAtFixedRate(new TrackLocation(), 0,
				LOCATION_SCHEDULE);
	}

	private void stopUpdateDriverLoaction() {
		isContinueDriverRequest = false;
		if (timerDriverLocation != null) {
			timerDriverLocation.cancel();
			timerDriverLocation = null;
		}

	}

	private void animateCameraToMarkerWithZoom(LatLng latLng) {

		CameraUpdate cameraUpdate = null;
		cameraUpdate = CameraUpdateFactory
				.newLatLngZoom(latLng, Const.MAP_ZOOM);
		map.animateCamera(cameraUpdate);
	}

	private void animateCameraToMarker(LatLng latLng) {

		CameraUpdate cameraUpdate = null;
		cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
		map.animateCamera(cameraUpdate);
	}

	private String convertKmFromMeters(float disatanceInMeters) {
		return new DecimalFormat("0.0").format(0.001f * disatanceInMeters);
	}

	private void startCheckingStatusUpdate() {
		stopCheckingStatusUpdate();
		if (PreferenceHelper.getInstance(activity).getRequestId() != Const.NO_REQUEST) {
			isContinueStatusRequest = true;
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerRequestStatus(), Const.DELAY,
					Const.TIME_SCHEDULE);
		}
	}

	private void stopCheckingStatusUpdate() {
		isContinueStatusRequest = false;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private class TimerRequestStatus extends TimerTask {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (isContinueStatusRequest) {
				isContinueStatusRequest = false;
				getRequestStatus(String
						.valueOf(PreferenceHelper.getInstance(activity).getRequestId()));
			}
		}

	}

	private void getRequestStatus(String requestId) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_REQUEST_STATUS + Const.Params.ID + "="
						+ PreferenceHelper.getInstance(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ PreferenceHelper.getInstance(activity).getSessionToken()
						+ "&" + Const.Params.REQUEST_ID + "=" + requestId);

		new HttpRequester(activity, map, Const.ServiceCode.GET_REQUEST_STATUS,
				true, this);
	}

	private void getPath(String requestId) {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_PATH + Const.Params.ID + "="
						+ PreferenceHelper.getInstance(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ PreferenceHelper.getInstance(activity).getSessionToken()
						+ "&" + Const.Params.REQUEST_ID + "=" + requestId);
		new HttpRequester(activity, map, Const.ServiceCode.GET_PATH, true, this);
	}

	private void setRequestTime(long time) {
		PreferenceHelper.getInstance(activity).putRequestTime(time);
	}

	private void drawTrip(LatLng latlng) {

		if (map != null && this.isVisible()) {
			
			points.add(latlng);
			lineOptions = new PolylineOptions();
			lineOptions.addAll(points);
			lineOptions.width(15);
			lineOptions.color(getResources().getColor(R.color.skyblue));

			map.addPolyline(lineOptions);
		}

	}

	class WalkerStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String response = intent.getStringExtra(Const.EXTRA_WALKER_STATUS);
			Log.d("hey", response);
			AppLog.Log("Response ---- Trip", response);
			if (TextUtils.isEmpty(response))
				return;
			stopCheckingStatusUpdate();

			if (activity.pContent.isSuccess(response)) {

//				if (selector == activity.pContent.checkRequestStatus(response)) {
//					selector++;

					switch (activity.pContent.checkRequestStatus(response)) {
					case Const.IS_WALK_STARTED:
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_driver_arrvied)));
						showNotificationPopUp(getString(R.string.text_driver_arrvied));
						changeNotificationPopUpUI(3);
						
						Log.d("yyy", "3");
						isContinueStatusRequest = true;
						isTripStarted = false;
						if(etaCountDownTimer != null) {
							etaCountDownTimer.cancel();
						}
						shareeta.setText("Share ETA " + "(0 MINS)");
						myMarker.remove();
						break;
					case Const.IS_COMPLETED:
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_trip_started)));
						showNotificationPopUp(getString(R.string.text_trip_started));
						changeNotificationPopUpUI(4);
						tripcancel.setVisibility(View.GONE);
						Log.d("yyy", "4");
						if (!isAllLocationReceived) {
							isAllLocationReceived = true;
							getPath(String.valueOf(PreferenceHelper.getInstance(activity)
									.getRequestId()));
						}
						isContinueStatusRequest = true;
						isTripStarted = true;
						startCheckingStatusUpdate();
						break;
					case Const.IS_WALKER_ARRIVED:
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_driver_started)));
						showNotificationPopUp(getString(R.string.text_driver_started));
						changeNotificationPopUpUI(2);
						Log.d("yyy", "2");
						isContinueStatusRequest = true;
						break;
					case Const.IS_WALKER_STARTED:
						tvStatus.setText(Html
								.fromHtml(getString(R.string.text_job_accepted)));
						showNotificationPopUp(getString(R.string.text_job_accepted));
						changeNotificationPopUpUI(1);
						Log.d("yyy", "1");
						isContinueStatusRequest = true;
						break;
					case Const.IS_WALKER_RATED:
						stopCheckingStatusUpdate();
						isTripStarted = false;
						if (notificationWindow.isShowing())
							notificationWindow.dismiss();
						if (driverStatusWindow.isShowing())
							driverStatusWindow.dismiss();

						driver = activity.pContent.getDriverDetail(response);
						Log.d("hey", "trip" + response);
						driver.setLastDistance(lastDistance);
						driver.setLastTime(lastTime);
						activity.gotoRateFragment(driver);
						break;

					default:

						break;

					}
//				}
			}
			else {
				isContinueStatusRequest = true;
			}
		}
	}

	private void initPreviousDrawPath() {
		lineOptions = new PolylineOptions();
		lineOptions.addAll(points);
		lineOptions.width(15);
		lineOptions.color(getResources().getColor(R.color.skyblue));
		if (map != null && this.isVisible())
			map.addPolyline(lineOptions);
		points.clear();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				walkerReceiver);
		if (notificationWindow.isShowing())
			notificationWindow.dismiss();
		if (driverStatusWindow.isShowing())
			driverStatusWindow.dismiss();
	}

	/* added by amal */
	private String strAddress = null;

	private void getAddressFromLocation(final LatLng latlng,
			final MyFontTextView et) {

		/*
		 * et.setText("Waiting for Address"); et.setTextColor(Color.GRAY);
		 */
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 */

		Geocoder gCoder = new Geocoder(getActivity());
		try {
			final List<Address> list = gCoder.getFromLocation(latlng.latitude,
					latlng.longitude, 1);
			if (list != null && list.size() > 0) {
				Address address = list.get(0);
				StringBuilder sb = new StringBuilder();
				if (address.getAddressLine(0) != null) {

					sb.append(address.getAddressLine(0)).append(", ");
				}
				sb.append(address.getLocality()).append(", ");
				// sb.append(address.getPostalCode()).append(",");
				sb.append(address.getCountryName());
				strAddress = sb.toString();

				strAddress = strAddress.replace(",null", "");
				strAddress = strAddress.replace("null", "");
				strAddress = strAddress.replace("Unnamed", "");
				if (!TextUtils.isEmpty(strAddress)) {

					et.setText(strAddress);

				}
			}
			/*
			 * getActivity().runOnUiThread(new Runnable() {
			 * 
			 * @Override public void run() { // TODO Auto-generated method stub
			 * if (!TextUtils.isEmpty(strAddress)) {
			 * 
			 * et.setText(strAddress);
			 * 
			 * 
			 * } else { et.setText("");
			 * 
			 * }
			 * 
			 * } });
			 */

		} catch (IOException exc) {
			exc.printStackTrace();
		}
		// }
		// }).start();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("xxx",
				"in activity result of fragment    "
						+ String.valueOf(requestCode));
		switch (requestCode) {
		case PICK_CONTACT:
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				Cursor c = getActivity().getContentResolver().query(
						contactData, null, null, null, null);

				if (c.getCount() > 0) {
					while (c.moveToNext()) {
						String id = c.getString(c
								.getColumnIndex(ContactsContract.Contacts._ID));
						String name = c
								.getString(c
										.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						if (Integer
								.parseInt(c.getString(c
										.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
							System.out.println("name : " + name + ", ID : "
									+ id);

							// get the phone number
							Cursor pCur = getActivity()
									.getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = ?",
											new String[] { id }, null);
							while (pCur.moveToNext()) {
								String phone = pCur
										.getString(pCur
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								System.out.println("phone" + phone);
								Log.d("xxx", phone);
							}
							pCur.close();
						}

						/*
						 * if (c.moveToFirst()) { String number =
						 * c.getString(c.getColumnIndex
						 * (ContactsContract.Contacts.DISPLAY_NAME));
						 * Toast.makeText(getActivity(), number,
						 * Toast.LENGTH_LONG).show(); Log.d("xxx", number);
						 */
						// TODO Fetch other Contact details as you want to use
					}
				}
			}
			break;

		case PICK_MULTIPLE_CONTACT:
			if (data != null) {
				Bundle bundle = data.getExtras();
				selectedContacts = bundle.getStringArrayList("sel_contacts");
				Log.d("amal", "Selected contacts-->" + selectedContacts);
				if (selectedContacts.size() < 0) {
					Toast.makeText(activity, "No contacts selected",
							Toast.LENGTH_LONG).show();
				} else {

					String[] phones = new String[selectedContacts.size()];
					for (int i = 0; i < selectedContacts.size(); i++) {
						phones[i] = selectedContacts.get(i).replaceAll("\\s+", "");
					}
					for (int i = 0; i < phones.length; i++) {
						phonesarray.add(phones[i]);
					}
					String phonesingleton = null;
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < phonesarray.size(); i++) {
						sb.append(phonesarray.get(i));
						if (i != phonesarray.size() - 1)
							sb.append(",");
					}
					phonesingleton = sb.toString();
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(Const.URL,
							Const.ServiceType.SEND_ETA
									+ Const.Params.ID
									+ "="
									+ PreferenceHelper.getInstance(activity)
											.getUserId()
									+ "&"
									+ Const.Params.TOKEN
									+ "="
									+ PreferenceHelper.getInstance(activity)
											.getSessionToken()
									+ "&"
									+ Const.Params.REQUEST_ID
									+ "="
									+ String.valueOf(PreferenceHelper.getInstance(activity)
											.getRequestId()) + "&"
									+ Const.Params.PHONE + "=" + phonesingleton
									+ "&" + Const.Params.ETA + "=" + duration);
					Log.d("amal", "map from eta  " + map.toString());
					new HttpRequester(activity, map,
							Const.ServiceCode.SEND_ETA, true, this);
				}
			}
			break;
		}
	}

	private void getTime() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GOOGLE_MAP_API + Const.Params.MAP_ORIGINS
						+ "=" + String.valueOf(myMarker.getPosition().latitude)
						+ ","
						+ String.valueOf(myMarker.getPosition().longitude)
						+ "&" + Const.Params.MAP_DESTINATIONS + "="
						+ driver.getD_latitude() + ","
						+ driver.getD_longitude());

		Log.d("amal", "getTime " + map);
		new HttpRequester(activity, map, Const.ServiceCode.GET_MAP_TIME, true, this);
	}

	private void requestTripCancel(){
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		AndyUtils.removeCustomProgressDialog();
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_canceling_request), true, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.CANCEL_REQUEST);
		map.put(Const.Params.ID, String.valueOf(PreferenceHelper.getInstance(activity).getUserId()));
		map.put(Const.Params.TOKEN,
				String.valueOf(PreferenceHelper.getInstance(activity).getSessionToken()));
		map.put(Const.Params.REQUEST_ID,
				String.valueOf(PreferenceHelper.getInstance(activity).getRequestId()));
		new HttpRequester(activity, map, Const.ServiceCode.CANCEL_REQUEST, this);
	}

	private class EtaCountDownTimer extends CountDownTimer {

		private long lnStartTime = 0;
		private long lnInterval = 0;
		public EtaCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
			lnStartTime = startTime;
			lnInterval = interval;
		}

		@Override
		public void onFinish() {
			this.cancel();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			int time = (int) (millisUntilFinished / lnInterval);
			if (!isVisible()) {
				return;
			}

			shareeta.setText("Share ETA " + "(" + time + " MINS)");
//			tvDist.setText(strDistance);
		}
	}
}
