package es.ubu.lsi.model.conciertos;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="COMPRA")
@NamedQuery(name="Compra.findAll", query="select c from Compra c")
public class Compra implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	private int idCompra;
	
	@Column(nullable = false)
	private int n_tickets;
	
	@ManyToOne
    @JoinColumn(name="NIF", nullable=false)
    private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name="IDCONCIERTO", nullable = false)
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
