package controlador;

import javax.swing.JOptionPane;

import modelo.AccesoDenegadoException;
import modelo.AlmacenDatosLocal;
import modelo.AutenticacionServicio;
import modelo.AutorizacionServicio;
import modelo.FlotaServicio;
import modelo.ItinerarioServicio;
import modelo.MonederoRepositorio;
import modelo.MonederoRepositorioArchivo;
import modelo.MonederoServicio;
import modelo.Usuario;
import vista.GestionFlotaVentana;
import vista.GestionItinerariosVentana;
import vista.LoginVentana;
import vista.MonederoVentana;
import vista.PrincipalAdministradorVentana;
import vista.PrincipalPasajeroVentana;
import vista.RegistroVentana;

//Implementación real de Navegador: crea las ventanas Swing y sus controladores.
public class NavegadorSwing implements Navegador {

    private final AutenticacionServicio autenticacion;
    private final AutorizacionServicio autorizacion;
    private final FlotaServicio flota;
    private final ItinerarioServicio itinerarios;
    private final MonederoServicio monedero;
    private Usuario usuarioActual;

    //Constructor principal con persistencia del monedero
    public NavegadorSwing(AutenticacionServicio autenticacion, AutorizacionServicio autorizacion,
                        FlotaServicio flota, ItinerarioServicio itinerarios,
                        MonederoRepositorio monederoRepositorio) {
        this.autenticacion = autenticacion;
        this.autorizacion = autorizacion;
        this.flota = flota;
        this.itinerarios = itinerarios;
        this.monedero = new MonederoServicio(monederoRepositorio);
    }

    
    public NavegadorSwing(AutenticacionServicio autenticacion, AutorizacionServicio autorizacion,
                        FlotaServicio flota, ItinerarioServicio itinerarios) {
        this(autenticacion, autorizacion, flota, itinerarios,
                new MonederoRepositorioArchivo(AlmacenDatosLocal.porDefecto()));
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

    private void mostrarGestionFlota() {
        GestionFlotaVentana ventana = new GestionFlotaVentana();
        new GestionFlotaControlador(ventana, flota,usuarioActual );
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
}