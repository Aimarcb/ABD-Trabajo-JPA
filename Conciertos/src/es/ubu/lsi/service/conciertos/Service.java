package es.ubu.lsi.service.conciertos;

import java.util.Date;
import java.util.List;

import es.ubu.lsi.model.conciertos.Grupo;
import es.ubu.lsi.service.PersistenceException;

/**
 * Transaction service.
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a>
 * @author <a href="mailto:srarribas@ubu.es">Sandra Rodríguez</a> 
 * @since 1.0
 *
 */
public interface Service {
	

	/**
	 * Alta de una nueva compra sobre un concierto y un cliente.
	 * 
	 * @param fecha fecha
	 * @param nif nif
	 * @param grupo identificador del grupo
	 * @throws PersistenceException si se produce un error
	 */
	public void comprar(Date fecha, String nif, int grupo, int tickets) throws PersistenceException;

	/**
	 * Desactiva un grupo y elimina todos los registros asociados.
	 * 
	 * @param grupo id_grupo
	 * @throws PersistenceException si se produce un error
	 */
	public void desactivar(int grupo) throws PersistenceException;
	
	/**
	 * Consulta gupos. En este caso en particular es importante recuperar 
	 * toda la información vinculada a los grupos, conciertos, compras y anulaciones
	 * 
	 * @return grupos
	 * @throws PersistenceException si se produce un error
	 */
	public List<Grupo> consultarGrupos() throws PersistenceException;

}
