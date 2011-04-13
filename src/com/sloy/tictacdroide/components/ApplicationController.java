package com.sloy.tictacdroide.components;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;
import com.openfeint.api.Notification;
import com.openfeint.api.OpenFeint;
import com.openfeint.api.OpenFeintDelegate;
import com.openfeint.api.OpenFeintSettings;
import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.components.FacebookSessionEvents.AuthListener;
import com.sloy.tictacdroide.openfeint.NotificationsDelegate;
import com.sloy.tictacdroide.openfeint.OFCredentials;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class ApplicationController extends Application {

	// Twitter
	private Twitter mTwitter;
	private RequestToken currentRequestToken;
	private OAuthHelper oAuthHelper;

	// Facebook
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;

	@Override
	public void onCreate() {
		super.onCreate();

		/* Inicia el asunto Twitter */
		oAuthHelper = new OAuthHelper(this);
		mTwitter = new TwitterFactory().getInstance();
		oAuthHelper.configureOAuth(mTwitter);

		/* Inicia el asunto Facebook */
		mFacebook = new Facebook("214161981927534");
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		FacebookSessionStore.restore(mFacebook, this);
		FacebookSessionEvents.addAuthListener(new AuthListener() {
			@Override
			public void onAuthSucceed() {
				// Guarda la sesión y tal
				FacebookSessionStore.save(getFacebook(),getApplicationContext());
			}
			
			@Override
			public void onAuthFail(String error) {
			}
		});
		

		/* Inicializa la referencia a SharedPreferences */
		PreferenceManager.setDefaultValues(this, R.xml.opciones, false);
		Utils.preferencias = PreferenceManager
				.getDefaultSharedPreferences(this);

		// Create, Initialise and then load the Sound manager
		SoundManager.getInstance();
		SoundManager.initSounds(this);
		SoundManager.loadSounds();

		/* Zona del maestro */
		// Establece el tema de la aplicación
		ThemeManager.setTheme(this);

		/* OpenFeint */
		// crea el objeto para configuración
		OpenFeintSettings settings = new OpenFeintSettings(OFCredentials.NAME,
				OFCredentials.KEY, OFCredentials.SECRET, OFCredentials.ID);
		// inicializa con las opciones anteriores y el delegado por defecto
		// (llama al login)
		OpenFeint.initialize(this, settings, new OpenFeintDelegate() {
		});
		// establece delegate para notificaciones (no quiero mostrar cuando
		// actualiza %)
		Notification.setDelegate(new NotificationsDelegate());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		if(!SoundManager.persistent){
			SoundManager.cleanup();
		}else{
			SoundManager.persistent = false;
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log
				.d("tictacdroide",
						"Application: onLowMemory() - ending application");
		try{
			this.finalize();
		}catch(Throwable e){
			Log.e("tictacdroide", "Error ending application: " + e.toString());
		}
	}

	/* Twitter stuff */
	public Twitter getTwitter() {
		return mTwitter;
	}

	public boolean isTwitterAuthorized() {
		return oAuthHelper.hasAccessToken();
	}

	public String beginTwitterAuthorization() {
		try{
			// if (null == currentRequestToken) {
			if(true){
				currentRequestToken = mTwitter.getOAuthRequestToken();
			}
			return currentRequestToken.getAuthorizationURL();
		}catch(TwitterException e){
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public boolean authorizeTwitter(String pin) {
		try{
			AccessToken accessToken = mTwitter.getOAuthAccessToken(
					currentRequestToken, pin);
			oAuthHelper.storeAccessToken(accessToken);
			return true;
		}catch(TwitterException e){
			throw new RuntimeException("Unable to authorize user", e);
		}
	}

	public void authorizedTwitter() {
		try{
			AccessToken accessToken = mTwitter.getOAuthAccessToken();
			oAuthHelper.storeAccessToken(accessToken);
		}catch(TwitterException e){
			throw new RuntimeException("Unable to authorize user", e);
		}

	}

	public void logoutTwitter() {
		oAuthHelper.removeAccessToken();
		oAuthHelper.configureOAuth(mTwitter);
	}

	public void loginTwitter() {
		startActivity(new Intent(
				this,
				com.sloy.tictacdroide.activities.TwitterAuthorizationActivity.class));
	}

	/* Facebook Stuff */

	public Facebook getFacebook() {
		return this.mFacebook;
	}

	public void beginFacebookAuthorization(Activity activity,
			DialogListener listener) {
		mFacebook.authorize(activity, new String[]{"publish_stream"}, listener);
	}
	
	public Boolean isFacebookAuthorized(){
		return mFacebook.isSessionValid();
	}
	
	public void logoutFacebook(final Handler h){
		FacebookSessionEvents.onLogoutBegin();
		mAsyncRunner.logout(getApplicationContext(), new BaseRequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				h.post(new Runnable() {
	                public void run() {
	                    FacebookSessionEvents.onLogoutFinish();
	                }
	            });
			}
			@Override
			public void onFacebookError(FacebookError e, Object state) {
				MyLog.e(e);
			}
		});
	}
	
	public void authorizeFacebookCallback(int requestCode, int resultCode, Intent data){
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	
}
