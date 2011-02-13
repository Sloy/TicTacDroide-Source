package com.sloy.tictacdroide.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.components.ThemeManager;
import com.sloy.tictacdroide.components.Utils;
import com.sloy.tictacdroide.constants.ThemeID;

public class FirstScreen extends Activity {
	
//	public String TAG = "TicTacDroide";
	private CheckBox cbSonido, cbVibracion;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);
        //
	
    	// Fondo
        Drawable auxDrw = ThemeManager.getDrawable(ThemeID.FONDO);
    	if(auxDrw!=null)((LinearLayout)findViewById(R.id.linLay_FS)).setBackgroundDrawable(auxDrw);
    	
        // Control de audio multimedia
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        if(Utils.preferencias.getBoolean("cbPreguntarSonidos", false)){
        	cbSonido = (CheckBox) findViewById(R.id.cbActivarSonidos);
        	cbSonido.setChecked(Utils.preferencias.getBoolean("cbSonido", true));
        	cbVibracion = (CheckBox) findViewById(R.id.cbActivarVibracion);
        	cbVibracion.setChecked(Utils.preferencias.getBoolean("cbVibrar", true));
        	
        	Button btComenzar = (Button) findViewById(R.id.btFirstGo);
        	btComenzar.setOnClickListener(new View.OnClickListener() {
        		@Override
        		public void onClick(View v) {
        			SharedPreferences.Editor editor = Utils.preferencias.edit();
        			// activar Sonido?
        			Boolean activarSonido = cbSonido.isChecked();
        	    	editor.putBoolean("cbSonido", activarSonido);
        	    	// activar vibración?
        	    	Boolean activarVibracion = cbVibracion.isChecked();
        	    	editor.putBoolean("cbVibrar", activarVibracion);
//        	    	
//        	    	// recordar?
//        	    	CheckBox cbPreguntarSonidos = (CheckBox) findViewById(R.id.cbPreguntarSonidos);
//        	    	editor.putBoolean("cbPreguntarSonidos", cbPreguntarSonidos.isChecked());
        	    	
        	    	// guarda
        	    	editor.commit();
        	    	// comienza...
        	    	continuar();
        		}
			});//OnClickListener
        }else{
        	continuar();
        }//else   
    }//onCreate
    
    private void continuar(){
    	// Acaba con esta actividad para que no vuelva a aparecer con el "atrás"
    	this.finish();
    	// Inicia el menú principal
    	startActivity(new Intent(getApplicationContext(),com.sloy.tictacdroide.activities.MenuPrincipal.class));
    }
    
}