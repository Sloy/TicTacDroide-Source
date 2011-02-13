package com.sloy.tictacdroide.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.openfeint.api.ui.Dashboard;
import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.components.SoundManager;
import com.sloy.tictacdroide.components.ThemeManager;
import com.sloy.tictacdroide.components.Utils;
import com.sloy.tictacdroide.constants.ThemeID;

public class MenuPrincipal extends Activity {
	
//	public String TAG = "TicTacDroide";
	/**
	 * Objeto AlertDialog  para mostrar información.
	 */
	 private AlertDialog alert;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);
        //

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
		SoundManager.playSound(SoundManager.INICIO);
		
        ImageButton bUnJugador = (ImageButton) findViewById(R.id.bUnJugador);
        ImageButton bDosJugadores = (ImageButton) findViewById(R.id.bDosJugadores);
        ImageButton bOpciones = (ImageButton) findViewById(R.id.bOpciones);
        ImageButton bDashboard = (ImageButton) findViewById(R.id.bDashboard);
        ImageButton bTitulo = (ImageButton) findViewById(R.id.bTitulo);
        
        /* Custom theme */
        Drawable auxDrw = ThemeManager.getDrawable(ThemeID.FONDO);
    	// Fondo
    	if(auxDrw!=null)((LinearLayout)findViewById(R.id.linLay_MM)).setBackgroundDrawable(auxDrw);
    	// Titulo
    	auxDrw = ThemeManager.getDrawable(ThemeID.TITULO);
    	if(auxDrw!=null) bTitulo.setImageDrawable(auxDrw);
    	// Icono 1 player
    	auxDrw = ThemeManager.getDrawable(ThemeID.MAINMENU_1P);
    	if(auxDrw!=null) bUnJugador.setImageDrawable(auxDrw);
    	// Icono 2 players
    	auxDrw = ThemeManager.getDrawable(ThemeID.MAINMENU_2P);
    	if(auxDrw!=null) bDosJugadores.setImageDrawable(auxDrw);
    	// Icono opciones
    	auxDrw = ThemeManager.getDrawable(ThemeID.MAINMENU_SETTINGS);
    	if(auxDrw!=null) bOpciones.setImageDrawable(auxDrw);
    	// Icono OpenFeint
    	auxDrw = ThemeManager.getDrawable(ThemeID.MAINMENU_OPENFEINT);
    	if(auxDrw!=null) bDashboard.setImageDrawable(auxDrw);
    	/*/ Custom Theme */
    	
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format(getString(R.string.acercaDialog_text),getString(R.string.app_version),getString(R.string.fecha_actualizacion)))
            .setCancelable(true)
            .setIcon(android.R.drawable.ic_menu_info_details)
            .setInverseBackgroundForced(true)
            .setTitle(R.string.acercaDialog_title)
            .setPositiveButton(R.string.acercaDialog_market, new DialogInterface.OnClickListener() {
                @Override
				public void onClick(DialogInterface dialog, int id) {
                	SoundManager.playSound(SoundManager.NAVEGAR);
                	startActivity(new Intent(Intent.ACTION_VIEW,
        					Uri.parse(getApplicationContext().getString(R.string.market))));
                }
            })
            .setNeutralButton(R.string.alert_button_sugerencias, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sugerencias();
				}
			})
            .setNegativeButton(R.string.menu_volver, new DialogInterface.OnClickListener(){
            	@Override
				public void onClick(DialogInterface dialog, int id) {
            		SoundManager.playSound(SoundManager.ATRAS);
                    dialog.dismiss();
                }
			});
        alert = builder.create();
        
        bUnJugador.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.vibrar(getApplicationContext(), Utils.CORTO);
				startActivityForResult(new Intent(getApplicationContext(), com.sloy.tictacdroide.activities.MenuUnJugador.class), 123);
				SoundManager.playSound(SoundManager.NAVEGAR);
				
			}
		});
        
        bDosJugadores.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.vibrar(getApplicationContext(), Utils.CORTO);
				startActivityForResult(new Intent(getApplicationContext(),com.sloy.tictacdroide.activities.MenuDosJugadores.class), 123);
//				startActivity(new Intent(getApplicationContext(),com.srv.tictacdroide.Resultados.class));
				SoundManager.playSound(SoundManager.NAVEGAR);
			}
		});
        
        bOpciones.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.vibrar(getApplicationContext(), Utils.CORTO);
				startActivityForResult(new Intent(getApplicationContext(),com.sloy.tictacdroide.activities.Opciones.class), Opciones.REQUEST_CODE);
				SoundManager.playSound(SoundManager.NAVEGAR);
			}
		});
        
        bDashboard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Utils.isNetworkAvailable(MenuPrincipal.this)){
					Utils.vibrar(getApplicationContext(), Utils.CORTO);
					Dashboard.open();
					SoundManager.playSound(SoundManager.NAVEGAR);
				}else{
					Toast.makeText(getApplicationContext(), R.string.internet_required, Toast.LENGTH_LONG).show();
				}
			}
		});
        
        bTitulo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.vibrar(getApplicationContext(), Utils.CORTO);
				alert.show();
				SoundManager.playSound(SoundManager.NAVEGAR);
			}
		});
    }
    
    /**
	 * Añade los elementos al menú con sus respectivos iconos.
	 */
  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	  super.onCreateOptionsMenu(menu);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.principal, menu);
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
    		case R.id.menu_acerca:
    			Utils.vibrar(getApplicationContext(), Utils.CORTO);
				alert.show();
				SoundManager.playSound(SoundManager.NAVEGAR);
				break;
    	}
    	return true;
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// Finalizar
           if (resultCode == 321 && requestCode == 123) {
               finish();
            }
           // Ha cambiado el tema
           if(requestCode==Opciones.REQUEST_CODE && resultCode==Opciones.RESULT_CODE_THEME_CHANGED){
        	   Intent i = new Intent(getApplicationContext(),com.sloy.tictacdroide.activities.MenuPrincipal.class);
        	   startActivity(i);
        	   SoundManager.persistent = true;
        	   this.finish();
           }
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_MENU){
			SoundManager.playSound(SoundManager.MENU);
		}
		if(keyCode==KeyEvent.KEYCODE_BACK){
			SoundManager.playSound(SoundManager.ATRAS);
		}
		return super.onKeyDown(keyCode, event);
		
	}
	
	private void sugerencias(){
		/* Muestra un aviso sobre esta opciï¿½n. Si el usuario acepta, le envï¿½a
		 * a la aplicaciï¿½n de email. Si no, nada. */
		AlertDialog.Builder builderSugerencias = new AlertDialog.Builder(this);
		builderSugerencias.setMessage(R.string.email_warning)
			.setPositiveButton(R.string.alert_button_aceptar, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					emailIntent.setType("plain/text");
					emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.email_address)});
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.email_text));
					startActivity(Intent.createChooser(emailIntent, getString(R.string.email_intent)));
				}
			})
			.setNegativeButton(R.string.alert_button_cancelar, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		builderSugerencias.create().show();
	}
	
	
}