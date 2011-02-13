package com.sloy.tresenraya;

import java.util.Random;


public class Bot {
	
	private int[][] tablero,tableroMinimax;
	public boolean bajoAmenaza;
	public int idBot,idOponente,dificultad;
	
	
	public Bot(int[][] tablero, int idBot, int dificultad){
		this.tablero = tablero;
		this.idBot = idBot;
		this.dificultad = (dificultad > 5 || dificultad < 0) ? 3 : dificultad;
		this.idOponente = (idBot==0) ? 1 : 0;
		this.bajoAmenaza = false;
	}
	
	public int[] mejorPosicion(){
		switch(this.dificultad){
		case 5:
			return minimax();
		case 4:
			/* Buscar jaque mate */
			if(buscarJaque()[0]!=-1 && buscarJaque()[1]!=-1){
				return buscarJaque();
			}
			
			/* Buscar amenaza */
			if(buscarAmenaza()[0]!=-1 && buscarAmenaza()[1]!=-1){
				return buscarAmenaza();
			}
			
			/* Si el centro está libre coloca la ficha ahí */
			if(isCentroLibre()){
				return new int[] {1,1};
			}
			
			/* No hay jugadas, posición aleatoria */
			return posicionAleatoria();
		case 3:
			/* Buscar jaque mate */
			if(buscarJaque()[0]!=-1 && buscarJaque()[1]!=-1){
				return buscarJaque();
			}
			
			/* Buscar amenaza */
			if(buscarAmenaza()[0]!=-1 && buscarAmenaza()[1]!=-1){
				return buscarAmenaza();
			}
			
			/* No hay jugadas, posición aleatoria */
			return posicionAleatoria();
		case 2:
			/* Buscar amenaza */
			if(buscarAmenaza()[0]!=-1 && buscarAmenaza()[1]!=-1){
				return buscarAmenaza();
			}
			
			/* No hay jugadas, posición aleatoria */
			return posicionAleatoria();
		
		default:
			/* No hay jugadas, posición aleatoria */
			return posicionAleatoria();
			
		}
	}
	

	
	/**
	 * Busca posibles amenazas del jugador contrario.
	 * @return Array con las coordenadas de la amenaza, null si no hay amenazas.
	 */
	public int[] buscarJaque(){
		int[] jaque = {-1,-1};
		for(int i=0;i<this.tablero.length;i++){
			for(int j=0;j<this.tablero.length;j++){
				if(tablero[i][j] == -1){ // Si la casilla está vacía
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
					if(tablero[i][j] == -1){ // Si la casilla está vacía
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

	/**
	 * Comprueba si en el tablero ha ganado alguien.
	 * @param tablero
	 * @return
	 */
	public int ganaPartida(int[][] tab){
		/* Diagonales */
		if(tab[0][0] != -1 && tab[0][0] == tab[1][1] 
		     && tab [0][0] == tab[2][2]){
			return tab[0][0];
		}
		if(tab[0][2] != -1 && tab[0][2] == tab[1][1] 
		        && tab[0][2] == tab [2][0]){
			return tab[0][2];
		}
		/* Horizontales y verticales */
		for(int i=0;i<tab.length;i++){
			/* Horizontales */
			if(tab[i][0] != -1 && tab[i][0] == tab[i][1]
			         && tab[i][0] == tab[i][2]){
				return tab[i][0];
			}
			/* Verticales */
			if(tab[0][i] != -1 && tab[0][i] == tab[1][i]
			          && tab[0][i] == tab[2][i]){
				return tab[0][i];
			}
		}
		return -1;
	}

	/* -- Algoritmo Minimax -- */
	public int[] minimax(){
		this.tableroMinimax = crearTableroMinimax(this.tablero);
		int[] coord = new int[2];
		int v = Integer.MIN_VALUE;
		int aux;
		for(int i=0;i<tableroMinimax.length;i++){
			for(int j=0;j<tableroMinimax.length;j++){
				if(tableroMinimax[i][j]==-1){
					tableroMinimax[i][j]=1;
					aux=min();
					if(aux>v){
						v=aux;
						coord = new int[]{i,j};
					}
					tableroMinimax[i][j] = -1;
				}
			}
		}
		return coord;

	}
	
	private int min(){
		if(finPartida()){
			return (ganaPartida()!=-1) ? 1 : 0;
		}
		int v=Integer.MAX_VALUE;
		int aux;
		for(int i=0;i<tableroMinimax.length;i++){
			for(int j=0;j<tableroMinimax.length;j++){
				if(tableroMinimax[i][j]==-1){
					tableroMinimax[i][j]=0;
					aux=max();
					if(aux<v){
						v=aux;
					}
					tableroMinimax[i][j] = -1;
				}
			}
		}
		return v;
	}
	
	//
	private int max(){
		if(finPartida()){
			return (ganaPartida()!=-1) ? -1 : 0;
		}
		int v=Integer.MIN_VALUE;
		int aux;
		for(int i=0;i<tableroMinimax.length;i++){
			for(int j=0;j<tableroMinimax.length;j++){
				if(tableroMinimax[i][j]==-1){
					tableroMinimax[i][j]=1;
					aux=min();
					if(aux>v){
						v=aux;
					}
					tableroMinimax[i][j] = -1;
				}
			}
		}
		return v;
	}

	
	public boolean finPartida(){
		return tableroCompleto() || ganaPartida()!=-1;
	}
	
	public boolean tableroCompleto(){
		for(int i=0;i<tableroMinimax.length;i++){
			for(int j=0;j<tableroMinimax.length;j++){
				if(tableroMinimax[i][j]==-1){
					return false;
				}
			}
		}
		return true;
	}
	
	public int ganaPartida(){
		/* Diagonales */
		if(tableroMinimax[0][0] != -1 && tableroMinimax[0][0] == tableroMinimax[1][1] 
		     && tableroMinimax [0][0] == tableroMinimax[2][2]){
			return tableroMinimax[0][0];
		}
		if(tableroMinimax[0][2] != -1 && tableroMinimax[0][2] == tableroMinimax[1][1] 
		        && tableroMinimax[0][2] == tableroMinimax [2][0]){
			return tableroMinimax[0][2];
		}
		/* Horizontales y verticales */
		for(int i=0;i<tableroMinimax.length;i++){
			/* Horizontales */
			if(tableroMinimax[i][0] != -1 && tableroMinimax[i][0] == tableroMinimax[i][1]
			         && tableroMinimax[i][0] == tableroMinimax[i][2]){
				return tableroMinimax[i][0];
			}
			/* Verticales */
			if(tableroMinimax[0][i] != -1 && tableroMinimax[0][i] == tableroMinimax[1][i]
			          && tableroMinimax[0][i] == tableroMinimax[2][i]){
				return tableroMinimax[0][i];
			}
		}
		return -1;
	}
	
	
	/**
	 * Clona el tablero valor a valor para evitar interferencias.
	 * @param tablero tablero original.
	 * @return Matriz con un nuevo tablero independiente.
	 */
	public int[][] crearTableroMinimax(int[][] tablero){
		int[][] tableroMinimax = new int[tablero.length][tablero.length];
		for(int i=0;i<tablero.length;i++){
			for(int j=0;j<tablero.length;j++){
				if(tablero[i][j] == idBot){
					tableroMinimax[i][j] = 1;
				}else if(tablero[i][j] == idOponente){
					tableroMinimax[i][j] = 0;
				}else{
					tableroMinimax[i][j] = -1;
				}
			}
				
		}
		return tableroMinimax;
	}
	
}
