/**
 * 
 */
package com.jozibear247_cab;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

import com.hb.views.PinnedSectionListView;
import com.jozibear247_cab.adapter.HistoryAdapter;
import com.jozibear247_cab.models.History;
import com.jozibear247_cab.parse.HttpRequester;
import com.jozibear247_cab.parse.ParseContent;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.AppLog;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * @author Kishan H Dhamat
 * 
 */
public class HistoryActivity extends ActionBarBaseActivitiy implements
		OnItemClickListener {
	private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
	private PinnedSectionListView lvHistory;
	private HistoryAdapter historyAdapter;
	private ArrayList<History> historyList;
	private ArrayList<History> historyListOrg;
//	private PreferenceHelper preferenceHelper;
	private ParseContent parseContent;
	private ImageView tvNoHistory;
	private ArrayList<Date> dateList = new ArrayList<Date>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		setIconMenu(R.drawable.ub__nav_history);
		setTitle(getString(R.string.text_history));
		setIcon(R.drawable.back);
		lvHistory = (PinnedSectionListView) findViewById(R.id.lvHistory);
		lvHistory.setOnItemClickListener(this);
		historyList = new ArrayList<History>();

		tvNoHistory = (ImageView) findViewById(R.id.ivEmptyView);
		//
		// actionBar.setDisplayHomeAsUpEnabled(true);
		// actionBar.setHomeButtonEnabled(true);
		// actionBar.setTitle(getString(R.string.text_history));
//		preferenceHelper = new PreferenceHelper(this);
		parseContent = new ParseContent(this);
		dateList = new ArrayList<Date>();
		historyListOrg = new ArrayList<History>();
		getHistory();

	}

	/**
	 * 
	 */
	private void getHistory() {
		// TODO Auto-generated method stub
		if (!AndyUtils.isNetworkAvailable(this)) {
			AndyUtils.showToast(
					getResources().getString(R.string.dialog_no_inter_message),
					this);
			return;
		}
		AndyUtils.showCustomProgressDialog(this,
				getResources().getString(R.string.progress_getting_history),
				false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.HISTORY + Const.Params.ID + "="
				+ PreferenceHelper.getInstance(this).getUserId() + "&" + Const.Params.TOKEN + "="
				+ PreferenceHelper.getInstance(this).getSessionToken());
		AppLog.Log("History", Const.ServiceType.HISTORY + Const.Params.ID + "="
				+ PreferenceHelper.getInstance(this).getUserId() + "&" + Const.Params.TOKEN + "="
				+ PreferenceHelper.getInstance(this).getSessionToken());
		new HttpRequester(this, map, Const.ServiceCode.HISTORY, true, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		if (mSeparatorsSet.contains(position))
			return;
		History history = historyListOrg.get(position);
		showHistoryBillDialog(history.getTimecost(), history.getTotal(),
				history.getDistanceCost(), history.getBasePrice(),
				history.getTime(), history.getDistance(),
				history.getCurrency(), null, "", "", "", "", "",history.getActual_total(),""); // any prob
																	// keep only
																	// till null
																	// and check,
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uberdriverforx.parse.AsyncTaskCompleteListener#onTaskCompleted(java
	 * .lang.String, int)
	 */
	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
		switch (serviceCode) {
		case Const.ServiceCode.HISTORY:
			AppLog.Log("TAG", "History Response :" + response);
			Log.d("mahi", "history done" + response);
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				final Calendar cal = Calendar.getInstance();
				historyListOrg.clear();
				historyList.clear();
				dateList.clear();
				parseContent.parseHistory(response, historyList);

				Collections.sort(historyList, new Comparator<History>() {
					@Override
					public int compare(History o1, History o2) {

						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd hh:mm:ss");
						try {

							String firstStrDate = o1.getDate();
							String secondStrDate = o2.getDate();

							Date date2 = dateFormat.parse(secondStrDate);
							Date date1 = dateFormat.parse(firstStrDate);
							return date2.compareTo(date1);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						return 0;

					}
				});

				HashSet<Date> listToSet = new HashSet<Date>();
				for (int i = 0; i < historyList.size(); i++) {
					AppLog.Log("date", historyList.get(i).getDate() + "");
					if (listToSet.add(sdf.parse(historyList.get(i).getDate()))) {
						dateList.add(sdf.parse(historyList.get(i).getDate()));
					}

				}

				for (int i = 0; i < dateList.size(); i++) {

					cal.setTime(dateList.get(i));
					History item = new History();
					item.setDate(sdf.format(dateList.get(i)));
					historyListOrg.add(item);

					mSeparatorsSet.add(historyListOrg.size() - 1);
					for (int j = 0; j < historyList.size(); j++) {
						Calendar messageTime = Calendar.getInstance();
						messageTime.setTime(sdf.parse(historyList.get(j)
								.getDate()));
						if (cal.getTime().compareTo(messageTime.getTime()) == 0) {
							historyListOrg.add(historyList.get(j));
						}
					}
				}

				if (historyList.size() > 0) {
					lvHistory.setVisibility(View.VISIBLE);
					tvNoHistory.setVisibility(View.GONE);
				} else {
					lvHistory.setVisibility(View.GONE);
					tvNoHistory.setVisibility(View.VISIBLE);
				}
				Log.i("historyListOrg size  ", "" + historyListOrg.size());

				historyAdapter = new HistoryAdapter(this, historyListOrg,
						mSeparatorsSet);
				lvHistory.setAdapter(historyAdapter);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnActionNotification:
			onBackPressed();
			break;

		default:
			break;
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
}
