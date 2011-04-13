package com.sloy.tictacdroide.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.components.MyLog;
import com.sloy.tictacdroide.components.Theme;
import com.sloy.tictacdroide.components.ThemeManager;
import com.sloy.tictacdroide.components.Utils;
import com.sloy.tictacdroide.constants.Codes.Results;
import com.sloy.tictacdroide.quickactions.ActionItem;
import com.sloy.tictacdroide.quickactions.QuickAction;
import com.sloy.tictacdroide.quickactions.ThemeLisener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ThemeRepo extends Activity {
	
	private static String GET_THEMES_URL = "http://3.tictacdroide.appspot.com/getthemes";
	private List<Theme> mThemeList;
	private ListView mListView;
	private PackageManager mPackageManager;
	private Window mWindow;
//	private Resources mResources;
	
	private static Boolean marketEnabled;
	
	private enum LinkType{
		MARKET, DIRECT, URL;
	}
	
	private enum ErrorType{
		LAUNCHING_MARKET, LOADING_THEME_LIST_TO_UI;

	}
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWindow = getWindow();
		mWindow.requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//		w.requestFeature(w.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.repo);
		mPackageManager = getPackageManager();
//		mResources = getResources();
		
		/* Quick actions */
		final ActionItem aiDownloadMarket = new ActionItem();
		aiDownloadMarket.setTitle(getString(R.string.repo_action_download_market));
		aiDownloadMarket.setIcon(getResources().getDrawable(R.drawable.action_item_market));
		aiDownloadMarket.setOnClickListener(new ThemeLisener() {
			@Override
			public void onClick(View v) {
				if(DownloadLink.isMarketCapable(this.getTheme())){
					startActivity(new Intent("android.intent.action.VIEW", Uri.parse(this.getTheme().getMarketLink())));
				}else{
					notifyError(ErrorType.LAUNCHING_MARKET);
				}
			}
		});
		
		final ActionItem aiDownloadDirect = new ActionItem();
		aiDownloadDirect.setTitle(getString(R.string.repo_action_download_direct));
		aiDownloadDirect.setIcon(getResources().getDrawable(R.drawable.action_item_apk));
		aiDownloadDirect.setOnClickListener(new ThemeLisener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent("android.intent.action.VIEW", Uri.parse(this.getTheme().getDirectLink())));
			}
		});
		
		final ActionItem aiUninstall = new ActionItem();
		aiUninstall.setTitle(getString(R.string.repo_action_uninstall));
		aiUninstall.setIcon(getResources().getDrawable(R.drawable.action_item_uninstall));
		aiUninstall.setOnClickListener(new ThemeLisener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_DELETE); 
				Uri data = Uri.fromParts("package", this.getTheme().getPackageName(), null);
				intent.setData(data);
				startActivity(intent);
			}
		});
		
		final ActionItem aiApply = new ActionItem();
		aiApply.setTitle(getString(R.string.repo_action_apply));
		aiApply.setIcon(getResources().getDrawable(R.drawable.action_item_apply));
		aiApply.setOnClickListener(new ThemeLisener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = Utils.preferencias.edit();
				editor.putString("themePackageName", this.getTheme().getPackageName());
				editor.commit();
				ThemeManager.setTheme(getApplicationContext());
				setResult(Results.THEME_CHANGED);
				finish();
				Toast.makeText(getApplicationContext(), R.string.repo_applying, Toast.LENGTH_SHORT).show();
			}
		});
		
		final ActionItem aiMarketView = new ActionItem();
		aiMarketView.setTitle(getString(R.string.repo_action_seeMarket));
		aiMarketView.setIcon(getResources().getDrawable(R.drawable.action_item_market));
		aiMarketView.setOnClickListener(new ThemeLisener() {
			@Override
			public void onClick(View v) {
				if(DownloadLink.isMarketCapable(this.getTheme())){
					startActivity(new Intent("android.intent.action.VIEW", Uri.parse(this.getTheme().getMarketLink())));
				}else{
					notifyError(ErrorType.LAUNCHING_MARKET);
				}
				
			}
		});
		
		// Determinar si el market está activo o no
		marketEnabled = canUseMarket();

		mListView = (ListView)findViewById(android.R.id.list);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Theme t = mThemeList.get(pos);
				QuickAction qa = new QuickAction(view, mThemeList.get(pos));
				if(checkThemeExists(t)){
					// ya lo tiene instalado
					qa.addActionItem(aiApply);
					qa.addActionItem(aiUninstall);
					if(DownloadLink.isMarketCapable(t)){
						qa.addActionItem(aiMarketView);
					}
				}else{
					// no está instalado
					if(DownloadLink.isMarketCapable(t)){
						qa.addActionItem(aiDownloadMarket);
					}
					qa.addActionItem(aiDownloadDirect);
				}
				qa.show();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new LoadThemesToUI().execute();
		// Avisa al usuario si no permite aplicaciones externas al Market
		int result = Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
		if (result == 0) {
			new AlertDialog.Builder(this)
				.setTitle(R.string.repo_error_origins_title)
				.setMessage(R.string.repo_error_origins_message)
				.setPositiveButton(R.string.repo_error_origins_button_yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent();
					    intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
					    startActivity(intent);
					}
				})
				.setNegativeButton(R.string.repo_error_origins_button_no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create().show();
		}
	}

	/**
	 * Devuelve una lista con los temas disponibles en el servidor
	 * @return
	 */
	private List<Theme> getThemes() {
		List<Theme> list = new ArrayList<Theme>();
		try {
//			String resp = suputamadre(new URL("http://localhost:8888/getthemes"));
			String resp = Utils.suputamadre(new URL(GET_THEMES_URL));
			JSONObject jsonMain = new JSONObject(resp);

			for (int i = 0; true; i++) {
				String aux = jsonMain.optString("theme_" + i);
				if (aux == ""){
					break;
				}
				list.add(new Theme(new JSONObject(aux)));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	private boolean loadThemesIntoUI(){
		boolean ok = false;
		// Carga la lista de temas
		if(mThemeList!=null){
			try{
				mListView.setAdapter(new ThemesAdapter());
				ok = true;
			}catch(Exception e){
				MyLog.e(e);
			}
		}
		return ok;
	}
	
	private boolean checkThemeExists(Theme t){
		boolean exist = false;
		// Intenta obtener la información del tema ya instalado
		ApplicationInfo appInfo;
		try {
			appInfo = mPackageManager.getApplicationInfo(t.getPackageName(), 0);
		} catch (NameNotFoundException e1) {
			appInfo =null;
		}
		if(appInfo!=null){
			//existe
			exist = true;
		}
		return exist;
	}
	
	private Drawable getInstalledThemeIcon(Theme t){
		Drawable icon = null;
//		if(checkThemeExists(t)){ //no lo compruebo para agilizar la operación
			try {
				icon = mPackageManager.getApplicationIcon(t.getPackageName());
			} catch (NameNotFoundException e) {
//				MyLog.e("El tema -"+t+"- se encontró pero se pudo obtener el icono");
			}
//		}
		return icon;
	}
	
	private boolean canUseMarket(){
		ApplicationInfo appinfo = null;
		try{
			appinfo = mPackageManager.getApplicationInfo("com.android.vending", 0);
		}catch (NameNotFoundException e) {
			MyLog.d("Market no disponible");
		}
		return appinfo!=null;
	}
	
	private void notifyError(ErrorType et){
		String msg;
		switch (et) {
		case LAUNCHING_MARKET:
			msg = getString(R.string.repo_error_notify_launchingMarket);
			break;
		case LOADING_THEME_LIST_TO_UI:
			msg = getString(R.string.repo_error_notify_loadingUI);
			break;
		default:
			msg = getString(R.string.repo_error_notify_unknown);
			break;
		}
			new AlertDialog.Builder(this)
				.setTitle(R.string.repo_error_notify_title)
				.setMessage(msg)
				.setNeutralButton(R.string.repo_error_notify_button,  new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create().show();
	}
	
	
	private class ThemesAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return mThemeList.size();
		}
		@Override
		public Object getItem(int arg0) {
			return mThemeList.get(arg0);
		}
		@Override
		public long getItemId(int arg0) {
			return mThemeList.get(arg0).hashCode();
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			Theme t = mThemeList.get(position);
			if(convertView==null){
				// Infla el layout con el item de tema
				LayoutInflater li = getLayoutInflater();
				v = li.inflate(R.layout.repo_item_theme, null);
				// Establece el nombre
				TextView name = (TextView) v.findViewById(R.id.theme_name);
				name.setText(t.getName());
				// Establece el autor
				TextView author = (TextView) v.findViewById(R.id.theme_author);
				author.setText(getString(R.string.repo_author_prefix)+" "+t.getAuthor());
				// Establece la imagen del tipo (según el tema)
				ImageView type = (ImageView)v.findViewById(R.id.theme_type);
				type.setImageResource(DownloadLink.getActiveLinkDrawableId(t));
				// Acciones según si tiene o no el tema instalado
				ImageView gotit = (ImageView)v.findViewById(R.id.theme_gotit);
				if(checkThemeExists(t)){
					gotit.setImageResource(R.drawable.repo_state_gotit);
				}/*else{ //Ya viene por defecto
					gotit.setImageResource(R.drawable.repo_state_download);
				}*/
				// Establece el icono si puede
				Drawable iconDrw = getInstalledThemeIcon(t);
				if(iconDrw!=null){
					ImageView icon = (ImageView)v.findViewById(R.id.theme_icon);
					icon.setImageDrawable(iconDrw);
				}
			}else{
				v=convertView;
			}
			return v;
		}
	}
	
	private static class DownloadLink{
		public static LinkType getActiveLinkType(Theme mTheme){
			String url = getActiveLink(mTheme);
			LinkType lt;
			if(url.startsWith("market://")||url.startsWith("http://market.android.com/")){
				lt = LinkType.MARKET;
			}else if(url.endsWith(".apk")){
				lt = LinkType.DIRECT;
			}else{
				lt = LinkType.URL;
			}
			return lt;
		}
		public static String getActiveLink(Theme mTheme){
			String res = mTheme.getDirectLink();
			if(marketEnabled && mTheme.hasMarket()){
				res = mTheme.getMarketLink();
			}
			return res;
		}
		public static int getActiveLinkDrawableId(Theme mTheme){
			int res;
			switch(getActiveLinkType(mTheme)){
				case MARKET:
					res = R.drawable.repo_type_market;
					break;
				case DIRECT:
					res = R.drawable.repo_type_apk;
					break;
				case URL:
				default:
					res = R.drawable.repo_type_url;
			}
			return res;
		}
		public static boolean isMarketCapable(Theme t){
			return marketEnabled && t.getMarketLink()!="";
		}

	}
	
	private class LoadThemesToUI extends AsyncTask<Void, Void, List<Theme>>{

		@Override
		protected void onPostExecute(List<Theme> adapter) {
			MyLog.d("OnPostExecute");
			if(!loadThemesIntoUI()){
				notifyError(ErrorType.LOADING_THEME_LIST_TO_UI);
			}
			setProgressBarIndeterminateVisibility(false);
			super.onPostExecute(adapter);
		}

		@Override
		protected void onPreExecute() {
			MyLog.d("OnPreExecute");
			setProgressBarIndeterminateVisibility(true);
			super.onPreExecute();
		}

		@Override
		protected List<Theme>  doInBackground(Void... params) {
			MyLog.d("doInBackground");
			// [TEST] Empieza a contar...
			long tBefore = System.currentTimeMillis();
			// Carga la lista de temas disponibles
			mThemeList = getThemes();
			// [TEST] ...acaba la cuenta, y muestra el tiempo
			long tAfter = System.currentTimeMillis();
			MyLog.d("Ha tardado: "+(tAfter - tBefore) + " milisegundos");
			return mThemeList;
			
		}
	}
}
