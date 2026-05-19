package es.ubu.lsi.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.model.conciertos.Cliente;
import es.ubu.lsi.model.conciertos.Compra;
import es.ubu.lsi.model.conciertos.Concierto;
import es.ubu.lsi.model.conciertos.Grupo;
import es.ubu.lsi.service.PersistenceException;
import es.ubu.lsi.service.conciertos.IncidentError;
import es.ubu.lsi.service.conciertos.IncidentException;
import es.ubu.lsi.service.conciertos.Service;
import es.ubu.lsi.service.conciertos.ServiceImpl;
import es.ubu.lsi.test.util.ExecuteScript;
import es.ubu.lsi.test.util.PoolDeConexiones;

/**
 * Test client.
 * 
 * Realiza los test sobre las transacciones pedidas en la práctica
 * 
 * @author <a href="mailto:jmaudes@ubu.es">Jesús Maudes</a>
 * @author <a href="mailto:rmartico@ubu.es">Raúl Marticorena</a>
 * @author <a href="mailto:pgdiaz@ubu.es">Pablo García</a>
 * @author <a href="mailto:srarribas@ubu.es">Sandra Rodríguez</a>
 * @since 1.0
 */
public class TestClientAlumno {
	//El enunciado indica que, se copiará el código original y se ampliará en una clase TestClientAlumno
	//Se asume que el código original a copiar es el de TestClient. Por lo tanto, existen tests duplicados

	/** Logger. */
	private static final Logger logger = LoggerFactory.getLogger(TestClient.class);

	/** Connection pool. */
	private static PoolDeConexiones pool;

	/** Path. */
	private static final String SCRIPT_PATH = "sql/";

	/** Simple date format. */
	private static SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	/**
	 * Main.
	 * 
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Iniciando...");
			init();
			System.out.println("Probando el servicio...");
			testService();
			System.out.println("FIN.............");
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error grave en la aplicación {}", ex.getMessage());
		}
	}

	/**
	 * Init pool.
	 */
	static public void init() {
		try {
			// Acuerdate de q la primera vez tienes que crear el .bindings con:
			// PoolDeConexiones.reconfigurarPool();
			// Inicializacion de Pool
			pool = PoolDeConexiones.getInstance();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Create tables.
	 */
	static public void createTables() {
		ExecuteScript.run(SCRIPT_PATH + "script.sql");
	}

	/**
	 * Test service using JDBC and JPA.
	 */
	static void testService() throws Exception {
		createTables();
		Service implService = null;
		try {
			// JPA Service
			implService = new ServiceImpl();
			System.out.println("Framework y servicio iniciado...");

			// insertar compra correcta
			insertarCompraCorrecta(implService);

			// insertar compra con grupo incorrecto
			insertarCompraGrupoIncorrecto(implService);

			// insertar compra con cliente incorrecto
			insertarCompraClienteIncorrecto(implService);

			// insertar compra con concierto incorrecto
			insertarCompraConciertoIncorrecto(implService);

			// insertar compra con número de tickets incorrecto. Nuevo
			insertarCompraTicketsIncorrectos(implService);

			// insertar compra con número de tickets negativo. Nuevo
			insertarCompraTicketsNegativos(implService);

			// insertar compra excediendo el aforo restante
			comprarConciertoSinAforo(implService);

			// insertar compra con 0 tickets. Nuevo
			insertarCompraCeroTickets(implService);

			// consulta todos los grupos
			consultarGruposUsandoGrafo(implService);

			// desactivacion correcta
			desactivacionCorrecta(implService);
			
			// intentar comprar un concierto cuando hay dos simultaneos. Nuevo
			insertarCompraQueTieneMasDeUnConcierto(implService);

			// desactivacion incorrecta
			desactivacionIncorrecta(implService);
			
			// intenta hacer dos compras con el mismo cliente. Según la lógica de negocio planteada por el enunciado,
			// puede ocurrir. Nuevo
			insertarDosComprasConElMismoCliente(implService);
			
			// comprueba que al desactivar un grupo se eliminen sus conciertos y compras. Nuevo
			desactivacionEliminaConciertosYCompras(implService);
			
			// Insertar compra a grupo desactivado (activo = 0)
			insertarCompraGrupoDesactivado(implService);
						
			// Validar control estricto de fecha-hora del concierto
			insertarCompraHoraIncorrecta(implService);
			
		} catch (Exception e) { // for testing code...
			logger.error(e.getMessage());
			e.printStackTrace();
		} finally {
			pool = null;
		}
	} // testClient

	/**
	 * Desactiva a un grupo que no existe
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void desactivacionIncorrecta(Service implService) throws Exception {
		try {
			System.out.println("Desactivar grupo incorrecto");
			implService.desactivar(98);

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.NOT_EXIST_MUSIC_GROUP) {
				System.out.println("\tOK detecta correctamente que NO existe el grupo");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de desactivar con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en desactivar ", ex);
		} catch (Exception ex) {
			logger.error(
					"ERROR GRAVE de programación en transacción de desactivar con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave desactivando grupo ", ex);
		}

	}
	
	private static void desactivacionEliminaConciertosYCompras(Service implService) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			System.out.println("Desactivar grupo eliminando conciertos y compras");
			implService.desactivar(1);

			con = pool.getConnection();
			st = con.createStatement();

			// Comprobar que no quedan conciertos del grupo
			rs = st.executeQuery("select count(*) from concierto where idgrupo = 1");
			rs.next();
			int numConciertos = rs.getInt(1);
			rs.close();
			
			// Comprobar que no quedan compras de conciertos del grupo
			rs = st.executeQuery("select count(*) from compra co join concierto c on co.idconcierto = c.idconcierto where c.idgrupo = 1");
			rs.next();
			int numCompras = rs.getInt(1);
			if (numConciertos == 0 && numCompras == 0) {
				System.out.println("\tOK eliminados correctamente conciertos y compras");
			} else {
				System.out.println("\tERROR no se eliminaron correctamente conciertos o compras");
				System.out.println("\tConciertos restantes: " + numConciertos);
				System.out.println("\tCompras restantes: " + numCompras);
			}
			con.commit();
		} catch (Exception ex) {
			logger.error("ERROR grave en test. " + ex.getLocalizedMessage());
			if (con != null) {
				con.rollback();
			}
			throw ex;
		} finally {
			cerrarRecursos(con, st, rs);
		}
	}

	/**
	 * Inserta una compra con grupo incorrecto
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarCompraGrupoIncorrecto(Service implService) throws Exception {
		try {
			System.out.println("Insertar compra con grupo incorrecto");
			implService.comprar(dateformat.parse("01/11/2023 21:00:00"), "1111111F", -11, 3);

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.NOT_EXIST_MUSIC_GROUP) {
				System.out.println("\tOK detecta correctamente que NO existe el grupo");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en comprar ", ex);
		} catch (Exception ex) {
			logger.error("ERROR GRAVE de programación en transacción compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave insertando compra", ex);
		}

	}

	/**
	 * Inserta una compra con cliente incorrecto
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarCompraClienteIncorrecto(Service implService) throws Exception {
		try {
			System.out.println("Insertar compra con cliente incorrecto");
			implService.comprar(dateformat.parse("01/11/2023 21:00:00"), "1111111Z", 1, 3);

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.NOT_EXIST_CLIENT) {
				System.out.println("\tOK detecta correctamente que NO existe el cliente");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de desactivar con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en desactivar ", ex);
		} catch (Exception ex) {
			logger.error("ERROR GRAVE de programación en transacción compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave insertando compra", ex);
		}

	}

	/**
	 * Inserta una compra con concierto incorrecto
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarCompraConciertoIncorrecto(Service implService) throws Exception {
		try {
			System.out.println("Insertar compra con concierto incorrecto");
			implService.comprar(dateformat.parse("01/11/2023 17:00:00"), "1111111F", 1, 3);

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.NOT_EXIST_CONCERT) {
				System.out.println("\tOK detecta correctamente que NO existe el concierto");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de desactivar con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en compra ", ex);
		} catch (Exception ex) {
			logger.error("ERROR GRAVE de programación en transacción compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave compra", ex);
		}

	}
	
	private static void insertarCompraQueTieneMasDeUnConcierto(Service implService) throws Exception {
		Connection con = pool.getConnection();
		Statement st = con.createStatement();
		try {
			con = pool.getConnection();
			st = con.createStatement();
			st.executeUpdate("insert into concierto values (99, 'Duplicado', 'Madrid', TO_TIMESTAMP('01-11-2023 21:00:00', 'DD-MM-YYYY HH24:MI:SS'), 50, 20, 1)");
			con.commit();
			
			System.out.println("Intenta comprar entradas para un concierto cuando se han detectado que 2 coinciden con los datos");
			implService.comprar(dateformat.parse("01/11/2023 21:00:00"), "1111111F", 1, 3);
			System.out.println("\tERROR no detecta múltiples conciertos");

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.MORE_THAN_ONE_CONCERT) {
				System.out.println("\tOK detecta correctamente múltiples conciertos");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de desactivar con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en compra ", ex);
		} catch (Exception ex) {
			logger.error("ERROR GRAVE de programación en transacción compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave compra", ex);
		}

	}

	/**
	 * Inserta una compra con número de tickets incorrecto
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarCompraTicketsIncorrectos(Service implService) throws Exception {
		try {
			System.out.println("Insertar compra con concierto incorrecto");
			implService.comprar(dateformat.parse("01/11/2023 21:00:00"), "1111111F", 1, 103);

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.NOT_AVAILABLE_TICKETS) {
				System.out.println("\tOK detecta correctamente que NO existen estos tickets disponibles");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de desactivar con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en desactivar ", ex);
		} catch (Exception ex) {
			logger.error("ERROR GRAVE de programación en transacción compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave insertando compra", ex);
		}

	}

	/**
	 * Desactiva a un grupo correctamente
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void desactivacionCorrecta(Service implService) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			System.out.println("Desactivar grupo correcto");
			implService.desactivar(1);

			con = pool.getConnection();

			st = con.createStatement();
			rs = st.executeQuery("SELECT activo FROM GRUPO where idgrupo = 1");

			StringBuilder resultado = new StringBuilder();
			rs.next();
			resultado.append(rs.getString(1));

			logger.debug(resultado.toString());
			String cadenaEsperada =
			// @formatter:off
			"0"; // nueva fila
			// @formatter:on

			if (cadenaEsperada.equals(resultado.toString())) {
				System.out.println("\tOK grupo correctamente desactivado");
			} else {
				System.out.println("\tERROR deasctivando grupo");
			}
			rs.close();
			con.commit();
		} catch (Exception ex) {
			logger.error("ERROR grave en test. " + ex.getLocalizedMessage());
			con.rollback();
			throw ex;
		} finally {
			cerrarRecursos(con, st, rs);
		}

	}

	/**
	 * Prueba consulta de vehiculos, cargando datos completos desde un grafo de
	 * entidades.
	 * 
	 * @param implService implementación del servicio
	 */
	private static void consultarGruposUsandoGrafo(Service implService) {
		try {
			System.out.println("Información completa con grafos de entidades...");
			List<Grupo> grupos = implService.consultarGrupos();
			for (Grupo grupo : grupos) {
				System.out.println(grupo.toString());
				Set<Concierto> conciertos = grupo.getConciertos();
				for (Concierto concierto : conciertos) {
					System.out.println("\t" + concierto.toString());
					Set<Compra> compras = concierto.getCompras();
					for (Compra compra : compras) {
						System.out.println("\t\t" + compra.toString());
						Cliente cliente = compra.getCliente();
						System.out.println("\t\t" + cliente.toString());
					}
				}
			}
			System.out.println("OK Sin excepciones en la consulta completa y acceso posterior");
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de consultas de grupos con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en consulta de grupos", ex);
		}
	}

	/**
	 * Inserta una compra correcta.
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarCompraCorrecta(Service implService) throws Exception {

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			System.out.println("Insertar compra correcta");
			implService.comprar(dateformat.parse("01/11/2023 21:00:00"), "1111111F", 1, 3);

			con = pool.getConnection();

			st = con.createStatement();
			rs = st.executeQuery(
					"SELECT idcompra||'-'||nif||'-'||idconcierto||'-'||n_tickets FROM COMPRA ORDER BY idcompra desc");

			StringBuilder resultado = new StringBuilder();
			rs.next();
			resultado.append(rs.getString(1));

			logger.debug(resultado.toString());
			String cadenaEsperada =
			// @formatter:off
			"6-1111111F-1-3"; // nueva fila
			// @formatter:on

			if (cadenaEsperada.equals(resultado.toString())) {
				System.out.println("\tOK compra bien insertada");
			} else {
				System.out.println("\tERROR compra mal insertada");
			}
			rs.close();
			rs = st.executeQuery("SELECT TICKETS FROM CONCIERTO WHERE IDCONCIERTO = 1");
			StringBuilder resultadoTickets = new StringBuilder();
			rs.next();
			resultadoTickets.append(rs.getString(1));

			String ticketsEsperados = "97";
			if (ticketsEsperados.equals(resultadoTickets.toString())) {
				System.out.println("\tOK actualiza bien los tickets del concierto");
			} else {
				System.out.println("\tERROR no actualiza bien los tickets del concierto");
			}
			con.commit();
		} catch (Exception ex) {
			logger.error("ERROR grave en test. " + ex.getLocalizedMessage());
			if (con != null) {
				con.rollback();
			}
			throw ex;
		} finally {
			cerrarRecursos(con, st, rs);
		}
	}
	
	/**
	 * Se comprueba que realizar dos compras con el mismo cliente no salta error. Esto es para comprobar
	 * que la secuencia se genera correctamente
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarDosComprasConElMismoCliente(Service implService) throws Exception {
		try {
			System.out.println("Insertar compra correcta");
			implService.comprar(dateformat.parse("01/11/2023 21:00:00"), "1111111F", 1, 3);
			implService.comprar(dateformat.parse("01/11/2023 21:00:00"), "1111111F", 1, 1);
			
			System.out.println("\tOK no salta error por hacer ");
		} catch (Exception ex) {
			logger.error("ERROR grave en test. " + ex.getLocalizedMessage());
			throw ex;
		}
	}
	

	/**
	 * Inserta una compra con número de tickets negativo
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarCompraTicketsNegativos(Service implService) throws Exception {
		try {
			System.out.println("Insertar compra con tickets negativos");
			implService.comprar(dateformat.parse("01/11/2023 21:00:00"), "1111111F", 1, -2);

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.TICKET_PAR_NEGATIVE) {
				System.out.println("\tOK detecta correctamente que NO existen estos tickets disponibles");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en compra ", ex);
		} catch (Exception ex) {
			logger.error("ERROR GRAVE de programación en transacción compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave insertando compra", ex);
		}

	}

	/**
	 * Inserta una compra excediendo el aforo restante
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void comprarConciertoSinAforo(Service implService) throws Exception {
		try {
			System.out.println("Comprar excediendo aforo restante");
			// Intentamos comprar 98 cuando solo quedan 97
			implService.comprar(dateformat.parse("01/11/2023 21:00:00"), "1111111F", 1, 98); 

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.NOT_AVAILABLE_TICKETS) {
				System.out.println("\tOK detecta correctamente que NO existen estos tickets disponibles");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en compra ", ex);
		} catch(Exception ex) {
			logger.error("ERROR GRAVE de programación en transacción compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave insertando compra", ex);
		}
	}

	/**
	 * Inserta una compra con cero tickets  
	 * 
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarCompraCeroTickets(Service implService) throws Exception {
		try {
			System.out.println("Insertar compra con cero tickets");
			implService.comprar(dateformat.parse("01/11/2023 21:00:00"), "1111111F", 1, 0);

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.TICKET_PAR_NEGATIVE) {
				System.out.println("\tOK detecta correctamente que NO existen estos tickets disponibles");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado:  " + ex.getError().toString());
			}
		} catch (PersistenceException ex) {
			logger.error("ERROR en transacción de compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error en compra ", ex);
		} catch (Exception ex) {
			logger.error("ERROR GRAVE de programación en transacción compra con JPA: " + ex.getLocalizedMessage());
			throw new RuntimeException("Error grave insertando compra", ex);
		}

	}
	
	/**
	 * Intenta comprar para un concierto de un grupo que está desactivado (activo = 0).
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarCompraGrupoDesactivado(Service implService) throws Exception {
		try {
			System.out.println("Insertar compra para un grupo desactivado (activo = 0)");
			implService.desactivar(1);
			
			implService.comprar(dateformat.parse("02/11/2023 22:00:00"), "1111111F", 1, 2);
			System.out.println("\tERROR: Ha permitido comprar entradas para un grupo desactivado");

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.NOT_EXIST_MUSIC_GROUP) { 
				System.out.println("\tOK detecta correctamente que el grupo está inactivo/no disponible");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado: " + ex.getError().toString());
			}
		} catch (Exception ex) {
			logger.error("ERROR grave en test de grupo desactivado: " + ex.getLocalizedMessage());
			throw ex;
		}
	}

	/**
	 * Intenta comprar indicando un formato de fecha correcto pero una hora en la que 
	 * no existe ningún concierto programado para ese grupo.
	 * @param implService implementación del servicio
	 * @throws Exception error en test
	 */
	private static void insertarCompraHoraIncorrecta(Service implService) throws Exception {
		try {
			System.out.println("Insertar compra con fecha válida pero hora sin concierto programado");
			// El día es correcto para el grupo, pero no la hora (04:00 AM)
			implService.comprar(dateformat.parse("01/11/2023 04:00:00"), "1111111F", 1, 2);
			System.out.println("\tERROR: Ha permitido la compra en una hora sin concierto");

		} catch (IncidentException ex) {
			if (ex.getError() == IncidentError.NOT_EXIST_CONCERT) {
				System.out.println("\tOK detecta correctamente que no hay concierto programado a esa hora");
			} else {
				System.out.println("\tERROR detecta un error diferente al esperado: " + ex.getError().toString());
			}
		} catch (Exception ex) {
			logger.error("ERROR grave en test de hora incorrecta: " + ex.getLocalizedMessage());
			throw ex;
		}
	}

	/**
	 * Cierra recursos de la transacción.
	 * 
	 * @param con conexión
	 * @param st  sentencia
	 * @param rs  conjunto de datos
	 * @throws SQLException si se produce cualquier error SQL
	 */
	private static void cerrarRecursos(Connection con, Statement st, ResultSet rs) throws SQLException {
		if (rs != null && !rs.isClosed())
			rs.close();
		if (st != null && !st.isClosed())
			st.close();
		if (con != null && !con.isClosed())
			con.close();
	}

} 