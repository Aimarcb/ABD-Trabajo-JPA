package es.ubu.lsi.service.conciertos;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.*;

import es.ubu.lsi.dao.conciertos.GrupoDAO;
import es.ubu.lsi.model.conciertos.Cliente;
import es.ubu.lsi.model.conciertos.Compra;
import es.ubu.lsi.model.conciertos.Concierto;
import es.ubu.lsi.model.conciertos.Grupo;
import es.ubu.lsi.service.PersistenceException;
import es.ubu.lsi.service.PersistenceService;

public class ServiceImpl extends PersistenceService implements Service {
	@Override
	public void comprar(Date fecha, String nif, int grupo, int tickets) throws PersistenceException {
		EntityManager em = this.createSession();
		int sigIdCompra = 5; //Corresponde a la secuencia de compra. Se ha implementado así para mayor simpleza
		try {
			beginTransaction(em);
			//Comprobar los tickets del parámetro
			if (tickets <= 0) {
				throw new IncidentException(IncidentError.TICKET_PAR_NEGATIVE);
			}
			//Comprobar grupo
			Grupo grupoSel = em.find(Grupo.class, grupo); //Según los apuntes es más eficiente
			if (grupoSel == null) {
				throw new IncidentException(IncidentError.NOT_EXIST_MUSIC_GROUP);
			}
			
			//Sacar concierto
			TypedQuery<Concierto> conciertoQuery = em.createQuery("select c from Concierto c where c.grupo.idGrupo = ?1 and c.fecha = ?2", Concierto.class);
			conciertoQuery.setParameter(1, grupo); //Asumimos que un grupo solo tiene un concierto en una fecha, para determinar qué concierto es
			conciertoQuery.setParameter(2, fecha);
			List<Concierto> conciertos = conciertoQuery.getResultList();
			if (conciertos.isEmpty()) {
				throw new IncidentException(IncidentError.NOT_EXIST_CONCERT);
			}
			//Comprobar que haya tickets
			Concierto concierto = conciertos.get(0);
			if (concierto.getTickets() < tickets) {
				throw new IncidentException(IncidentError.NOT_AVAILABLE_TICKETS);
			}
			//Sacar cliente
			Cliente cliente = em.find(Cliente.class, nif); //Según los apuntes es más eficiente
			if (cliente == null) {
				throw new IncidentException(IncidentError.NOT_EXIST_CLIENT);
			}

			//Insertar compra
			Compra compra = new Compra();
			compra.setCliente(cliente);
			compra.setConcierto(concierto);
			compra.setN_tickets(tickets);
			compra.setIdCompra(++sigIdCompra); 
			
			//Actualizar concierto
			concierto.setTickets(concierto.getTickets()-tickets);
			em.persist(compra); //Para insertar en la BD
			
			commitTransaction(em);
		} catch (Exception e) {
			rollbackTransaction(em);
			throw e;
		} finally {
			close(em);
		}
	}

	@Override
	public void desactivar(int grupo) throws PersistenceException {
		EntityManager em = this.createSession();
		try {
			beginTransaction(em);
			//Comprobar si existe grupo y desactivarlo
			Grupo grupoSel = em.find(Grupo.class, grupo); //Según los apuntes es más eficiente
			if (grupoSel == null) {
				throw new IncidentException(IncidentError.NOT_EXIST_MUSIC_GROUP);
			}
			grupoSel.setActivo(0); 
			
			
			TypedQuery<Concierto> queryConciertos = em.createQuery("select c from Concierto c where c.grupo.idGrupo=?1", Concierto.class);
			queryConciertos.setParameter(1, grupo);
			List<Concierto> conciertos = queryConciertos.getResultList();
			Iterator<Concierto> i = conciertos.iterator();
			//Se borran las compras de sus conciertos y sus conciertos
			Query queryBorrado = em.createQuery("delete from Compra c where c.concierto = ?1");
			while (i.hasNext()) {
				queryBorrado.setParameter(1,i.next()).executeUpdate(); //Actua directamente sobre la BD
			}
			em.createQuery("delete from Concierto c where c.grupo.idGrupo = ?1").setParameter(1, grupo).executeUpdate();
			
			em.merge(grupoSel); //Actualiza la versión de persistencia en función de las que hay en la BD
			commitTransaction(em);
		} catch (Exception e) {
			rollbackTransaction(em);
			throw e;
		} finally {
			close(em);
		}
		
	}

	@Override
	public List<Grupo> consultarGrupos() throws PersistenceException {
		EntityManager em = this.createSession();
		
		try {
			beginTransaction(em);

	        GrupoDAO grupoDAO = new GrupoDAO(em);
	        List<Grupo> resultado = grupoDAO.findAllWithGraph();

	        commitTransaction(em);
	        
	        return resultado;
		} catch (Exception e) {
	        rollbackTransaction(em);
	        throw new PersistenceException("Error al consultar el grafo de grupos desde el DAO", e);
	    } finally {
	        close(em);
	    }
	}

}
