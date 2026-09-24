package vista;

/**
 * Lo que el controlador necesita de la pantalla de login. La vista solo muestra y captura datos;
 * las decisiones las toma el controlador.
 */
public interface LoginVista {

    String getNombreUsuario();

    /** Devuelve la contraseña ingresada. El controlador la borra de memoria después de usarla. */
    char[] getContrasena();

    void mostrarError(String mensaje);

    void limpiarContrasena();

    /** Registra la acción que se ejecuta al presionar "Iniciar Sesión" (o Enter). */
    void setAccionIniciarSesion(Runnable accion);

    /** Acción opcional para abrir el formulario de registro. */
    default void setAccionRegistrarse(Runnable accion) {
        // Las vistas falsas de las pruebas no necesitan implementar registro.
    }

    void mostrar();

    void cerrar();
}
