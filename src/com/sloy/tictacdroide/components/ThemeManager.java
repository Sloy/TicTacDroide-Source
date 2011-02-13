package com.sloy.tictacdroide.components;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ThemeManager {
	
	//TODO theme constants

	public static final String THEME_DEFAULT = "TicTacDroide default";
	public static Resources themeResources = null;
	public static String themePackage = null;
	
	public static void setTheme(Context ctx, String themePack){
		PackageManager pm = ctx.getPackageManager();
		themePackage = themePack;
    	themeResources = null;
    	try{
    		themeResources = pm.getResourcesForApplication(themePackage);
    	}catch(NameNotFoundException e){
    		Log.d("tictacdroide", "Theme not found: "+e.getMessage());
    	}
	}
	
	public static void setTheme(Context ctx){
		String themePackageName = Utils.preferencias.getString("themePackageName", "");
		ThemeManager.setTheme(ctx, themePackageName);
	}
	
	public static Drawable getDrawable(String name){
		Drawable drw = null;
		try{
			int resource_id = themeResources.getIdentifier(name, "drawable", themePackage);
			if(resource_id!=0){
				drw = themeResources.getDrawable(resource_id);
			}
		}catch(NullPointerException e){
		}
		return drw;
	}
	
	public static FontConfig getFontConfig(){
		/* Font */
		Typeface themeFont = null;
		try{
			themeFont=Typeface.createFromAsset(themeResources.getAssets(), "themefont.ttf");
		}catch(Exception e){
		}
		
		
		/* Color */
		int color = 0;
		try{
			int resource_id = themeResources.getIdentifier("text_color", "color", themePackage);
			if(resource_id!=0){
				color = themeResources.getColor(resource_id);
			}
		}catch(NullPointerException e){
		}
		
		/* Size */
		float size = 0;
		try{
			int resource_id = themeResources.getIdentifier("text_size", "dimen", themePackage);
			if(resource_id!=0){
				size = themeResources.getDimension(resource_id);
			}
		}catch(NullPointerException e){
		}
		
		/* Bold */
		boolean bold = true;
		try{
			int resource_id = themeResources.getIdentifier("text_bold", "bool", themePackage);
			if(resource_id!=0){
				bold = themeResources.getBoolean(resource_id);
			}
		}catch(NullPointerException e){
		}
		
		return new FontConfig(themeFont, color, size, bold);
	}
	
	public static String getString(String name){
		String str = null;
		try{
			int resource_id = themeResources.getIdentifier(name, "string", themePackage);
			if(resource_id!=0){
				str = themeResources.getString(resource_id);
			}
		}catch(NullPointerException e){
		}
		return str;
	}
	
}