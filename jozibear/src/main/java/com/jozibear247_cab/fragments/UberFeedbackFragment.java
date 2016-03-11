package com.jozibear247_cab.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.jozibear247_cab.R;
import com.jozibear247_cab.models.Driver;
import com.jozibear247_cab.parse.HttpRequester;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		driver = (Driver) getArguments().getParcelable(Const.DRIVER);

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

			if (driver.getBill().getPayment_mode().equals("3")) {
				if (!driver.getBill().getIsPaid().equals("1")) {
					activity.showBillDialog(driver.getBill().getTimeCost(),
							driver.getBill().getTotal(), driver.getBill()
									.getDistanceCost(), driver.getBill()
									.getBasePrice(),
							driver.getBill().getTime(), driver.getBill()
									.getDistance(), driver.getBill()
									.getCurrency(),
							getString(R.string.pay_with_paygate), driver
									.getBill().getPayment_mode(), driver
									.getBill().getPrimary_id(), driver
									.getBill().getSecoundry_id(), driver
									.getBill().getPrimary_amount(), driver
									.getBill().getSecoundry_amount(), driver
									.getBill().getActual_total(), driver
									.getBill().getIsPaid());
				}
			} else

			if (driver.getBill().getPayment_mode().equals("2")) {
				if (!driver.getBill().getIsPaid().equals("1")) {
					activity.showBillDialog(driver.getBill().getTimeCost(),
							driver.getBill().getTotal(), driver.getBill()
									.getDistanceCost(), driver.getBill()
									.getBasePrice(),
							driver.getBill().getTime(), driver.getBill()
									.getDistance(), driver.getBill()
									.getCurrency(),
							getString(R.string.pay_with_paypal), driver
									.getBill().getPayment_mode(), driver
									.getBill().getPrimary_id(), driver
									.getBill().getSecoundry_id(), driver
									.getBill().getPrimary_amount(), driver
									.getBill().getSecoundry_amount(), driver
									.getBill().getActual_total(), driver
									.getBill().getIsPaid());
				}
			} else

			{
				activity.showBillDialog(driver.getBill().getTimeCost(), driver
						.getBill().getTotal(), driver.getBill()
						.getDistanceCost(), driver.getBill().getBasePrice(),
						driver.getBill().getTime(), driver.getBill()
								.getDistance(), driver.getBill().getCurrency(),

						getString(R.string.text_confirm), driver.getBill()
								.getPayment_mode(), driver.getBill()
								.getPrimary_id(), driver.getBill()
								.getSecoundry_id(), driver.getBill()
								.getPrimary_amount(), driver.getBill()
								.getSecoundry_amount(), driver.getBill()
								.getActual_total(), driver.getBill()
								.getIsPaid());

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
			// if (isValidate()) {
			rating();
			/*
			 * } else AndyUtils.showToast( activity.getResources().getString(
			 * R.string.text_empty_feedback), activity);
			 */

			break;
		case R.id.btnSkip:
			PreferenceHelper.getInstance(activity).clearRequestData();
			activity.gotoMapFragment();
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
				AndyUtils.showToast(
						getString(R.string.text_feedback_completed), activity);
				activity.gotoMapFragment();
			}
			break;

		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.fragments.BaseFragmentRegister#OnBackPressed()
	 */

}
