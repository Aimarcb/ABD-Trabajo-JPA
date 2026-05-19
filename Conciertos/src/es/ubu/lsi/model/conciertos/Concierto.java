package es.ubu.lsi.model.conciertos;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="CONCIERTO")
@NamedQuery(name="Concierto.findAll", query="select c from Concierto c")
public class Concierto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private int idConcierto;
	@Column(nullable = false, length = 100)
	private String nombre;
	@Column(nullable = false, length = 100)
	private String ciudad;
	
	@Temporal (TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date fecha;
	
	@Column(nullable = false)
	private int tickets;
	@Column(nullable = false)
	private float precio;
	
	@ManyToOne
	@JoinColumn(name="IDGRUPO", nullable=false)
	private Grupo grupo;
	
	@OneToMany(mappedBy="concierto")
	private Set<Compra> compras;
	
	public Concierto() {
		compras = new HashSet<>();
	}

	public int getIdConcierto() {
		return idConcierto;
	}

	public void setIdConcierto(int idConcierto) {
		this.idConcierto = idConcierto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getTickets() {
		return tickets;
	}

	public void setTickets(int tickets) {
		this.tickets = tickets;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public Grupo getGrupo() {
		return grupo;
	}

	public void setGrupo(Grupo grupo) {
		this.grupo = grupo;
	}

	public Set<Compra> getCompras() {
		return compras;
	}

	public void setCompras(Set<Compra> compras) {
		this.compras = compras;
	}

	@Override
	public String toString() {
		return "Concierto [idconcierto=" + idConcierto + ", nombre=" + nombre + ", ciudad=" + ciudad + ", fecha=" + fecha + ", tickets=" + tickets + ", precio=" + precio + "]";
	}
}
