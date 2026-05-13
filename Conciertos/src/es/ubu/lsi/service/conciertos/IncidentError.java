package es.ubu.lsi.service.conciertos;

/**
 * Error code.
 * 
 * Listado de posibles errores que se pueden producir.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a> 
 * @author <a href="mailto:srarribas@ubu.es">Sandra Rodríguez</a> 
 * @since 1.0
 *
 */
public enum IncidentError {
	
	// If we have more error messages, add new values to the the end of enum type...
	NOT_EXIST_MUSIC_GROUP("No existe grupo"),
	NOT_EXIST_CLIENT("No existe cliente"),
	NOT_AVAILABLE_TICKETS("Número de tickts no disponible"),
	NOT_EXIST_CONCERT("No existe el concierto para la fecha y el grupo indicado");	
	

	
	/** Text. */
	private String text;
	
	/** Constructor. */
	private IncidentError(String text) {
		this.text = text;
	}

	/**
	 * Gets text.
	 * 
	 * @return text
	 */
	public String getText() {
		return text;
	}
}
