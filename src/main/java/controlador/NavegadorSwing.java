package controlador;

import modelo.AutenticacionServicio;
import modelo.AutorizacionServicio;
import modelo.FlotaServicio;
import modelo.ItinerarioServicio;
import modelo.Usuario;
import vista.GestionFlotaVentana;
import vista.GestionItinerariosVentana;
import vista.LoginVentana;
import vista.PrincipalAdministradorVentana;
import vista.PrincipalPasajeroVentana;

/** Implementación real de Navegador: crea las ventanas Swing y sus controladores. */
public class NavegadorSwing implements Navegador {

    private final AutenticacionServicio autenticacion;
    private final AutorizacionServicio autorizacion;
    @SuppressWarnings("unused")
    private final FlotaServicio flota;
    @SuppressWarnings("unused")
    private final ItinerarioServicio itinerarios;

    public NavegadorSwing(AutenticacionServicio autenticacion, AutorizacionServicio autorizacion,
                          FlotaServicio flota, ItinerarioServicio itinerarios) {
        this.autenticacion = autenticacion;
        this.autorizacion = autorizacion;
        this.flota = flota;
        this.itinerarios = itinerarios;
    }

    @Override
    public void mostrarLogin() {
        LoginVentana ventana = new LoginVentana();
        new LoginControlador(ventana, autenticacion, autorizacion, this);
        ventana.mostrar();
    }

    @Override
    public void mostrarPantallaPasajero(Usuario usuario) {
        PrincipalPasajeroVentana ventana = new PrincipalPasajeroVentana(usuario);
        new PrincipalControlador(ventana, this);
        ventana.mostrar();
    }

    @Override
    public void mostrarPantallaAdministrador(Usuario usuario) {
        PrincipalAdministradorVentana ventana = new PrincipalAdministradorVentana(usuario);
        new PrincipalControlador(ventana, this);
        ventana.setAccionGestionFlota(this::mostrarGestionFlota);
        ventana.setAccionGestionItinerarios(this::mostrarGestionItinerarios);
        ventana.mostrar();
    }

    private void mostrarGestionFlota() {
        GestionFlotaVentana ventana = new GestionFlotaVentana();
        new GestionFlotaControlador(ventana);
        ventana.mostrar();
    }

    private void mostrarGestionItinerarios() {
        GestionItinerariosVentana ventana = new GestionItinerariosVentana();
        new GestionItinerariosControlador(ventana);
        ventana.mostrar();
    }
}
