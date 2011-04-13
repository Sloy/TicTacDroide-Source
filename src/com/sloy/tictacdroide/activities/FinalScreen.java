package com.sloy.tictacdroide.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.components.ApplicationController;
import com.sloy.tictacdroide.components.BaseRequestListener;
import com.sloy.tictacdroide.components.FacebookSessionEvents;
import com.sloy.tictacdroide.components.MyLog;
import com.sloy.tictacdroide.components.StatisticsDBAdapter;
import com.sloy.tictacdroide.components.ThemeManager;
import com.sloy.tictacdroide.components.Utils;
import com.sloy.tictacdroide.constants.ThemeID;
import com.sloy.tictacdroide.constants.Codes.Results;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class FinalScreen extends Activity {

	public static final String ESTADO_INT = "estado";
	public static final String JUGADOR_1_STR = "jug1";
	public static final String JUGADOR_2_STR = "jug2";
	public static final String PUNTUACION_1_INT = "punt1";
	public static final String PUNTUACION_2_INT = "punt2";
	public static final String DIFICULTAD_INT = "dif";

	private ApplicationController mApp; 
	private int estado;
	private String jugador1;
	private String jugador2;
	private Integer puntuacion1;
	private Integer punuacion2;
	private int dificultad;

	private boolean tweeted = false;
	private boolean facebooked = false;
//	private AuthListener authListener;
	
	private Drawable fichaEndX = null, fichaEndO = null, fichaEndTie = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.final_screen);
		setTitle(R.string.fsTitle);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		mApp = ((ApplicationController)getApplication());
		
		/*authListener = new AuthListener() {
			@Override
			public void onAuthSucceed() {
				checkStatus();
			}
			@Override
			public void onAuthFail(String error) {
			}
		};
		*/
//		FacebookSessionEvents.addAuthListener(authListener);

		/* Theme stuff */
		Drawable auxDrw = null;
		// Ficha X Small
		auxDrw = ThemeManager.getDrawable(ThemeID.FICHA_END_X);
		if(auxDrw != null){
			fichaEndX = auxDrw;
		}else{
			fichaEndX = getResources().getDrawable(R.drawable.end_x);
		}
		// Ficha O Small
		auxDrw = ThemeManager.getDrawable(ThemeID.FICHA_END_O);
		if(auxDrw != null){
			fichaEndO = auxDrw;
		}else{
			fichaEndO = getResources().getDrawable(R.drawable.end_o);
		}
		// Ficha Gray Small
		auxDrw = ThemeManager.getDrawable(ThemeID.FICHA_END_TIE);
		if(auxDrw != null){
			fichaEndTie = auxDrw;
		}else{
			fichaEndTie = getResources().getDrawable(R.drawable.end_tie);
		}

		TextView tvGanador = (TextView)findViewById(R.id.fs_gana_text);
		ImageView ivGanador = (ImageView)findViewById(R.id.fs_gana_img);
		((Button)findViewById(R.id.fs_bt_nueva))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						nuevaPartida();
					}
				});
		((Button)findViewById(R.id.fs_bt_volver))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						volver();
					}
				});
		((Button)findViewById(R.id.fs_bt_menu))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						menu();
					}
				});
		((ImageButton)findViewById(R.id.fs_share_twitter))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						compartirTwitter();
					}
				});
		((ImageButton)findViewById(R.id.fs_share_facebook))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						compartirFacebook();
					}
				});

		// Recoge la información recibida del Intent
		Bundle bundle = getIntent().getExtras();
		if(bundle == null){
			MyLog.d("No se recibió datos");
			finish(); // Si no recibe nada cierra la Activity
			return;
		}
		estado = bundle.getInt(ESTADO_INT);
		jugador1 = bundle.getString(JUGADOR_1_STR);
		jugador2 = bundle.getString(JUGADOR_2_STR);
		puntuacion1 = bundle.getInt(PUNTUACION_1_INT);
		punuacion2 = bundle.getInt(PUNTUACION_2_INT);
		dificultad = bundle.getInt(DIFICULTAD_INT);

		if(dificultad == 0){ // humano
			// Pone los títulos de las puntuaciones
			((TextView)findViewById(R.id.fs_puntuacion_1_title))
					.setText(jugador1 + ":");
			((TextView)findViewById(R.id.fs_puntuacion_2_title))
					.setText(jugador2 + ":");
			// Pone la puntuación 2 del jugador 2
			((TextView)findViewById(R.id.fs_puntuacion_2)).setText(punuacion2
					.toString());
			// Quita el menú para compartir
			((LinearLayout)findViewById(R.id.fs_share_menu))
					.setVisibility(View.GONE);
		}else{ // CPU
			// Pone los títulos de las puntuaciones
			((TextView)findViewById(R.id.fs_puntuacion_1_title))
					.setText(R.string.fsYourScore);
			((TextView)findViewById(R.id.fs_puntuacion_2_title))
					.setText(R.string.fsBest);
			StatisticsDBAdapter mDbHelper = new StatisticsDBAdapter(this);
			mDbHelper.open();
			Integer best = mDbHelper.getBestScore(dificultad);
			mDbHelper.close();
			((TextView)findViewById(R.id.fs_puntuacion_2)).setText(best
					.toString());
		}
		// Pone la puntuación del Jugador 1
		((TextView)findViewById(R.id.fs_puntuacion_1)).setText(puntuacion1
				.toString());

		// Según cómo acabe la partida...
		Drawable drw = null;
		switch (estado){
			case -1:
				drw = fichaEndTie;
				tvGanador.setText(R.string.fsTie);
				break;
			case 0:
				drw = fichaEndX;
				tvGanador.setText(String.format(getString(R.string.fsWinner),
						jugador1));
				break;
			case 1:
				drw = fichaEndO;
				tvGanador.setText(String.format(getString(R.string.fsWinner),
						jugador2));
				break;
		}
		ivGanador.setImageDrawable(drw);
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkStatus();
	}

	private void checkStatus() {
		// Comprueba el estado de Twitter y actualiza el icono
		if(mApp.isTwitterAuthorized()){
			((ImageButton)findViewById(R.id.fs_share_twitter))
					.setImageResource(R.drawable.ic_twitter);
		}else{
			((ImageButton)findViewById(R.id.fs_share_twitter))
					.setImageResource(R.drawable.ic_twitter_disabled);
		}
		// Lo mismo para Facebook
		if(mApp.isFacebookAuthorized()){
			((ImageButton)findViewById(R.id.fs_share_facebook))
					.setImageResource(R.drawable.ic_facebook);
		}else{
			((ImageButton)findViewById(R.id.fs_share_facebook))
					.setImageResource(R.drawable.ic_facebook_disabled);
		}
	}

	private void nuevaPartida() {
		setResult(Results.NUEVA_PARTIDA);
		finish();
	}

	private void volver() {
		setResult(Results.MENU_PARTIDA);
		finish();
	}

	private void menu() {
		setResult(Results.MENU_PRINCIPAL);
		finish();
	}

	private void compartirFacebook() {
		if(facebooked){
			Toast.makeText(this, R.string.fsTweetAlready, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		/* Publica el resultado */
		if(!Utils.isNetworkAvailable(getApplicationContext())){
			Toast.makeText(this, R.string.internet_required,Toast.LENGTH_SHORT).show();
			return;
		}
		if(dificultad != 0){
			ApplicationController app = (ApplicationController)getApplication();
			if(!app.isFacebookAuthorized()){
				Toast.makeText(this, R.string.fsFacebookNotConnected,
						Toast.LENGTH_SHORT).show();
				app.beginFacebookAuthorization(FinalScreen.this, new FacebookSessionEvents.LoginDialogListener());
				return;
			}

			String resVerbal = "";
			switch (estado){
				case -1:
					resVerbal = getString(R.string.fsTweetResultTied);
					break;
				case 0:
					resVerbal = getString(R.string.fsTweetResultWon);
					break;
				case 1:
					resVerbal = getString(R.string.fsTweetResultLosed);
					break;
			}
			String lvVerbal = getResources().getStringArray(R.array.dificultad)[dificultad - 1];

			String status = String.format(getString(R.string.fsTweetTemplate),
					resVerbal, lvVerbal, puntuacion1);

			Bundle parameters = new Bundle();
			parameters.putString("message", status);
			final Handler h = new Handler();
			new AsyncFacebookRunner(app.getFacebook()).request("me/feed", parameters, "POST", new BaseRequestListener() {
				@Override
				public void onComplete(String response, Object state) {
					
					h.post(new Runnable() {
		                public void run() {
		                	Toast.makeText(FinalScreen.this, R.string.fsFacebookSent,Toast.LENGTH_SHORT).show();
		                }
					});
				}
			},null);
		}

	}

	private void compartirTwitter() {
		if(tweeted){
			Toast.makeText(this, R.string.fsTweetAlready, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		/* Twitea el resultado */
		if(!Utils.isNetworkAvailable(getApplicationContext())){
			Toast
					.makeText(this, R.string.internet_required,
							Toast.LENGTH_SHORT).show();
			return;
		}
		if(dificultad != 0){
			ApplicationController app = (ApplicationController)getApplication();
			if(!app.isTwitterAuthorized()){
				startActivity(new Intent(
						getApplicationContext(),
						com.sloy.tictacdroide.activities.TwitterAuthorizationActivity.class));
				Toast.makeText(this, R.string.fsTweetNotConnected,
						Toast.LENGTH_SHORT).show();
				return;
			}
			Twitter tw = app.getTwitter();
			String resVerbal = "";
			switch (estado){
				case -1:
					resVerbal = getString(R.string.fsTweetResultTied);
					break;
				case 0:
					resVerbal = getString(R.string.fsTweetResultWon);
					break;
				case 1:
					resVerbal = getString(R.string.fsTweetResultLosed);
					break;
			}
			String lvVerbal = getResources().getStringArray(R.array.dificultad)[dificultad - 1];

			String status = String.format(getString(R.string.fsTweetTemplate),
					resVerbal, lvVerbal, puntuacion1);
			if(status.length() <= 140){
				try{
					tw.updateStatus(status);
					Toast.makeText(this, R.string.fsTweetSent,
							Toast.LENGTH_SHORT).show();
					Utils.vibrar(this, Utils.CORTO);
					tweeted = true;
				}catch(TwitterException e){
					MyLog.e(e);
				}

			}else{
				MyLog.e("Tweet Demasiado largo, bro");
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mApp.authorizeFacebookCallback(requestCode, resultCode, data);
	}
}