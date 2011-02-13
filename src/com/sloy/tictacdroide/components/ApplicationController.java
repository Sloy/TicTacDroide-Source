package com.sloy.tictacdroide.components;

import android.app.Application;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.openfeint.api.Notification;
import com.openfeint.api.OpenFeint;
import com.openfeint.api.OpenFeintDelegate;
import com.openfeint.api.OpenFeintSettings;
import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.openfeint.NotificationsDelegate;
import com.sloy.tictacdroide.openfeint.OFCredentials;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

public class ApplicationController extends Application {

	private Twitter twitter;
	private RequestToken currentRequestToken;
	private OAuthHelper oAuthHelper;
	  
	@Override
	public void onCreate() {
		super.onCreate();
		
		/* Inicia el asunto Twitter */
		oAuthHelper = new OAuthHelper(this);
	    twitter = new TwitterFactory().getInstance();
	    oAuthHelper.configureOAuth(twitter);
	    
		/* Inicializa la referencia a SharedPreferences */
        PreferenceManager.setDefaultValues(this, R.xml.opciones, false);
        Utils.preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        
        //Create, Initialise and then load the Sound manager
        SoundManager.getInstance();
        SoundManager.initSounds(this);
        SoundManager.loadSounds();
        
        /* Zona del maestro */
        // Establece el tema de la aplicación
        ThemeManager.setTheme(this);
        
        /* OpenFeint */
        // crea el objeto para configuración
		OpenFeintSettings settings = new OpenFeintSettings(OFCredentials.NAME, OFCredentials.KEY, OFCredentials.SECRET, OFCredentials.ID);
		// inicializa  con las opciones anteriores y el delegado por defecto (llama al login)
		OpenFeint.initialize(this, settings, new OpenFeintDelegate(){});
		// establece delegate para notificaciones (no quiero mostrar cuando actualiza %)
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
		Log.d("tictacdroide", "Application: onLowMemory() - ending application");
		try {
			this.finalize();
		} catch (Throwable e) {
			Log.e("tictacdroide", "Error ending application: "+e.toString());
		}
	}
	
	/* Twitter stuff */
	public Twitter getTwitter() {
	    return twitter;
	  }

	  public boolean isAuthorized() {
	    return oAuthHelper.hasAccessToken();
	  }
	  
	  public String beginAuthorization() {
	    try {
	      //if (null == currentRequestToken) {
	      if(true){
	        currentRequestToken = twitter.getOAuthRequestToken();
	      }
	      return currentRequestToken.getAuthorizationURL();
	    } catch (TwitterException e) {
	      e.printStackTrace();
	    } catch(Exception e){
	    	e.printStackTrace();
	    }
	    return null;
	  }
	  
	  public boolean authorize(String pin) {
	    try {
	      AccessToken accessToken = twitter.getOAuthAccessToken(currentRequestToken, pin);
	      oAuthHelper.storeAccessToken(accessToken);
	      return true;
	    } catch (TwitterException e) {
	      throw new RuntimeException("Unable to authorize user", e); 
	    }
	  }

	  public void authorized() {
	    try {
	      AccessToken accessToken = twitter.getOAuthAccessToken();
	      oAuthHelper.storeAccessToken(accessToken);
	    } catch (TwitterException e) {
	      throw new RuntimeException("Unable to authorize user", e); 
	    }
	    
	  }
	  
	  public void logout(){
		  oAuthHelper.removeAccessToken();
		  oAuthHelper.configureOAuth(twitter);
	  }
	  
	  public void login(){
		  startActivity(new Intent(this, com.sloy.tictacdroide.activities.TwitterAuthorizationActivity.class));
	  }
	  

}
