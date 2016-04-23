package com.jozibear247_cab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.jozibear247_cab.adapter.DrawerAdapter;
import com.jozibear247_cab.component.MyFontPopUpTextView;
import com.jozibear247_cab.db.DBHelper;
import com.jozibear247_cab.fragments.UberFeedbackFragment;
import com.jozibear247_cab.fragments.UberMapFragment;
import com.jozibear247_cab.fragments.UberTripFragment;
import com.jozibear247_cab.models.ApplicationPages;
import com.jozibear247_cab.models.Driver;
import com.jozibear247_cab.models.Referral;
import com.jozibear247_cab.models.User;
import com.jozibear247_cab.parse.HttpRequester;
import com.jozibear247_cab.parse.ParseContent;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.AppLog;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;
import com.splunk.mint.Mint;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Hardik A Bhalodi
 */
public class MainDrawerActivity extends ActionBarBaseActivitiy {
	private DrawerAdapter adapter;
	MenuDrawer mMenuDrawer;
	// DrawerLayout drawerLayout;
	private ListView listDrawer;
	// private ActionBarDrawerToggle drawerToggel;
//	public PreferenceHelper pHelper;
	public ParseContent pContent;
	private ArrayList<ApplicationPages> listMenu;
	private boolean isDataRecieved = false, isRecieverRegistered = false,
			isNetDialogShowing = false, isGpsDialogShowing = false;
	private AlertDialog internetDialog, gpsAlertDialog, locationAlertDialog;
	private DBHelper dbHelper;
	private AQuery aQuery;
	private LocationManager manager;
	private EditText edit_referal_code;
	private ImageView ivMenuProfile;
	private MyFontPopUpTextView tvMenuName;
	private ImageOptions imageOptions;

//	private Driver driverInfo;
	private String payment_mode[] = { "Pay By Cash", "Pay By Card/EFT" };

	public static boolean popon = false;
	private boolean doubleBackToExitPressedOnce;
	private Context context=this;
	private UberTripFragment tripFrag = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dbHelper = new DBHelper(getApplicationContext());
		User user = dbHelper.getUser();
		aQuery = new AQuery(this);
		Mint.initAndStartSession(MainDrawerActivity.this, "b18b0b2f"); 

		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_WINDOW);
		mMenuDrawer.setContentView(R.layout.activity_map);
		mMenuDrawer.setMenuView(R.layout.menu_drawer);
		mMenuDrawer.setDropShadowEnabled(false);

		btnActionMenu.setVisibility(View.VISIBLE);
		setIcon(R.drawable.notification_box);

		imageOptions = new ImageOptions();
		imageOptions.memCache = true;
		imageOptions.fileCache = true;
		imageOptions.targetWidth = 200;
		imageOptions.fallback = R.drawable.default_user;

		// setContentView(R.layout.activity_map);
//		pHelper = new PreferenceHelper(this);
		pContent = new ParseContent(this);

		// drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		listDrawer = (ListView) findViewById(R.id.left_drawer);
		listMenu = new ArrayList<ApplicationPages>();
		adapter = new DrawerAdapter(this, listMenu);
		listDrawer.setAdapter(adapter);

		ivMenuProfile = (ImageView) findViewById(R.id.ivMenuProfile);
		aQuery.id(ivMenuProfile).progress(R.id.pBar)
				.image(user.getPicture(), imageOptions);
		tvMenuName = (MyFontPopUpTextView) findViewById(R.id.tvMenuName);
		tvMenuName.setText(user.getFname() + " " + user.getLname());

		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// drawerToggel = new ActionBarDrawerToggle(this, drawerLayout,
		// R.drawable.slide_btn, 0, 0);
		// drawerLayout.setDrawerListener(drawerToggel);
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setHomeButtonEnabled(true);
		// actionBar.setHomeAsUpIndicator(R.drawable.slide_btn);

		listDrawer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {
				// drawerLayout.closeDrawer(listDrawer);
				mMenuDrawer.closeMenu();
				if (position == 0) {
					startActivity(new Intent(MainDrawerActivity.this,
							ProfileActivity.class));
				} else if (position == 1) {
					startActivity(new Intent(MainDrawerActivity.this,
							UberViewPaymentActivity.class));
				} else if (position == 2) {
					startActivity(new Intent(MainDrawerActivity.this,
							HistoryActivity.class));
				} else if (position == 3) {
					getReferralCode();

				} else if (position == 4) {
					startActivity(new Intent(MainDrawerActivity.this,
							promotionActivity.class));
				} else if (position == (listMenu.size() - 1)) {

					new AlertDialog.Builder(MainDrawerActivity.this)
							.setTitle(getString(R.string.dialog_logout))
							.setMessage(getString(R.string.dialog_logout_text))
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											// continue with delete
											HashMap<String, String> map = new HashMap<String, String>();
											map.put(Const.URL, Const.ServiceType.LOGOUT);
											map.put(Const.Params.TOKEN,
													PreferenceHelper.getInstance(MainDrawerActivity.this).getSessionToken());
											map.put(Const.Params.ID,
													PreferenceHelper.getInstance(MainDrawerActivity.this).getUserId());
											new HttpRequester(context, map, Const.ServiceCode.LOGOUT, MainDrawerActivity.this);

											PreferenceHelper.getInstance(MainDrawerActivity.this).Logout();
											goToMainActivity();
										}
									})
							.setNegativeButton("CANCEL",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											// do nothing
											dialog.cancel();
										}
									})
									.setCancelable(false)
							.setIcon(android.R.drawable.ic_dialog_alert).show();
				} else {
					Intent intent = new Intent(MainDrawerActivity.this,
							MenuDescActivity.class);
					intent.putExtra(Const.Params.TITLE, listMenu.get(position)
							.getTitle());
					intent.putExtra(Const.Params.CONTENT, listMenu
							.get(position).getData());
					startActivity(intent);
				}

			}
		});
		// mMenuDrawer.peekDrawer();
		
	
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			ShowGpsDialog();
		} else {
			removeGpsDialog();
		}
		registerReceiver(internetConnectionReciever, new IntentFilter(
				"android.net.conn.CONNECTIVITY_CHANGE"));
		registerReceiver(GpsChangeReceiver, new IntentFilter(
				LocationManager.PROVIDERS_CHANGED_ACTION));
		isRecieverRegistered = true;
		if (AndyUtils.isNetworkAvailable(this)
				&& manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			if (!isDataRecieved) {
				isDataRecieved = true;
				checkStatus();
			}
		}

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		hideKeyboard();
		// if (drawerToggel.onOptionsItemSelected(item)) {
		// return true;
		// }
		switch (item.getItemId()) {
		case android.R.id.home:
			mMenuDrawer.toggleMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		// drawerToggel.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// drawerToggel.onConfigurationChanged(newConfig);
	}

	public void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View view = this.getCurrentFocus();
		if (view != null) {
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnActionMenu:
			hideKeyboard();
			mMenuDrawer.toggleMenu();
			break;
		case R.id.tvTitle:
			
			hideKeyboard();
			break;
		default:
			break;
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (popon == true) {
			UberMapFragment.setpickpopupInvisible();
			UberMapFragment.setmarkerVisibile();
			popon = false;
		}
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		// Toast.makeText(this, "Please click back again to exit",
		// Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);

		// super.onBackPressed();

	}

	private void getRequestInProgress() {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.REQUEST_IN_PROGRESS + Const.Params.ID + "="
						+ PreferenceHelper.getInstance(this).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ PreferenceHelper.getInstance(this).getSessionToken());
		new HttpRequester(this, map, Const.ServiceCode.GET_REQUEST_IN_PROGRESS,
				true, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.ActionBarBaseActivitiy#onTaskCompleted(java.lang.String,
	 * int)
	 */
	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		super.onTaskCompleted(response, serviceCode);

		switch (serviceCode) {
		case Const.ServiceCode.GET_REQUEST_IN_PROGRESS:
			AndyUtils.removeCustomProgressDialog();
			if (pContent.isSuccess(response)) {
				if (pContent.getRequestInProgress(response) == Const.NO_REQUEST) {
					AndyUtils.removeCustomProgressDialog();
					gotoMapFragment();
				} else {
					PreferenceHelper.getInstance(this).putRequestId(pContent.getRequestId(response));
					getRequestStatus(String.valueOf(PreferenceHelper.getInstance(this).getRequestId()));
				}
			} else if (pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
				Log.d("yyy", "invalid token");
				if (PreferenceHelper.getInstance(this).getLoginBy().equalsIgnoreCase(Const.MANUAL))
					login();
				else
					loginSocial(PreferenceHelper.getInstance(this).getUserId(), PreferenceHelper.getInstance(this).getLoginBy());
			} else if (pContent.getErrorCode(response) == Const.REQUEST_ID_NOT_FOUND) {
				AndyUtils.removeCustomProgressDialog();
				PreferenceHelper.getInstance(this).clearRequestData();
				gotoMapFragment();
			}
			
			getMenuItems();
			break;
		case Const.ServiceCode.GET_REQUEST_STATUS:
			AndyUtils.removeCustomProgressDialog();

			if (pContent.isSuccess(response)) {
				switch (pContent.checkRequestStatus(response)) {
				case Const.IS_WALK_STARTED:
				case Const.IS_WALKER_ARRIVED:
				case Const.IS_COMPLETED:
				case Const.IS_WALKER_STARTED:
					Driver driver = pContent.getDriverDetail(response);
					gotoTripFragment(driver);
					break;

				case Const.IS_WALKER_RATED:
					gotoRateFragment(pContent.getDriverDetail(response));
					break;
				default:
					gotoMapFragment();
					break;
				}

			} else if (pContent.getErrorCode(response) == Const.INVALID_TOKEN) {
				login();
			} else if (pContent.getErrorCode(response) == Const.REQUEST_ID_NOT_FOUND) {
				PreferenceHelper.getInstance(this).clearRequestData();
				gotoMapFragment();
			}
			getMenuItems();
			break;
		case Const.ServiceCode.LOGIN:
			if (pContent.isSuccessWithStoreId(response)) {
				checkStatus();
			}
			break;
		case Const.ServiceCode.GET_PAGES:
			listMenu.clear();
			pContent.parsePages(listMenu, response);
			ApplicationPages applicationPages = new ApplicationPages();
			applicationPages.setData("");
			applicationPages.setId(-3);
			applicationPages.setTitle(getString(R.string.dialog_logout));
			listMenu.add(applicationPages);
			adapter.notifyDataSetChanged();

			break;
		case Const.ServiceCode.GET_REFERREL:
			if (pContent.isSuccess(response)) {
				Referral ref = pContent.parseReffrelCode(response);
				if (ref != null) {
					showReferralDialog(ref.getReferralCode());
				}

			} else {
				showReferralDialog("");

			}
			AndyUtils.removeCustomProgressDialog();

			break;

		case Const.ServiceCode.UPDATE_REFFRAL_CODE:

			AndyUtils.removeCustomProgressDialog();
			Log.d("yyy", "kumar " + response);
			getReferralCode();
			if (pContent.isSuccess(response)) {
				
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(
								R.string.toast_update_referal_success),
						Toast.LENGTH_LONG).show();
			}

			break;

		}

	}

	private void getRequestStatus(String requestId) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_REQUEST_STATUS
				+ Const.Params.ID + "=" + PreferenceHelper.getInstance(this).getUserId() + "&"
				+ Const.Params.TOKEN + "=" + PreferenceHelper.getInstance(this).getSessionToken() + "&"
				+ Const.Params.REQUEST_ID + "=" + requestId);

		new HttpRequester(this, map, Const.ServiceCode.GET_REQUEST_STATUS,
				true, this);
	}

	public void gotoMapFragment() {
		UberMapFragment frag = UberMapFragment.newInstance();
		addFragment(frag, false, Const.FRAGMENT_MAP);
	}

	public void gotoTripFragment(Driver driver) {
//		if(tripFrag == null) {
			tripFrag = new UberTripFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(Const.DRIVER, driver);
			tripFrag.setArguments(bundle);
			addFragment(tripFrag, false, Const.FRAGMENT_TRIP);
//		} else {
//			tripFrag.onResume();
//		}
	}

	public void gotoRateFragment(final Driver driver) {
		try {
			if (TextUtils.isEmpty(driver.getLastTime()))
				driver.setLastTime(0 + " " + getString(R.string.text_mins));
			if (TextUtils.isEmpty(driver.getLastDistance()))
				driver.setLastDistance(0.0 + " " + getString(R.string.text_kms));
//			driver.getBill().setPayment_mode("1");
			UberFeedbackFragment feedBack = new UberFeedbackFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(Const.DRIVER, driver);
			feedBack.setArguments(bundle);
			addFragmentWithStateLoss(feedBack, false, Const.FRAGMENT_FEEDBACK);

//			List<String> paymentoption = new ArrayList<String>();
//			paymentoption.add(payment_mode[0]); // Pay By Cash
//			paymentoption.add(payment_mode[1]); // Pay By Card/EFT
//			final CharSequence[] paymentoptions = paymentoption
//					.toArray(new CharSequence[paymentoption.size()]);
//
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("Payment Mode");
//			builder.setItems(paymentoptions,
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface optiondialog, int which) {
//							Log.d("mahi", "payment type"
//									+ paymentoptions[which].toString());
//							if (paymentoptions[which].toString().equals(
//									payment_mode[0])) { // Pay By Cash
//								driver.getBill().setPayment_mode("1");
//							} else if (paymentoptions[which].toString()
//									.equals(payment_mode[1])) { // Pay By Card/EFT(PayGate)
//								driver.getBill().setPayment_mode("2");
//							}
//							UberFeedbackFragment feedBack = new UberFeedbackFragment();
//							Bundle bundle = new Bundle();
//							bundle.putParcelable(Const.DRIVER, driver);
//							feedBack.setArguments(bundle);
//							addFragmentWithStateLoss(feedBack, false, Const.FRAGMENT_FEEDBACK);
//						}
//					});
//			builder.setNegativeButton("Cancel",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface arg0, int arg1) {
//							arg0.dismiss();
//						}
//					});
//			AlertDialog alert = builder.create();
//			alert.setCancelable(true);
//			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void login() {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					this);
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.LOGIN);
		map.put(Const.Params.EMAIL, PreferenceHelper.getInstance(this).getEmail());
		map.put(Const.Params.PASSWORD, PreferenceHelper.getInstance(this).getPassword());
		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
		map.put(Const.Params.DEVICE_TOKEN, PreferenceHelper.getInstance(this).getDeviceToken());
		map.put(Const.Params.LOGIN_BY, Const.MANUAL);
		new HttpRequester(this, map, Const.ServiceCode.LOGIN, this);

	}

	private void checkStatus() {
		AndyUtils.showCustomProgressDialog(this,
				getString(R.string.text_gettting_request_stat), false, null);
		if (PreferenceHelper.getInstance(this).getRequestId() == Const.NO_REQUEST) {
			getRequestInProgress();
		} else {
			getRequestStatus(String.valueOf(PreferenceHelper.getInstance(this).getRequestId()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.ActionBarBaseActivitiy#isValidate()
	 */
	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		return false;
	}

	private void loginSocial(String id, String loginType) {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this,
				getResources().getString(R.string.text_signin), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.LOGIN);
		map.put(Const.Params.SOCIAL_UNIQUE_ID, id);
		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
		map.put(Const.Params.DEVICE_TOKEN,
				PreferenceHelper.getInstance(this).getDeviceToken());
		map.put(Const.Params.LOGIN_BY, loginType);
		new HttpRequester(this, map, Const.ServiceCode.LOGIN, this);

	}

	private void getMenuItems() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_PAGES);
		AppLog.Log(Const.TAG, Const.URL);

		new HttpRequester(this, map, Const.ServiceCode.GET_PAGES, true, this);
	}

	private void getMenuItemsDetail(String id) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_PAGES_DETAIL);
		new HttpRequester(this, map, Const.ServiceCode.GET_PAGES_DETAILS, true,
				this);
	}

	public BroadcastReceiver GpsChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			final LocationManager manager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				// do something
				removeGpsDialog();
			} else {
				// do something else
				if (isGpsDialogShowing) {
					return;
				}
				ShowGpsDialog();
			}

		}
	};
	public BroadcastReceiver internetConnectionReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo activeWIFIInfo = connectivityManager
					.getNetworkInfo(connectivityManager.TYPE_WIFI);

			if (activeWIFIInfo.isConnected() || activeNetInfo.isConnected()) {
				removeInternetDialog();
			} else {
				if (isNetDialogShowing) {
					return;
				}
				showInternetDialog();
			}
		}
	};

	private void ShowGpsDialog() {
		AndyUtils.removeCustomProgressDialog();
		isGpsDialogShowing = true;
		AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(
				MainDrawerActivity.this);
		gpsBuilder.setCancelable(false);
		gpsBuilder
				.setTitle(getString(R.string.dialog_no_gps))
				.setMessage(getString(R.string.dialog_no_gps_messgae))
				.setPositiveButton(getString(R.string.dialog_enable_gps),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
								Intent intent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(intent);
								removeGpsDialog();
							}
						})

				.setNegativeButton(getString(R.string.dialog_exit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								removeGpsDialog();
								finish();
							}
						});
		gpsAlertDialog = gpsBuilder.create();
		gpsAlertDialog.show();
	}

	public void showLocationOffDialog() {

		AlertDialog.Builder gpsBuilder = new AlertDialog.Builder(
				MainDrawerActivity.this);
		gpsBuilder.setCancelable(false);
		gpsBuilder
				.setTitle(getString(R.string.dialog_no_location_service_title))
				.setMessage(getString(R.string.dialog_no_location_service))
				.setPositiveButton(
						getString(R.string.dialog_enable_location_service),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
								dialog.dismiss();
								Intent viewIntent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(viewIntent);

							}
						})

				.setNegativeButton(getString(R.string.dialog_exit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								dialog.dismiss();
								finish();
							}
						});
		locationAlertDialog = gpsBuilder.create();
		locationAlertDialog.show();
	}

	private void removeLocationoffDialog() {
		if (locationAlertDialog != null && locationAlertDialog.isShowing()) {
			locationAlertDialog.dismiss();
			locationAlertDialog = null;
		}
	}

	private void removeGpsDialog() {
		if (gpsAlertDialog != null && gpsAlertDialog.isShowing()) {
			gpsAlertDialog.dismiss();
			isGpsDialogShowing = false;
			gpsAlertDialog = null;
		}
	}

	private void removeInternetDialog() {
		if (internetDialog != null && internetDialog.isShowing()) {
			internetDialog.dismiss();
			isNetDialogShowing = false;
			internetDialog = null;
		}
	}

	private void showInternetDialog() {
		AndyUtils.removeCustomProgressDialog();
		isNetDialogShowing = true;
		AlertDialog.Builder internetBuilder = new AlertDialog.Builder(
				MainDrawerActivity.this);
		internetBuilder.setCancelable(false);
		internetBuilder
				.setTitle(getString(R.string.dialog_no_internet))
				.setMessage(getString(R.string.dialog_no_inter_message))
				.setPositiveButton(getString(R.string.dialog_enable_3g),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// continue with delete
								Intent intent = new Intent(
										android.provider.Settings.ACTION_SETTINGS);
								startActivity(intent);
								removeInternetDialog();
							}
						})
				.setNeutralButton(getString(R.string.dialog_enable_wifi),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// User pressed Cancel button. Write
								// Logic Here
								startActivity(new Intent(
										Settings.ACTION_WIFI_SETTINGS));
								removeInternetDialog();
							}
						})
				.setNegativeButton(getString(R.string.dialog_exit),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// do nothing
								removeInternetDialog();
								finish();
							}
						});
		internetDialog = internetBuilder.create();
		internetDialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (isRecieverRegistered) {
			unregisterReceiver(internetConnectionReciever);
			unregisterReceiver(GpsChangeReceiver);
		}
	}

	private void showReferralDialog(final String refCode) {
		final Dialog mDialog = new Dialog(this, R.style.MyDialog);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		mDialog.getWindow().setBackgroundDrawable(
//				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.ref_code_layout);

		// TextView tvTitle = (TextView) mDialog.findViewById(R.id.tvTitle);
		// tvTitle.setText(getString(R.string.text_ref_code) + refCode);

		 edit_referal_code = (EditText) mDialog
				.findViewById(R.id.edit_referal_code);
		edit_referal_code.setText(refCode);
		final TextView blinkingtext = (TextView) mDialog.findViewById(R.id.blinkingtext);

		if (TextUtils.isEmpty(edit_referal_code.getText().toString())) {
			blinkingtext.setVisibility(View.VISIBLE);
			Animation anim = new AlphaAnimation(0.0f, 1.0f);
			anim.setDuration(500);
			anim.setStartOffset(20);
			anim.setRepeatMode(Animation.REVERSE);
			anim.setRepeatCount(Animation.INFINITE);
			blinkingtext.startAnimation(anim);
		}

		Button btnCancel = (Button) mDialog.findViewById(R.id.btnCancel);
		ImageView btnShare = (ImageView) mDialog.findViewById(R.id.imageView_share);

		btnShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(edit_referal_code.getText().toString())){
					String update_refCode = edit_referal_code.getText().toString();
					Intent sharingIntent = new Intent(Intent.ACTION_SEND);
					sharingIntent.setType("text/html");
					sharingIntent.putExtra(Intent.EXTRA_HTML_TEXT,
							"Use my promo code, "+ update_refCode
									+ ", and get exciting offers "
									+ System.getProperty("line.separator")
									+ " https://play.google.com/store/apps/details?id=com.jozibear247_cab");
					startActivity(Intent.createChooser(sharingIntent,
							"Share Referral Code"));
				} else {
					Toast.makeText(context, "Please add a Referral Code", Toast.LENGTH_LONG).show();
				}
			}
		});

		Button btnupdate = (Button) mDialog.findViewById(R.id.btnupdate);
		btnupdate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

		String update_refCode = edit_referal_code.getText().toString();
		UpdateReferralCode(update_refCode);
		if(blinkingtext.getVisibility() == View.VISIBLE)
			mDialog.dismiss();
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

		mDialog.show();
	}

	private void getReferralCode() {
		AndyUtils.showCustomProgressDialog(this,
				getString(R.string.text_getting_ref_code), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_REFERRAL + Const.Params.ID
				+ "=" + PreferenceHelper.getInstance(this).getUserId() + "&" + Const.Params.TOKEN + "="
				+ PreferenceHelper.getInstance(this).getSessionToken());

		new HttpRequester(this, map, Const.ServiceCode.GET_REFERREL, true, this);
	}

	private void UpdateReferralCode(String code) {
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet), this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this,
				getResources().getString(R.string.text_referral_code), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.GET_REFERRAL);
		map.put(Const.Params.ID, PreferenceHelper.getInstance(this).getUserId());
		map.put(Const.Params.TOKEN, PreferenceHelper.getInstance(this).getSessionToken());
		if (TextUtils.isEmpty(edit_referal_code.getText().toString())) {
			map.put(Const.Params.REFERRAL_CODE, " ");
		} else
			map.put(Const.Params.REFERRAL_CODE, code);
		Log.d("pavan", "Request  " + map);

		new HttpRequester(this, map, Const.ServiceCode.UPDATE_REFFRAL_CODE, false, this);
	}

}
