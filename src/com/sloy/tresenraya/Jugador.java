package com.sloy.tresenraya;


public class Jugador {

	/**
	 * Cadena con el nombre del jugador.
	 */
	private String nombre;
	
	/**
	 * Booleano que define si es humano o CPU.
	 */
	private boolean humano;
	
	/**
	 * Ficha que representa al jugador en el tablero
	 */
	private String ficha;

	/**
	 * Medición de la puntuación que va obteniendo el jugador a lo largo de la partida.
	 */
	private Double puntos;
	
	/**
	 * 
	 */
	private Bot bot;
	
	/**
	 * Consctructor que define el nombre y el estado de humano.
	 * @param nombre Cadena con el nombre del jugador
	 * @param humano Booleano true si es humano, false si es CPU.
	 * @param p 
	 */
	public Jugador(String nombre, int idYo, String ficha, boolean humano, int dificultad, Partida p){
		this.nombre = nombre;
		this.ficha = ficha;
		this.humano = humano;
		this.puntos = 0.0;
		this.bot = new Bot(p.tablero,idYo, dificultad);
	}


	/** 
	 * Devuelve el nombre del jugador.
	 * @return Cadena con el nombre.
	 */
	public String getNombre(){
		return this.nombre;
	}
	
	/**
	 * Devuelve la fucha del jugador.
	 * @return Cadena con la ficha.
	 */
	public String getFicha(){
		return this.ficha;
	}
	
	/**
	 * Devuelve la puntuación actual del jugador.
	 * @return Double con la puntuación
	 */
	public Double getPuntos(){
		return this.puntos;
	}
	
	/**
	 * Obtiene el bot del jugador
	 * @return referencia al Bot
	 */
	public Bot getBot(){
		return this.bot;
	}
	
	/**
	 * Suma los puntos que se pasan como parámetros a la puntuación del jugador
	 * @param p puntos que se deben sumar
	 * @return La nueva puntuación después de sumarle los puntos.
	 */
	public Double addPuntos(Double p){
		return this.puntos += p;
	}
	
	public Integer getPuntosFinal(Integer tiempo, Integer dificultad){
		return (int) (getPuntos()*((100+dificultad*10)/(tiempo/1000)));
	}
	
	/**
	 * Devuelve si el jugador es humano o no.
	 * @return true si es humano, false si es CPU.
	 */
	public boolean isHumano(){
		return humano;
	}
	
	/**
	 * Bot que detecta la mejor posición posible.
	 */
	public int[] mejorPosicion(int[][] tablero, int idJugador){
		return bot.mejorPosicion();
	}
}
