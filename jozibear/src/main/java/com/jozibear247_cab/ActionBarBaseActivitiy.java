package com.jozibear247_cab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidquery.callback.ImageOptions;
import com.jozibear247_cab.component.MyTitleFontTextView;
import com.jozibear247_cab.fragments.UberBaseFragmentRegister;
import com.jozibear247_cab.models.History;
import com.jozibear247_cab.parse.AsyncTaskCompleteListener;
import com.jozibear247_cab.parse.HttpRequester;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author Hardik A Bhalodi
 */
@SuppressLint("NewApi")
abstract public class ActionBarBaseActivitiy extends ActionBarActivity
		implements OnClickListener, AsyncTaskCompleteListener {

	public ActionBar actionBar;
	private int mFragmentId = 0;
	private String mFragmentTag = null;

	public ImageButton btnNotification, btnActionMenu, btnadddestination;
	public MyTitleFontTextView tvTitle;
	public AutoCompleteTextView etSource;
	public String currentFragment = null;

//	public final int PAYPAL_RESPONSE = 100;

	protected abstract boolean isValidate();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		actionBar = getSupportActionBar();
		// Custom Action Bar
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		LayoutInflater inflater = (LayoutInflater) actionBar.getThemedContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View customActionBarView = inflater.inflate(R.layout.custom_action_bar,
				null);
		btnNotification = (ImageButton) customActionBarView
				.findViewById(R.id.btnActionNotification);
		btnNotification.setOnClickListener(this);
		/*btnadddestination = (ImageButton) customActionBarView
				.findViewById(R.id.btnAdddestination);
		btnadddestination.setOnClickListener(this);
		btnadddestination.setBackgroundResource(R.drawable.create_contact);
*/
		btnadddestination = (ImageButton) customActionBarView
				.findViewById(R.id.btnAdddestination);
		//btnadddestination.setOnClickListener(this);
		btnadddestination.setBackgroundResource(R.drawable.create_contact);

		tvTitle = (MyTitleFontTextView) customActionBarView
				.findViewById(R.id.tvTitle);
		tvTitle.setOnClickListener(this);

		etSource = (AutoCompleteTextView) customActionBarView
				.findViewById(R.id.etEnterSouce);

		btnActionMenu = (ImageButton) customActionBarView
				.findViewById(R.id.btnActionMenu);
		btnActionMenu.setOnClickListener(this);

		try {
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
					ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
							| ActionBar.DISPLAY_SHOW_TITLE);
			actionBar.setCustomView(customActionBarView,
					new ActionBar.LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.MATCH_PARENT));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setFbTag(String tag) {

		mFragmentId = 0;
		mFragmentTag = tag;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Fragment fragment = null;

		Log.d("pavan", "response");

//		if (requestCode == PAYPAL_RESPONSE) {
//
//			switch (resultCode) {
//			case Activity.RESULT_OK:
//				// The payment succeeded
//				String payKey = data
//						.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
//				Log.d("pavan", "success " + payKey);
//				sendPaypalData(payKey);
//				Toast.makeText(getApplicationContext(),
//						"Payment done succesfully ", Toast.LENGTH_LONG).show();
//
//				btnConfirm.setText(getString(R.string.text_close));
//				// Tell the user their payment succeeded
//				break;
//			case Activity.RESULT_CANCELED:
//				Toast.makeText(getApplicationContext(),
//						"Payment Canceled , Try again ", Toast.LENGTH_LONG)
//						.show();
//
//				break;
//			case PayPalActivity.RESULT_FAILURE:
//				Toast.makeText(getApplicationContext(),
//						"Payment failed , Try again ", Toast.LENGTH_LONG)
//						.show();
//
//				break;
//			}
//
//		} else {

			if (mFragmentId > 0) {
				fragment = getSupportFragmentManager().findFragmentById(
						mFragmentId);
			} else if (mFragmentTag != null
					&& !mFragmentTag.equalsIgnoreCase("")) {
				fragment = getSupportFragmentManager().findFragmentByTag(
						mFragmentTag);
			}
			if (fragment != null) {
				fragment.onActivityResult(requestCode, resultCode, data);
			}

//		}

	}

	public void startActivityForResult(Intent intent, int requestCode,
			int fragmentId) {

		mFragmentId = fragmentId;
		mFragmentTag = null;
		super.startActivityForResult(intent, requestCode);
	}

	public void startActivityForResult(Intent intent, int requestCode,
			String fragmentTag) {

		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startActivityForResult(intent, requestCode);
	}

	public void startActivityForResult(Intent intent, int requestCode,
			int fragmentId, Bundle options) {

		mFragmentId = fragmentId;
		mFragmentTag = null;
		super.startActivityForResult(intent, requestCode, options);
	}

	public void startActivityForResult(Intent intent, int requestCode,
			String fragmentTag, Bundle options) {

		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startActivityForResult(intent, requestCode, options);
	}

	public void startIntentSenderForResult(Intent intent, int requestCode,
			String fragmentTag, Bundle options) {

		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startActivityForResult(intent, requestCode, options);
	}

	@Override
	@Deprecated
	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags) throws SendIntentException {
		// TODO Auto-generated method stub
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags);
	}

	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags, String fragmentTag)
			throws SendIntentException {

		// TODO Auto-generated method stub
		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags);
	}

	@Override
	@Deprecated
	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags, Bundle options)
			throws SendIntentException {
		// TODO Auto-generated method stub
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags, options);
	}

	public void startIntentSenderForResult(IntentSender intent,
			int requestCode, Intent fillInIntent, int flagsMask,
			int flagsValues, int extraFlags, Bundle options, String fragmentTag)
			throws SendIntentException {

		// TODO Auto-generated method stub
		mFragmentTag = fragmentTag;
		mFragmentId = 0;
		super.startIntentSenderForResult(intent, requestCode, fillInIntent,
				flagsMask, flagsValues, extraFlags, options);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode,
			Bundle options) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode, options);
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub

		Log.d("pavan", "Response is " + response);
	}

	public void addFragment(Fragment fragment, boolean addToBackStack, String tag) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
				R.anim.slide_in_left, R.anim.slide_out_right);
		if (addToBackStack) {
			ft.addToBackStack(tag);
		}
		ft.replace(R.id.content_frame, fragment, tag);
		try {
			ft.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			addFragmentAllowingStateLoss(fragment, addToBackStack, tag);
			e.printStackTrace();
		}
	}
	
	public void addFragmentAllowingStateLoss(Fragment fragment, boolean addToBackStack,
			String tag) {

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
				R.anim.slide_in_left, R.anim.slide_out_right);
		if (addToBackStack) {

			ft.addToBackStack(tag);

		}
		ft.replace(R.id.content_frame, fragment, tag);
		ft.commitAllowingStateLoss();
	}

	public void addFragmentWithStateLoss(Fragment fragment,
			boolean addToBackStack, String tag) {

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();

		if (addToBackStack) {

			ft.addToBackStack(tag);

		}
		ft.replace(R.id.content_frame, fragment, tag);
		ft.commitAllowingStateLoss();
	}

	public void removeAllFragment(Fragment replaceFragment,
			boolean addToBackStack, String tag) {

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();

		manager.popBackStackImmediate(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);

		if (addToBackStack) {

			ft.addToBackStack(tag);
		}
		ft.replace(R.id.content_frame, replaceFragment);
		ft.commit();
	}

	public void clearBackStackImmidiate() {

		FragmentManager manager = getSupportFragmentManager();

		manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	public void clearBackStack() {
		FragmentManager manager = getSupportFragmentManager();
		if (manager.getBackStackEntryCount() > 0) {
			FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
			manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(currentFragment)) {
			FragmentManager manager = getSupportFragmentManager();
			UberBaseFragmentRegister frag = ((UberBaseFragmentRegister) manager
					.findFragmentByTag(currentFragment));

			if (frag != null && frag.isVisible())
				frag.OnBackPressed();
			else
				super.onBackPressed();
		} else {
			super.onBackPressed();
		}
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		default:
			break;
		}
		return true;
	}

	protected ImageOptions getAqueryOption() {
		ImageOptions options = new ImageOptions();
		options.targetWidth = 200;
		options.memCache = true;
		options.fallback = R.drawable.default_user;
		options.fileCache = true;
		return options;
	}

	Button btnConfirm;

	public void showHistoryBillDialog(History history) {
		initLibrary();

		final Dialog mDialog = new Dialog(this,
				android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.bill_layout);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		DecimalFormat perHourFormat = new DecimalFormat("0.0");
		//
//		String basePricetmp = String.valueOf(decimalFormat.format(Double
//				.parseDouble(basePrice)));
		String totalTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(history.getTotal())));
		String distCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(history.getDistanceCost())));
		String timeCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(history.getTimecost())));

		String actualtotal = String.valueOf(decimalFormat.format(history.getActual_total()));
		double yousave=Math.abs(history.getActual_total()-Double.parseDouble(history.getTotal()));

		((TextView) mDialog.findViewById(R.id.tvBasePrice)).setText(history.getCurrency() + " "
				+ String.valueOf(decimalFormat.format(Double
				.parseDouble(history.getBasePrice()))));
		((TextView) mDialog.findViewById(R.id.tvBillDistancePerMile))
				.setText(history.getCurrency() + " "
						+ String.valueOf(decimalFormat.format(Double
						.parseDouble(history.getPricePerUnitDistance())))
						+ " " + getResources().getString(R.string.text_cost_per_km));
		((TextView) mDialog.findViewById(R.id.tvBillTimePerHour))
				.setText(history.getCurrency() + " "
						+ String.valueOf(decimalFormat.format(Double
						.parseDouble(history.getPricePerUnitTime()))) + " "
						+ getResources().getString(R.string.text_cost_per_min));
		((TextView) mDialog.findViewById(R.id.tvDis1)).setText(history.getCurrency() + " "
				+ distCostTmp);

		((TextView) mDialog.findViewById(R.id.tvTime1)).setText(history.getCurrency() + " "
				+ timeCostTmp);

		((TextView) mDialog.findViewById(R.id.tvTotal1)).setText(history.getCurrency() + " "
				+ totalTmp);
		((TextView) mDialog.findViewById(R.id.tvtotalcostvalue))
				.setText(history.getCurrency() + " " + actualtotal);

		((TextView) mDialog.findViewById(R.id.tvyousavevalue)).setText(history.getCurrency()
				+ " "
				+ String.valueOf(decimalFormat.format(yousave)));

		btnConfirm = (Button) mDialog.findViewById(R.id.btnBillDialogClose);
		btnConfirm.setText("CLOSE");
		btnConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

		mDialog.setCancelable(false);
		mDialog.show();
	}

	public void setTitle(String str) {
		tvTitle.setText(str);
	}

	public void setIconMenu(int img) {
		btnActionMenu.setImageResource(img);
	}

	public void setIcon(int img) {
		btnNotification.setImageResource(img);
	}

	public void goToMainActivity() {
		Intent i = new Intent(this, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
		finish();
	}

	public void initLibrary() {
//		PayPal pp = PayPal.getInstance();
//		if (pp == null) {
//
//			pp = PayPal.initWithAppID(this, Const.PAYPAL_CLIENT_ID,
//					PayPal.ENV_SANDBOX);
//
//		}
	}

//	public void PayPalButtonClick(String primary_id, String primary_amount,
//			String secoundry_id, String secoundry_amount) {
//		// Create a basic PayPal payment
//
//		// PayPalPayment newPayment = new PayPalPayment();
//		// newPayment.setSubtotal(new BigDecimal("1.0"));
//		// newPayment.setCurrencyType("USD");
//		// newPayment.setRecipient("npavankumar34@gmail.com");
//		// newPayment.setMerchantName("My Company");
//		// Log.d("pavan", "calling intent");
//		// if( PayPal.getInstance()!=null){
//		// Log.d("pavan", "in if");
//		// Intent paypalIntent = PayPal.getInstance().checkout(newPayment,
//		// this);
//		// startActivityForResult(paypalIntent, 1);
//		//
//
//		Log.d("pavan", "primary " + primary_id);
//		Log.d("pavan", "primary_amount " + primary_amount);
//
//		Log.d("pavan", "secoundry_amount " + secoundry_amount);
//		Log.d("pavan", "secoundry_id " + secoundry_id);
//
//		PayPalReceiverDetails receiver0, receiver1;
//		receiver0 = new PayPalReceiverDetails();
//		receiver0.setRecipient(primary_id);
//		receiver0.setSubtotal(new BigDecimal(primary_amount));
//
//		receiver1 = new PayPalReceiverDetails();
//		receiver1.setRecipient(secoundry_id);
//		receiver1.setSubtotal(new BigDecimal(secoundry_amount));
//
//		PayPalAdvancedPayment advPayment = new PayPalAdvancedPayment();
//		advPayment.setCurrencyType(Const.CURRENCY_TYPE);
//
//		if (!primary_amount.equals("0"))
//			advPayment.getReceivers().add(receiver0);
//		if (!secoundry_amount.equals("0"))
//			advPayment.getReceivers().add(receiver1);
//		Intent paypalIntent = PayPal.getInstance().checkout(advPayment, this);
//		this.startActivityForResult(paypalIntent, PAYPAL_RESPONSE);
//
//	}

	private void sendPaypalData(String resonse) {
		if (!AndyUtils.isNetworkAvailable(getApplicationContext())) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					(Activity) getApplicationContext());
			return;
		}
		// AndyUtils.showCustomProgressDialog((ActionBarBaseActivitiy)getBaseContext(),
		// getString(R.string.text_contacting), true, this);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.SEND_PAYPAL_RESPONSE);
		map.put(Const.Params.TOKEN, PreferenceHelper.getInstance(
				getApplicationContext()).getSessionToken());
		map.put(Const.Params.ID,
				PreferenceHelper.getInstance(getApplicationContext()).getUserId());
		map.put(Const.Params.PAYPAL_DATA, resonse);
		map.put(Const.Params.REQUEST_ID, "1");
		// map.put(Const.Params.DISTANCE, "0");
		new HttpRequester(getApplicationContext(), map,
				Const.ServiceCode.UPDATE_PAYPAL_ID, this);
	}

}
