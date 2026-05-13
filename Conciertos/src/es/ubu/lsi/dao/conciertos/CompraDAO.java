package es.ubu.lsi.dao.conciertos;

import java.util.List;

import javax.persistence.EntityManager;

import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.conciertos.Compra;

public class CompraDAO extends JpaDAO<Compra, Integer>{

	public CompraDAO(EntityManager em) {
		super(em);
	}

	@Override
	public List<Compra> findAll() {
		return getEntityManager().createQuery("SELECT c1 FROM Compra c1", Compra.class)
		        .getResultList();
	}

}
