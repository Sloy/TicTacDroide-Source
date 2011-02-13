package com.sloy.tresenraya;

import java.util.Random;

public class CopyOfBot {
	
	int[][] tablero;
	
	private int idBot;
	private int idOponente;
	
	/** 
	 * Identificador de jugador nulo
	 */
	public static int NULO = -1;
	
	public CopyOfBot(int[][] tablero, int idBot){
		this.tablero = tablero;
		this.idBot = idBot;
		this.idOponente = (idBot==0) ? 1 : 0;
		
	}
	
	public int[] mejorPosicion(){
		/* Buscar jaque mate */
		int[] jaque = buscarJaque();
		if(jaque[0]!=-1 && jaque[1]!=-1){
			return jaque;
		}
		
		/* Buscar amenaza */
		int[] amenaza = buscarAmenaza();
		if(amenaza[0]!=-1 && amenaza[1]!=-1){
			return amenaza;
		}
		
		/* Si el centro está libre coloca la ficha ahí */
		if(isCentroLibre()){
			return new int[] {1,1};
		}
		
		/* No hay jugadas, posición aleatoria */
		return posicionAleatoria();
	}
	
	/**
	 * Busca posibles amenazas del jugador contrario.
	 * @return Array con las coordenadas de la amenaza, null si no hay amenazas.
	 */
	public int[] buscarJaque(){
		int[] jaque = {-1,-1};
		for(int i=0;i<this.tablero.length;i++){
			for(int j=0;j<this.tablero.length;j++){
				if(tablero[i][j] == NULO){ // Si la casilla está vacía
					int[][] tableroAlternativo = clonarTablero(this.tablero);
					tableroAlternativo[i][j] = idBot;
					if(ganaPartida(tableroAlternativo) == idBot){
						jaque[0] = i;
						jaque[1] = j;
						return jaque;
					}
				}
			}
		}
		return jaque;
	}
	
	/**
	 * Busca posibles amenazas del jugador contrario.
	 * @return Array con las coordenadas de la amenaza, null si no hay amenazas.
	 */
	public int[] buscarAmenaza(){
		int[] amenaza = {-1,-1};
		for(int i=0;i<this.tablero.length;i++){
			for(int j=0;j<this.tablero.length;j++){
				if(tablero[i][j] == NULO){ // Si la casilla está vacía
// no funciona		int[][] tableroAlternativo = this.tablero.clone();
					int[][] tableroAlternativo = clonarTablero(this.tablero);
					tableroAlternativo[i][j] = idOponente;
					if(ganaPartida(tableroAlternativo) == idOponente){
						amenaza[0] = i;
						amenaza[1] = j;
						return amenaza;
					}
				}
			}
		}
		return amenaza;
	}

	/**
	 * Comprueba si el centro está libre.
	 * @return true si está libre, false si está ocupado.
	 */
	public boolean isCentroLibre(){
		if(tablero[1][1]==-1){
			return true;
		}
		return false;
	}
	
	/** 
	 * Busca una posición aleatoria
	 * @param tablero
	 * @return coordenadas de una posición aleatoria libre.
	 */
	public int [] posicionAleatoria(){
		int y,x;
		while(true){
			Random rand = new Random();
			y = rand.nextInt(3);
			x = rand.nextInt(3);
			if(this.tablero[y][x]==-1){
				break;
			}
		} 
		int[] coord = {y,x};
		return coord;
	}
	
	/**
	 * Comprueba si en el tablero ha ganado alguien.
	 * @param tablero
	 * @return
	 */
	public int ganaPartida(int[][] tab){
		/* Diagonales */
		if(tab[0][0] != NULO && tab[0][0] == tab[1][1] 
		     && tab [0][0] == tab[2][2]){
			return tab[0][0];
		}
		if(tab[0][2] != NULO && tab[0][2] == tab[1][1] 
		        && tab[0][2] == tab [2][0]){
			return tab[0][2];
		}
		/* Horizontales y verticales */
		for(int i=0;i<tab.length;i++){
			/* Horizontales */
			if(tab[i][0] != NULO && tab[i][0] == tab[i][1]
			         && tab[i][0] == tab[i][2]){
				return tab[i][0];
			}
			/* Verticales */
			if(tab[0][i] != NULO && tab[0][i] == tab[1][i]
			          && tab[0][i] == tab[2][i]){
				return tab[0][i];
			}
		}
		return NULO;
	}
	
	
	/**
	 * Clona el tablero valor a valor para evitar interferencias.
	 * @param tablero tablero original.
	 * @return Matriz con un nuevo tablero independiente.
	 */
	public static int[][] clonarTablero(int[][] tablero){
		int[][] tableroAlternativo = new int[tablero.length][tablero.length];
		for(int i=0;i<tablero.length;i++){
			for(int j=0;j<tablero.length;j++){
				tableroAlternativo[i][j] = tablero[i][j];
			}
		}
		return tableroAlternativo;
	}
	
}
