package controlador;

import java.util.Arrays;
import modelo.Usuario;
import modelo.CredencialesInvalidasException;
import modelo.AutenticacionServicio;
import modelo.AutorizacionServicio;
import vista.LoginVista;

/**
 * Controlador de la pantalla de inicio de sesión (HU-001).
 *
 * <p>Escenario 1: credenciales correctas, se autentica y se redirige a la pantalla del rol.<br>
 * Escenario 2: credenciales incorrectas, se muestra "Usuario o contraseña incorrectos".</p>
 */
public class LoginControlador {

    private final LoginVista vista;
    private final AutenticacionServicio autenticacion;
    private final AutorizacionServicio autorizacion;
    private final Navegador navegador;

    public LoginControlador(LoginVista vista, AutenticacionServicio autenticacion,
                            AutorizacionServicio autorizacion, Navegador navegador) {
        this.vista = vista;
        this.autenticacion = autenticacion;
        this.autorizacion = autorizacion;
        this.navegador = navegador;
        this.vista.setAccionIniciarSesion(this::iniciarSesion);
        this.vista.setAccionRegistrarse(this::abrirRegistro);
    }

    private void abrirRegistro() {
        if (vista instanceof vista.LoginVentana ventana) {
            new vista.RegistroVentana(autenticacion).mostrar();
        }
    }

    /** Se ejecuta cuando el usuario presiona "Iniciar Sesión". */
    public void iniciarSesion() {
        char[] contrasena = vista.getContrasena();
        Usuario usuario;
        try {
            usuario = autenticacion.autenticar(vista.getNombreUsuario(), contrasena);
        } catch (CredencialesInvalidasException e) {
            vista.mostrarError(e.getMessage());
            vista.limpiarContrasena();
            return;
        } finally {
            // La contraseña no debe quedarse en memoria más tiempo del necesario
            if (contrasena != null) {
                Arrays.fill(contrasena, '\0');
            }
        }
        redirigirSegunRol(usuario);
        vista.cerrar();
    }

    /** Abre primero la pantalla nueva y después cierra el login, para que siempre haya una ventana abierta. */
    private void redirigirSegunRol(Usuario usuario) {
        if (autorizacion.esAdministrador(usuario)) {
            navegador.mostrarPantallaAdministrador(usuario);
        } else {
            navegador.mostrarPantallaPasajero(usuario);
        }
    }
}
