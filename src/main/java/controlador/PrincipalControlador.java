package controlador;

import vista.PrincipalVista;

/** Controlador de las pantallas principales (pasajero y administrador): por ahora solo cierra la sesión. */
public class PrincipalControlador {

    private final PrincipalVista vista;
    private final Navegador navegador;

    public PrincipalControlador(PrincipalVista vista, Navegador navegador) {
        this.vista = vista;
        this.navegador = navegador;
        this.vista.setAccionCerrarSesion(this::cerrarSesion);
    }

    public void cerrarSesion() {
        navegador.mostrarLogin();
        vista.cerrar();
    }
}
