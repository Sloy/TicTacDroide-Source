package com.sloy.tictacdroide.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.openfeint.api.resource.Achievement;
import com.sloy.tictacdroide.R;
import com.sloy.tictacdroide.components.SoundManager;
import com.sloy.tictacdroide.components.StatisticsDBAdapter;
import com.sloy.tictacdroide.components.ThemeManager;
import com.sloy.tictacdroide.components.Utils;
import com.sloy.tictacdroide.constants.AnimationsID;
import com.sloy.tictacdroide.constants.ThemeID;
import com.sloy.tictacdroide.constants.Codes.Requests;
import com.sloy.tictacdroide.constants.Codes.Results;
import com.sloy.tictacdroide.openfeint.AchievementsChecker;
import com.sloy.tictacdroide.openfeint.AchievementsID;
import com.sloy.tictacdroide.openfeint.ScoreSubmit;
import com.sloy.tresenraya.Jugador;
import com.sloy.tresenraya.Partida;

import java.util.Date;

public class PartidaTicTac extends Activity implements OnClickListener {

//	public String TAG;
	
	private static Double PTS_CREA_AMENAZA = 5d;
	private static Double PTS_CORTA_AMENAZA = 3d;
	private static Double PTS_GANA = 10d;
	private static Double PTS_PIERDE = -5d;
	private static Integer DELAY_FICHA = 500;
	private static Integer DELAY_PARTIDA = 700;
	
	private long momentoInicio, momentoFin;
	
	/**
	 * Array de objetos ImageButton, representación gráfica del tablero.
	 */
	private ImageButton[][] casillas = new ImageButton[3][3];
	
	/**
	 * TextView's con los nombres de los jugadores // y los contadores
	 */
	private TextView lJugador1, lJugador2, countJugador1, countJugador2, countEmpate; 
	
	/**
	 * Iconos de los jugadores
	 */
	private ImageView imgJugador1,imgJugador2;
	 
	 /**
	  * Objeto Partida que maneja el juego.
	  */
	 Partida p;
	 
	 /**
	  * Quién comienza el turno.
	  */
	 private int turnoInicial, empieza=1;
	 
	 /**
	  * Nombres de los jugadores recogidos del Activity anterior.
	  */
	 private String[] nombre = new String[2];
	 
	 /**
	  * Si los jugadores son humanos o no, recogido del Activity anterior.
	  */
	 private boolean[] humano = new boolean[2];
	 
	 /**
	  * Dificultad de los jugadores.
	  */
	 private int[] dificultad = new int[2];
	 
	 /**
	  * Contadores internos.
	  */
	 private int count1=0,count2=0,count0=0;
	 
	 /**
	  * 
	  */
	 private StatisticsDBAdapter mDbHelper;
	 
	 private Drawable fichaX=null,fichaO=null,fichaN=null,fichaXNull=null,fichaONull=null,fichaEndX=null,fichaEndO=null,fichaEndTie=null;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.partida);
        //
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
		if(Utils.showAds){
			AdView adView = (AdView)findViewById(R.id.ad);
			AdRequest request = new AdRequest();
//			request.setTesting(true);
		    adView.loadAd(request);
			adView.setVisibility(View.VISIBLE);
		}
        
//        TAG = getString(R.string.TAG);
        /* Obtiene los datos de la actividad que la llama */
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
        	for(int i=0;i<2;i++){
        		nombre[i] = fromFuture(bundle.getStringArray("nombre")[i]);
        		humano[i] = bundle.getBooleanArray("humano")[i];
        		dificultad[i] = bundle.getIntArray("dificultad")[i];
        		turnoInicial = bundle.getInt("turnoInicial");
        	}
        }
        
        /* Prepara los elementos gráficos de los jugadores */
        lJugador1 = (TextView) findViewById(R.id.lbJugador1);
        lJugador1.setText(this.nombre[0]);
        lJugador2 = (TextView) findViewById(R.id.lbJugador2);
        lJugador2.setText(this.nombre[1]);
        countJugador1 = (TextView) findViewById(R.id.countJugador1);
        countJugador2 = (TextView) findViewById(R.id.countJugador2);
        countEmpate = (TextView) findViewById(R.id.countEmpate);
        imgJugador1 = (ImageView) findViewById(R.id.imgJugador1);
        imgJugador2 = (ImageView) findViewById(R.id.imgJugador2);
        
        /* Prepara el tablero */
        casillas[0][0] = (ImageButton) findViewById(R.id.cas00);
        casillas[0][1] = (ImageButton) findViewById(R.id.cas01);
        casillas[0][2] = (ImageButton) findViewById(R.id.cas02);
        casillas[1][0] = (ImageButton) findViewById(R.id.cas10);
        casillas[1][1] = (ImageButton) findViewById(R.id.cas11);
        casillas[1][2] = (ImageButton) findViewById(R.id.cas12);
        casillas[2][0] = (ImageButton) findViewById(R.id.cas20);
        casillas[2][1] = (ImageButton) findViewById(R.id.cas21);
        casillas[2][2] = (ImageButton) findViewById(R.id.cas22);  
        for(int i=0;i<casillas.length;i++){
    		for(int j=0;j<casillas.length;j++){
    			casillas[i][j].setOnClickListener(this);
    		}
    	}
        
        /* Custom theme */
        Drawable auxDrw = ThemeManager.getDrawable(ThemeID.FONDO);
    	// Fondo
        if(auxDrw!=null){
			((LinearLayout)findViewById(R.id.linLay_P)).setBackgroundDrawable(auxDrw);
		}
        // Titulo
    	auxDrw = ThemeManager.getDrawable(ThemeID.TITULO);
    	if(auxDrw!=null){
			((ImageView)findViewById(R.id.imgTitulo)).setImageDrawable(auxDrw);
		}
    	// Tablero
    	auxDrw = ThemeManager.getDrawable(ThemeID.TABLERO);
    	if(auxDrw!=null){
			((TableLayout)findViewById(R.id.tablero)).setBackgroundDrawable(auxDrw);
		}
    	// -- Dinamic Drawables --
    	// Ficha X
    	auxDrw = ThemeManager.getDrawable(ThemeID.FICHA_X);
    	if(auxDrw!=null){
    		fichaX =  auxDrw;
    	}else{
    		fichaX = getResources().getDrawable(R.drawable.player_x);
    	}
    	// Ficha X Gray
    	auxDrw = ThemeManager.getDrawable(ThemeID.FICHA_X_INACTIVE);
    	if(auxDrw!=null){
    		fichaXNull =  auxDrw;
    	}else{
    		fichaXNull = getResources().getDrawable(R.drawable.player_x_inactive);
    	}
    	// Ficha O
    	auxDrw = ThemeManager.getDrawable(ThemeID.FICHA_O);
    	if(auxDrw!=null){
    		fichaO =  auxDrw;
    	}else{
    		fichaO = getResources().getDrawable(R.drawable.player_o);
    	}
    	// Ficha O Gray
    	auxDrw = ThemeManager.getDrawable(ThemeID.FICHA_O_INACTIVE);
    	if(auxDrw!=null){
    		fichaONull =  auxDrw;
    	}else{
    		fichaONull = getResources().getDrawable(R.drawable.player_o_inactive);
    	}
    	// Ficha N
    	auxDrw = ThemeManager.getDrawable(ThemeID.FICHA_NONE);
    	if(auxDrw!=null){
    		fichaN =  auxDrw;
    	}else{
    		fichaN = getResources().getDrawable(R.drawable.player_none);
    	}
    	// Ficha X Small
    	auxDrw = ThemeManager.getDrawable(ThemeID.FICHA_END_X);
    	if(auxDrw!=null){
    		fichaEndX =  auxDrw;
    	}else{
    		fichaEndX = getResources().getDrawable(R.drawable.end_x);
    	}
    	// Ficha O Small
    	auxDrw = ThemeManager.getDrawable(ThemeID.FICHA_END_O);
    	if(auxDrw!=null){
    		fichaEndO =  auxDrw;
    	}else{
    		fichaEndO = getResources().getDrawable(R.drawable.end_o);
    	}
    	// Ficha Gray Small
    	auxDrw = ThemeManager.getDrawable(ThemeID.FICHA_END_TIE);
    	if(auxDrw!=null){
    		fichaEndTie =  auxDrw;
    	}else{
    		fichaEndTie = getResources().getDrawable(R.drawable.end_tie);
    	}
        
        /* Prepara el acceso a la base de datos */
        mDbHelper = new StatisticsDBAdapter(this);
        mDbHelper.open();
        
        /* Comienza el juego */
        this.nuevaPartida();
    }
    
    /**
     * Recoge los clics en la pantalla
     */
    @Override
	public void onClick(View v){
    	if(p.esperandoRespuesta){
	    	for(int i=0;i<casillas.length;i++){
	    		for(int j=0;j<casillas.length;j++){
	    			if (casillas[i][j].isPressed()){
	    				if(p.isCasillaLibre(i, j)){
	    					this.ponerFicha(i, j);
							p.esperandoRespuesta = false;
							this.acabaTurno();
	    				}
	    			}
	    		}
	    	}
    	}
    }
    
	/**
	 * Añade los elementos al menú con sus respectivos iconos.
	 */
  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	  super.onCreateOptionsMenu(menu);
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.partida, menu);
	  return true;
    }
  
  /**
   * Añade las acciones al menú según el botón que se pulse. 
   */
  	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    		case R.id.menu_salir:
    			new AlertDialog.Builder(this)
    				.setTitle(R.string.menu_salir)
    				.setMessage(R.string.confirmarSalir)
    				.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
    				.setPositiveButton(R.string.Si, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							cierraJuego();
						}
					})
					.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					})
    				.create().show();
    			break;
    		case R.id.menu_volver:
    			cierraPartida();
    			break;
    		case R.id.menu_nuevaPartida:
    			nuevaPartida();
    			break;
    	}
    	return true;
    }
    
    /**
	 * Coloca una ficha en el tablero
	 * @param y
	 * @param x
	 */
	public void ponerFicha(int y, int x){
		/* Pone la ficha en el objeto de la partida */
		p.ponerFicha(y, x);
		
		/* Comprueba si ha creado situación de amenaza para sumar puntos */
		int[] jaque = getJugadorActivo().getBot().buscarJaque();
		if(jaque[0]!=-1&&jaque[1]!=-1){
			// hay amenaza, suma 5 puntos
			getJugadorActivo().addPuntos(PTS_CREA_AMENAZA);
		}
		
		/* Si estaba bajo amenaza, comprueba que la haya cortado para dar puntos */
		if(getJugadorActivo().getBot().bajoAmenaza){
			int[] amenaza = getJugadorActivo().getBot().buscarAmenaza();
			if(amenaza[0]==-1&&amenaza[1]==-1){
				 getJugadorActivo().addPuntos(PTS_CORTA_AMENAZA);
			}
		}
		
		/* Comptueba la ficha correspondiente al jugador*/
		Drawable drw;
		if(getJugadorActivo().getFicha().equalsIgnoreCase("X")){
			drw = fichaX;
		}else if(getJugadorActivo().getFicha().equalsIgnoreCase("O")){
			drw = fichaO;
		}else{
			drw = fichaN;
		}
		this.casillas[y][x].setImageDrawable(drw);
		this.casillas[y][x].startAnimation(Utils.animar(getApplicationContext(), AnimationsID.PONER_FICHA));
		
		/* Reproduce un sonido */
		SoundManager.playSound(SoundManager.PONER_FICHA);
		/* Crea una vibración */
		Utils.vibrar(getApplicationContext(), Utils.CORTO);
	}
	
	/**
	 * Vacía el tablero de fichas
	 */
	public void limpiarTablero(){
		for(int i=0;i<casillas.length;i++){
			for(int j=0;j<casillas.length;j++){
				casillas[i][j].setImageDrawable(fichaN);
			}
		}
	}

	/**
     * Genera una nueva partida
     */
    public void nuevaPartida(){
    	this.momentoInicio = new Date().getTime();
    	this.limpiarTablero();
    	findViewById(R.id.tablero).startAnimation(Utils.animar(getApplicationContext(), AnimationsID.LIMPIAR_TABLERO));
    	
		/* Empieza partida */
    	if(turnoInicial<0){
    		switch(empieza){
    		case 0:
    			empieza=1;
    			break;
    		case 1:
    			empieza=0;
    			break;
    		}
    	}else{
    		empieza=turnoInicial;
    	}
		p = new Partida(this.nombre,this.humano,dificultad,empieza);		
		
		/* sonido */
		SoundManager.playSound(SoundManager.NUEVA_PARTIDA);
		/* Empieza el turno */
		empiezaTurno();
	}
    
	/**
	 * Empieza el turno actual.
	 */
	public void empiezaTurno(){
        
		/* Establece los iconos de los jugadores en color o gris según turno */
		if(p.getTurno()==0){
			imgJugador1.setImageDrawable(fichaX);
			imgJugador2.setImageDrawable(fichaONull);
		}else{
			imgJugador1.setImageDrawable(fichaXNull);
			imgJugador2.setImageDrawable(fichaO);
		}
		
		/* Comprueba si está bajo amenaza, para luego evaluar si la ha cortado */
		int[] amenaza = getJugadorActivo().getBot().buscarAmenaza();
		if(amenaza[0]!=-1&&amenaza[1]!=-1){
			 getJugadorActivo().getBot().bajoAmenaza = true;
		}else{
			getJugadorActivo().getBot().bajoAmenaza = false;
		}
		
		if(getJugadorActivo().isHumano()){ // Si es humano
			p.turnoHumano();
		}else{ // Si es CPU
			new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                	int[] coord = p.turnoCPU();
                	ponerFicha(coord[0], coord[1]);
                	acabaTurno();
                }
            }, DELAY_FICHA); 
		}
	}
	
	/**
	 * Instrucciones de comprobación cuando acaba un turno.
	 */
	public void acabaTurno(){
		int anim;
		if(p.nuevoTurno()==0){
			anim = AnimationsID.GIRO_1;
		}else{
			anim = AnimationsID.GIRO_2;
		}
		if(p.jugadores[0].isHumano()&&p.jugadores[1].isHumano()){
			for(int i=0;i<casillas.length;i++){
	    		for(int j=0;j<casillas.length;j++){		
	    			casillas[i][j].startAnimation(Utils.animar(this, anim));
	    		}
	    	}
		}
		/* ¿Gana? */
		int gana = p.ganaPartida();
		if(gana != -1){
			/* gana partida, da puntos exta y quita al perdedor */
			p.jugadores[gana].addPuntos(PTS_GANA);
			p.jugadores[gana==0 ? 1:0].addPuntos(PTS_PIERDE);
			
			String s = String.format(getString(R.string.alert_haGanado),p.jugadores[gana].getNombre());
			Drawable drw;
			if(p.jugadores[gana].getFicha().equalsIgnoreCase("X")){
				drw = fichaEndX;
			}else{
				drw = fichaEndO;
			}
			
			if(gana==0){
				count1++;
			}else{
				count2++;
			}
			this.finPartida(gana,s,drw);			
			if(p.jugadores[gana].isHumano()){
				SoundManager.playSound(SoundManager.GANA_JUGADOR);
			}else{
				SoundManager.playSound(SoundManager.GANA_MAQUINA);
			}
		}else
		/* ¿Lleno? */
		if(p.isLleno()){
			String s = getString(R.string.alert_empate);
			count0++;
			SoundManager.playSound(SoundManager.EMPATE);
			this.finPartida(gana,s,fichaEndTie);
		}else{
			
			this.empiezaTurno();
		}
		countEmpate.setText(String.valueOf(count0));
		countJugador1.setText(String.valueOf(count1));
		countJugador2.setText(String.valueOf(count2));
	}
	
	public void finPartida(int gana, String s, Drawable drw){
		//para acortar la llamada al siguiente método
		Jugador j1 = p.jugadores[0];
		Jugador j2 = p.jugadores[1];
		
		// Calula el tiempo en milisegundos que ha durado la partida
		this.momentoFin = new Date().getTime();
		Integer diferencia = (int) (momentoFin-momentoInicio);
		final int puntuacion1 = j1.getPuntosFinal(diferencia,j2.getBot().dificultad);
		final int puntuacion2 = j2.getPuntosFinal(diferencia,j1.getBot().dificultad);
		
		/* Guarda las estadísticas en la base de datos */
		mDbHelper.createEntry(gana, diferencia,(int) new Date().getTime(), j1.getNombre(), j1.isHumano(), puntuacion1, j1.getBot().dificultad, j2.getNombre(), j2.isHumano(), puntuacion2, j2.getBot().dificultad);

		
		//mDbHelper.checkAll();
		new AchievementsChecker(getApplicationContext(), mDbHelper);
		
		/* OpenFeint */
		if(!j2.isHumano()){
			new ScoreSubmit(puntuacion1, diferencia, j2.getBot().dificultad, gana, mDbHelper);
		}
		
//		Log.i("tictacdroide", p.jugadores[0].getNombre()+ " tiene " +p.jugadores[0].getPuntosFinal(diferencia,p.jugadores[1].getBot().dificultad)+" puntos");
//		Log.i("tictacdroide", p.jugadores[1].getNombre()+ " tiene " +p.jugadores[1].getPuntosFinal(diferencia,p.jugadores[1].getBot().dificultad)+" puntos");
		
		
		Utils.vibrar(getApplicationContext(), Utils.LARGO);
		
		// Muestra la Final Screen
		final Intent dialogLauncher = new Intent(getApplicationContext(), com.sloy.tictacdroide.activities.FinalScreen.class);
		dialogLauncher.putExtra(FinalScreen.ESTADO_INT, gana);
		dialogLauncher.putExtra(FinalScreen.JUGADOR_1_STR, j1.getNombre());
		dialogLauncher.putExtra(FinalScreen.JUGADOR_2_STR, j2.getNombre());
		dialogLauncher.putExtra(FinalScreen.PUNTUACION_1_INT, puntuacion1);
		dialogLauncher.putExtra(FinalScreen.PUNTUACION_2_INT, puntuacion2);
		dialogLauncher.putExtra(FinalScreen.DIFICULTAD_INT, j2.isHumano()?0:j2.getBot().dificultad);
		
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
            	startActivityForResult(dialogLauncher, Requests.FINAL_SCREEN);
            }
        }, DELAY_PARTIDA); 
        
	}
	
	
	private Jugador getJugadorActivo(){
		return p.jugadores[p.getTurno()];
	}
	
	private void cierraJuego(){
		setResult(Results.SALIR);
		finish();
	}
	
	private void cierraPartida(){
		SoundManager.playSound(SoundManager.CIERRA_PARTIDA);
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Requests.FINAL_SCREEN){
			// Comprueba la acción que desea hacer el usuario
			switch (resultCode) {
			case Results.NUEVA_PARTIDA:
				nuevaPartida();
				break;
			case Results.MENU_PARTIDA:
				cierraPartida();
				break;
			case Results.MENU_PRINCIPAL:
				setResult(Results.MENU_PRINCIPAL);
				cierraPartida();
				break;
			case Results.SALIR:
				cierraJuego();
			default:
				Log.d("tictacdrodie", "Respuesta por defecto");
				Toast.makeText(this, "Respuesta por defecto", Toast.LENGTH_SHORT).show();
				nuevaPartida();
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_MENU){
			SoundManager.playSound(SoundManager.MENU);
		}
		if(keyCode==KeyEvent.KEYCODE_BACK){
			cierraPartida();
		}
		return super.onKeyDown(keyCode, event);
		
	}
	
	
	private String fromFuture(String s){
		String nombre = s;
		if(s.equalsIgnoreCase("HONEYCOMB")){
			nombre = "Marty McFly";
			Achievement a = new Achievement(AchievementsID.TIME_TRAVELER);
			a.unlock(new Achievement.UnlockCB() {
				@Override
				public void onSuccess(boolean arg0) {
				//	Log.d("tictacdroide", "Time traveler desbloqueado");
				}
			});
		}else if(s.equalsIgnoreCase("MARTY MCFLY")){
			Achievement a = new Achievement(AchievementsID.PILTRAFA);
			a.unlock(new Achievement.UnlockCB() {
				@Override
				public void onSuccess(boolean arg0) {
				//	Log.d("tictacdroide", "Piltrafa desbloqueado");
				}
			});
			AlertDialog.Builder martyBuilder = new AlertDialog.Builder(this);
			martyBuilder.setMessage("Jajajaja ¿Tú Marty McFly? ¡Más quisieras!")
			.setTitle("El futuro es el presente, y éste ya ha pasado")
			.setIcon(R.drawable.ic_menu_emoticons)
			.setNegativeButton(":'(", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			AlertDialog alert = martyBuilder.create();
			alert.show();
			nombre = "Piltrafa";
		}
		return nombre;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mDbHelper.close();
	}
}