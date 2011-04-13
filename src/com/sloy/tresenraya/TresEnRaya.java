package com.sloy.tresenraya;

/**
 * Clase principal del juego. Define toda la lógica del mismo.
 * @author RaFa
 * @version 1.0 Comenzado 25/07/2010 - 21h || Acabado 27/07/2010 - 21h
 */
public class TresEnRaya {
	
	/**
	 * Matriz 3x3 que representa el tablero.
	 */
	public int[][] tablero = new int[3][3];
	
	/**
	 * Identificador del jugador1.
	 */
	public static int JUGADOR1 = 0;
	
	/**
	 * Identificador del jugador2.
	 */
	public static int JUGADOR2 = 1;
	
	/** 
	 * Identificador de jugador nulo
	 */
	public static int NULO = -1;

	/**
	 * Define de quién es el turno actual. Inicialmente el jugador1.
	 */
	private int turno;
	
	// ----- MÉTODOS ------
	
	/** 
	 * Constructor. Vacía el tablero.
	 */
	public TresEnRaya(){
		vaciarTablero();
		turno = JUGADOR1; // Por defecto
	}
	
	/**
	 * Establece el turno.
	 * @param id Id del jugador, [0,1]
	 */
	public void estableceTurno(int id){
		if(id==0||id==1){
			turno = id;
		}
	}
	
	/**
	 * Llena el tabler de fichas nulas.
	 */
	public void vaciarTablero(){
		for(int i=0;i<tablero.length;i++){
			for(int j=0;j<tablero.length;j++){
				tablero[i][j] = NULO;
			}
		}
	}
	
	/**
	 * Pasa a un nuevo turno para el jugador contrario.
	 */
	public int nuevoTurno(){
		return turno = (turno == JUGADOR1) ? JUGADOR2 : JUGADOR1;
	}
	
	/**
	 * Informa si el tablero está lleno.
	 * @return
	 */
	public boolean isLleno(){
		for(int i=0;i<tablero.length;i++){
			for(int j=0;j<tablero.length;j++){
				if(tablero[i][j] == NULO){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Comprueba si la casilla dada está libre o no.
	 * @param y Posición en el eje vertical.
	 * @param x Posición en el eje horizontal.
	 * @return true si está libre, false si está ocupada.
	 */
	public boolean isCasillaLibre(int y, int x){
		if(tablero[y][x]==NULO){
			return true;
		}
		return false;
	}
	
	/**
	 * Coloca una ficha en la posición según las coordenadas dadas. 
	 * @param y Posición en el eje vertical.
	 * @param x Posición en el eje horizontal.
	 */
	public void ponerFicha(int y, int x){
		if(isCasillaLibre(y,x)){
			tablero[y][x] = turno;
		}
	}
	
	/**
	 * Comprueba si hay un ganador en la partida.
	 * @return El identificador del jugador ganador, NULO si no hay.
	 */
	public int ganaPartida(){
		/* Diagonales */
		if(tablero[0][0] != NULO && tablero[0][0] == tablero[1][1] 
		     && tablero [0][0] == tablero[2][2]){
			return tablero[0][0];
		}
		if(tablero[0][2] != NULO && tablero[0][2] == tablero[1][1] 
		        && tablero[0][2] == tablero [2][0]){
			return tablero[0][2];
		}
		/* Horizontales y verticales */
		for(int i=0;i<tablero.length;i++){
			/* Horizontales */
			if(tablero[i][0] != NULO && tablero[i][0] == tablero[i][1]
			         && tablero[i][0] == tablero[i][2]){
				return tablero[i][0];
			}
			/* Verticales */
			if(tablero[0][i] != NULO && tablero[0][i] == tablero[1][i]
			          && tablero[0][i] == tablero[2][i]){
				return tablero[0][i];
			}
		}
		return NULO;
	}
	
	public int getTurno(){
		return turno;
	}
}
