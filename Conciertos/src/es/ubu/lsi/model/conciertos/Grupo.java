package es.ubu.lsi.model.conciertos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="GRUPO")
@NamedQuery(name="Grupo.findAll", query="select g from Grupo g")
public class Grupo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private int idGrupo;
	@Column(nullable = false, length = 50)
	private String nombre;
	@Column(length = 20)
	private String estilo;
	@Column(nullable = false)
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
