package es.ubu.lsi.model.conciertos;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="COMPRA")
public class Compra implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	private int idCompra;
	
	private int n_tickets;
	
	@ManyToOne
    @JoinColumn(name="NIF")
    private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name="IDCONCIERTO")
	private Concierto concierto;
	
	public Compra() {
		
	}

	public int getIdCompra() {
		return idCompra;
	}

	public void setIdCompra(int idCompra) {
		this.idCompra = idCompra;
	}

	public int getN_tickets() {
		return n_tickets;
	}

	public void setN_tickets(int n_tickets) {
		this.n_tickets = n_tickets;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Concierto getConcierto() {
		return concierto;
	}

	public void setConcierto(Concierto concierto) {
		this.concierto = concierto;
	}
	
}
