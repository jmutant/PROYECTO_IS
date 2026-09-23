package ve.ucv.campusexpress.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ve.ucv.campusexpress.controlador.NavegadorFalso.Pantalla;
import ve.ucv.campusexpress.modelo.Rol;
import ve.ucv.campusexpress.modelo.repositorio.UsuarioRepositorioMemoria;
import ve.ucv.campusexpress.modelo.servicio.AutenticacionServicio;
import ve.ucv.campusexpress.modelo.servicio.AutorizacionServicio;

/** Pruebas de los criterios de aceptación de HU-001 a nivel del controlador (Dado / Cuando / Entonces). */
class LoginControladorTest {

    /** Texto literal del criterio de aceptación (escenario 2 de HU-001). */
    private static final String MENSAJE_CREDENCIALES_INVALIDAS = "Usuario o contraseña incorrectos";

    private AutenticacionServicio autenticacion;
    private LoginVistaFalsa vista;
    private NavegadorFalso navegador;

    @BeforeEach
    void preparar() {
        autenticacion = new AutenticacionServicio(new UsuarioRepositorioMemoria());
        vista = new LoginVistaFalsa();
        navegador = new NavegadorFalso();
        new LoginControlador(vista, autenticacion, new AutorizacionServicio(), navegador);
    }

    // ---------- Escenario 1: inicio de sesión exitoso ----------

    @ParameterizedTest
    @EnumSource(value = Rol.class, names = "ADMINISTRADOR", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("HU-001 · Dado un pasajero registrado, cuando ingresa credenciales correctas, entonces va a la pantalla de Pasajero")
    void pasajeroVaALaPantallaDePasajero(Rol rol) {
        autenticacion.registrar("pasajero1", "Pasajero Uno", "clave123".toCharArray(), rol);

        vista.iniciarSesionCon("pasajero1", "clave123");

        assertEquals(Pantalla.PASAJERO, navegador.pantalla);
        assertEquals("pasajero1", navegador.usuario.getNombreUsuario());
        assertEquals(rol, navegador.usuario.getRol());
        assertTrue(vista.cerrada);
        assertTrue(vista.errores.isEmpty());
    }

    @Test
    @DisplayName("HU-001 · Dado un administrador registrado, cuando ingresa credenciales correctas, entonces va a la pantalla de Administrador")
    void administradorVaALaPantallaDeAdministrador() {
        autenticacion.registrar("admin1", "Administrador Uno", "admin123".toCharArray(), Rol.ADMINISTRADOR);

        vista.iniciarSesionCon("admin1", "admin123");

        assertEquals(Pantalla.ADMINISTRADOR, navegador.pantalla);
        assertEquals("admin1", navegador.usuario.getNombreUsuario());
        assertTrue(vista.cerrada);
        assertTrue(vista.errores.isEmpty());
    }

    // ---------- Escenario 2: credenciales inválidas ----------

    @Test
    @DisplayName("HU-001 · Dado un login, cuando la contraseña es incorrecta, entonces se muestra el error y no se navega")
    void contrasenaIncorrectaMuestraElError() {
        autenticacion.registrar("estudiante1", "Estudiante Uno", "clave123".toCharArray(), Rol.ESTUDIANTE);

        vista.iniciarSesionCon("estudiante1", "incorrecta");

        assertEquals(1, vista.errores.size());
        assertEquals(MENSAJE_CREDENCIALES_INVALIDAS, vista.errores.get(0));
        assertEquals(Pantalla.NINGUNA, navegador.pantalla);
        assertFalse(vista.cerrada);
        assertTrue(vista.contrasenaLimpiada);
    }

    @Test
    @DisplayName("HU-001 · Dado un login, cuando el usuario no existe, entonces se muestra el mismo error y no se navega")
    void usuarioInexistenteMuestraElError() {
        vista.iniciarSesionCon("fantasma", "clave123");

        assertEquals(1, vista.errores.size());
        assertEquals(MENSAJE_CREDENCIALES_INVALIDAS, vista.errores.get(0));
        assertEquals(Pantalla.NINGUNA, navegador.pantalla);
        assertFalse(vista.cerrada);
    }

    @Test
    @DisplayName("Dado un login con los campos vacíos, cuando se presiona Iniciar Sesión, entonces se muestra el error")
    void camposVaciosMuestranElError() {
        vista.iniciarSesionCon("", "");

        assertEquals(MENSAJE_CREDENCIALES_INVALIDAS, vista.errores.get(0));
        assertEquals(Pantalla.NINGUNA, navegador.pantalla);
    }

    @Test
    @DisplayName("Tras un intento fallido se puede reintentar con credenciales correctas")
    void sePuedeReintentarTrasUnError() {
        autenticacion.registrar("estudiante1", "Estudiante Uno", "clave123".toCharArray(), Rol.ESTUDIANTE);

        vista.iniciarSesionCon("estudiante1", "incorrecta");
        vista.iniciarSesionCon("estudiante1", "clave123");

        assertEquals(1, vista.errores.size());
        assertEquals(Pantalla.PASAJERO, navegador.pantalla);
        assertTrue(vista.cerrada);
    }

    // ---------- Seguridad ----------

    @Test
    @DisplayName("La contraseña se borra de memoria después de cada intento, sea exitoso o fallido")
    void laContrasenaSeBorraDeMemoria() {
        autenticacion.registrar("estudiante1", "Estudiante Uno", "clave123".toCharArray(), Rol.ESTUDIANTE);

        vista.iniciarSesionCon("estudiante1", "incorrecta");
        assertTrue(todoCeros(vista.contrasena));

        vista.iniciarSesionCon("estudiante1", "clave123");
        assertTrue(todoCeros(vista.contrasena));
    }

    private static boolean todoCeros(char[] caracteres) {
        for (char caracter : caracteres) {
            if (caracter != '\0') {
                return false;
            }
        }
        return true;
    }
}
