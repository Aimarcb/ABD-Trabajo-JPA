package es.ubu.lsi.model.conciertos;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class DireccionPostal implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String direccion;
	private String cp;
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
