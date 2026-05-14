package es.ubu.lsi.service.conciertos;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.*;
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
		int sigIdCompra = 5;
		try {
			beginTransaction(em);
			//Comprobar grupo
			Grupo grupoSel = em.find(Grupo.class, grupo); //Según los apuntes es más eficiente
			if (grupoSel == null) {
				throw new IncidentException(IncidentError.NOT_EXIST_MUSIC_GROUP);
			}
			
			//Sacar concierto
			TypedQuery<Concierto> conciertoQuery = em.createQuery("select c from Concierto c where c.grupo.idGrupo = ?1 and c.fecha = ?2", Concierto.class);
			conciertoQuery.setParameter(1, grupo);
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
			TypedQuery<Cliente> clienteQuery = em.createQuery("select c from Cliente c where c.nif = ?1", Cliente.class);
			clienteQuery.setParameter(1, nif);
			List<Cliente> clientes = clienteQuery.getResultList(); 
			if (clientes.isEmpty()) {
				throw new IncidentException(IncidentError.NOT_EXIST_CLIENT);
			}
			//Insertar compra
			Compra compra = new Compra();
			compra.setCliente(clientes.get(0));
			compra.setConcierto(concierto);
			compra.setN_tickets(tickets);
			compra.setIdCompra(++sigIdCompra); 
			em.persist(compra); //Para insertar en la BD
			
			//Se podría hacer con los setters y un persist. Tiene sentido esto?
			//Actualizar concierto
			em.createQuery("update Concierto c set c.tickets = c.tickets - ?1 where c = ?2").setParameter(1, tickets).setParameter(2, concierto).executeUpdate();
			//En JPQL se puede meter el objeto entero como argumento. También se podría hacer getId de Concierto
			
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
				queryBorrado.setParameter(1,i.next()).executeUpdate();
			}
			em.createQuery("delete from Concierto c where c.grupo.idGrupo = ?1").setParameter(1, grupo).executeUpdate();
			
			em.merge(grupoSel); //Actualiza la versión de la base de datos de esta misma tabla
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
		// TODO Auto-generated method stub
		return null;
	}

}
