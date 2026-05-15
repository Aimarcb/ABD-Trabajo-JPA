package es.ubu.lsi.dao.conciertos;

import java.util.List;

import javax.persistence.EntityManager;

import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.conciertos.Cliente;

public class ClienteDAO extends JpaDAO<Cliente, String>{
	private EntityManager em = null;
	public ClienteDAO(EntityManager em) {
		super(em);
		this.em = em;
	}

	@Override
	public List<Cliente> findAll() {
		return em.createNamedQuery("Cliente.findAll", Cliente.class).getResultList();
	}

}
