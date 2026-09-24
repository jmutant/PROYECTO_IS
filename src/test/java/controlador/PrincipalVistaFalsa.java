package controlador;

import vista.PrincipalVista;

/** Pantalla principal falsa para probar {@link PrincipalControlador}. */
class PrincipalVistaFalsa implements PrincipalVista {

    boolean cerrada;
    private Runnable accionCerrarSesion;

    @Override
    public void setAccionCerrarSesion(Runnable accion) {
        this.accionCerrarSesion = accion;
    }

    @Override
    public void mostrar() {
    }

    @Override
    public void cerrar() {
        cerrada = true;
    }

    /** Simula que el usuario presiona "Cerrar sesión". */
    void presionarCerrarSesion() {
        accionCerrarSesion.run();
    }
}
