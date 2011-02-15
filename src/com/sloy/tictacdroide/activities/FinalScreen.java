package com.sloy.tictacdroide.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sloy.tictacdroide.R;


public class FinalScreen extends Activity {

	public static final String ESTADO_INT = "estado";
	public static final String JUGADOR_1_STR = "jug1";
	public static final String JUGADOR_2_STR = "jug2";
	public static final String PUNTUACION_1_INT = "punt1";
	public static final String PUNTUACION_2_INT = "punt2";
	public static final String DIFICULTAD_INT = "dif";
	
	public static final int REQUEST_CODE = 15;
	public static final int RESULT_NUEVA = 16;
	public static final int RESULT_VOLVER = 17;
	public static final int RESULT_MENU= 18;
	
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
        
        // Recoge la información recibida del Intent
        Bundle bundle = getIntent().getExtras();
        if(bundle==null){
        	Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        	finish(); //Si no recibe nada cierra la Activity
        	return;
        }
        int estado = bundle.getInt(ESTADO_INT);
        String jugador1 = bundle.getString(JUGADOR_1_STR);
        String jugador2 = bundle.getString(JUGADOR_2_STR);
        Integer puntuacion1 = bundle.getInt(PUNTUACION_1_INT);
        Integer punuacion2 = bundle.getInt(PUNTUACION_2_INT);
        int dificultad = bundle.getInt(DIFICULTAD_INT);
        
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
        
        
        if(dificultad==0){ //humano
        	// Pone los títulos de las puntuaciones
        	((TextView)findViewById(R.id.fs_puntuacion_1_title)).setText(jugador1+":");
        	((TextView)findViewById(R.id.fs_puntuacion_2_title)).setText(jugador2+":");
        	// Pone la puntuación 2 del jugador 2
        	((TextView)findViewById(R.id.fs_puntuacion_2)).setText(punuacion2);
        }else{
        	// Pone los títulos de las puntuaciones
        	((TextView)findViewById(R.id.fs_puntuacion_1_title)).setText("Tu puntuación:");
        	((TextView)findViewById(R.id.fs_puntuacion_2_title)).setText("Mejor:");
        	// Pone en la puntuación 2 la mejor
        	//TODO: sacar de la BD la mejor puntuación (de esta dificultad)
        	((TextView)findViewById(R.id.fs_puntuacion_2)).setText("???");
        }
        // Pone la puntuación del Jugador 1
        ((TextView)findViewById(R.id.fs_puntuacion_1)).setText(puntuacion1.toString());
                
	}
	
	private void nuevaPartida(){
		setResult(RESULT_NUEVA);
		finish();
	}
	
	private void volver(){
		setResult(RESULT_VOLVER);
		finish();
	}
	
	private void menu(){
		setResult(RESULT_MENU);
		finish();
	}

}
