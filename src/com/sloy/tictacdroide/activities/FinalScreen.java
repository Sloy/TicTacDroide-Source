package com.sloy.tictacdroide.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;

import com.sloy.tictacinterface.R;

public class FinalScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.final_screen);
        setTitle("Fin de partida");
        getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        // Recoge la información recibida de fuera
        Bundle bundle = getIntent().getExtras();
        if(bundle==null) finish(); //Si no recibe nada cierra la Activity
        /* -- Datos a recibir -- 
         * Estado de la partida (-1,0,1)
         * Nombre del ganador
         * Puntuaciones de ambos jugadores
         * Dificultad del bot (0=humano)
         * */
        
	}

}
