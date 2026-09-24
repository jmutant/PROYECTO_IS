package controlador;

import modelo.Usuario;

/** Navegador falso: solo recuerda a qué pantalla se le pidió ir. */
class NavegadorFalso implements Navegador {

    enum Pantalla { NINGUNA, LOGIN, PASAJERO, ADMINISTRADOR }

    Pantalla pantalla = Pantalla.NINGUNA;
    Usuario usuario;

    @Override
    public void mostrarLogin() {
        pantalla = Pantalla.LOGIN;
    }

    @Override
    public void mostrarPantallaPasajero(Usuario usuario) {
        pantalla = Pantalla.PASAJERO;
        this.usuario = usuario;
    }

    @Override
    public void mostrarPantallaAdministrador(Usuario usuario) {
        pantalla = Pantalla.ADMINISTRADOR;
        this.usuario = usuario;
    }
}
