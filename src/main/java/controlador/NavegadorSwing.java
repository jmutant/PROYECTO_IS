package controlador;

import modelo.Usuario;
import modelo.AutenticacionServicio;
import modelo.AutorizacionServicio;
import modelo.FlotaServicio;
import modelo.ItinerarioServicio;
import modelo.ConductorRepositorioMemoria;
import modelo.ItinerarioRepositorioMemoria;
import modelo.UnidadRepositorioMemoria;
import vista.GestionFlotaVentana;
import vista.GestionItinerariosVentana;
import vista.LoginVentana;
import vista.PrincipalAdministradorVentana;
import vista.PrincipalPasajeroVentana;

/** Implementación real de Navegador: crea ventanas y sus controladores. */
public class NavegadorSwing implements Navegador {

    private final AutenticacionServicio autenticacion;
    private final AutorizacionServicio autorizacion;
    private final FlotaServicio flota;
    private final ItinerarioServicio itinerarios;

    /** Constructor de compatibilidad para código que solo requiera HU-001. */
    public NavegadorSwing(AutenticacionServicio autenticacion, AutorizacionServicio autorizacion) {
        this(autenticacion, autorizacion,
                new FlotaServicio(new UnidadRepositorioMemoria(), autorizacion),
                new ItinerarioServicio(new ItinerarioRepositorioMemoria(), new UnidadRepositorioMemoria(),
                        new ConductorRepositorioMemoria(), autorizacion));
    }

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
        ventana.setAccionGestionFlota(() -> mostrarGestionFlota(usuario));
        ventana.setAccionGestionItinerarios(() -> mostrarGestionItinerarios(usuario));
        ventana.mostrar();
    }

    private void mostrarGestionFlota(Usuario usuario) {
        GestionFlotaVentana ventana = new GestionFlotaVentana();
        new GestionFlotaControlador(ventana, flota, usuario);
        ventana.mostrar();
    }

    private void mostrarGestionItinerarios(Usuario usuario) {
        GestionItinerariosVentana ventana = new GestionItinerariosVentana();
        new GestionItinerariosControlador(ventana, itinerarios, usuario);
        ventana.mostrar();
    }
}
