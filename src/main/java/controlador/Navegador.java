package controlador;

import modelo.Usuario;

/**
 * Decide qué pantalla se muestra a continuación. Al ser una interfaz, los controladores se
 * pueden probar sin abrir ventanas reales (ver las pruebas unitarias del paquete controlador).
 */
public interface Navegador {

    void mostrarLogin();

    /** Pantalla principal de los usuarios con rol de pasajero (Estudiante, Empleado, etc.). */
    void mostrarPantallaPasajero(Usuario usuario);

    void mostrarPantallaAdministrador(Usuario usuario);
}
