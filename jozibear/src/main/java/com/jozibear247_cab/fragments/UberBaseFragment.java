package com.jozibear247_cab.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View.OnClickListener;

import com.androidquery.callback.ImageOptions;
import com.jozibear247_cab.MainDrawerActivity;
import com.jozibear247_cab.R;
import com.jozibear247_cab.parse.AsyncTaskCompleteListener;
import com.jozibear247_cab.parse.HttpRequester;
import com.jozibear247_cab.utils.AndyUtils;
import com.jozibear247_cab.utils.Const;
import com.jozibear247_cab.utils.PreferenceHelper;

import java.util.HashMap;

/**
 * @author Hardik A Bhalodi
 */
@SuppressLint("ValidFragment")
abstract public class UberBaseFragment extends Fragment implements
		OnClickListener, AsyncTaskCompleteListener {
	MainDrawerActivity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activity = (MainDrawerActivity) getActivity();
	}

	protected abstract boolean isValidate();

	@Override
	public void onTaskCompleted(final String response, int serviceCode) {
		// TODO Auto-generated method stub

	}

	protected ImageOptions getAqueryOption() {
		ImageOptions options = new ImageOptions();
		options.targetWidth = 200;
		options.memCache = true;
		options.fallback = R.drawable.default_user;
		options.fileCache = true;
		return options;
	}

	protected void login() {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.LOGIN);
		map.put(Const.Params.EMAIL, PreferenceHelper.getInstance(activity).getEmail());
		map.put(Const.Params.PASSWORD, PreferenceHelper.getInstance(activity).getPassword());
		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
		map.put(Const.Params.DEVICE_TOKEN, PreferenceHelper.getInstance(activity).getDeviceToken());
		map.put(Const.Params.LOGIN_BY, Const.MANUAL);
		new HttpRequester(activity, map, Const.ServiceCode.LOGIN, this);

	}

	protected void loginSocial(String id, String loginType) {
		if (!AndyUtils.isNetworkAvailable(activity)) {
			AndyUtils.showToast(getResources().getString(R.string.no_internet),
					activity);
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Const.URL, Const.ServiceType.LOGIN);
		map.put(Const.Params.SOCIAL_UNIQUE_ID, id);
		map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
		map.put(Const.Params.DEVICE_TOKEN,
				PreferenceHelper.getInstance(activity).getDeviceToken());
		map.put(Const.Params.LOGIN_BY, loginType);
		new HttpRequester(activity, map, Const.ServiceCode.LOGIN, this);

	}

}
