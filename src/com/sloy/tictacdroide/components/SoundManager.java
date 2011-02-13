package com.sloy.tictacdroide.components;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.sloy.tictacdroide.R;


public class SoundManager {
	
	public static final int INICIO=1,NAVEGAR=2,ATRAS=3,MENU=4,PONER_FICHA=5,EMPATE=6,GANA_JUGADOR=7,GANA_MAQUINA=8,NUEVA_PARTIDA=9,CIERRA_PARTIDA=10,RESTAURAR_NOMBRES=11;
	
	static private SoundManager _instance;
	private static SoundPool mSoundPool; 
	private static HashMap<Integer, Integer> mSoundPoolMap; 
	private static AudioManager  mAudioManager;
	private static Context mContext;
	public static boolean persistent = false;
	
	private SoundManager()
	{}
	
	/**
	 * Requests the instance of the Sound Manager and creates it
	 * if it does not exist.
	 * 
	 * @return Returns the single instance of the SoundManager
	 */
	static synchronized public SoundManager getInstance() 
	{
	    if (_instance == null) 
	      _instance = new SoundManager();
	    return _instance;
	 }
	
	/**
	 * Initialises the storage for the sounds
	 * 
	 * @param theContext The Application context
	 */
	public static void initSounds(Context theContext) 
	{ 
		 mContext = theContext;
	     mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
	     mSoundPoolMap = new HashMap<Integer, Integer>(); 
	     mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE); 	    
	} 
	
	/**
	 * Add a new Sound to the SoundPool
	 * 
	 * @param Index - The Sound Index for Retrieval
	 * @param SoundID - The Android ID for the Sound asset.
	 */
	public static void addSound(int index,int soundID)
	{
		Context ctx = mContext;
		try{
			mSoundPoolMap.put(index, mSoundPool.load(ctx, soundID, 1));
		}catch(NullPointerException e){
			Log.d("tictacdroide", "Error when addSound: "+e.getMessage());
		}
	}
	
	/**
	 * Loads the various sound assets
	 * Currently hardcoded but could easily be changed to be flexible.
	 */
	public static void loadSounds()
	{
		addSound(INICIO, R.raw.inicio);
		addSound(NAVEGAR, R.raw.cambio_pantalla);
		addSound(ATRAS, R.raw.atras);
		addSound(MENU, R.raw.menu);
		addSound(PONER_FICHA, R.raw.pone_ficha);
		addSound(EMPATE, R.raw.empate2);
		addSound(GANA_JUGADOR, R.raw.gana_humano);
		addSound(GANA_MAQUINA, R.raw.gana_cpu3);
		addSound(NUEVA_PARTIDA, R.raw.nueva_partida);
		addSound(CIERRA_PARTIDA, R.raw.cierra_partida);
		addSound(RESTAURAR_NOMBRES, R.raw.restaurar_nombres);
	}
	
	/**
	 * Plays a Sound
	 * 
	 * @param index - The Index of the Sound to be played
	 * @param speed - The Speed to play not, not currently used but included for compatibility
	 */
	public static void playSound(int index,float speed) 
	{ 		
		try{
			 if (!Utils.preferencias.getBoolean("cbSonido", true)) return;
		     float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		     streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     mSoundPool.play(mSoundPoolMap.get(index), streamVolume*3/4, streamVolume*3/4, 1, 0, speed);
		}catch(Exception e){
			Log.d("tictacdroide", "Error playing sound: "+e.getMessage());
			loadSounds();
		}
	}
	
	public static void playSound(int index){
		playSound(index,1);
	}
	
	/**
	 * Stop a Sound
	 * @param index - index of the sound to be stopped
	 */
	public static void stopSound(int index)
	{
		mSoundPool.stop(mSoundPoolMap.get(index));
	}
	
	public static void cleanup()
	{
		mSoundPool.release();
		mSoundPool = null;
	    mSoundPoolMap.clear();
	    mAudioManager.unloadSoundEffects();
	    _instance = null;
	}
}