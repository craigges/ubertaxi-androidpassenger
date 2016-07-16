package com.jozibear247_cab.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jozibear247_cab.MainDrawerActivity;
import com.jozibear247_cab.R;
import com.jozibear247_cab.UberViewPaymentActivity;
import com.jozibear247_cab.adapter.PlacesAutoCompleteAdapter;
import com.jozibear247_cab.adapter.VehicalTypeListAdapter;
import com.jozibear247_cab.component.MyFontButton;
import com.jozibear247_cab.component.MyFontEdittextView;
import com.jozibear247_cab.component.MyFontTextView;
import com.jozibear247_cab.interfaces.OnProgressCancelListener;
import com.jozibear247_cab.models.Card;
import com.jozibear247_cab.models.Driver;
import com.jozibear247_cab.models.VehicalType;
import com.jozibear247_cab.models.Walkerinfo;
import com.jozibear247_cab.parse.HttpRequester;
import com.jozibear247_cab.parse.ParseContent;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.AppLog;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Hardik A Bhalodi
 */

public class UberMapFragment extends UberBaseFragment implements
		OnProgressCancelListener {
	private static UberMapFragment instance;
	private PlacesAutoCompleteAdapter adapter;
	private AutoCompleteTextView etSource, enterdestination;
	private ParseContent pContent;
	public static boolean isMapTouched = false;
	private float currentZoom = -1;

	private GoogleMap map;
	private LocationClient client;
	private LatLng curretLatLng;
	private String strAddress = null;

	private boolean isContinueRequest;
	private Timer timer;
	private WalkerStatusReceiver walkerReceiver;

	private ImageButton btnMyLocation;

	private FrameLayout mapFrameLayout;

	private GridView listViewType;

	private ArrayList<VehicalType> listType;

	private ArrayList<Marker> drivermarkers;

	private VehicalTypeListAdapter typeAdapter;

	private int selectedPostion = -1;
	private boolean isGettingVehicalType = true;

//	private boolean isLocationFound;
	// private Animation topToBottomAnimation, bottomToTopAnimation,
	// buttonTopToBottomAnimation;

	private MyFontButton btnSelectService, btnRequestCap,
			btnratecard, btnfareestimate, btnpromocard;
//	private static MyFontButton bubble;
	private static SlidingDrawer drawer;
	private static LinearLayout markers;
	private static RelativeLayout pickuppop;
	private static ImageButton btnadddestination;
	private ArrayList<Walkerinfo> walkerlist;
	private TextView eta;
	private RelativeLayout destaddlayout;
	private ImageButton clearfield;
	private SharedPreferences promopref;
	SharedPreferences.Editor editorpromo;
	private String promoglobal;
	int paydebt_indicator = 0;
	private Animation slidedown, slideup;
	private LatLng destlatlng_places;
	private View mapView;
	// PopupWindow window;
	private AlertDialog promoCodeDlg;
	private boolean isRequestCapNow;

	private UberMapFragment() {
	}

	public static UberMapFragment newInstance() {
		if(instance == null) {
			instance = new UberMapFragment();
		}

		return instance;
	}

	static TextView markerBubblePickMeUp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mapView = inflater.inflate(R.layout.fragment_map, container, false);
//		isLocationFound = false;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		markerBubblePickMeUp = (MyFontButton) mapView.findViewById(R.id.markerBubblePickMeUp);
		markerBubblePickMeUp.setOnClickListener(this);

//		bubble = (MyFontButton) mapView.findViewById(R.id.markerBubblePickMeUp);
		selectedPostion = 0;/* modified by Amal *//* reverted back */

		listViewType = (GridView) mapView.findViewById(R.id.gvTypes);

		promopref = getActivity().getSharedPreferences("promocode", Context.MODE_PRIVATE);
		editorpromo = promopref.edit();

		markers = (LinearLayout) mapView.findViewById(R.id.layoutMarker);
		pickuppop = (RelativeLayout) mapView.findViewById(R.id.pickuppop);

		// llBottomLayout = (LinearLayout)
		// view.findViewById(R.id.llBottomLayout);
		mapFrameLayout = (FrameLayout) mapView.findViewById(R.id.mapFrameLayout);

		mapFrameLayout.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN | MotionEvent.ACTION_MOVE:
					UberMapFragment.isMapTouched = true;
					break;

				case MotionEvent.ACTION_UP:
					UberMapFragment.isMapTouched = false;
					break;
				}
				return true;
			}
		});
		btnMyLocation = (ImageButton) mapView.findViewById(R.id.btnMyLocation);
		btnSelectService = (MyFontButton) mapView.findViewById(R.id.btnSelectService);
		btnSelectService.setOnClickListener(this);

		btnRequestCap = (MyFontButton) mapView.findViewById(R.id.btn_request_cap);
		btnRequestCap.setOnClickListener(this);
//		btnpayment = (MyFontButton) mapView.findViewById(R.id.btnpayment);
//		btnpayment.setOnClickListener(this);
		enterdestination = (AutoCompleteTextView) mapView.findViewById(R.id.EnterDestination);
		destaddlayout = (RelativeLayout) mapView.findViewById(R.id.destinationaddlayout);
		clearfield = (ImageButton) mapView.findViewById(R.id.clearfield);
		clearfield.setOnClickListener(this);

		btnfareestimate = (MyFontButton) mapView.findViewById(R.id.btnfareestimate);
		btnfareestimate.setOnClickListener(this);
		eta = (TextView) mapView.findViewById(R.id.eta);
		btnratecard = (MyFontButton) mapView.findViewById(R.id.btnratecard);
		btnratecard.setOnClickListener(this);
		btnpromocard = (MyFontButton) mapView.findViewById(R.id.btnpromocode);
		btnpromocard.setOnClickListener(this);
		slidedown = AnimationUtils.loadAnimation(getActivity(), R.anim.destaddtopbottom);
		slideup = AnimationUtils.loadAnimation(getActivity(), R.anim.destaddbottomtop);
		// etSource = (AutoCompleteTextView)
		// view.findViewById(R.id.etEnterSouce);
		drawer = (SlidingDrawer) mapView.findViewById(R.id.drawer);
		isRequestCapNow = false;
		setUpMapIfNeeded();
		hideKeyboard();

		return mapView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		drivermarkers = new ArrayList<Marker>();

		btnadddestination = activity.btnadddestination;
		btnadddestination.setOnClickListener(this);

		etSource = activity.etSource;
		activity.tvTitle.setVisibility(View.GONE);
		etSource.setVisibility(View.VISIBLE);
		IntentFilter filter = new IntentFilter(Const.INTENT_WALKER_STATUS);
		walkerReceiver = new WalkerStatusReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				walkerReceiver, filter);
		pContent = new ParseContent(activity);

		walkerlist = new ArrayList<Walkerinfo>();
		walkerarrayformarker = new ArrayList<UberMapFragment.walkerinfo_marker>();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		adapter = new PlacesAutoCompleteAdapter(activity, R.layout.autocomplete_list_text);
		etSource.setAdapter(adapter);

		enterdestination.setAdapter(adapter);
		enterdestination.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				hideKeyboard();
			}
		});

		// PopUp Window
		// LayoutInflater inflate = LayoutInflater.from(activity);
		// LinearLayout ll=(LinearLayout) inflate.inflate(R.layout.popup_window,
		// null);

		// window = new
		// PopupWindow(ll,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		// window.showAsDropDown(activity.btnNotification);

		etSource.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				hideKeyboard();
				final String selectedDestPlace = adapter.getItem(arg2);

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						final LatLng latlng = getLocationFromAddress(selectedDestPlace);
						if (latlng != null) {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									isMapTouched = true;
									curretLatLng = latlng;
									// removemarkers();

									animateCameraToMarker(latlng);
									getallproviders();
								}
							});
						}
					}
				}).start();
			}
		});
		listType = new ArrayList<VehicalType>();
		typeAdapter = new VehicalTypeListAdapter(activity, listType, this);
		listViewType.setAdapter(typeAdapter);
		getVehicalTypes();
		// drawer.lock();
		listViewType.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				for (int i = 0; i < listType.size(); i++)
					listType.get(i).isSelected = false;
				listType.get(position).isSelected = true;
				// btnSelectService.setCompoundDrawables(new , top, right,
				// bottom)
				// onItemClick(position);
				selectedPostion = position;

				removemarkers();
				getallproviders();

				typeAdapter.notifyDataSetChanged();
				/* added by amal */
				if (drawer.isOpened()) {
					drawer.animateClose();
					drawer.unlock();
				}
			}
		});
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
		activity.tvTitle.setVisibility(View.GONE);
		activity.btnNotification.setVisibility(View.INVISIBLE);
		etSource.setVisibility(View.VISIBLE);
		startCheckingStatusUpdate();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (map == null) {
			map = ((SupportMapFragment) activity.getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			map.setMyLocationEnabled(true);
			map.getUiSettings().setMyLocationButtonEnabled(false);
			map.getUiSettings().setZoomControlsEnabled(false);
			map.setOnMapClickListener(new OnMapClickListener() {

				@Override
				public void onMapClick(LatLng point) {
					Log.d("mahi", "Map clicked");
					hideKeyboard();
				}
			});
			map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {

				@Override
				public void onMyLocationChange(Location loc) {
					// TODO Auto-generated method stub

				}
			});

			btnMyLocation.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Location loc = map.getMyLocation();
					if (loc != null) {
						LatLng latLang = new LatLng(loc.getLatitude(), loc.getLongitude());
						animateCameraToMarker(latLang);
						getallproviders();
					}
				}
			});

			map.setOnCameraChangeListener(new OnCameraChangeListener() {

				public void onCameraChange(CameraPosition camPos) {
					// TODO Auto-generated method stub
					if (currentZoom == -1) {
						currentZoom = camPos.zoom;
					} else if (camPos.zoom != currentZoom) {
						currentZoom = camPos.zoom;
						return;
					}

					if (!isMapTouched) {
						curretLatLng = camPos.target;
						// removemarkers();
						getallproviders();
						// if (pickuppop.getVisibility() == View.VISIBLE)
						// gettime();
						getAddressFromLocation(camPos.target, etSource);
					}
					isMapTouched = false;
					// setMarker(camPos.target);
				}
			});

			if (map != null) {
				// Log.i("Map", "Map Fragment");
			}
		}

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
					LatLng latLang = new LatLng(loc.getLatitude(),
							loc.getLongitude());
					animateCameraToMarker(latLang);
				} else {
					activity.showLocationOffDialog();
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

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		stopCheckingStatusUpdate();
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		SupportMapFragment f = (SupportMapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		if (f != null) {
			try {
				getFragmentManager().beginTransaction().remove(f).commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(mapView != null) {
			ViewGroup parent = (ViewGroup) mapView.getParent();
			if(parent != null) {
				parent.removeView(mapView);
			}
		}
		map = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(walkerReceiver);
		activity.tvTitle.setVisibility(View.VISIBLE);
		etSource.setVisibility(View.GONE);
	}

	public static void setmarkerVisibile() {
		// markers.setVisibility(View.VISIBLE);
		markerBubblePickMeUp.setVisibility(View.VISIBLE);
		drawer.setVisibility(View.VISIBLE);
	}

	public void setmarkerInvisibile() {
		// markers.setVisibility(View.INVISIBLE);
		markerBubblePickMeUp.setVisibility(View.INVISIBLE);
		drawer.setVisibility(View.INVISIBLE);
	}

	public void setpickpopupVisible() {
		MainDrawerActivity.popon = true;
		pickuppop.setVisibility(View.VISIBLE);
		btnadddestination.setVisibility(View.VISIBLE);
	}

	public static void setpickpopupInvisible() {
		MainDrawerActivity.popon = false;
		pickuppop.setVisibility(View.INVISIBLE);
		btnadddestination.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.markerBubblePickMeUp:
			if (isValidate()) {
				new AlertDialog.Builder(getActivity())
						.setTitle("")
						.setMessage("Cash Only Accepted")
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int which) {
										setmarkerInvisibile();
										setpickpopupVisible();
										gettime();
										try {
											btnRequestCap.setText("Request "
													+ listType.get(selectedPostion).getName());
										} catch (Exception e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								})
						.setNegativeButton(android.R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int which) {
										dialog.cancel();
									}
								})
						.setIcon(android.R.drawable.ic_dialog_alert)
						.show();
			}

			// getCards(); modified by amal
			/*
			 * if (isValidate()) { requestCaps(); //modified by amal }
			 */
			break;
		case R.id.btnSelectService:
			if (drawer.isOpened()) {
				drawer.animateClose();
				drawer.unlock();
			} else {
				drawer.animateOpen();
				drawer.lock();
			}
			break;

		case R.id.btn_request_cap:
//			payment_type = 3; // PayGate
//			Log.d("amal", Integer.toString(payment_type));
			btnRequestCap.setEnabled(false);
			btnRequestCap.postDelayed(new Runnable() {
				@Override
				public void run() {
					btnRequestCap.setEnabled(true);
				}
			}, 3000);
//			if (payment_type == -1) {
//				new AlertDialog.Builder(getActivity())
//						.setTitle("No Payment option selected")
//						.setMessage(
//								"Please select any given payment option to request a ride")
//						.setPositiveButton(android.R.string.ok,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int which) {
//										// continue with delete
//									}
//								}).setIcon(android.R.drawable.ic_dialog_alert)
//						.show();
//			}
//			if (payment_type == 0)
//				getCards();
			if (/*payment_type == 1
					|| payment_type == 2
					|| payment_type == 3
					&& */!TextUtils.isEmpty(enterdestination.getText().toString())) {
				Log.d("amal", "going to pick up");
				isRequestCapNow = true;
				getDistance();
			} else if (destaddlayout.getVisibility() == View.VISIBLE
					&& TextUtils.isEmpty(enterdestination.getText().toString())) {
				Toast.makeText(getActivity(), "Enter destination address",
						Toast.LENGTH_LONG).show();

			} else {
				Toast.makeText(getActivity(), "Enter destination address",
						Toast.LENGTH_LONG).show();
				destaddlayout.setVisibility(View.VISIBLE);
				destaddlayout.startAnimation(slidedown);
			}
			break;
//		case R.id.btnpayment:
//			selectPayment();
//			break;
		case R.id.btnAdddestination:
			destaddlayout.setVisibility(View.VISIBLE);
			destaddlayout.startAnimation(slidedown);
			break;
		case R.id.btnfareestimate:
			if (enterdestination.getVisibility() == View.VISIBLE
					&& !TextUtils.isEmpty(enterdestination.getText().toString())) {
				isRequestCapNow = false;
				getDistance();
			} else if (destaddlayout.getVisibility() == View.VISIBLE
					&& TextUtils.isEmpty(enterdestination.getText().toString())) {
				Toast.makeText(getActivity(), "Enter destination address",
						Toast.LENGTH_LONG).show();
			} else {
				destaddlayout.setVisibility(View.VISIBLE);
				destaddlayout.startAnimation(slidedown);
				Toast.makeText(getActivity(), "Enter destination address",
						Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.btnratecard:
			// showpopup();
			/*
			 * final Dialog mDialog = new Dialog(getActivity(),
			 * R.style.MyFareestimateDialog);
			 * 
			 * // mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			 * mDialog.getWindow().setBackgroundDrawable( new
			 * ColorDrawable(android.graphics.Color.TRANSPARENT));
			 * mDialog.setContentView(R.layout.ratecard);
			 * mDialog.setTitle("Rate Card");
			 */

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflate = getActivity().getLayoutInflater();
			View contentview = inflate.inflate(R.layout.ratecard, null);
			View titleview = inflate.inflate(R.layout.ratecardcustomtitle, null);
			builder.setView(contentview).setCustomTitle(titleview);
			final AlertDialog mDialog = builder.create();

			MyFontTextView ratecardtitle;
			TextView baseprice, distanceprice, timeprice;
			baseprice = (TextView) contentview.findViewById(R.id.Basepricefield);
			distanceprice = (TextView) contentview.findViewById(R.id.Distancepricefield);
			timeprice = (TextView) contentview.findViewById(R.id.Timepricefield);
			ratecardtitle = (MyFontTextView) titleview.findViewById(R.id.ratecardtitle);

			baseprice.setText(listType.get(selectedPostion).getCurrency() + " "
					+ listType.get(selectedPostion).getBasePrice());
			distanceprice.setText(listType.get(selectedPostion).getCurrency()
					+ " "
					+ listType.get(selectedPostion).getPricePerUnitDistance()
					+ "/" + listType.get(selectedPostion).getUnit());
			timeprice.setText(listType.get(selectedPostion).getCurrency() + " "
					+ listType.get(selectedPostion).getPricePerUnitTime()
					+ "/min");
			ratecardtitle.setText(listType.get(selectedPostion).getName());
			mDialog.show();
			Log.d("amal", Integer.toString(selectedPostion));
			break;
		case R.id.btnpromocode:
			ApplyPromo();
			break;

		case R.id.clearfield:
			if (TextUtils.isEmpty(enterdestination.getText().toString())) {
				destaddlayout.startAnimation(slideup);
				destaddlayout.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(enterdestination.getText().toString()))
				enterdestination.setText("");
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void showOptions() {
		// TODO Auto-generated method stub
		Log.d("pavan", "in show option");
		listViewType.setVisibility(View.GONE);
		drawer.setVisibility(View.GONE);
	}

	private void getAddressFromLocation(final LatLng latlng, final EditText et) {
		et.setText("Waiting for Address");
		et.setTextColor(Color.GRAY);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Geocoder gCoder = new Geocoder(getActivity());
				try {
					final List<Address> list = gCoder.getFromLocation(
							latlng.latitude, latlng.longitude, 1);
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
					}
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							if (!TextUtils.isEmpty(strAddress)) {
								et.setFocusable(false);
								et.setFocusableInTouchMode(false);
								et.setText(strAddress);
								et.setTextColor(getResources().getColor(android.R.color.black));
								et.setFocusable(true);
								et.setFocusableInTouchMode(true);
							} else {
								et.setText("");
								et.setTextColor(getResources().getColor(android.R.color.black));
							}
							etSource.setEnabled(true);
						}
					});
				} catch (IOException exc) {
					exc.printStackTrace();
					getAddressFromGooleApi(latlng);
				}
			}

		}).start();
	}

	private void animateCameraToMarker(LatLng latLng) {
		etSource.setFocusable(false);
		etSource.setFocusableInTouchMode(false);
		CameraUpdate cameraUpdate = null;

		cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, Const.MAP_ZOOM);
		map.animateCamera(cameraUpdate);
		etSource.setFocusable(true);
		etSource.setFocusableInTouchMode(true);
	}

	private LatLng getLocationFromAddress(final String place) {
		Log.d("amal", place);
		LatLng loc = null;
		Geocoder gCoder = new Geocoder(getActivity());
		try {
			final List<Address> list = gCoder.getFromLocationName(place, 1);
			// TODO Auto-generated method stub
			if (list != null && list.size() > 0) {
				loc = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
			}
		} catch (IOException e) {
			getlocfromaddressfromGoogleApi(place);
			if (destlatlng_places != null)
				loc = destlatlng_places;
			e.printStackTrace();
		}

		return loc;
	}

	@Override
	public void onProgressCancel() {
		stopCheckingStatusUpdate();
		cancleRequest();
		// stopCheckingStatusUpdate();
	}

	private void getAddressFromGooleApi(LatLng latlong) {
		// call google api here
		AndyUtils.removeCustomProgressDialog();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GOOGLE_LOCATION + latlong.latitude
				+ "," + latlong.longitude);
		AppLog.Log("pavan", Const.URL);
		new HttpRequester(activity, map, Const.ServiceCode.GET_ADDRESS, true, this);
	}

	private void getlocfromaddressfromGoogleApi(String address) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GOOGLE_ADDRESS + address);
		AppLog.Log("pavan", Const.URL);
		new HttpRequester(activity, map, Const.ServiceCode.GET_LOCATION, true, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.fragments.BaseFragment#isValidate()
	 */
	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		String msg = null;
		if (curretLatLng == null) {
			msg = getString(R.string.text_location_not_found);
		} else if (selectedPostion == -1) {
			msg = getString(R.string.text_select_type);
		} else if (TextUtils.isEmpty(etSource.getText().toString())
				|| etSource.getText().toString().equalsIgnoreCase("Waiting for Address")) {
			msg = getString(R.string.text_waiting_for_address);
		}
		if (msg == null)
			return true;
		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
		return false;
	}

	private void paydebt() {
		Log.d("amal", "in here");
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet), activity);
			return;
		}
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_creating_request), true, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.PAY_DEBT);
		map.put(Const.Params.TOKEN, PreferenceHelper.getInstance(activity).getSessionToken());
		map.put(Const.Params.ID, PreferenceHelper.getInstance(activity).getUserId());
		new HttpRequester(activity, map, Const.ServiceCode.PAY_DEBT, this);
	}

	private void requestCaps() {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet), activity);
			return;
		}
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_creating_request), true, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.CREATE_REQUEST);
		map.put(Const.Params.TOKEN, PreferenceHelper.getInstance(activity).getSessionToken());
		map.put(Const.Params.ID, PreferenceHelper.getInstance(activity).getUserId());
		map.put(Const.Params.LATITUDE, String.valueOf(curretLatLng.latitude));
		map.put(Const.Params.LONGITUDE, String.valueOf(curretLatLng.longitude));
		map.put(Const.Params.TYPE, String.valueOf(listType.get(selectedPostion).getId()));
		if (enterdestination.getVisibility() == View.VISIBLE
				&& !TextUtils.isEmpty(enterdestination.getText().toString())) {
			LatLng destlatlng = getLocationFromAddress(enterdestination
					.getText().toString());
			if (destlatlng != null) {
				map.put(Const.Params.DEST_LATITUDE, String.valueOf(destlatlng.latitude));
				map.put(Const.Params.DEST_LONGITUDE, String.valueOf(destlatlng.longitude));
			} else {
				AndyUtils.removeCustomProgressDialog();
				Toast.makeText(activity, "Please Enter The Address Correctly",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		if (promopref.getString("promocode", "") != "") {
			map.put(Const.Params.PROMO_CODE,
					promopref.getString("promocode", ""));
		}
//		map.put(Const.Params.COD, String.valueOf(payment_type));
		map.put(Const.Params.DISTANCE, "1");
		Log.d("xxx", "map " + map.toString());
		new HttpRequester(activity, map, Const.ServiceCode.CREATE_REQUEST, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.fragments.BaseFragment#onTaskCompleted(java.lang.String,
	 * int)
	 */
	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		super.onTaskCompleted(response, serviceCode);

		String distance = "";
		String duration = "";
		// AndyUtils.removeCustomProgressDialog();
		switch (serviceCode) {
		case Const.ServiceCode.CREATE_REQUEST:
			Log.d("amal", response);
			AndyUtils.removeCustomProgressDialog();
			if (activity.pContent.isSuccess(response)) {
				editorpromo.putString("promocode", "");
				editorpromo.commit();
				// AndyUtils.removeCustomProgressDialog();
				PreferenceHelper.getInstance(activity).putRequestId(activity.pContent
						.getRequestId(response));
				AndyUtils.showCustomProgressDialog(activity,
						getString(R.string.text_contacting), false, this);
				startCheckingStatusUpdate();
			} else {
				if (activity.pContent.getErrorCode(response) == 417) {
					String errormsg = "";
					try {
						JSONObject obj = new JSONObject(response);
						errormsg = obj.getString("error");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					AlertDialog.Builder debtalert = new AlertDialog.Builder(activity);
					debtalert
							.setTitle("Could not request ride")
							.setMessage(errormsg)
							.setPositiveButton("Pay",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											paydebt_indicator = 1;
											getCards();
										}

									});
					debtalert.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							});
					debtalert.show();
				}
			}
			break;
		case Const.ServiceCode.GET_REQUEST_STATUS:
			if (activity.pContent.isSuccess(response)) {
				switch (activity.pContent.checkRequestStatus(response)) {
				case Const.IS_WALK_STARTED:
				case Const.IS_WALKER_ARRIVED:
				case Const.IS_COMPLETED:
				case Const.IS_WALKER_STARTED:
					AndyUtils.removeCustomProgressDialog();
					stopCheckingStatusUpdate();
					Driver driver = activity.pContent.getDriverDetail(response);
					if (btnadddestination.getVisibility() == View.VISIBLE) {
						btnadddestination.setVisibility(View.GONE);
					}
					removeThisFragment();
					activity.gotoTripFragment(driver);
					break;
				case Const.IS_WALKER_RATED:
					stopCheckingStatusUpdate();
					activity.gotoRateFragment(activity.pContent
							.getDriverDetail(response));
					break;

				case Const.IS_REQEUST_CREATED:
					if (PreferenceHelper.getInstance(activity).getRequestId() != Const.NO_REQUEST)
						AndyUtils.showCustomProgressDialog(activity,
								getString(R.string.text_contacting), false, this);
					isContinueRequest = true;
					break;
				case Const.NO_REQUEST:
					if (!isGettingVehicalType) {
						AndyUtils.removeCustomProgressDialog();
					}
					stopCheckingStatusUpdate();
					break;
				default:
					isContinueRequest = false;
					break;
				}
				Log.d("mahi", "response" + response);
			} else if (activity.pContent.getErrorCode(response) == Const.REQUEST_ID_NOT_FOUND) {
				AndyUtils.removeCustomProgressDialog();
				PreferenceHelper.getInstance(activity).clearRequestData();
				isContinueRequest = false;
			} else if (activity.pContent.getErrorCode(response) == Const.REQUEST_CANCEL) {
				AndyUtils.removeCustomProgressDialog();
				PreferenceHelper.getInstance(activity).clearRequestData();
				isContinueRequest = false;
			}

			else if (activity.pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
				if (PreferenceHelper.getInstance(activity).getLoginBy()
						.equalsIgnoreCase(Const.MANUAL))
					login();
				else
					loginSocial(PreferenceHelper.getInstance(activity).getUserId(),
							PreferenceHelper.getInstance(activity).getLoginBy());
			}

			else {
				isContinueRequest = true;
			}
			break;
		case Const.ServiceCode.LOGIN:
			if (activity.pContent.isSuccessWithStoreId(response)) {

			}
			break;
		case Const.ServiceCode.CANCEL_REQUEST:
			Log.d("mahi", "response in cancel request" + response);
			if (activity.pContent.isSuccess(response)) {

			}
			PreferenceHelper.getInstance(activity).clearRequestData();
			AndyUtils.removeCustomProgressDialog();
			break;
		case Const.ServiceCode.GET_VEHICAL_TYPES:

			if (activity.pContent.isSuccess(response)) {
				listType.clear();
				activity.pContent.parseTypes(response, listType);
				if (listType.size() > 0) {
					if (listType != null && listType.get(0) != null)
						listType.get(0).isSelected = true;
					typeAdapter.notifyDataSetChanged();
				}
			}
			AndyUtils.removeCustomProgressDialog();
			break;
		/* added by amal */
		case Const.ServiceCode.GET_CARDS:
			Log.d("amal", "GET CARD" + response);
			if (pContent.isSuccess(response)) {
				ArrayList<Card> listCards;
				listCards = new ArrayList<Card>();
				listCards.clear();
				pContent.parseCards(response, listCards);
				if (listCards.size() > 0) {
					if (paydebt_indicator == 1) {
						paydebt();
						paydebt_indicator = 0;
					} else
						requestCaps();
				}
				adapter.notifyDataSetChanged();
			} else {
				Log.d("yyy", "in else of 0 card");
				startActivity(new Intent(getActivity(),
						UberViewPaymentActivity.class));
			}
			AndyUtils.removeCustomProgressDialog();
			break;

		case Const.ServiceCode.PAY_DEBT:
			Log.d("amal", "pay debt" + response);
			if (activity.pContent.isSuccess(response)) {
				Toast.makeText(activity, "Debt cleared successfully",
						Toast.LENGTH_LONG).show();
			} else
				Toast.makeText(activity, "Debt not cleared successfully",
						Toast.LENGTH_LONG).show();
			AndyUtils.removeCustomProgressDialog();
			break;
		case Const.ServiceCode.GET_MAP_DETAILS:
			AndyUtils.removeCustomProgressDialog();
			Log.d("amal", "in distance success" + response);
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
								JSONObject jobj_distance = jobj1.getJSONObject("distance");
								Log.d("amal",
										"distance "
												+ jobj_distance.getString("text")
												+ " , "
												+ jobj_distance.getString("value"));

								JSONObject jobj_duration = jobj1.getJSONObject("duration");
								Log.d("amal",
										"distance "
												+ jobj_duration.getString("text")
												+ " , "
												+ jobj_duration.getString("value"));

								duration = jobj_duration.getString("value");
								distance = jobj_distance.getString("value");
								getEstimation(distance, duration);
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

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
								JSONObject jobj_distance = jobj1.getJSONObject("distance");
								JSONObject jobj_duration = jobj1.getJSONObject("duration");
								duration = jobj_duration.getString("text");
								eta.setText("Pick up time is approximately "
										+ duration);
							}
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			AndyUtils.removeCustomProgressDialog();
			break;

		case Const.ServiceCode.FARE_CALCULATOR:
			AndyUtils.removeCustomProgressDialog();
			Log.d("amal", "in farecalc success" + response);
			if (response != null) {
				try {
					JSONObject jObject = new JSONObject(response);
					if (jObject.getString("success").equals("true")) {
						if(isRequestCapNow) {
							new AlertDialog.Builder(getActivity())
									.setTitle("")
									.setMessage(
											"Estimated Fare = " + jObject.getString("currency") + " " + jObject.getString("estimated_fare"))
									.setPositiveButton(android.R.string.ok,
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,
																	int which) {
													requestCaps();
												}
											})
									.setNegativeButton(android.R.string.cancel,
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface dialog,
																	int which) {
													dialog.cancel();
												}
											})
									.setIcon(android.R.drawable.ic_dialog_alert)
									.show();
						} else {
							ShowFare(jObject.getString("estimated_fare"),
									jObject.getString("currency"));
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			break;
		case Const.ServiceCode.GETPROVIDER_ALL:
			Log.d("amal", response);
			if (activity.pContent.isSuccess(response)) {
				walkerlist.clear();
				try {
					JSONObject jsonobject = new JSONObject(response);
					JSONArray jsonarr = jsonobject.getJSONArray("walkers");
					for (int i = 0; i < jsonarr.length(); i++) {
						JSONObject obj = jsonarr.getJSONObject(i);
						Walkerinfo walkerinfo = new Walkerinfo();
						walkerinfo.setId(obj.getString("id"));
						walkerinfo.setLatitude(obj.getString("latitude"));
						walkerinfo.setLongitude(obj.getString("longitude"));
						walkerinfo.setTime_cost(obj.getString("time_cost"));
						walkerinfo.setDistance_cost(obj
								.getString("distance_cost"));
						walkerinfo.setType(obj.getString("type"));
						walkerinfo.setDistance(obj.getString("distance"));
						walkerinfo.setBase_price(obj.getString("base_price"));
						Log.d("amal", obj.getString("base_price"));
						walkerlist.add(walkerinfo);

						int flag = 0;
						if (walkerarrayformarker.size() == 0) {
							Marker m = map
									.addMarker(new MarkerOptions()
											.position(
													new LatLng(
															Double.parseDouble(walkerinfo
																	.getLatitude()),
															Double.parseDouble(walkerinfo
																	.getLongitude())))
											.icon(BitmapDescriptorFactory
													.fromResource(R.drawable.pin_driver_car)));
							walkerinfo_marker mwalkerinfo_marker = new walkerinfo_marker(
									walkerinfo, m);
							walkerarrayformarker.add(mwalkerinfo_marker);
						}

						for (int k = 0; k < walkerarrayformarker.size(); k++) {
							Log.d("hey", "going to for loop");

							Log.d("hey",
									String.valueOf(walkerarrayformarker.get(k)
											.getWalkerlistclone().getId()));
							Log.d("hey", String.valueOf(walkerinfo.getId()));
							if (TextUtils.equals(
									String.valueOf(walkerarrayformarker.get(k)
											.getWalkerlistclone().getId()),
									String.valueOf(walkerinfo.getId()))) {
								flag++;
								if (walkerarrayformarker.get(k)
										.getWalkerlistclone().getLatitude() != walkerinfo
										.getLatitude()
										|| walkerarrayformarker.get(k)
												.getWalkerlistclone()
												.getLongitude() != walkerinfo
												.getLongitude()) {

									walkerarrayformarker.get(k)
											.getDrivermarkers().remove();
									walkerarrayformarker.get(k)
											.setWalkerlistclone(walkerinfo);
									Marker m = map
											.addMarker(new MarkerOptions()
													.position(
															new LatLng(
																	Double.parseDouble(walkerinfo
																			.getLatitude()),
																	Double.parseDouble(walkerinfo
																			.getLongitude())))
													.icon(BitmapDescriptorFactory
															.fromResource(R.drawable.pin_driver_car)));
									walkerarrayformarker.get(k)
											.setDrivermarkers(m);
								}
							}
						}
						if (flag == 0) {
							Log.d("hey", "in flag");
							Marker m = map
									.addMarker(new MarkerOptions()
											.position(
													new LatLng(
															Double.parseDouble(walkerinfo
																	.getLatitude()),
															Double.parseDouble(walkerinfo
																	.getLongitude())))
											.icon(BitmapDescriptorFactory
													.fromResource(R.drawable.pin_driver_car)));
							walkerinfo_marker mwalkerinfo_marker = new walkerinfo_marker(
									walkerinfo, m);
							walkerarrayformarker.add(mwalkerinfo_marker);

						}

					}

					for (int s = 0; s < walkerarrayformarker.size(); s++) {
						int flag1 = 0;
						for (int r = 0; r < walkerlist.size(); r++) {
							if (TextUtils.equals(walkerarrayformarker.get(s)
									.getWalkerlistclone().getId(), walkerlist
									.get(r).getId())) {
								flag1++;
							}

						}
						if (flag1 == 0) {
							walkerarrayformarker.get(s).getDrivermarkers()
									.remove();
							walkerarrayformarker.remove(s);
						}

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// markthewalkers();

			} else {
				removemarkers();
				walkerlist.clear();

			}
			if (pickuppop.getVisibility() == View.VISIBLE) {
				gettime();
			}
			AndyUtils.removeCustomProgressDialog();
			break;

		case Const.ServiceCode.GET_PROMO_REQUEST:
			if (activity.pContent.isSuccess(response)) {
				String discount = "";

				try {
					JSONObject obj = new JSONObject(response);
					discount = obj.getString("discount");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				promoresponse.setText("You get " + discount
						+ " off in your current ride");
				editorpromo.putString("promocode", promoglobal);
				editorpromo.commit();
				Toast.makeText(activity, "PromoCode entered successfully",
						Toast.LENGTH_LONG).show();
				promoCodeDlg.cancel();
			}

			AndyUtils.removeCustomProgressDialog();
			break;

		case Const.ServiceCode.GET_ADDRESS:

			AndyUtils.removeCustomProgressDialog();

			try {
				Log.d("pavan", "map response" + response);
				JSONObject jobj = new JSONObject(response);

				if (jobj.get("status").equals("OK")) {

					Log.d("pavan", "in okay");

					JSONArray jarray = jobj.getJSONArray("results");

					if (jarray.length() > 0) {

						JSONObject jobj1 = jarray.getJSONObject(0);

						strAddress = jobj1.getString("formatted_address");
						strAddress = strAddress.replace(",null", "");
						strAddress = strAddress.replace("null", "");
						strAddress = strAddress.replace("Unnamed", "");

						getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (!TextUtils.isEmpty(strAddress)) {
									etSource.setFocusable(false);
									etSource.setFocusableInTouchMode(false);
									etSource.setText(strAddress);
									etSource.setTextColor(getResources()
											.getColor(android.R.color.black));
									etSource.setFocusable(true);
									etSource.setFocusableInTouchMode(true);

								} else {
									etSource.setText("");
									etSource.setTextColor(getResources()
											.getColor(android.R.color.black));
								}
								etSource.setEnabled(true);
							}
						});
					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case Const.ServiceCode.GET_LOCATION:

			try {
				JSONObject jsonobject = new JSONObject(response);

				if (jsonobject.get("status").equals("OK")) {
					JSONArray jarray = jsonobject.getJSONArray("results");

					if (jarray.length() > 0) {

						JSONObject jobj1 = jarray.getJSONObject(0);
						JSONObject jobj2 = jobj1.getJSONObject("geometry");
						JSONObject jobj3 = jobj2.getJSONObject("location");
						destlatlng_places = new LatLng(jobj3.getDouble("lat"),
								jobj3.getDouble("lng"));

					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
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
			if (isContinueRequest) {
				isContinueRequest = false;
				getRequestStatus(String
						.valueOf(PreferenceHelper.getInstance(activity).getRequestId()));
			}
		}

	}

	private void getRequestStatus(String requestId) {

		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_REQUEST_STATUS + Const.Params.ID + "="
						+ PreferenceHelper.getInstance(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ PreferenceHelper.getInstance(activity).getSessionToken()
						+ "&" + Const.Params.REQUEST_ID + "=" + requestId);

		AppLog.Log("GET REQUEST STATUS",
				Const.ServiceType.GET_REQUEST_STATUS + Const.Params.ID + "="
						+ PreferenceHelper.getInstance(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ PreferenceHelper.getInstance(activity).getSessionToken()
						+ "&" + Const.Params.REQUEST_ID + "=" + requestId);

		new HttpRequester(activity, map, Const.ServiceCode.GET_REQUEST_STATUS,
				true, this);
	}

	private void startCheckingStatusUpdate() {
		stopCheckingStatusUpdate();
		if (PreferenceHelper.getInstance(activity).getRequestId() != Const.NO_REQUEST) {
			isContinueRequest = true;
			timer = new Timer();
			timer.scheduleAtFixedRate(new TimerRequestStatus(), Const.DELAY,
					Const.TIME_SCHEDULE);
		}
	}

	private void stopCheckingStatusUpdate() {
		isContinueRequest = false;
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private void cancleRequest() {
		Log.d("mahi", "in cancel request");
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

	class WalkerStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String response = intent.getStringExtra(Const.EXTRA_WALKER_STATUS);
			if (TextUtils.isEmpty(response))
				return;
			stopCheckingStatusUpdate();

			if (activity.pContent.isSuccess(response)) {
				switch (activity.pContent.checkRequestStatus(response)) {
				case Const.IS_WALK_STARTED:
				case Const.IS_WALKER_ARRIVED:
				case Const.IS_COMPLETED:
				case Const.IS_WALKER_STARTED:
					AndyUtils.removeCustomProgressDialog();
					// stopCheckingStatusUpdate();
					Driver driver = activity.pContent.getDriverDetail(response);
					Log.d("amal", "driver detail  --->" + driver.toString());
					if (btnadddestination.getVisibility() == View.VISIBLE) {
						btnadddestination.setVisibility(View.GONE);
					}
					removeThisFragment();

					activity.gotoTripFragment(driver);
					break;
				case Const.IS_WALKER_RATED:
					// stopCheckingStatusUpdate();
					activity.gotoRateFragment(activity.pContent
							.getDriverDetail(response));
					break;

				case Const.IS_REQEUST_CREATED:
					AndyUtils.showCustomProgressDialog(activity,
							getString(R.string.text_contacting), false,
							UberMapFragment.this);
					startCheckingStatusUpdate();
					isContinueRequest = true;
					break;
				default:
					isContinueRequest = false;
					break;
				}

			} else if (activity.pContent.getErrorCode(response) == Const.REQUEST_ID_NOT_FOUND) {
				AndyUtils.removeCustomProgressDialog();
				PreferenceHelper.getInstance(activity).clearRequestData();
				isContinueRequest = false;
			} else if (activity.pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
				if (PreferenceHelper.getInstance(activity).getLoginBy()
						.equalsIgnoreCase(Const.MANUAL))
					login();
				else
					loginSocial(PreferenceHelper.getInstance(activity).getUserId(),
							PreferenceHelper.getInstance(activity).getLoginBy());
			} else {
				isContinueRequest = true;
				startCheckingStatusUpdate();
			}
			// startCheckingStatusUpdate();

		}
	}

	private void removeThisFragment() {
		try {
			getActivity().getSupportFragmentManager().beginTransaction()
					.remove(this).commitAllowingStateLoss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getVehicalTypes() {
		isGettingVehicalType = true;
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_VEHICAL_TYPES);
		AppLog.Log(Const.TAG, Const.URL);
		new HttpRequester(activity, map, Const.ServiceCode.GET_VEHICAL_TYPES,
				true, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	public void onItemClick(int pos) {
		selectedPostion = pos;

	}

	/*
	 * Added by Amal
	 */
	private void getCards() {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		AndyUtils.showCustomProgressDialog(getActivity(),
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_CARDS + Const.Params.ID + "="
				+ PreferenceHelper.getInstance(activity).getUserId() + "&"
				+ Const.Params.TOKEN + "="
				+ PreferenceHelper.getInstance(activity).getSessionToken());
		new HttpRequester(getActivity(), map, Const.ServiceCode.GET_CARDS,
				true, this);
	}

	private void getDistance() {

		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		AndyUtils.showCustomProgressDialog(getActivity(),
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();

		LatLng deslatlng = getLocationFromAddress(enterdestination.getText()
				.toString());
		if (deslatlng != null) {
			map.put(Const.URL,
					Const.ServiceType.GOOGLE_MAP_API + Const.Params.MAP_ORIGINS
							+ "=" + String.valueOf(curretLatLng.latitude) + ","
							+ String.valueOf(curretLatLng.longitude) + "&"
							+ Const.Params.MAP_DESTINATIONS + "="
							+ String.valueOf(deslatlng.latitude) + ","
							+ String.valueOf(deslatlng.longitude));

			Log.d("amal", "request from getdistance " + map);
			new HttpRequester(activity, map, Const.ServiceCode.GET_MAP_DETAILS,
					true, this);
		} else {
			AndyUtils.removeCustomProgressDialog();
			Toast.makeText(activity, "Please Enter The Address Correctly",
					Toast.LENGTH_LONG).show();
			return;
		}

	}

	TextView promoresponse;

	private void ApplyPromo() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflate = getActivity().getLayoutInflater();
		View contentview = inflate.inflate(R.layout.promocard, null);
		View titleview = inflate.inflate(R.layout.promocodecustomtitle, null);
		builder.setView(contentview).setCustomTitle(titleview);
		promoCodeDlg = builder.create();
		promoCodeDlg.show();
		MyFontButton apply, cancel;

		final MyFontEdittextView promofield;
		apply = (MyFontButton) contentview.findViewById(R.id.promoApply);
		cancel = (MyFontButton) contentview.findViewById(R.id.promoCancel);
		promofield = (MyFontEdittextView) contentview.findViewById(R.id.promo);
		promoresponse = (TextView) contentview.findViewById(R.id.promoresponse);

		apply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendpromo(promofield.getText().toString());

			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				promoCodeDlg.dismiss();

			}
		});

	}

	void sendpromo(String promostring) {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		promoglobal = promostring;
		AndyUtils.showCustomProgressDialog(getActivity(),
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_PROMO_REQUEST + Const.Params.ID + "="
						+ PreferenceHelper.getInstance(activity).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ PreferenceHelper.getInstance(activity).getSessionToken()
						+ "&" + Const.Params.PROMO_CODE + "=" + promostring);

		new HttpRequester(activity, map, Const.ServiceCode.GET_PROMO_REQUEST,
				true, this);
	}

	private void gettime() {

		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		if (walkerlist.isEmpty()) {
			eta.setText("No Providers Nearby");
		} else {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Const.URL,
					Const.ServiceType.GOOGLE_MAP_API + Const.Params.MAP_ORIGINS
							+ "=" + String.valueOf(curretLatLng.latitude) + ","
							+ String.valueOf(curretLatLng.longitude) + "&"
							+ Const.Params.MAP_DESTINATIONS + "="
							+ walkerlist.get(0).getLatitude() + ","
							+ walkerlist.get(0).getLongitude());

			Log.d("amal", "request " + map);
			new HttpRequester(activity, map, Const.ServiceCode.GET_MAP_TIME,
					true, this);
		}
	}

	private void getEstimation(String distance, String duration) {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					activity);
			return;
		}

		AndyUtils.showCustomProgressDialog(getActivity(),
				getString(R.string.progress_loading), false, null);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.FARE_CALCULATOR);
		map.put(Const.Params.TOKEN,
				PreferenceHelper.getInstance(activity).getSessionToken());
		map.put(Const.Params.ID,
				PreferenceHelper.getInstance(activity).getUserId());
		map.put(Const.Params.TIME, duration);
		map.put(Const.Params.DISTANCE, distance);
		map.put(Const.Params.BASE_PRICE, listType.get(selectedPostion).getBasePrice());
		map.put(Const.Params.PRICE_PER_UNIT_DISTANCE, listType.get(selectedPostion).getPricePerUnitDistance());
		map.put(Const.Params.PRICE_PER_UNIT_TIME, listType.get(selectedPostion).getPricePerUnitTime());
		if (promopref.getString("promocode", "") != "") {
			map.put(Const.Params.PROMO_CODE, promopref.getString("promocode", ""));
		}

		// map.put(Const.Params.TYPE,
		// String.valueOf(listType.get(selectedPostion).getId()));
		//
		// map.put(Const.Params.DISTANCE, "1");
		Log.d("amal", "request " + map);
		new HttpRequester(activity, map, Const.ServiceCode.FARE_CALCULATOR,
				this);

		// Log.d("amal", "request " + map);

	}

	private void ShowFare(String fare, String currency) {
		Log.d("amal", "in show fare");
		final Dialog mDialog = new Dialog(getActivity(),
				R.style.MyFareestimateDialog);

		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.fareestimate);

		TextView fromaddress, toaddress, estimatedfare, servicename;
		MyFontButton fareclose;
		fromaddress = (TextView) mDialog.findViewById(R.id.fromaddress);
		toaddress = (TextView) mDialog.findViewById(R.id.toaddress);
		estimatedfare = (TextView) mDialog.findViewById(R.id.estimatedfare);
		servicename = (TextView) mDialog.findViewById(R.id.servicename);
		fareclose = (MyFontButton) mDialog.findViewById(R.id.faredialogclose);
		fromaddress.setText(etSource.getText().toString());
		toaddress.setText(enterdestination.getText().toString());
		estimatedfare.setText(currency + " " + fare);
		servicename.setText(listType.get(selectedPostion).getName());
		fareclose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mDialog.dismiss();

			}
		});
		mDialog.show();

	}

	private void getallproviders() {
		Log.d("amal", "in get all provider");
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}

		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(Const.URL, Const.ServiceType.GETPROVIDER_ALL);
			map.put(Const.Params.TOKEN,
					PreferenceHelper.getInstance(activity).getSessionToken());
			map.put(Const.Params.ID, PreferenceHelper.getInstance(activity).getUserId());
			map.put(Const.Params.LATITUDE,
					String.valueOf(curretLatLng.latitude));
			map.put(Const.Params.LONGITUDE,
					String.valueOf(curretLatLng.longitude));
			if(listType != null && listType.size() > 0) {
				map.put(Const.Params.TYPE,
						String.valueOf(listType.get(selectedPostion).getId()));
			} else {
				map.put(Const.Params.TYPE, "0");
			}
			map.put(Const.Params.DISTANCE, "1");
			new HttpRequester(activity, map, Const.ServiceCode.GETPROVIDER_ALL,
					this);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void removemarkers() {
		for (int a = 0; a < walkerarrayformarker.size(); a++) {
			walkerarrayformarker.get(a).getDrivermarkers().remove();
		}
		walkerarrayformarker.clear();
		/*
		 * for (int i = 0; i < drivermarkers.size(); i++) { if
		 * (drivermarkers.get(i) != null) drivermarkers.get(i).remove(); }
		 * drivermarkers.clear();
		 */
	}

	void markthewalkers() {

		for (int i = 0; i < walkerlist.size(); i++) {
			Marker m = map.addMarker(new MarkerOptions().position(
					new LatLng(Double.parseDouble(walkerlist.get(i)
							.getLatitude()), Double.parseDouble(walkerlist.get(
							i).getLongitude()))).icon(
					BitmapDescriptorFactory
							.fromResource(R.drawable.pin_driver_car)));
			drivermarkers.add(m);
		}

	}

	private class walkerinfo_marker {
		private Walkerinfo walkerlistclone;
		private Marker drivermarkers;

		public walkerinfo_marker(Walkerinfo walkerinfo, Marker m) {
			walkerlistclone = walkerinfo;
			drivermarkers = m;
		}

		public Walkerinfo getWalkerlistclone() {
			return walkerlistclone;
		}

		public void setWalkerlistclone(Walkerinfo walkerlistclone) {
			this.walkerlistclone = walkerlistclone;
		}

		public Marker getDrivermarkers() {
			return drivermarkers;
		}

		public void setDrivermarkers(Marker drivermarkers) {
			this.drivermarkers = drivermarkers;
		}

	}

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = getActivity().getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private ArrayList<walkerinfo_marker> walkerarrayformarker;

}
