package com.sloy.tictacdroide.components;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.sloy.tictacdroide.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {

	public static boolean showAds = true;
	public static SharedPreferences preferencias;
	public static Vibrator v = null;
	public static final int CORTO = 50;
	public static final int LARGO = 500;
	
	
	static public void vibrar(Context ctx, int t){
		//boolean activo = Utils.p.getBoolean("cbVibrar", true);
		if(!preferencias.getBoolean("cbVibrar", true)){
			return;
		}
		if(v==null){
			v = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
		}
		v.vibrate(t);
	}
	
	static public void guardarNombre(String id, String nombre){
		SharedPreferences.Editor editor = preferencias.edit();
    	editor.putString(id, nombre);
    	editor.commit();
	}
	
	static public Animation animar(Context ctx, int id){
		int res = R.anim.none;
		if(Utils.preferencias.getBoolean("cbAnimaciones", true)){
			res = id;
		}
		return AnimationUtils.loadAnimation(ctx, res);
	}
	
	public static boolean isNetworkAvailable(Context ctx) {
		ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String suputamadre(URL url) throws IOException{
		HttpURLConnection c = (HttpURLConnection) url.openConnection();
		c.setRequestMethod("GET");
		c.setReadTimeout(15 * 1000);
		c.setUseCaches(false);
		c.connect();
		// read the output from the server
		BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line + "\n");
		}
		String fuck =  stringBuilder.toString();	
		return fuck;
	}
	
	/* Drawable from URL */
	
}