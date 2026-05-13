package es.ubu.lsi.service.conciertos;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import es.ubu.lsi.model.conciertos.Grupo;
import es.ubu.lsi.service.PersistenceException;
import es.ubu.lsi.service.PersistenceService;

public class ServiceImpl extends PersistenceService implements Service {
	EntityManager em = this.createSession();
	@Override
	public void comprar(Date fecha, String nif, int grupo, int tickets) throws PersistenceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void desactivar(int grupo) throws PersistenceException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Grupo> consultarGrupos() throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

}
