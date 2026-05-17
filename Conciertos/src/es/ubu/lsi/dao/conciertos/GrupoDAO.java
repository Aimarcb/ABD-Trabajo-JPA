package es.ubu.lsi.dao.conciertos;

import java.util.List;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;

import es.ubu.lsi.dao.JpaDAO;
import es.ubu.lsi.model.conciertos.Grupo;

public class GrupoDAO extends JpaDAO<Grupo, Integer>{
	private EntityManager em = null;
	public GrupoDAO(EntityManager em) {
		super(em);
		this.em = em;
	}

	@Override
	public List<Grupo> findAll() {
		return em.createNamedQuery("Grupo.findAll", Grupo.class).getResultList();
	}
	
	public List<Grupo> findAllWithGraph() {
	    EntityGraph<?> grafo = em.getEntityGraph("Grupo.graph");
	    
	    return getEntityManager()
	        .createNamedQuery("Grupo.findAll", Grupo.class)
	        .setHint("javax.persistence.loadgraph", grafo)
	        .getResultList();
	}

}
