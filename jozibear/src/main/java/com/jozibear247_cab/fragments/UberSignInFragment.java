package com.jozibear247_cab.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.jozibear247_cab.MainDrawerActivity;
import com.jozibear247_cab.R;
import com.jozibear247_cab.component.MyFontButton;
import com.jozibear247_cab.component.MyFontEdittextView;
import com.jozibear247_cab.parse.HttpRequester;
import com.jozibear247_cab.parse.ParseContent;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import java.util.HashMap;

/**
 * @author Hardik A Bhalodi
 */
public class UberSignInFragment extends UberBaseFragmentRegister
		implements
		com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks,
		com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener {

	private MyFontEdittextView etEmail, etPassword;
	private MyFontButton btnSignIn;
	private ImageButton btnGPlus;
	private ImageButton btnFb;
	private ParseContent parseContent;
	// Gplus
	private ConnectionResult mConnectionResult;
	private GoogleApiClient mGoogleApiClient;
	private boolean mIntentInProgress;
	private static final int RC_SIGN_IN = 0;
	private boolean mSignInClicked;

	// FB
	private SimpleFacebook mSimpleFacebook;

	private Button btnForgetPassowrd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Scope scope = new Scope("https://www.googleapis.com/auth/plus.login");
		// Scope scopePro = new
		// Scope("https://www.googleapis.com/auth/plus.me");
		mGoogleApiClient = new GoogleApiClient.Builder(activity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, Plus.PlusOptions.builder().build())
				.addScope(scope).build();
		parseContent = new ParseContent(activity);

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
		activity.setTitle(getResources().getString(R.string.text_signin_small));
		activity.setIconMenu(R.drawable.taxi);
		View view = inflater.inflate(R.layout.login, container, false);
		etEmail = (MyFontEdittextView) view.findViewById(R.id.etEmail);
		etPassword = (MyFontEdittextView) view.findViewById(R.id.etPassword);
		btnSignIn = (MyFontButton) view.findViewById(R.id.btnSignIn);
		btnGPlus = (ImageButton) view.findViewById(R.id.btnGplus);
		btnFb = (ImageButton) view.findViewById(R.id.btnFb);
		btnForgetPassowrd = (Button) view.findViewById(R.id.btnForgetPassword);
		btnForgetPassowrd.setOnClickListener(this);
		btnGPlus.setOnClickListener(this);
		btnSignIn.setOnClickListener(this);
		btnFb.setOnClickListener(this);
		btnSignIn.setOnClickListener(this);

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		activity.currentFragment = Const.FRAGMENT_SIGNIN;
		activity.actionBar.setTitle(getString(R.string.text_signin_small));
		mSimpleFacebook = SimpleFacebook.getInstance(activity);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnFb:
			if (!mSimpleFacebook.isLogin()) {
				activity.setFbTag(Const.FRAGMENT_SIGNIN);
				mSimpleFacebook.login(new OnLoginListener() {

					@Override
					public void onFail(String arg0) {
						// TODO Auto-generated method stub
						Toast.makeText(activity, "fb login failed",
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onException(Throwable arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onThinking() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onNotAcceptingPermissions(Type arg0) {
						// TODO Auto-generated method stub
						// Log.w("UBER",
						// String.format(
						// "You didn't accept %s permissions",
						// arg0.name()));
					}

					@Override
					public void onLogin() {
						// TODO Auto-generated method stub
						Toast.makeText(activity, "success", Toast.LENGTH_SHORT)
								.show();
					}
				});
			} else {
				getProfile();
			}
			break;
		case R.id.btnGplus:
			mSignInClicked = true;
			if (!mGoogleApiClient.isConnecting()) {
				AndyUtils.showCustomProgressDialog(activity,
						getString(R.string.text_getting_info), true, null);

				mGoogleApiClient.connect();
			}
			break;
		case R.id.btnSignIn:
			if (isValidate()) {
				login();
			}
			break;
		case R.id.btnForgetPassword:
			activity.addFragment(new ForgetPasswordFragment(), true,
					Const.FOREGETPASS_FRAGMENT_TAG);
			break;

		default:
			break;
		}
	}

	private void getProfile() {
		AndyUtils.showCustomProgressDialog(activity,
				getString(R.string.text_getting_info), true, null);

		mSimpleFacebook.getProfile(new OnProfileListener() {
			@Override
			public void onComplete(Profile profile) {
				AndyUtils.removeCustomProgressDialog();
				Log.i("Uber", "My profile id = " + profile.getId());
				btnGPlus.setEnabled(false);
				btnFb.setEnabled(false);
				loginSocial(profile.getId(), Const.SOCIAL_FACEBOOK);
			}
		});
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the
			// user clicks
			// 'sign-in'.

			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all

				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
		mSignInClicked = false;
		btnGPlus.setEnabled(false);

		String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
		Person currentPerson = Plus.PeopleApi
				.getCurrentPerson(mGoogleApiClient);

		String personName = currentPerson.getDisplayName();

		String personPhoto = currentPerson.getImage().toString();
		String personGooglePlusProfile = currentPerson.getUrl();
		loginSocial(currentPerson.getId(), Const.SOCIAL_GOOGLE);
		// signIn();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	private void resolveSignInError() {

		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				activity.startIntentSenderForResult(mConnectionResult
						.getResolution().getIntentSender(), RC_SIGN_IN, null,
						0, 0, 0, Const.FRAGMENT_SIGNIN);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == RC_SIGN_IN) {

			if (resultCode != Activity.RESULT_OK) {
				mSignInClicked = false;
				AndyUtils.removeCustomProgressDialog();
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		} else {

			mSimpleFacebook.onActivityResult(activity, requestCode, resultCode,
					data);
			if (mSimpleFacebook.isLogin()) {
				getProfile();
			} else {
				Toast.makeText(activity, "facebook login failed",
						Toast.LENGTH_SHORT).show();
			}

			super.onActivityResult(requestCode, resultCode, data);

		}

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isValidate() {
		// TODO Auto-generated method stub
		String msg = null;
		if (TextUtils.isEmpty(etEmail.getText().toString())&&TextUtils.isEmpty(etPassword.getText().toString())) {
			Toast.makeText(getActivity(), "Please Enter the Email ID",
					   Toast.LENGTH_LONG).show();
		} 
		if (TextUtils.isEmpty(etEmail.getText().toString())) {
			msg = getResources().getString(R.string.text_enter_email);
		} else if (!AndyUtils.eMailValidation(etEmail.getText().toString())) {
			msg = getResources().getString(R.string.text_enter_valid_email);
		}
		if (TextUtils.isEmpty(etPassword.getText().toString())) {
			msg = getResources().getString(R.string.text_enter_password);
		}
		if (msg == null)
			return true;

		Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
		return false;
		
	}

	// private void signIn() {
	// Intent intent = new Intent(activity, MainDrawerActivity.class);
	// startActivity(intent);
	// activity.finish();
	// }

	private void login() {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		AndyUtils.showCustomProgressDialog(activity,
				getResources().getString(R.string.text_signing), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.LOGIN);
		map.put(Const.Params.EMAIL, etEmail.getText().toString());
		map.put(Const.Params.PASSWORD, etPassword.getText().toString());
		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
		map.put(Const.Params.DEVICE_TOKEN,
				PreferenceHelper.getInstance(activity).getDeviceToken());
		map.put(Const.Params.LOGIN_BY, Const.MANUAL);
		new HttpRequester(activity, map, Const.ServiceCode.LOGIN, this);

	}

	private void loginSocial(String id, String loginType) {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		AndyUtils.showCustomProgressDialog(activity,
				getResources().getString(R.string.text_signin), false, null);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.LOGIN);
		map.put(Const.Params.SOCIAL_UNIQUE_ID, id);
		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
		map.put(Const.Params.DEVICE_TOKEN,
				PreferenceHelper.getInstance(activity).getDeviceToken());
		map.put(Const.Params.LOGIN_BY, loginType);
		new HttpRequester(activity, map, Const.ServiceCode.LOGIN, this);

	}

	@Override
	public void onTaskCompleted(String response, int serviceCode) {
		// TODO Auto-generated method stub
		AndyUtils.removeCustomProgressDialog();
		super.onTaskCompleted(response, serviceCode);
		switch (serviceCode) {
		case Const.ServiceCode.LOGIN:
			if (parseContent.isSuccessWithStoreId(response)) {
				parseContent.parseUserAndStoreToDb(response);
				PreferenceHelper.getInstance(activity).putPassword(etPassword.getText()
						.toString());
				startActivity(new Intent(activity, MainDrawerActivity.class));
				activity.finish();
			} else {
				btnFb.setEnabled(true);
				btnGPlus.setEnabled(true);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public boolean OnBackPressed() {
		// TODO Auto-generated method stub
		// activity.removeAllFragment(new UberMainFragment(), false,
		// Const.FRAGMENT_MAIN);
		activity.goToMainActivity();
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uberorg.fragments.BaseFragmentRegister#OnBackPressed()
	 */

}
