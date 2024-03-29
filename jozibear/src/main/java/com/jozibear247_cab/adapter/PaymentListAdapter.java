/**
 * 
 */
package com.jozibear247_cab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jozibear247_cab.R;
import com.jozibear247_cab.models.Card;

import java.util.ArrayList;

/**
 * @author Hardik A Bhalodi
 * 
 */
public class PaymentListAdapter extends BaseAdapter {

	/**
	 * 
	 */
	// public static final String AMERICAN_EXPRESS = "American Express";
	// public static final String DISCOVER = "Discover";
	// public static final String JCB = "JCB";
	// public static final String DINERS_CLUB = "Diners Club";
	// public static final String VISA = "Visa";
	// public static final String MASTERCARD = "MasterCard";
	// public static final String UNKNOWN = "Unknown";
	// public static final String[] PREFIXES_AMERICAN_EXPRESS = { "34", "37" };
	// public static final String[] PREFIXES_DISCOVER = { "60", "62", "64", "65"
	// };
	// public static final String[] PREFIXES_JCB = { "35" };
	// public static final String[] PREFIXES_DINERS_CLUB = { "300", "301",
	// "302",
	// "303", "304", "305", "309", "36", "38", "37", "39" };
	// public static final String[] PREFIXES_VISA = { "4" };
	// public static final String[] PREFIXES_MASTERCARD = { "50", "51", "52",
	// "53", "54", "55" };
	private LayoutInflater inflater;
	private ViewHolder holder;
	private ArrayList<Card> listCard;

	public PaymentListAdapter(Context context, ArrayList<Card> listCard) {
		// TODO Auto-generated constructor stub
		this.listCard = listCard;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listCard.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		return listCard.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.view_payment_list_item,
					parent, false);
			holder = new ViewHolder();
			holder.ivCard = (ImageView) convertView.findViewById(R.id.ivCard);
			holder.tvNo = (TextView) convertView.findViewById(R.id.tvNo);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvNo.setText("*****" + listCard.get(position).getLastFour());
		holder.ivCard.setImageResource(R.drawable.ub__nav_payment);
		return convertView;
	}

	private class ViewHolder {
		public ImageView ivCard;
		public TextView tvNo;
	}

	// public String getType(String number) {
	// if (!TextUtils.isBlank(number)) {
	// if (TextUtils.hasAnyPrefix(number, PREFIXES_AMERICAN_EXPRESS)) {
	// return AMERICAN_EXPRESS;
	// } else if (TextUtils.hasAnyPrefix(number, PREFIXES_DISCOVER)) {
	// return DISCOVER;
	// } else if (TextUtils.hasAnyPrefix(number, PREFIXES_JCB)) {
	// return JCB;
	// } else if (TextUtils.hasAnyPrefix(number, PREFIXES_DINERS_CLUB)) {
	// return DINERS_CLUB;
	// } else if (TextUtils.hasAnyPrefix(number, PREFIXES_VISA)) {
	// return VISA;
	// } else if (TextUtils.hasAnyPrefix(number, PREFIXES_MASTERCARD)) {
	// return MASTERCARD;
	// } else {
	// return UNKNOWN;
	// }
	// }
	// return UNKNOWN;
	// }

}
