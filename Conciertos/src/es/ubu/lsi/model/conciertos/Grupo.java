package es.ubu.lsi.model.conciertos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="GRUPO")
public class Grupo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private int idGrupo;
	
	private String nombre;
	private String estilo;
	private int activo;
	
	@OneToMany(mappedBy="grupo") 
    private Set<Concierto> conciertos;
	
	public Grupo() {
		conciertos = new HashSet<>();
	}

	public int getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEstilo() {
		return estilo;
	}

	public void setEstilo(String estilo) {
		this.estilo = estilo;
	}

	public int getActivo() {
		return activo;
	}

	public void setActivo(int activo) {
		this.activo = activo;
	}

	public Set<Concierto> getConciertos() {
		return conciertos;
	}

	public void setConciertos(Set<Concierto> conciertos) {
		this.conciertos = conciertos;
	}
}
