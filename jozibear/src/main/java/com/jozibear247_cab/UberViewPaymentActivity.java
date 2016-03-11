/**
 * 
 */
package com.jozibear247_cab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jozibear247_cab.adapter.PaymentListAdapter;
import com.jozibear247_cab.models.Card;
import com.jozibear247_cab.parse.HttpRequester;
import com.jozibear247_cab.parse.ParseContent;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Hardik A Bhalodi
 * 
 */
public class UberViewPaymentActivity extends ActionBarBaseActivitiy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.ActionBarBaseActivitiy#onCreate(android.os.Bundle)
	 */
	private ListView listViewPayment;
	private PaymentListAdapter adapter;
	private ArrayList<Card> listCards;
	private int REQUEST_ADD_CARD = 1;
	private LinearLayout tvNoHistory;
	private TextView tvHeaderText;
	private View v;
	private ImageView btnAddNewPayment;
	private ParseContent pContent;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.activity_view_payment);

		setTitle(getString(R.string.text_cards));
		setIcon(R.drawable.back);
		setIconMenu(R.drawable.ic_payment);
		pContent = new ParseContent(this);
		listViewPayment = (ListView) findViewById(R.id.listViewPayment);
		tvNoHistory = (LinearLayout) findViewById(R.id.llEmptyView);
		tvHeaderText = (TextView) findViewById(R.id.tvHeaderText);
		btnAddNewPayment = (ImageView) findViewById(R.id.btnAddNewPayment);
		btnAddNewPayment.setOnClickListener(this);
		v = findViewById(R.id.line);
		v.setVisibility(View.GONE);
		listCards = new ArrayList<Card>();
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setHomeButtonEnabled(true);
		// actionBar.setTitle(getString(R.string.text_cards));
		adapter = new PaymentListAdapter(this, listCards);
		listViewPayment.setAdapter(adapter);

		listViewPayment
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int pos, long arg3) {
						final Card cardsingle = (Card) listViewPayment
								.getItemAtPosition(pos);
					
						new AlertDialog.Builder(context)
								.setTitle("Delete entry")
								.setMessage(
										"Are you sure you want to delete card *****"+cardsingle.getLastFour()
												+ "?")
								.setPositiveButton(android.R.string.yes,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												removecards(cardsingle
														.getId());
											}
										})
								.setNegativeButton(android.R.string.no,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												// do nothing
											}
										})

								.show();

						return true;
					}

				});

		getCards();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnActionNotification:
			onBackPressed();
			break;
		case R.id.btnAddNewPayment:
			startActivityForResult(new Intent(this,
					UberAddPaymentActivity.class), REQUEST_ADD_CARD);
			break;
		default:
			break;
		}
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

	private void removecards(int cardid) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.REMOVE_CARD);
		map.put(Const.Params.ID, PreferenceHelper.getInstance(this).getUserId());
		map.put(Const.Params.TOKEN,
				PreferenceHelper.getInstance(this).getSessionToken());
		map.put(Const.Params.CARD_ID, String.valueOf(cardid));
		
Log.d("hey", "in remove card");
		new HttpRequester(this, map, Const.ServiceCode.REMOVE_CARD, this);
	}

	
	private void getCards() {
		AndyUtils.showCustomProgressDialog(this,
				getString(R.string.progress_loading), false, null);
		Log.d("user details", "ID=  " + PreferenceHelper.getInstance(this).getUserId()
				+ "TOKEN=  " + PreferenceHelper.getInstance(this).getSessionToken());

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_CARDS + Const.Params.ID + "="
						+ PreferenceHelper.getInstance(this).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ PreferenceHelper.getInstance(this).getSessionToken());
		new HttpRequester(this, map, Const.ServiceCode.GET_CARDS, true, this);
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
		AndyUtils.removeCustomProgressDialog();
		Log.d("user detail", response);
		switch (serviceCode) {
		case Const.ServiceCode.GET_CARDS:
			Log.d("hey", "get cards"+response);
			if (pContent.isSuccess(response)) {
				listCards.clear();
				pContent.parseCards(response, listCards);
				if (listCards.size() > 0) {
					listViewPayment.setVisibility(View.VISIBLE);
					tvNoHistory.setVisibility(View.GONE);
					v.setVisibility(View.VISIBLE);
					tvHeaderText.setVisibility(View.VISIBLE);
					btnAddNewPayment.setImageResource(R.drawable.another_card);
				} 
				adapter.notifyDataSetChanged();
			}else {
				listViewPayment.setVisibility(View.GONE);
				tvNoHistory.setVisibility(View.VISIBLE);
				tvHeaderText.setVisibility(View.GONE);
				v.setVisibility(View.GONE);
				btnAddNewPayment.setImageResource(R.drawable.add_credit);
			}
			break;
			
		case Const.ServiceCode.REMOVE_CARD:
			Log.d("hey", response);
			Log.d("hey", String.valueOf(pContent.isSuccess(response)));
			if(pContent.isSuccess(response)){
				Log.d("hey", "hmmm");
				getCards();
				Toast.makeText(context, "Card removed successfully", Toast.LENGTH_LONG).show();
			}
			break;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.ActionBarBaseActivitiy#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case Activity.RESULT_OK:
			getCards();
			break;

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

}
