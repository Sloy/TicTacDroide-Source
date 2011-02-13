package com.sloy.tictacdroide.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.KeyEvent;

import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.components.ApplicationController;
import com.sloy.tictacdroide.components.LoaderImageView;
import com.sloy.tictacdroide.components.SoundManager;
import com.sloy.tictacdroide.components.ThemeManager;
import com.sloy.tictacdroide.components.TwitterLoginPreference;
import com.sloy.tictacdroide.components.Utils;
import com.sloy.tictacdroide.constants.ThemeID;

import twitter4j.ProfileImage;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;

public class Opciones extends PreferenceActivity implements OnPreferenceChangeListener{

	public static final int REQUEST_CODE = 666;
	public static final int RESULT_CODE_THEME_CHANGED = 555;
	
	private  ApplicationController app;
	private TwitterLoginPreference twLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.opciones);
		
		/* Mostrar la descripción del tema */
		Preference dlThemesAbout = findPreference("dlThemesAbout");
		dlThemesAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				//Muestra la Descripción del tema
				String themeDesc = ThemeManager.getString(ThemeID.THEME_DESCRIPTION);
				if(themeDesc==null)return false;
				
				new AlertDialog.Builder(Opciones.this)
				.setTitle(getString(R.string.botonAcerca)+" "+ThemeManager.getString(ThemeID.THEME_NAME))
				.setMessage(themeDesc)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create().show();
				return true;
			}
		});      
		app = (ApplicationController) getApplication();
		twLogin = (TwitterLoginPreference)findPreference("twLogin");
		twLogin.setWidgetLayoutResource(R.layout.twitter_login_pref);
        twLogin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if(!app.isAuthorized()){
					// Log-in
					startActivity(new Intent(getApplicationContext(), com.sloy.tictacdroide.activities.TwitterAuthorizationActivity.class));
				}else{
					app.logout();
					twitterCheck();
				}
				return true;
			}
		});
        
	}
	
	  @Override
	protected void onResume() {
		super.onResume();
		/* Twitter check */
		twitterCheck();
		 
		 /* Carga los temas */
			ListPreference lp = (ListPreference)findPreference("themePackageName");
			lp.setOnPreferenceChangeListener(this);
			
			Intent intent=new Intent("com.sloy.tictacdroide.THEMES");
	        intent.addCategory("android.intent.category.DEFAULT");
	        PackageManager pm=getPackageManager();
	        List<ResolveInfo> themes=pm.queryIntentActivities(intent, 0);
	        String[] entries = new String[themes.size()+1];
	        String[] values = new String[themes.size()+1];
	        entries[0]=ThemeManager.THEME_DEFAULT;
	        values[0]=ThemeManager.THEME_DEFAULT;
	        for(int i=0;i<themes.size();i++){
	        	String appPackageName=(themes.get(i)).activityInfo.packageName.toString();
	        	String themeName=(themes.get(i)).loadLabel(pm).toString();
	        	entries[i+1]=themeName;
	        	values[i+1]=appPackageName;
	        }
	        lp.setEntries(entries);
	        lp.setEntryValues(values);
	}

	@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(keyCode==KeyEvent.KEYCODE_BACK){
				SoundManager.playSound(SoundManager.ATRAS);
			}
			return super.onKeyDown(keyCode, event);
			
		}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.getKey().equals("themePackageName")) {
			SharedPreferences.Editor editor = Utils.preferencias.edit();
			editor.putString("themePackageName", newValue.toString());
			editor.commit();
			ThemeManager.setTheme(getApplicationContext());
			setResult(RESULT_CODE_THEME_CHANGED);
			return false;
			}
		return false;
	}	
	
	private void twitterCheck(){
		if(app.isAuthorized()){
        	twLogin.mTwitter = app.getTwitter();
        }else{
        	twLogin.mTwitter=null;
        }
	}
}