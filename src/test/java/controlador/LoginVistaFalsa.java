package controlador;

import java.util.ArrayList;
import java.util.List;
import vista.LoginVista;

/** Vista de login falsa para probar el controlador sin abrir ventanas Swing. */
class LoginVistaFalsa implements LoginVista {

    String nombreUsuario = "";
    char[] contrasena = new char[0];
    final List<String> errores = new ArrayList<>();
    boolean contrasenaLimpiada;
    boolean cerrada;
    private Runnable accionIniciarSesion;

    @Override
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    @Override
    public char[] getContrasena() {
        return contrasena;
    }

    @Override
    public void mostrarError(String mensaje) {
        errores.add(mensaje);
    }

    @Override
    public void limpiarContrasena() {
        contrasenaLimpiada = true;
    }

    @Override
    public void setAccionIniciarSesion(Runnable accion) {
        this.accionIniciarSesion = accion;
    }

    @Override
    public void mostrar() {
    }

    @Override
    public void cerrar() {
        cerrada = true;
    }

    /** Simula que el usuario escribe sus datos y presiona "Iniciar Sesión". */
    void iniciarSesionCon(String usuario, String clave) {
        this.nombreUsuario = usuario;
        this.contrasena = clave.toCharArray();
        accionIniciarSesion.run();
    }
}
