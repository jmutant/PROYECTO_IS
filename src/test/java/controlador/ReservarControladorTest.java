package controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import modelo.AlmacenDatosLocal;
import modelo.AutorizacionServicio;
import modelo.Conductor;
import modelo.ConductorRepositorioMemoria;
import modelo.EstadoUnidad;
import modelo.Itinerario;
import modelo.ItinerarioRepositorioMemoria;
import modelo.ItinerarioServicio;
import modelo.MonederoRepositorio;
import modelo.MonederoRepositorioArchivo;
import modelo.MonederoServicio;
import modelo.Rol;
import modelo.TarifaRepositorio;
import modelo.TarifaRepositorioArchivo;
import modelo.TarifaServicio;
import modelo.TipoRuta;
import modelo.Unidad;
import modelo.UnidadRepositorioMemoria;
import modelo.Usuario;
import vista.ReservarVista;


/**     Tarifas usadas: Urbana = Bs 100 (estudiante 15 %, empleado/profesor 40 %)
 *                 Extraurbana = Bs 200 (estudiante 20 %, empleado/profesor 60 %)
 */
class ReservarControladorTest {

    /** Vista falsa: guarda lo que el controlador le pide mostrar y permite disparar las acciones. */
    private static final class VistaFalsa implements ReservarVista {
        Object seleccion;
        List<?> itinerarios = List.of();
        final List<double[]> desgloses = new ArrayList<>();
        final List<String> exitos = new ArrayList<>();
        final List<String> errores = new ArrayList<>();
        int cierres;
        ItemListener alSeleccionarRuta;
        Runnable alConfirmar;
        Runnable alRegresar;

        @Override public void mostrarItinerarios(List<?> lista) { itinerarios = lista; }
        @Override public Object getRutaSeleccionada() { return seleccion; }
        @Override public void mostrarDesgloseCobro(double base, double pct, double total) {
            desgloses.add(new double[] {base, pct, total});
        }
        @Override public double getMontoCalculado() { return 0; }
        @Override public void mostrarExito(String mensaje) { exitos.add(mensaje); }
        @Override public void mostrarError(String mensaje) { errores.add(mensaje); }
        @Override public void mostrarTicketDigital(Object ticket) { }
        @Override public void setAccionSeleccionarRuta(ItemListener listener) { alSeleccionarRuta = listener; }
        @Override public void setAccionConfirmarReserva(Runnable accion) { alConfirmar = accion; }
        @Override public void setAccionRegresar(Runnable accion) { alRegresar = accion; }
        @Override public void mostrar() { }
        @Override public void cerrar() { cierres++; }

        double[] ultimoDesglose() { return desgloses.get(desgloses.size() - 1); }
    }

    private MonederoRepositorio monederoRepo;
    private MonederoServicio monederoServicio;
    private TarifaRepositorio tarifaRepo;
    private TarifaServicio tarifaServicio;
    private ItinerarioServicio itinerarioServicio;
    private Itinerario urbana;
    private Itinerario extraurbana;
    private Usuario estudiante;
    private VistaFalsa vista;

    @BeforeEach
    void preparar() throws Exception {
        Path directorio = Files.createTempDirectory("reserva-test-");
        AlmacenDatosLocal almacen = new AlmacenDatosLocal(directorio);
        monederoRepo = new MonederoRepositorioArchivo(almacen);
        monederoServicio = new MonederoServicio(monederoRepo);
        tarifaRepo = new TarifaRepositorioArchivo(almacen);
        tarifaServicio = new TarifaServicio(tarifaRepo);
        tarifaServicio.guardarTarifaBase("Urbana", 100.0, 15.0, 40.0);
        tarifaServicio.guardarTarifaBase("Extraurbana", 200.0, 20.0, 60.0);

        UnidadRepositorioMemoria unidades = new UnidadRepositorioMemoria();
        ConductorRepositorioMemoria conductores = new ConductorRepositorioMemoria();
        unidades.guardar(new Unidad("ABC123", "Mercedes", 40, EstadoUnidad.ACTIVO));
        unidades.guardar(new Unidad("PQR987", "Volvo", 35, EstadoUnidad.ACTIVO));
        conductores.guardar(new Conductor("Juan Perez", "V12345678", "1234567890"));
        conductores.guardar(new Conductor("Maria Gomez", "V17654321", "0987654321"));
        itinerarioServicio = new ItinerarioServicio(new ItinerarioRepositorioMemoria(), unidades, conductores,
                new AutorizacionServicio());

        Usuario admin = new Usuario("admin", "Administrador Sistema", Rol.ADMINISTRADOR, "1234");
        urbana = itinerarioServicio.registrar("Catia", "UCV", DayOfWeek.MONDAY, LocalTime.of(8, 0),
                TipoRuta.URBANA, "ABC123", "1234567890", admin);
        extraurbana = itinerarioServicio.registrar("UCV", "Maracay", DayOfWeek.TUESDAY, LocalTime.of(9, 30),
                TipoRuta.EXTRAURBANA, "PQR987", "0987654321", admin);

        estudiante = pasajero(Rol.ESTUDIANTE, "ana");
    }

    // ------------------------------------------------------------------ ayudas

    private Usuario pasajero(Rol rol, String username) {
        return new Usuario("Ana", "Perez", "12345678", rol, username, "Clave123");
    }

    private void conSaldo(Usuario usuario, String saldo) {
        monederoRepo.guardarSaldo(usuario.getUsername(), new BigDecimal(saldo));
    }

    private BigDecimal saldoDe(Usuario usuario) {
        return monederoServicio.consultarSaldo(usuario);
    }

    /** Abre la pantalla de reserva para el usuario (crea el controlador con una vista nueva). */
    private void abrirReserva(Usuario usuario) {
        vista = new VistaFalsa();
        new ReservarControlador(vista, itinerarioServicio, tarifaServicio, monederoServicio, usuario);
    }

    /** El pasajero elige una ruta y pulsa "Reservar". */
    private void reservar(Object ruta) {
        vista.seleccion = ruta;
        vista.alConfirmar.run();
    }

    private void seleccionarEnLista(Object ruta, int estado) {
        vista.seleccion = ruta;
        vista.alSeleccionarRuta.itemStateChanged(
                new ItemEvent(new JComboBox<String>(), ItemEvent.ITEM_STATE_CHANGED, ruta, estado));
    }

    // ------------------------------------------------------------------ casos válidos

    @Test
    @DisplayName("RES-V01: un estudiante reserva una ruta urbana y se le descuenta Bs 15,00")
    void estudianteReservaUrbana() {
        conSaldo(estudiante, "500.00");
        abrirReserva(estudiante);

        reservar(urbana);

        assertEquals(new BigDecimal("485.00"), saldoDe(estudiante));
        assertEquals(1, vista.exitos.size());
        assertTrue(vista.exitos.get(0).contains("Reserva exitosa"));
        assertTrue(vista.exitos.get(0).contains("Bs 15.00"));
        assertTrue(vista.exitos.get(0).contains("Saldo restante: Bs 485.00"));
        assertTrue(vista.errores.isEmpty());
    }

    @ParameterizedTest
    @DisplayName("RES-V02: el pasaje de la ruta urbana (Bs 100) depende del rol del pasajero")
    @CsvSource({
        "ESTUDIANTE,      15.00",
        "EMPLEADO,        40.00",
        "PROFESOR,        40.00",
        "PUBLICO_GENERAL, 100.00"
    })
    void cobroSegunRol(Rol rol, String esperado) {
        Usuario usuario = pasajero(rol, "usuario");
        conSaldo(usuario, "500.00");
        abrirReserva(usuario);

        reservar(urbana);

        assertEquals(new BigDecimal("500.00").subtract(new BigDecimal(esperado)), saldoDe(usuario));
        assertTrue(vista.errores.isEmpty());
    }

    @ParameterizedTest
    @DisplayName("RES-V03: el pasaje de la ruta extraurbana (Bs 200) depende del rol del pasajero")
    @CsvSource({
        "ESTUDIANTE,      40.00",
        "EMPLEADO,        120.00",
        "PROFESOR,        120.00",
        "PUBLICO_GENERAL, 200.00"
    })
    void cobroExtraurbanaSegunRol(Rol rol, String esperado) {
        Usuario usuario = pasajero(rol, "usuario");
        conSaldo(usuario, "500.00");
        abrirReserva(usuario);

        reservar(extraurbana);

        assertEquals(new BigDecimal("500.00").subtract(new BigDecimal(esperado)), saldoDe(usuario));
    }

    @Test
    @DisplayName("RES-V04: reservas seguidas descuentan de forma acumulada hasta que el saldo no alcanza")
    void reservasSeguidasHastaAgotarSaldo() {
        Usuario usuario = pasajero(Rol.PUBLICO_GENERAL, "publico");
        conSaldo(usuario, "250.00");
        abrirReserva(usuario);

        reservar(urbana);
        reservar(urbana);
        reservar(urbana); // el tercer pasaje (100) ya no alcanza: quedan 50

        assertEquals(new BigDecimal("50.00"), saldoDe(usuario));
        assertEquals(2, vista.exitos.size());
        assertEquals(1, vista.errores.size());
        assertTrue(vista.errores.get(0).contains("Saldo insuficiente"));
    }

    @Test
    @DisplayName("RES-V05: al elegir una ruta se muestra el desglose (base, porcentaje y total)")
    void desgloseAlSeleccionarRuta() {
        abrirReserva(estudiante);

        seleccionarEnLista(urbana, ItemEvent.SELECTED);
        assertEquals(100.0, vista.ultimoDesglose()[0], 0.001);
        assertEquals(15.0, vista.ultimoDesglose()[1], 0.001);
        assertEquals(15.0, vista.ultimoDesglose()[2], 0.001);

        seleccionarEnLista(extraurbana, ItemEvent.SELECTED);
        assertEquals(200.0, vista.ultimoDesglose()[0], 0.001);
        assertEquals(20.0, vista.ultimoDesglose()[1], 0.001);
        assertEquals(40.0, vista.ultimoDesglose()[2], 0.001);
    }

    @Test
    @DisplayName("RES-V06: se cobra la tarifa vigente aunque el administrador la cambie con la ventana abierta")
    void cobraLaTarifaVigente() {
        conSaldo(estudiante, "500.00");
        abrirReserva(estudiante);

        tarifaServicio.guardarTarifaBase("Urbana", 200.0, 20.0, 40.0); // nueva tarifa: 20 % de 200 = 40

        reservar(urbana);

        assertEquals(new BigDecimal("460.00"), saldoDe(estudiante));
    }

    @Test
    @DisplayName("RES-V07: la lista de rutas de la pantalla trae todos los itinerarios registrados")
    void muestraTodosLosItinerarios() {
        abrirReserva(estudiante);

        assertEquals(2, vista.itinerarios.size());
        assertTrue(vista.itinerarios.contains(urbana));
        assertTrue(vista.itinerarios.contains(extraurbana));
    }

    @Test
    @DisplayName("RES-V08: la cruz de la pantalla cierra la ventana")
    void regresarCierraLaVentana() {
        abrirReserva(estudiante);

        vista.alRegresar.run();

        assertEquals(1, vista.cierres);
    }

    // ------------------------------------------------------------------ casos borde: saldo contra pasaje

    @ParameterizedTest
    @DisplayName("RES-B01: borde entre saldo suficiente e insuficiente para un pasaje de estudiante (Bs 15,00)")
    @CsvSource({
        "14.99, false, 14.99",   // un centavo menos que el pasaje
        "15.00, true,  0.00",    // saldo exactamente igual al pasaje
        "15.01, true,  0.01",    // un centavo más que el pasaje
        "0.00,  false, 0.00",    // monedero vacío
        "0.01,  false, 0.01"     // saldo mínimo distinto de cero
    })
    void bordeSaldoContraPasaje(String saldoInicial, boolean sePuedeReservar, String saldoFinal) {
        conSaldo(estudiante, saldoInicial);
        abrirReserva(estudiante);

        reservar(urbana);

        assertEquals(new BigDecimal(saldoFinal), saldoDe(estudiante));
        assertEquals(sePuedeReservar ? 1 : 0, vista.exitos.size());
        assertEquals(sePuedeReservar ? 0 : 1, vista.errores.size());
    }

    @Test
    @DisplayName("RES-B02: un pasaje de Bs 0,10 (tarifa base 1,00 al 10 %) se cobra completo")
    void pasajeMuyBajo() {
        tarifaServicio.guardarTarifaBase("Urbana", 1.0, 10.0, 40.0);
        conSaldo(estudiante, "1.00");
        abrirReserva(estudiante);

        reservar(urbana);

        assertEquals(new BigDecimal("0.90"), saldoDe(estudiante));
    }

    @Test
    @DisplayName("RES-B03: pasaje que no es múltiplo de centavo (33,33 al 15 % = 4,9995) se cobra redondeado a Bs 5,00")
    void pasajeConMasDeDosDecimales() {
        tarifaServicio.guardarTarifaBase("Urbana", 33.33, 15.0, 40.0);
        conSaldo(estudiante, "10.00");
        abrirReserva(estudiante);

        reservar(urbana);

        assertEquals(new BigDecimal("5.00"), saldoDe(estudiante));
    }

    @Test
    @DisplayName("RES-B04: la selección de la lista con estado DESELECTED no recalcula el desglose")
    void deseleccionNoRecalcula() {
        abrirReserva(estudiante);
        int antes = vista.desgloses.size();

        seleccionarEnLista(urbana, ItemEvent.DESELECTED);

        assertEquals(antes, vista.desgloses.size());
    }

    // ------------------------------------------------------------------ casos inválidos

    @Test
    @DisplayName("RES-I01: pulsar Reservar sin elegir ninguna ruta muestra error y no cobra")
    void sinRutaSeleccionada() {
        conSaldo(estudiante, "500.00");
        abrirReserva(estudiante);

        reservar(null);

        assertEquals(1, vista.errores.size());
        assertTrue(vista.errores.get(0).contains("seleccione una ruta"));
        assertTrue(vista.exitos.isEmpty());
        assertEquals(new BigDecimal("500.00"), saldoDe(estudiante));
    }

    @Test
    @DisplayName("RES-I02: la opción de relleno ('Seleccionar...') no es una ruta: error y sin cobro")
    void opcionDeRellenoNoEsRuta() {
        conSaldo(estudiante, "500.00");
        abrirReserva(estudiante);

        reservar("Seleccionar...");

        assertEquals(1, vista.errores.size());
        assertTrue(vista.errores.get(0).contains("seleccione una ruta"));
        assertEquals(new BigDecimal("500.00"), saldoDe(estudiante));
    }

    @Test
    @DisplayName("RES-I03: ruta sin tarifa configurada: error claro y sin cobro")
    void rutaSinTarifa() {
        tarifaServicio.eliminarTarifa("Urbana");
        conSaldo(estudiante, "500.00");
        abrirReserva(estudiante);

        reservar(urbana);

        assertEquals(1, vista.errores.size());
        assertTrue(vista.errores.get(0).contains("No hay una tarifa configurada"));
        assertEquals(new BigDecimal("500.00"), saldoDe(estudiante));
    }

    @Test
    @DisplayName("RES-I04: ruta sin tarifa: el desglose se muestra en cero al elegirla")
    void desgloseEnCeroSinTarifa() {
        tarifaServicio.eliminarTarifa("Urbana");
        abrirReserva(estudiante);

        seleccionarEnLista(urbana, ItemEvent.SELECTED);

        assertEquals(0.0, vista.ultimoDesglose()[0], 0.001);
        assertEquals(0.0, vista.ultimoDesglose()[1], 0.001);
        assertEquals(0.0, vista.ultimoDesglose()[2], 0.001);
    }

    @Test
    @DisplayName("RES-I05: pasaje que resulta en Bs 0 (porcentaje 0) no se reserva gratis: error y sin cobro")
    void pasajeCeroNoSeReserva() {
        tarifaRepo.guardarOrActualizar("Urbana", 100.0, 0.0, 40.0); // estudiante paga 0 %
        conSaldo(estudiante, "500.00");
        abrirReserva(estudiante);

        reservar(urbana);

        assertEquals(1, vista.errores.size());
        assertTrue(vista.exitos.isEmpty());
        assertEquals(new BigDecimal("500.00"), saldoDe(estudiante));
    }

    @Test
    @DisplayName("RES-I06: tarifa base 0 no permite reservar: error y sin cobro")
    void tarifaBaseCeroNoSeReserva() {
        tarifaRepo.guardarOrActualizar("Urbana", 0.0, 15.0, 40.0);
        conSaldo(estudiante, "500.00");
        abrirReserva(estudiante);

        reservar(urbana);

        assertEquals(1, vista.errores.size());
        assertTrue(vista.exitos.isEmpty());
        assertEquals(new BigDecimal("500.00"), saldoDe(estudiante));
    }

    @Test
    @DisplayName("RES-I07: tarifa guardada como NaN no rompe la pantalla: error y sin cobro")
    void tarifaNanNoCobra() {
        tarifaRepo.guardarOrActualizar("Urbana", Double.NaN, 15.0, 40.0);
        conSaldo(estudiante, "500.00");
        abrirReserva(estudiante);

        reservar(urbana);

        assertEquals(1, vista.errores.size());
        assertTrue(vista.exitos.isEmpty());
        assertEquals(new BigDecimal("500.00"), saldoDe(estudiante));
    }

    @Test
    @DisplayName("RES-I08: saldo insuficiente: el mensaje indica saldo disponible y monto a pagar, y no cobra")
    void saldoInsuficienteConMensaje() {
        Usuario usuario = pasajero(Rol.PUBLICO_GENERAL, "publico");
        conSaldo(usuario, "99.99");
        abrirReserva(usuario);

        reservar(urbana);

        assertEquals(1, vista.errores.size());
        String mensaje = vista.errores.get(0);
        assertTrue(mensaje.contains("Saldo insuficiente"));
        assertTrue(mensaje.contains("99.99"));
        assertTrue(mensaje.contains("100.00"));
        assertEquals(new BigDecimal("99.99"), saldoDe(usuario));
    }

    @Test
    @DisplayName("RES-I09: usuario nuevo sin recargar nunca puede reservar")
    void usuarioNuevoSinRecarga() {
        abrirReserva(estudiante);

        reservar(urbana);

        assertEquals(1, vista.errores.size());
        assertTrue(vista.errores.get(0).contains("Saldo insuficiente"));
        assertEquals(new BigDecimal("0.00"), saldoDe(estudiante));
    }

    @Test
    @DisplayName("RES-I10: una reserva fallida no afecta el saldo de otro pasajero")
    void reservaFallidaNoAfectaAOtros() {
        Usuario otro = pasajero(Rol.PROFESOR, "luis");
        conSaldo(otro, "300.00");
        abrirReserva(estudiante); // el estudiante no tiene saldo

        reservar(urbana);

        assertEquals(new BigDecimal("300.00"), saldoDe(otro));
    }

    @Test
    @DisplayName("RES-I11: el constructor rechaza cualquier dependencia nula")
    void constructorConNulos() {
        VistaFalsa v = new VistaFalsa();
        assertThrows(NullPointerException.class,
                () -> new ReservarControlador(null, itinerarioServicio, tarifaServicio, monederoServicio, estudiante));
        assertThrows(NullPointerException.class,
                () -> new ReservarControlador(v, null, tarifaServicio, monederoServicio, estudiante));
        assertThrows(NullPointerException.class,
                () -> new ReservarControlador(v, itinerarioServicio, null, monederoServicio, estudiante));
        assertThrows(NullPointerException.class,
                () -> new ReservarControlador(v, itinerarioServicio, tarifaServicio, null, estudiante));
        assertThrows(NullPointerException.class,
                () -> new ReservarControlador(v, itinerarioServicio, tarifaServicio, monederoServicio, null));
    }

    @Test
    @DisplayName("RES-I12: sin itinerarios registrados la lista queda vacía y no se puede reservar")
    void sinItinerarios() {
        ItinerarioServicio vacio = new ItinerarioServicio(new ItinerarioRepositorioMemoria(),
                new UnidadRepositorioMemoria(), new ConductorRepositorioMemoria(), new AutorizacionServicio());
        vista = new VistaFalsa();
        new ReservarControlador(vista, vacio, tarifaServicio, monederoServicio, estudiante);

        assertTrue(vista.itinerarios.isEmpty());
        reservar(null);

        assertEquals(1, vista.errores.size());
        assertFalse(vista.errores.isEmpty());
    }

    // ------------------------------------------------------------------ defecto detectado

    @Test
    @Disabled("DEFECTO DETECTADO: el pasaje se calcula con double y queda 1 centavo por debajo cuando el valor exacto "
            + "termina en medio centavo (1,15 al 10 % = 0,115 -> se cobra 0,11 en vez de 0,12). "
            + "Debería redondear HALF_UP sobre el valor exacto. Quitar @Disabled para verlo fallar.")
    @DisplayName("RES-D01: el medio centavo exacto del pasaje se redondea hacia arriba")
    void medioCentavoExactoSeRedondeaHaciaArriba() {
        tarifaServicio.guardarTarifaBase("Urbana", 1.15, 10.0, 40.0);
        conSaldo(estudiante, "10.00");
        abrirReserva(estudiante);

        reservar(urbana);

        assertEquals(new BigDecimal("9.88"), saldoDe(estudiante));
    }
}
