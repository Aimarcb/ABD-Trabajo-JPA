package es.ubu.lsi.model.conciertos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="CLIENTE")
@NamedQuery(name="Cliente.findAll", query="select c from Cliente c")
public class Cliente implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 10)
    private String nif;
	@Column(nullable = false, length = 50)
	private String nombre;
	@Column(nullable = false, length = 100)
    private String apellidos;
    
    @Embedded
    private DireccionPostal direccion;
    
    @OneToMany(mappedBy="cliente")
    private Set<Compra> compras;
    
    public Cliente() {
    	compras = new HashSet<>();
    }

	public String getNif() {
		return nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public DireccionPostal getDireccion() {
		return direccion;
	}

	public void setDireccion(DireccionPostal direccion) {
		this.direccion = direccion;
	}

	public Set<Compra> getCompras() {
		return compras;
	}

	public void setCompras(Set<Compra> compras) {
		this.compras = compras;
	}
    
}
