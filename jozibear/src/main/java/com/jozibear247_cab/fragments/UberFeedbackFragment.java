package com.jozibear247_cab.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.jozibear247_cab.R;
import com.jozibear247_cab.models.Bill;
import com.jozibear247_cab.models.Driver;
import com.jozibear247_cab.parse.HttpRequester;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.AppLog;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * @author Hardik A Bhalodi
 */
public class UberFeedbackFragment extends UberBaseFragment {
	private EditText etComment;
	private RatingBar rtBar;
	private Button btnSubmit, btnSkip;
	private ImageView ivDriverImage;
	private Driver driver;
	private TextView tvDistance, tvTime, tvClientName;
	private Dialog m_invoiceDlg, m_webDlg;
	private PaymentResultReceiver paymentResultReceiver;
	private boolean m_bPayResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		driver = (Driver) getArguments().getParcelable(Const.DRIVER);
		IntentFilter filter = new IntentFilter(Const.INTENT_PAYMENT_RESULT);
		paymentResultReceiver = new PaymentResultReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				paymentResultReceiver, filter);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		activity.setTitle(getString(R.string.text_feedback));
		View view = inflater.inflate(R.layout.feedback, container, false);
		tvClientName = (TextView) view.findViewById(R.id.tvClientName);
		etComment = (EditText) view.findViewById(R.id.etComment);
		rtBar = (RatingBar) view.findViewById(R.id.ratingBar);
		btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
		btnSkip = (Button) view.findViewById(R.id.btnSkip);
		ivDriverImage = (ImageView) view.findViewById(R.id.ivDriverImage);
		tvDistance = (TextView) view.findViewById(R.id.tvDistance);
		tvTime = (TextView) view.findViewById(R.id.tvTime);
		// tvDistance.setText(driver.getLastDistance());
		tvDistance.setText(driver.getBill().getDistance() + " "
				+ driver.getBill().getUnit());
		tvTime.setText((int) (Double.parseDouble(driver.getBill().getTime()))
				+ " " + getString(R.string.text_mins));
		// tvTime.setText(driver.getLastTime());
		activity.btnNotification.setVisibility(View.GONE);
		btnSubmit.setOnClickListener(this);
		btnSkip.setOnClickListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if (driver != null) {
			new AQuery(activity).id(ivDriverImage).progress(R.id.pBar)
					.image(driver.getPicture());
			tvClientName.setText(driver.getFirstName() + " "
					+ driver.getLastName());

			Log.d("mahi", "is paid?" + driver.getBill().getIsPaid());
			// if (driver.getBill().getIsPaid().equals("1"))
			if (driver.getBill().getPayment_mode().equals("1")) { // Pay by Cash
				if (!driver.getBill().getIsPaid().equals("1")) {
					showBillDialog(driver.getBill());
				}
			} else if (driver.getBill().getPayment_mode().equals("2")) {
				if (!driver.getBill().getIsPaid().equals("1")) {
					showBillDialog(driver.getBill());
				}
			} else {
				showBillDialog(driver.getBill());
			}
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.d("mahi", "onResume");

		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnSubmit:
			rating();
			break;
		case R.id.btnSkip:
			PreferenceHelper.getInstance(activity).clearRequestData();
			if(m_bPayResult) {
				activity.gotoMapFragment();
			} else {
				AndyUtils.showToast(
						getString(R.string.text_account_blocked), activity);
				PreferenceHelper.getInstance(activity).Logout();
				activity.goToMainActivity();
			}
		default:
			break;
		}
	}

	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(etComment.getText().toString()))

			return false;

		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(paymentResultReceiver);
	}

	private void rating() {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_rating), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.RATING);
		map.put(Const.Params.TOKEN, PreferenceHelper.getInstance(activity).getSessionToken());
		map.put(Const.Params.ID, PreferenceHelper.getInstance(activity).getUserId());
		map.put(Const.Params.COMMENT, etComment.getText().toString());
		map.put(Const.Params.RATING, String.valueOf(((int) rtBar.getRating())));
		map.put(Const.Params.REQUEST_ID,
				String.valueOf(PreferenceHelper.getInstance(activity).getRequestId()));
		new HttpRequester(activity, map, Const.ServiceCode.RATING, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uberorg.fragments.UberBaseFragment#onTaskCompleted(java.lang.String,
	 * int)
	 */
	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		switch (serviceCode) {
		case Const.ServiceCode.RATING:
			AndyUtils.removeCustomProgressDialog();
			if (activity.pContent.isSuccess(response)) {
				PreferenceHelper.getInstance(activity).clearRequestData();
				if(m_bPayResult) {
					AndyUtils.showToast(
							getString(R.string.text_feedback_completed), activity);
					activity.gotoMapFragment();
				} else {
					AndyUtils.showToast(
							getString(R.string.text_account_blocked), activity);
					PreferenceHelper.getInstance(activity).Logout();
					activity.goToMainActivity();
				}
			}
			break;
//		case Const.ServiceCode.FINISH_PAYMENT:
//			AndyUtils.removeCustomProgressDialog();
//			break;
		}
	}

	public void showBillDialog(final Bill bill) {
//		initLibrary();
		Log.d("Bill", "primary_amount:" + bill.getPrimary_amount() +
				", secoundry_amount:" + bill.getSecoundry_amount() + ", Payment mode:" + bill.getPayment_mode());

		m_invoiceDlg = new Dialog(activity,
				android.R.style.Theme_Translucent_NoTitleBar);
		m_invoiceDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		m_invoiceDlg.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		m_invoiceDlg.setContentView(R.layout.bill_layout);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		DecimalFormat perHourFormat = new DecimalFormat("0.0");
//		String basePricetmp = String.valueOf(decimalFormat.format(Double
//				.parseDouble(basePrice)));
		String totalTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(bill.getTotal())));
		String distCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(bill.getDistanceCost())));
		String timeCostTmp = String.valueOf(decimalFormat.format(Double
				.parseDouble(bill.getTimeCost())));

		String actualtotal = String.valueOf(decimalFormat.format(bill.getActual_total()));
		double yousave=Math.abs(bill.getActual_total()-Double.parseDouble(bill.getTotal()));

		AppLog.Log("Distance:", bill.getDistance() + ", Time:" + bill.getTime());

		((TextView) m_invoiceDlg.findViewById(R.id.tvBasePrice)).setText(bill.getCurrency()
				+ " " + bill.getBasePrice());
		if (bill.getDistance().equals("0.00") || bill.getDistance().equals("0")) {
			((TextView) m_invoiceDlg.findViewById(R.id.tvBillDistancePerMile))
					.setText(bill.getCurrency()
							+ "0 "
							+ getResources().getString(R.string.text_cost_per_mile));
		} else
			((TextView) m_invoiceDlg.findViewById(R.id.tvBillDistancePerMile))
					.setText(bill.getCurrency()
							+ String.valueOf(perHourFormat.format((Double
							.parseDouble(bill.getDistanceCost()) / Double
							.parseDouble(bill.getDistance()))))
							+ " "
							+ getResources().getString(R.string.text_cost_per_mile));

		if (bill.getTime().equals("0.00") || bill.getTime().equals("0")) {
			((TextView) m_invoiceDlg.findViewById(R.id.tvBillTimePerHour))
					.setText(bill.getCurrency()
							+ "0 "
							+ getResources().getString(R.string.text_cost_per_min));
		} else
			((TextView) m_invoiceDlg.findViewById(R.id.tvBillTimePerHour))
					.setText(bill.getCurrency()
							+ String.valueOf(perHourFormat.format((Double
							.parseDouble(bill.getTimeCost()) / Double
							.parseDouble(bill.getTime()))))
							+ " "
							+ getResources().getString(R.string.text_cost_per_min));
		((TextView) m_invoiceDlg.findViewById(R.id.tvDis1)).setText(bill.getCurrency() + " "
				+ distCostTmp);
		((TextView) m_invoiceDlg.findViewById(R.id.tvTime1)).setText(bill.getCurrency() + " "
				+ timeCostTmp);
		((TextView) m_invoiceDlg.findViewById(R.id.tvTotal1)).setText(bill.getCurrency() + " "
				+ totalTmp);
		((TextView) m_invoiceDlg.findViewById(R.id.tvtotalcostvalue))
				.setText(bill.getCurrency() + " " + actualtotal);
		((TextView) m_invoiceDlg.findViewById(R.id.tvyousavevalue)).setText(bill.getCurrency()
				+ " " + String.valueOf(decimalFormat.format(yousave)));

		Button btnConfirm = (Button) m_invoiceDlg.findViewById(R.id.btnBillDialogClose);
		if (bill.getPayment_mode().equals("1")) {
			btnConfirm.setText("PAY BY CASH NOW");
		} else {
			btnConfirm.setText("PAY BY CARD/EFT NOW");
		}
		btnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bill.getPayment_mode().equals("1")) { // Pay by Cash
					sendPayByCash();
					m_invoiceDlg.dismiss();
				} else if (bill.getPayment_mode().equals("2")) { // Pay by Card/EFT
					// if confirm button press show paygate webview here
//					Intent i= new Intent(activity, PayGateWebView.class);
//					startActivity(i);
//					activity.finish();
					showBillWebDialog();
					m_invoiceDlg.dismiss();

				} else {
					m_invoiceDlg.dismiss();
				}
			}
		});

		m_invoiceDlg.setCancelable(false);
		m_invoiceDlg.show();
	}

	public void showBillWebDialog() {
//		initLibrary();
//		Log.d("Bill", "primary_amount:" + bill.getPrimary_amount() +
//				", secoundry_amount:" + bill.getSecoundry_amount() + ", Payment mode:" + bill.getPayment_mode());

		WebView mWebView = new WebView(activity);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(Const.ServiceType.HOST_URL + "user/paygate/" +
				PreferenceHelper.getInstance(activity).getRequestId());
		mWebView.setWebViewClient(new WebViewClient());
		m_webDlg = new Dialog(activity);
		m_webDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		m_webDlg.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		m_webDlg.setContentView(mWebView);
		m_webDlg.setCancelable(false);
		m_webDlg.show();
	}

	private void sendPayByCash() {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_waiting_finish_payment), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.SEND_CASH_RESPONSE);
		map.put(Const.Params.TOKEN, PreferenceHelper.getInstance(activity).getSessionToken());
		map.put(Const.Params.ID, PreferenceHelper.getInstance(activity).getUserId());
		map.put(Const.Params.REQUEST_ID,
				String.valueOf(PreferenceHelper.getInstance(activity).getRequestId()));
		// map.put(Const.Params.DISTANCE, "0");
		new HttpRequester(activity, map, Const.ServiceCode.UPDATE_PAYMENT, this);
	}

	class PaymentResultReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String response = intent.getStringExtra(Const.EXTRA_PAYMENT_RESULT);
			Log.d("hey", response);
			AppLog.Log("Response ---- Trip", response);
			if (TextUtils.isEmpty(response))
				return;

			if (activity.pContent.checkPaymentResult(response)) {
				m_bPayResult = true;
			}
			else {
				m_bPayResult = false;
			}
			if(m_invoiceDlg != null && m_invoiceDlg.isShowing())
				m_invoiceDlg.dismiss();
			if(m_webDlg != null && m_webDlg.isShowing())
				m_webDlg.dismiss();
			AndyUtils.removeCustomProgressDialog();
		}
	}

}
