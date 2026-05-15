package es.ubu.lsi.dao.conciertos;

import java.util.List;

import javax.persistence.EntityManager;

import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.conciertos.Compra;

public class CompraDAO extends JpaDAO<Compra, Integer>{
	private EntityManager em = null;
	public CompraDAO(EntityManager em) {
		super(em);
		this.em = em;
	}

	@Override
	public List<Compra> findAll() {
		return em.createNamedQuery("Compra.findAll", Compra.class).getResultList();
	}

}
