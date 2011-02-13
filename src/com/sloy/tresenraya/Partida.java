package com.sloy.tresenraya;

public class Partida extends TresEnRaya{
	
	/**
	 * Booleano que indica si se está esperando la respuesta del usuario.
	 */
	public boolean esperandoRespuesta;
	
	/**
	 * Booleano de estado que define si la partida está en curso o no. 
	 */
	public boolean activa = true;
	
	/**
	 * Array con 2 jugadores.
	 */
	public Jugador[] jugadores = new Jugador[2];
	
	/**
	 * Constructor por defecto.
	 */
	public Partida(){
		String[] nombre = new String[]{"Jugador1", "Jugador2"};
		boolean[] humano = new boolean[]{true,false};
		int[] dif = new int[]{0,3};
		creaJugadores(nombre,humano,dif);
	}
	
	/**
	 * Constructor con parámertos.
	 * @param nombre Array con los nombres de los jugadores.
	 * @param humano Array con los boleanos si son humanos o no los jugadores.
	 */
	public Partida(String[] nombre, boolean[] humano, int[] dificultad, int turnoInicial){
		creaJugadores(nombre, humano, dificultad);
		estableceTurno(turnoInicial);
	}

	/**
	 * Crea los jugadores con los valores dados.
	 * @param nombre Cadena con el nombre del jugador.
	 * @param humano Booleano true si es humano, false si es máquina.
	 */
	public void creaJugadores(String[] nombre, boolean[] humano, int[] dificultad){
		jugadores[0] = new Jugador(nombre[0], 0,"X",humano[0], dificultad[0], this);
		jugadores[1] = new Jugador(nombre[1], 1,"O",humano[1],dificultad[1], this);
	}
	
	public void turnoHumano() {
		esperandoRespuesta = true;
	}

	
	public int[] turnoCPU() {
		int[] coord = this.jugadores[this.getTurno()].mejorPosicion(this.tablero, this.getTurno());
		return coord;
	}
	
	
}
