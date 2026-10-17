package controlador;

import javax.swing.JOptionPane;

import modelo.AccesoDenegadoException;
import modelo.AlmacenDatosLocal;
import modelo.AutenticacionServicio;
import modelo.AutorizacionServicio;
import modelo.ConductorRepositorio;
import modelo.FlotaServicio;
import modelo.ItinerarioServicio;
import modelo.MonederoRepositorio;
import modelo.MonederoRepositorioArchivo;
import modelo.MonederoServicio;
import modelo.TarifaServicio;
import modelo.Usuario;
import vista.ConfiguracionTarifaVentana;
import vista.GestionConductoresVentana;
import vista.GestionFlotaVentana;
import vista.GestionItinerariosVentana;
import vista.LoginVentana;
import vista.MonederoVentana;
import vista.PrincipalAdministradorVentana;
import vista.PrincipalPasajeroVentana;
import vista.RegistroVentana;
import vista.ReservarVentana;

// Implementación real de Navegador: crea las ventanas Swing y sus controladores.
public class NavegadorSwing implements Navegador {

    private final AutenticacionServicio autenticacion;
    private final AutorizacionServicio autorizacion;
    private final FlotaServicio flota;
    private final ItinerarioServicio itinerarios;
    private final MonederoServicio monedero;
    private final TarifaServicio tarifaServicio;
    private Usuario usuarioActual;
    private final ConductorRepositorio conductores;

    // Constructor principal con persistencia del monedero
    public NavegadorSwing(AutenticacionServicio autenticacion, AutorizacionServicio autorizacion,
                        FlotaServicio flota, ItinerarioServicio itinerarios, ConductorRepositorio conductores,
                        MonederoRepositorio monederoRepositorio, TarifaServicio tarifaServicio) {
        this.autenticacion = autenticacion;
        this.autorizacion = autorizacion;
        this.flota = flota;
        this.itinerarios = itinerarios;
        this.conductores = conductores;
        this.monedero = new MonederoServicio(monederoRepositorio);
        this.tarifaServicio = tarifaServicio;
    }

    // Constructor secundario
    public NavegadorSwing(AutenticacionServicio autenticacion, AutorizacionServicio autorizacion,
                        FlotaServicio flota, ItinerarioServicio itinerarios, 
                        ConductorRepositorio conductores, TarifaServicio tarifaServicio) {
        this(autenticacion, autorizacion, flota, itinerarios, conductores,
                new MonederoRepositorioArchivo(AlmacenDatosLocal.porDefecto()), tarifaServicio);
    }

    @Override
    public void mostrarLogin() {
        LoginVentana ventana = new LoginVentana();
        new LoginControlador(ventana, autenticacion, autorizacion, this);
        ventana.mostrar();
    }

    @Override
    public void mostrarPantallaPasajero(Usuario usuario) {
        this.usuarioActual = usuario;
        PrincipalPasajeroVentana ventana = new PrincipalPasajeroVentana(usuario);
        new PrincipalControlador(ventana, this);
        ventana.setAccionRutasDisponibles(this::mostrarReservar);
        ventana.setAccionMonedero(this::mostrarMonedero);
        ventana.mostrar();
    }

    @Override
    public void mostrarPantallaAdministrador(Usuario usuario) {
        this.usuarioActual = usuario;
        PrincipalAdministradorVentana ventana = new PrincipalAdministradorVentana(usuario);
        new PrincipalControlador(ventana, this);
        ventana.setAccionGestionFlota(this::mostrarGestionFlota);
        ventana.setAccionGestionItinerarios(this::mostrarGestionItinerarios);
        ventana.setAccionCrearAdministrador(this::mostrarCrearAdministrador);
        ventana.setAccionMonedero(this::mostrarMonedero);
        ventana.setAccionConfigurarTarifa(this::mostrarConfigurarTarifa);
        ventana.setAccionRegistrarConductor(this::mostrarRegistroConductor);
        ventana.mostrar();
    }

    // Ventana "reserva": se abre desde el botón Rutas Disponibles del pasajero
    private void mostrarReservar() {
        ReservarVentana ventana = new ReservarVentana();
        new ReservarControlador(ventana, itinerarios, tarifaServicio, monedero, usuarioActual);
        ventana.mostrar();
    }

    private void mostrarMonedero() {
        if (usuarioActual == null) {
            return;
        }
        MonederoVentana ventana = new MonederoVentana(usuarioActual);
        new MonederoControlador(ventana, monedero, usuarioActual);
        ventana.mostrar();
    }

    private void mostrarConfigurarTarifa() {
        ConfiguracionTarifaVentana ventana = new ConfiguracionTarifaVentana();
        new TarifaControlador(ventana, tarifaServicio, usuarioActual);
        ventana.mostrar();
    }

    private void mostrarGestionFlota() {
        GestionFlotaVentana ventana = new GestionFlotaVentana();
        new GestionFlotaControlador(ventana, flota, usuarioActual);
        ventana.mostrar();
    }

    private void mostrarCrearAdministrador() {
        try {
            autorizacion.exigirAdministrador(usuarioActual);
        } catch (AccesoDenegadoException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new RegistroVentana(autenticacion, true).mostrar();
    }

    private void mostrarGestionItinerarios() {
        GestionItinerariosVentana ventana = new GestionItinerariosVentana();
        new GestionItinerariosControlador(ventana, itinerarios, usuarioActual);
        ventana.mostrar();
    }

    private void mostrarRegistroConductor() {
        try {
            autorizacion.exigirAdministrador(usuarioActual);
        } catch (AccesoDenegadoException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GestionConductoresVentana ventana = new GestionConductoresVentana();
        new ConductorControlador(ventana, conductores);
        ventana.mostrar();
    }  
}