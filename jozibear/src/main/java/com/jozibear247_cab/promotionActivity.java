package com.jozibear247_cab;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jozibear247_cab.component.MyFontButton;
import com.jozibear247_cab.component.MyFontEdittextView;
import com.jozibear247_cab.parse.AsyncTaskCompleteListener;
import com.jozibear247_cab.parse.HttpRequester;
import com.jozibear247_cab.parse.ParseContent;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class promotionActivity extends Activity implements AsyncTaskCompleteListener{

	private MyFontEdittextView promofield;
	private MyFontButton apply;
	private TextView ledger;
	private ParseContent pContent;
	Context context = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promotions);
		setTitle("PROMOTIONS");
		getActionBar().setIcon(R.drawable.promo);
		promofield=(MyFontEdittextView) findViewById(R.id.promotionpromo);
		apply=(MyFontButton) findViewById(R.id.promotionpromoApply);
		ledger= (TextView) findViewById(R.id.creditbalance);
		pContent = new ParseContent(this);
		context = this;
		apply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(promofield.getText().length()==0){
					AndyUtils.showToast("Please enter Promo Code",context );
				}else if(!AndyUtils.isNetworkAvailable(context)){
					AndyUtils.showToast(getResources().getString(
							R.string.dialog_no_inter_message), context);
				}else{
					applyReffralCode();
				}
			}
		});
		getledger();
		
	}
	
	private void applyReffralCode() {

		AndyUtils.showCustomProgressDialog(context,
				getString(R.string.progress_loading), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.APPLY_REFFRAL_CODE);
		map.put(Const.Params.REFERRAL_CODE, promofield.getText().toString());
		map.put(Const.Params.ID, PreferenceHelper.getInstance(this).getUserId());
		map.put(Const.Params.TOKEN, PreferenceHelper.getInstance(this).getSessionToken());
		new HttpRequester(this, map, Const.ServiceCode.APPLY_REFFRAL_CODE,
				this);

	}
	
	void getledger(){
		Log.d("yyy", "in ledger");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL,
				Const.ServiceType.GET_CREDITS + Const.Params.ID + "="
						+ PreferenceHelper.getInstance(this).getUserId() + "&"
						+ Const.Params.TOKEN + "="
						+ PreferenceHelper.getInstance(this).getSessionToken()
						);

		new HttpRequester(this, map, Const.ServiceCode.GET_CREDITS,
				true, this);
	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		
		AndyUtils.removeCustomProgressDialog();
		switch (serviceCode) {
		case Const.ServiceCode.APPLY_REFFRAL_CODE:
			promofield.setText("");
			if (pContent.isSuccess(response)) {
				Toast.makeText(context, "Applied Successfully", Toast.LENGTH_LONG).show();
				getledger();
			}
			break;
		case Const.ServiceCode.GET_CREDITS:
			Log.d("yyy", response);
			if (pContent.isSuccess(response)) {
				try {
					JSONObject jsonobject=new JSONObject(response);
					JSONObject creditobject=jsonobject.getJSONObject("credits");
					String currency=creditobject.getString("currency");
					ledger.setText(currency+" "+creditobject.getString("balance"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}
}
