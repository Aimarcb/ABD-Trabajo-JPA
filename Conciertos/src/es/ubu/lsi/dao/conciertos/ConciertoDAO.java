package es.ubu.lsi.dao.conciertos;

import java.util.List;

import javax.persistence.EntityManager;

import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.conciertos.Concierto;

public class ConciertoDAO extends JpaDAO<Concierto, Integer>{

	public ConciertoDAO(EntityManager em) {
		super(em);
	}

	@Override
	public List<Concierto> findAll() {
		return getEntityManager().createQuery("SELECT c FROM Concierto c", Concierto.class)
		        .getResultList();
	}

}
