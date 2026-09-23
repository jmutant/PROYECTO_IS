package ve.ucv.campusexpress.modelo.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ve.ucv.campusexpress.modelo.Rol;
import ve.ucv.campusexpress.modelo.Usuario;
import ve.ucv.campusexpress.modelo.excepcion.CredencialesInvalidasException;
import ve.ucv.campusexpress.modelo.excepcion.UsuarioDuplicadoException;
import ve.ucv.campusexpress.modelo.repositorio.UsuarioRepositorio;
import ve.ucv.campusexpress.modelo.repositorio.UsuarioRepositorioMemoria;

class AutenticacionServicioTest {

    /** Texto literal del criterio de aceptación (escenario 2 de HU-001). */
    private static final String MENSAJE_CREDENCIALES_INVALIDAS = "Usuario o contraseña incorrectos";

    private UsuarioRepositorio repositorio;
    private AutenticacionServicio servicio;

    @BeforeEach
    void preparar() {
        repositorio = new UsuarioRepositorioMemoria();
        servicio = new AutenticacionServicio(repositorio);
        servicio.registrar("estudiante1", "Estudiante Uno", "clave123".toCharArray(), Rol.ESTUDIANTE);
        servicio.registrar("admin1", "Administrador Uno", "admin123".toCharArray(), Rol.ADMINISTRADOR);
    }

    // ---------- HU-001, escenario 1: inicio de sesión exitoso ----------

    @Test
    @DisplayName("HU-001 · Credenciales correctas: se autentica al usuario registrado")
    void autenticaConCredencialesCorrectas() {
        Usuario usuario = servicio.autenticar("estudiante1", "clave123".toCharArray());

        assertEquals("estudiante1", usuario.getNombreUsuario());
        assertEquals("Estudiante Uno", usuario.getNombreCompleto());
        assertEquals(Rol.ESTUDIANTE, usuario.getRol());
    }

    @Test
    @DisplayName("HU-001 · Credenciales correctas de un administrador: el usuario devuelto conserva su rol")
    void autenticaAUnAdministrador() {
        Usuario usuario = servicio.autenticar("admin1", "admin123".toCharArray());

        assertEquals(Rol.ADMINISTRADOR, usuario.getRol());
    }

    @Test
    @DisplayName("El nombre de usuario no distingue mayúsculas ni espacios en los extremos")
    void nombreDeUsuarioSinDistinguirMayusculas() {
        Usuario usuario = servicio.autenticar("  ESTUDIANTE1 ", "clave123".toCharArray());

        assertEquals("estudiante1", usuario.getNombreUsuario());
    }

    // ---------- HU-001, escenario 2: credenciales inválidas ----------

    @Test
    @DisplayName("HU-001 · Contraseña incorrecta: se deniega el acceso con el mensaje del criterio de aceptación")
    void contrasenaIncorrecta() {
        CredencialesInvalidasException error = assertThrows(CredencialesInvalidasException.class,
                () -> servicio.autenticar("estudiante1", "otraClave".toCharArray()));

        assertEquals(MENSAJE_CREDENCIALES_INVALIDAS, error.getMessage());
    }

    @Test
    @DisplayName("HU-001 · Usuario inexistente: se deniega el acceso con el mismo mensaje")
    void usuarioInexistente() {
        CredencialesInvalidasException error = assertThrows(CredencialesInvalidasException.class,
                () -> servicio.autenticar("fantasma", "clave123".toCharArray()));

        assertEquals(MENSAJE_CREDENCIALES_INVALIDAS, error.getMessage());
    }

    @Test
    @DisplayName("La contraseña distingue mayúsculas y minúsculas")
    void contrasenaDistingueMayusculas() {
        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.autenticar("estudiante1", "CLAVE123".toCharArray()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Usuario vacío o en blanco: se trata como credenciales inválidas")
    void usuarioVacio(String nombreUsuario) {
        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.autenticar(nombreUsuario, "clave123".toCharArray()));
    }

    @Test
    void usuarioNulo() {
        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.autenticar(null, "clave123".toCharArray()));
    }

    @Test
    @DisplayName("Contraseña vacía o nula: se trata como credenciales inválidas")
    void contrasenaVaciaONula() {
        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.autenticar("estudiante1", new char[0]));
        assertThrows(CredencialesInvalidasException.class,
                () -> servicio.autenticar("estudiante1", null));
    }

    // ---------- Registro de usuarios ----------

    @Test
    @DisplayName("Registrar un usuario lo deja disponible para iniciar sesión")
    void registrarYAutenticar() {
        servicio.registrar("nuevo", "Usuario Nuevo", "secreta1".toCharArray(), Rol.PROFESOR);

        Usuario usuario = servicio.autenticar("nuevo", "secreta1".toCharArray());

        assertEquals(Rol.PROFESOR, usuario.getRol());
    }

    @Test
    @DisplayName("Solo se guarda el hash: la contraseña nunca queda en texto plano")
    void noGuardaLaContrasenaEnTextoPlano() {
        Usuario guardado = repositorio.buscarPorNombreUsuario("estudiante1").orElseThrow();

        assertNotNull(guardado.getHashContrasena());
        assertFalse(guardado.getHashContrasena().contains("clave123"));
    }

    @Test
    @DisplayName("No permite registrar dos usuarios con el mismo nombre (sin distinguir mayúsculas)")
    void rechazaUsuarioDuplicado() {
        assertThrows(UsuarioDuplicadoException.class,
                () -> servicio.registrar("estudiante1", "Otro", "clave123".toCharArray(), Rol.EMPLEADO));
        assertThrows(UsuarioDuplicadoException.class,
                () -> servicio.registrar("ESTUDIANTE1", "Otro", "clave123".toCharArray(), Rol.EMPLEADO));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "12345"})
    @DisplayName("Rechaza contraseñas más cortas que el mínimo permitido")
    void rechazaContrasenaCorta(String contrasena) {
        assertTrue(contrasena.length() < AutenticacionServicio.LONGITUD_MINIMA_CONTRASENA);

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrar("otro", "Otro", contrasena.toCharArray(), Rol.EMPLEADO));
    }

    @Test
    void rechazaContrasenaNulaAlRegistrar() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrar("otro", "Otro", null, Rol.EMPLEADO));
    }

    @Test
    @DisplayName("Rechaza datos incompletos al registrar (usuario, nombre o rol)")
    void rechazaDatosIncompletos() {
        char[] clave = "clave123".toCharArray();

        assertThrows(IllegalArgumentException.class, () -> servicio.registrar(" ", "Otro", clave, Rol.EMPLEADO));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrar("otro", " ", clave, Rol.EMPLEADO));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrar("otro", "Otro", clave, null));
    }
}
