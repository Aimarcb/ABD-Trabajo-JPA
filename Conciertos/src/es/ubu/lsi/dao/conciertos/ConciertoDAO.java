package es.ubu.lsi.dao.conciertos;

import java.util.List;

import javax.persistence.EntityManager;

import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.conciertos.Concierto;

public class ConciertoDAO extends JpaDAO<Concierto, Integer>{
	private EntityManager em = null;
	public ConciertoDAO(EntityManager em) {
		super(em);
		this.em = em;
	}

	@Override
	public List<Concierto> findAll() {
		return em.createNamedQuery("Concierto.findAll", Concierto.class).getResultList();
	}

}
