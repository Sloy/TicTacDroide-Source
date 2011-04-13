package com.sloy.tictacdroide.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.KeyEvent;

import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.components.ApplicationController;
import com.sloy.tictacdroide.components.FacebookSessionEvents;
import com.sloy.tictacdroide.components.FacebookSessionStore;
import com.sloy.tictacdroide.components.MyLog;
import com.sloy.tictacdroide.components.SoundManager;
import com.sloy.tictacdroide.components.ThemeManager;
import com.sloy.tictacdroide.components.TwitterLoginPreference;
import com.sloy.tictacdroide.components.Utils;
import com.sloy.tictacdroide.components.FacebookSessionEvents.AuthListener;
import com.sloy.tictacdroide.components.FacebookSessionEvents.LogoutListener;
import com.sloy.tictacdroide.constants.ThemeID;
import com.sloy.tictacdroide.constants.Codes.Requests;
import com.sloy.tictacdroide.constants.Codes.Results;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;

public class Opciones extends PreferenceActivity implements
		OnPreferenceChangeListener {

	private ApplicationController app;
	private TwitterLoginPreference twLogin;
	private Preference fbLogin;
	private Handler mHandler;
	private AuthListener authListener;
	private LogoutListener logoutListener;

	private final String PAYPAL_URL = "https://www.paypal.com/es/cgi-bin/webscr?cmd=_flow&SESSION=Ouoz6CBLpKl9cBhwlympwIjFRZWWIYht7tHVEaDS_KDI0kkUGrNws8pQeke&dispatch=5885d80a13c0db1f8e263663d3faee8d5fa8ff279e37c3d9d4e38bdbee0ede69";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.opciones);

		/* Facebook */
		authListener = new OpcionesAuthListener();
		logoutListener = new OpcionesLogoutListener();
		FacebookSessionEvents.addAuthListener(authListener);
		FacebookSessionEvents.addLogoutListener(logoutListener);
		mHandler = new Handler();
		
		/* Mostrar la descripción del tema */
		Preference dlThemesAbout = findPreference("dlThemesAbout");
		dlThemesAbout
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						// Muestra la Descripción del tema
						String themeDesc = ThemeManager
								.getString(ThemeID.THEME_DESCRIPTION);
						if(themeDesc == null){
							return false;
						}

						new AlertDialog.Builder(Opciones.this).setTitle(
								getString(R.string.botonAcerca)
										+ " "
										+ ThemeManager
												.getString(ThemeID.THEME_NAME))
								.setMessage(themeDesc).setPositiveButton(
										android.R.string.ok,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										}).create().show();
						return true;
					}
				});

		((Preference)findPreference("btThemeRepo"))
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						startActivityForResult(
								new Intent(
										getApplicationContext(),
										com.sloy.tictacdroide.activities.ThemeRepo.class),
								Requests.THEME_REPO);
						return true;
					}
				});

		/*((Preference)findPreference("btnDonate"))
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse(PAYPAL_URL)));
						return true;
					}
				});*/

		app = (ApplicationController)getApplication();
		twLogin = (TwitterLoginPreference)findPreference("twLogin");
		twLogin.setWidgetLayoutResource(R.layout.twitter_login_pref);
		twLogin
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						if(!app.isTwitterAuthorized()){
							// Log-in
							startActivity(new Intent(
									getApplicationContext(),
									com.sloy.tictacdroide.activities.TwitterAuthorizationActivity.class));
						}else{
							app.logoutTwitter();
							twitterCheck();
						}
						return true;
					}
				});

		fbLogin = (Preference)findPreference("fbLogin");
		fbLogin
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						if(!app.isFacebookAuthorized()){
							// Log-in
							MyLog.d("Login facebook");
							app.beginFacebookAuthorization(Opciones.this,
								 new FacebookSessionEvents.LoginDialogListener());
						}else{
							// Logout
							MyLog.d("Logout facebook");
							app.logoutFacebook(mHandler);
						}
						return true;
					}
				});

	}

	@Override
	protected void onDestroy() {
		FacebookSessionEvents.removeAuthListener(authListener);
		FacebookSessionEvents.removeLogoutListener(logoutListener);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		/* Twitter check */
		twitterCheck();
		facebookCheck();

		/* Carga los temas */
		ListPreference lp = (ListPreference)findPreference("themePackageName");
		lp.setOnPreferenceChangeListener(this);

		Intent intent = new Intent("com.sloy.tictacdroide.THEMES");
		intent.addCategory("android.intent.category.DEFAULT");
		PackageManager pm = getPackageManager();
		List<ResolveInfo> themes = pm.queryIntentActivities(intent, 0);
		String[] entries = new String[themes.size() + 1];
		String[] values = new String[themes.size() + 1];
		entries[0] = ThemeManager.THEME_DEFAULT;
		values[0] = ThemeManager.THEME_DEFAULT;
		for(int i = 0; i < themes.size(); i++){
			String appPackageName = (themes.get(i)).activityInfo.packageName
					.toString();
			String themeName = (themes.get(i)).loadLabel(pm).toString();
			entries[i + 1] = themeName;
			values[i + 1] = appPackageName;
		}
		lp.setEntries(entries);
		lp.setEntryValues(values);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		app.authorizeFacebookCallback(requestCode, resultCode, data);
		// Ha cambiado el tema
		if(requestCode == Requests.THEME_REPO
				&& resultCode == Results.THEME_CHANGED){
			setResult(Results.THEME_CHANGED);
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			SoundManager.playSound(SoundManager.ATRAS);
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.getKey().equals("themePackageName")){
			SharedPreferences.Editor editor = Utils.preferencias.edit();
			editor.putString("themePackageName", newValue.toString());
			editor.commit();
			ThemeManager.setTheme(getApplicationContext());
			setResult(Results.THEME_CHANGED);
			return false;
		}
		return false;
	}

	private void facebookCheck() {
		String title = getString(R.string.fb_login_title);
		String summary = getString(R.string.fb_login_summary);
		if(app.isFacebookAuthorized()){
			title = getString(R.string.fb_logout_title);
			summary = getString(R.string.fb_logout_summary);
		}
		fbLogin.setTitle(title);
		fbLogin.setSummary(summary);
	}

	private void twitterCheck() {
		String title = getString(R.string.tw_login_title);
		String summary = getString(R.string.tw_login_summary);
		if(app.isTwitterAuthorized()){
			try{
				Twitter tw = app.getTwitter();
				// Sets title and summary
				title = getString(R.string.tw_logout_title);
				summary = String.format(getString(R.string.tw_logout_summary),
						tw.getScreenName());
			}catch(TwitterException e){
				Log.e("tictacdroide", e.getMessage());
			}catch(IllegalStateException e){
				Log.e("tictacdroide", e.getMessage());
			}
		}else{
			twLogin.mTwitter = null;
		}
		twLogin.setTitle(title);
		twLogin.setSummary(summary);
	}

	

	public class OpcionesAuthListener implements
			FacebookSessionEvents.AuthListener {

		public void onAuthSucceed() {
			// Pone todo de cara al usuario según si está o no autorizado (debe
			// estarlo...)
			facebookCheck();
		}

		public void onAuthFail(String error) {
			MyLog.e("onAuthFail: " + error);
		}
	}

	public class OpcionesLogoutListener implements
			FacebookSessionEvents.LogoutListener {
		public void onLogoutBegin() {
			MyLog.d("FB: Logging out...");
			fbLogin.setEnabled(false);
		}

		public void onLogoutFinish() {
			// Limpia la sesión y tal
			FacebookSessionStore.clear(getApplicationContext());
			// Pone todo de cara al usuario según si está o no autorizado (debe
			// estarlo...)
			facebookCheck();
			fbLogin.setEnabled(true);
		}
	}
}