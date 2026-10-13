package controlador;

import modelo.Usuario;


public interface Navegador {

    void mostrarLogin();

    /** Pantalla principal de los usuarios con rol de pasajero (Estudiante, Empleado, etc.). */
    void mostrarPantallaPasajero(Usuario usuario);

    void mostrarPantallaAdministrador(Usuario usuario);
}
