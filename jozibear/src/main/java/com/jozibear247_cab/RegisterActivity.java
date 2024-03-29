package com.jozibear247_cab;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jozibear247_cab.fragments.UberRegisterFragment;
import com.jozibear247_cab.fragments.UberSignInFragment;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.Const;

/**
 * @author Hardik A Bhalodi
 */
public class RegisterActivity extends ActionBarBaseActivitiy {

//	Permission[] permissions = new Permission[] { Permission.EMAIL };
//	SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
//			.setPermissions(permissions).build();
//	public PreferenceHelper phelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// actionBar.hide();
		setContentView(R.layout.register_activity);
//		SimpleFacebook.setConfiguration(configuration);
//		phelper = new PreferenceHelper(this);
		// actionBar.setDisplayShowHomeEnabled(true);
		// actionBar.setDisplayHomeAsUpEnabled(true);

		setIcon(R.drawable.back);

		if (getIntent().getBooleanExtra("isSignin", false)) {
			gotSignInFragment();
		} else {
			goToRegisterFragment();
		}

		// UberMainFragment mainFrag = new UberMainFragment();
		// addFragment(mainFrag, false, Const.FRAGMENT_MAIN);

	}

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

	public void registerGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
		if (mHandleMessageReceiver != null) {
			AndyUtils.showCustomProgressDialog(this,
					getString(R.string.progress_loading), false, null);
			new GCMRegisterHendler(RegisterActivity.this,
					mHandleMessageReceiver);

		}
	}

	public void unregisterGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
		if (mHandleMessageReceiver != null) {

			if (mHandleMessageReceiver != null) {
				unregisterReceiver(mHandleMessageReceiver);
			}

		}

	}

	private void gotSignInFragment() {
		UberSignInFragment signInFrag = new UberSignInFragment();
		clearBackStack();
		addFragment(signInFrag, false, Const.FRAGMENT_SIGNIN);
	}

	private void goToRegisterFragment() {
		UberRegisterFragment regFrag = new UberRegisterFragment();
		clearBackStack();
		addFragment(regFrag, false, Const.FRAGMENT_REGISTER);
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

	public void showKeyboard(View v) {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		// View view = activity.getCurrentFocus();
		// if (view != null) {
		inputManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
		// }
	}

}
