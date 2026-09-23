package ve.ucv.campusexpress.vista;

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

    void mostrar();

    void cerrar();
}
