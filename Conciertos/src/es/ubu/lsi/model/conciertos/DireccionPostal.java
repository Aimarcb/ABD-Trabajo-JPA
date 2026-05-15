package es.ubu.lsi.model.conciertos;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class DireccionPostal implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(length = 100)
	private String direccion;
	@Column(length = 5)
	private String cp;
	@Column(length = 20)
	private String ciudad;
	
	public DireccionPostal() {
	}
	
	public DireccionPostal(String direccion, String cp, String ciudad) {
		this.direccion = direccion;
		this.cp = cp;
		this.ciudad = ciudad;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

}
