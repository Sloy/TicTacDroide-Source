package com.sloy.tictacdroide.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.sloy.tictacdroide.R;


public class FinalScreen extends Activity {

	public static final String ESTADO_INT = "estado";
	public static final String GANADOR_STR = "ganador";
	public static final String PUNTUACION_1_INT = "punt1";
	public static final String PUNTUACION_2_INT = "punt2";
	public static final String DIFICULTAD_INT = "dif";
	
	public static final int REQUEST_CODE = 15;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.final_screen);
        setTitle("Fin de partida");
        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        // Recoge la información recibida de fuera
        Bundle bundle = getIntent().getExtras();
        if(bundle==null){
        	Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        	finish(); //Si no recibe nada cierra la Activity
        }
        
        int estado = bundle.getInt(ESTADO_INT);
        String ganador = bundle.getString(GANADOR_STR);
        int puntuacion1 = bundle.getInt(PUNTUACION_1_INT);
        int punuacion2 = bundle.getInt(PUNTUACION_2_INT);
        int dificultad = bundle.getInt(DIFICULTAD_INT);
        
        Toast.makeText(this, "gana "+ganador, Toast.LENGTH_SHORT).show();
        
        /* -- Datos a recibir -- 
         * Estado de la partida (-1,0,1)
         * Nombre del ganador
         * Puntuaciones de ambos jugadores
         * Dificultad del bot (0=humano)
         * */
        
        
	}

}
