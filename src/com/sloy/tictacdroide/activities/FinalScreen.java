package com.sloy.tictacdroide.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.components.ApplicationController;
import com.sloy.tictacdroide.components.Utils;
import com.sloy.tictacdroide.constants.Codes.Results;

import twitter4j.Twitter;


public class FinalScreen extends Activity {
	

	public static final String ESTADO_INT = "estado";
	public static final String JUGADOR_1_STR = "jug1";
	public static final String JUGADOR_2_STR = "jug2";
	public static final String PUNTUACION_1_INT = "punt1";
	public static final String PUNTUACION_2_INT = "punt2";
	public static final String DIFICULTAD_INT = "dif";
	
	private int estado;
	private String jugador1;
	private String jugador2;
	private Integer puntuacion1;
	private Integer punuacion2;
	private int dificultad;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.final_screen);
        setTitle("Fin de partida");
        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView tvGanador = (TextView)findViewById(R.id.fs_gana_text);
        ImageView ivGanador = (ImageView)findViewById(R.id.fs_gana_img);
        ((Button)findViewById(R.id.fs_bt_nueva)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nuevaPartida();
			}
		});
        ((Button)findViewById(R.id.fs_bt_volver)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				volver();
			}
		});
        ((Button)findViewById(R.id.fs_bt_menu)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				menu();
			}
		});
        ((ImageButton)findViewById(R.id.fs_share_twitter)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				compartir();
			}
		});
        
        // Recoge la información recibida del Intent
        Bundle bundle = getIntent().getExtras();
        if(bundle==null){
        	Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        	finish(); //Si no recibe nada cierra la Activity
        	return;
        }
        estado = bundle.getInt(ESTADO_INT);
        jugador1 = bundle.getString(JUGADOR_1_STR);
        jugador2 = bundle.getString(JUGADOR_2_STR);
        puntuacion1 = bundle.getInt(PUNTUACION_1_INT);
        punuacion2 = bundle.getInt(PUNTUACION_2_INT);
        dificultad = bundle.getInt(DIFICULTAD_INT);
        
        if(dificultad==0){ //humano
        	// Pone los títulos de las puntuaciones
        	((TextView)findViewById(R.id.fs_puntuacion_1_title)).setText(jugador1+":");
        	((TextView)findViewById(R.id.fs_puntuacion_2_title)).setText(jugador2+":");
        	// Pone la puntuación 2 del jugador 2
        	((TextView)findViewById(R.id.fs_puntuacion_2)).setText(punuacion2);
        	// Quita el menú para compartir
        	((LinearLayout)findViewById(R.id.fs_share_menu)).setVisibility(View.GONE);
        }else{ //CPU
        	// Pone los títulos de las puntuaciones
        	((TextView)findViewById(R.id.fs_puntuacion_1_title)).setText("Tu puntuación:");
        	((TextView)findViewById(R.id.fs_puntuacion_2_title)).setText("Mejor:");
        	// Pone en la puntuación 2 la mejor
        	//TODO: sacar de la BD la mejor puntuación (de esta dificultad)
        	((TextView)findViewById(R.id.fs_puntuacion_2)).setText("???");
        }
        // Pone la puntuación del Jugador 1
        ((TextView)findViewById(R.id.fs_puntuacion_1)).setText(puntuacion1.toString());
        
        // Según cómo acabe la partida...
      //  Drawable drw = null;
        int resID = 0;
        switch (estado) {
		case -1:
			//drw = ThemeManager.getDrawable(ThemeID.FICHA_END_TIE);
			resID = R.drawable.end_tie;
			tvGanador.setText("Empate!");
			break;
		case 0:
			//drw = ThemeManager.getDrawable(ThemeID.FICHA_END_X);
			resID = R.drawable.end_x;
			tvGanador.setText(jugador1+" gana!");
			break;
		case 1:
			//drw = ThemeManager.getDrawable(ThemeID.FICHA_END_O); 
			resID = R.drawable.end_o;
			tvGanador.setText(jugador2+" gana!");
			break;
		}
        //ivGanador.setImageDrawable(drw);
        ivGanador.setImageResource(resID);
        
        
        
                
	}
	
	private void nuevaPartida(){
		setResult(Results.NUEVA_PARTIDA);
		finish();
	}
	
	private void volver(){
		setResult(Results.MENU_PARTIDA);
		finish();
	}
	
	private void menu(){
		setResult(Results.MENU_PRINCIPAL);
		finish();
	}
	
	private void compartir(){
		/* Twitea el resultado */
		//TODO: ojo chaval
		if(dificultad!=0){
			ApplicationController app = (ApplicationController) getApplication();
			if(!app.isAuthorized()){
				//TODO: Que te mande a las opciones a conectarte
				Toast.makeText(this, "Debes conectarte antes a Twitter", Toast.LENGTH_SHORT).show();
			}
			Twitter tw = app.getTwitter();
			String resVerbal="";
			switch (estado) {
				case -1:
					resVerbal="empatado";
					break;
				case 0:
					resVerbal="ganado";
					break;
				case 1:
					resVerbal="perdido";
					break;
			}
			String lvVerbal = getResources().getStringArray(R.array.dificultad)[dificultad-1];
			
			String status = "d sloy5101 [Prueba] He "+resVerbal+" a #TicTacDroide en nivel "+lvVerbal+" con una puntuación de "+puntuacion1;
			if(status.length()<=140){
				//tw.updateStatus(status);
				Toast.makeText(this, "Tweet sent", Toast.LENGTH_SHORT).show();
				Utils.vibrar(this, Utils.CORTO);
			}else{
				Log.e("tictacdroide", "Error: Demasiado largo, bro");
			}
		}
	}

}
