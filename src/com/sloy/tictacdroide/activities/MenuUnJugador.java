package com.sloy.tictacdroide.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.components.SoundManager;
import com.sloy.tictacdroide.components.ThemeManager;
import com.sloy.tictacdroide.components.Utils;
import com.sloy.tictacdroide.constants.ThemeID;
import com.sloy.tictacdroide.constants.Codes.Requests;
import com.sloy.tictacdroide.constants.Codes.Results;

public class MenuUnJugador extends Activity {
	
	public String TAG = "TicTacDroide";
	private EditText txtNombre;
	private ImageButton bComenzar;
	private Spinner dificultad;
	private RadioButton rb1, rb2, rb3;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu1jugador);
        // 
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        /* Referencia los objetos de la UI */
        rb1 = (RadioButton) findViewById(R.id.rbX);
        rb2 = (RadioButton) findViewById(R.id.rbO);
        rb3 = (RadioButton) findViewById(R.id.rbRand);
        txtNombre = (EditText) findViewById(R.id.txtNombreJugador);
        dificultad = (Spinner) findViewById(R.id.spDificultad);
        dificultad.setSelection(2);
        bComenzar = (ImageButton) findViewById(R.id.bComenzarPartida);
        bComenzar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.vibrar(getApplicationContext(), Utils.CORTO);
				comenzarPartida();
			}
		});
        
        /* Custom theme */
        Drawable auxDrw = ThemeManager.getDrawable(ThemeID.FONDO);
    	// Fondo
        if(auxDrw!=null)((LinearLayout)findViewById(R.id.linLay_M1)).setBackgroundDrawable(auxDrw);
        // Titulo
    	auxDrw = ThemeManager.getDrawable(ThemeID.TITULO);
    	if(auxDrw!=null) ((ImageView)findViewById(R.id.imgTitulo)).setImageDrawable(auxDrw);
    	// Green EditText
    	auxDrw = ThemeManager.getDrawable(ThemeID.MENU_PLAYER_EDITTEXT_GREEN);
    	if(auxDrw!=null) txtNombre.setBackgroundDrawable(auxDrw);
    	// Dificultad
    	auxDrw = ThemeManager.getDrawable(ThemeID.MENU_PLAYER_SPINNER);
    	if(auxDrw!=null) dificultad.setBackgroundDrawable(auxDrw);
    	// Radio X
    	auxDrw = ThemeManager.getDrawable(ThemeID.MENU_PLAYER_RADIO_X);
    	if(auxDrw!=null) rb1.setButtonDrawable(auxDrw);
    	// Radio O
    	auxDrw = ThemeManager.getDrawable(ThemeID.MENU_PLAYER_RADIO_O);
    	if(auxDrw!=null) rb2.setButtonDrawable(auxDrw);
    	// Radio R
    	auxDrw = ThemeManager.getDrawable(ThemeID.MENU_PLAYER_RADIO_R);
    	if(auxDrw!=null) rb3.setButtonDrawable(auxDrw);
    	// Start button
    	auxDrw = ThemeManager.getDrawable(ThemeID.MENU_PLAYER_START);
    	if(auxDrw!=null) bComenzar.setImageDrawable(auxDrw);
    	/*/ Custom Theme */
    	
        /* Establece los valores según las preferencias */
        String nombre = Utils.preferencias.getString("nombreJugadorSolo", getString(R.string.Jugador));
        txtNombre.setText(nombre);

    }
    
	/**
	 * Añade los elementos al menú con sus respectivos iconos.
	 */
  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	  super.onCreateOptionsMenu(menu);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.jugadores, menu);
	  return true;
    }
  
  /**
   * Añade las acciones al menú según el botón que se pulse. 
   */
  	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    		case R.id.menu_salir:
    			setResult(321);
    			finish();
    			break;
    		case R.id.menu_volver:
    			SoundManager.playSound(SoundManager.ATRAS);
    			finish();
    			break;
    		case R.id.menu_restaurarNombres:
    			if(Utils.preferencias.getBoolean("cbAnimaciones", true)){
    				Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
    				txtNombre.startAnimation(shake);
    			}
    			Utils.vibrar(this, Utils.LARGO);
    			txtNombre.setText(R.string.Jugador);
    			SoundManager.playSound(SoundManager.RESTAURAR_NOMBRES);
    			Utils.guardarNombre("nombreJugadorSolo", getString(R.string.Jugador));
    			break;
    	}
    	return true;
    }
    
    public void comenzarPartida(){
    	/* Calcula el valor de la dificultad */
    	int dif=0;
    	dif = ((int) dificultad.getSelectedItemId())+1;
    	dif = (dif<1 || dif>5) ? 0:dif;
    	int turno = 0;
    	/* Calcula el turno */
    	if(rb1.isChecked()){
    		turno = 0;
    	}else if(rb2.isChecked()){
    		turno = 1;
    	}else if(rb3.isChecked()){
    		turno = -1;
    	}
    	/* Extrae los datos */
    	String nombre = txtNombre.getText().toString();
    	/* Guarda los datos */
    	Utils.guardarNombre("nombreJugadorSolo", nombre);
    	/* Crea el Intent y le pasa los valores a la actividad de la partida */
    	Intent intent = new Intent("com.sloy.tictacdroide.activities.PartidaTicTac");
    	intent.putExtra("nombre", new String[]{nombre,getString(R.string.androide)});
    	intent.putExtra("dificultad", new int[]{0,dif});
    	intent.putExtra("humano", new boolean[]{true,false});
    	intent.putExtra("turnoInicial", turno);
    	startActivityForResult(intent, Requests.PARTIDA);
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
           if (resultCode == Results.SALIR && requestCode == Requests.PARTIDA) {
               setResult(Results.SALIR);
               finish();
            }
           else if(requestCode == Requests.PARTIDA && resultCode == Results.MENU_PRINCIPAL){
        	   finish();
           }
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_MENU){
			SoundManager.playSound(SoundManager.MENU);
		}
		if(keyCode==KeyEvent.KEYCODE_BACK){
			SoundManager.playSound(SoundManager.ATRAS);		}
		return super.onKeyDown(keyCode, event);
		
	}
} 	