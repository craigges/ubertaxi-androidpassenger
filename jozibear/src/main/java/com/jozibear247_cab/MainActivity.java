package com.jozibear247_cab;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.PreferenceHelper;
import com.splunk.mint.Mint;

public class MainActivity extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	private Button btnSignIn, btnRegister;
//	private PreferenceHelper pHelper;
	private boolean isReceiverRegister;
	private int oldOptions;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		BugSenseHandler.initAndStartSession(MainActivity.this, "49793880");
		Mint.initAndStartSession(MainActivity.this, "d839b367");

		if (PreferenceHelper.getInstance(this).getEmailActivation() == 1 && !TextUtils.isEmpty(PreferenceHelper.getInstance(this).getUserId())) {
			startActivity(new Intent(this, MainDrawerActivity.class));
			this.finish();
			return;
		}
		// TODO Auto-generated method stub
		isReceiverRegister = false;
//		pHelper = new PreferenceHelper(this);
		setContentView(R.layout.activity_main);

		btnSignIn = (Button) findViewById(R.id.btnSignIn);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		// rlLoginRegisterLayout = (RelativeLayout) view
		// .findViewById(R.id.rlLoginRegisterLayout);
		// tvMainBottomView = (MyFontTextView) view
		// .findViewById(R.id.tvMainBottomView);
		btnSignIn.setOnClickListener(this);
		btnRegister.setOnClickListener(this);
		if (TextUtils.isEmpty(PreferenceHelper.getInstance(this).getDeviceToken())) {
			isReceiverRegister = true;
			registerGcmReceiver(mHandleMessageReceiver);
		}
	}

	public void registerGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
		if (mHandleMessageReceiver != null) {
			AndyUtils.showCustomProgressDialog(this,
					getString(R.string.progress_loading), false, null);
			new GCMRegisterHendler(this, mHandleMessageReceiver);

		}
	}

	public void unregisterGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
		if (mHandleMessageReceiver != null) {

			if (mHandleMessageReceiver != null) {
				unregisterReceiver(mHandleMessageReceiver);
			}

		}

	}

	private BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			AndyUtils.removeCustomProgressDialog();
			if (intent.getAction().equals(CommonUtilities.DISPLAY_REGISTER_GCM)) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {

					int resultCode = bundle.getInt(CommonUtilities.RESULT);
					if (resultCode == Activity.RESULT_OK) {

					} else {
						Toast.makeText(MainActivity.this,
								getString(R.string.register_gcm_failed),
								Toast.LENGTH_SHORT).show();
						finish();
					}

				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent startRegisterActivity = new Intent(MainActivity.this,
				RegisterActivity.class);
		switch (v.getId()) {
		case R.id.btnSignIn:
			startRegisterActivity.putExtra("isSignin", true);
			break;
		case R.id.btnRegister:
			startRegisterActivity.putExtra("isSignin", false);
			break;
		}
		startActivity(startRegisterActivity);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		finish();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (isReceiverRegister) {
			unregisterGcmReceiver(mHandleMessageReceiver);
			isReceiverRegister = false;
		}
		super.onDestroy();
	}

}
