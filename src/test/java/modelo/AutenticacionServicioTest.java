package modelo;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas unitarias de CAJA NEGRA para el Registro de usuarios.
 *
 * Solo se usa la interfaz pública de AutenticacionServicio.registrarUsuario(...)
 * y se verifica el resultado observable (excepción, mensaje o que el usuario
 * quede registrado y pueda iniciar sesión).
 *
 * Particiones y valores límite:
 *   - Roles válidos (Estudiante, Empleado, Profesor, Público General) vs. Administrador / nulo.
 *   - Campos obligatorios: nombre, apellido, cédula, usuario y contraseña (nulo / vacío / en blanco).
 *   - Contraseña: longitud mínima 6 (límites 5 y 6), al menos un número y una mayúscula.
 *   - Nombre de usuario ya existente (también con distinta capitalización).
 */
class AutenticacionServicioTest {

    private static final String CLAVE_VALIDA = "Clave123";

    private AutenticacionServicio servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new AutenticacionServicio(new UsuarioRepositorioMemoria());
    }

    // ---------------------------------------------------------------- Casos válidos

    @ParameterizedTest
    @EnumSource(value = Rol.class, names = {"ESTUDIANTE", "EMPLEADO", "PROFESOR", "PUBLICO_GENERAL"})
    @DisplayName("Registro válido con cada rol permitido: el usuario queda registrado y puede iniciar sesión")
    void testRegistroValidoConCadaRol(Rol rol) {
        assertDoesNotThrow(() ->
            servicio.registrarUsuario("Maria", "Perez", "12345678", rol, "mperez", CLAVE_VALIDA)
        );

        assertTrue(servicio.existe("mperez"));
        Usuario iniciado = servicio.iniciarSesion("mperez", CLAVE_VALIDA);
        assertEquals(rol, iniciado.getRol());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcde1", "Abcdef1", "A1bcdefghijkl"})
    @DisplayName("Contraseñas válidas en el límite (6 caracteres) y por encima")
    void testContrasenaValidaEnLimites(String contrasena) {
        assertDoesNotThrow(() ->
            servicio.registrarUsuario("Maria", "Perez", "12345678", Rol.ESTUDIANTE, "mperez", contrasena)
        );
        assertTrue(servicio.existe("mperez"));
    }

    // ---------------------------------------------------------------- Campos obligatorios

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("registrosConCampoFaltante")
    @DisplayName("Falta un campo obligatorio: lanza IllegalArgumentException y no registra al usuario")
    void testCampoObligatorioFaltante(String caso, String nombre, String apellido, String cedula,
                                    String username, String password) {
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> servicio.registrarUsuario(nombre, apellido, cedula, Rol.ESTUDIANTE, username, password)
        );

        assertEquals("Todos los campos son obligatorios.", excepcion.getMessage());
        assertFalse(servicio.existe("mperez"));
    }

    static Stream<Arguments> registrosConCampoFaltante() {
        return Stream.of(
            Arguments.of("nombre nulo",        null,    "Perez", "12345678", "mperez", CLAVE_VALIDA),
            Arguments.of("nombre vacío",       "",      "Perez", "12345678", "mperez", CLAVE_VALIDA),
            Arguments.of("apellido en blanco", "Maria", "   ",   "12345678", "mperez", CLAVE_VALIDA),
            Arguments.of("cédula vacía",       "Maria", "Perez", "",         "mperez", CLAVE_VALIDA),
            Arguments.of("usuario nulo",       "Maria", "Perez", "12345678", null,     CLAVE_VALIDA),
            Arguments.of("usuario vacío",      "Maria", "Perez", "12345678", "",       CLAVE_VALIDA),
            Arguments.of("contraseña nula",    "Maria", "Perez", "12345678", "mperez", null),
            Arguments.of("contraseña vacía",   "Maria", "Perez", "12345678", "mperez", "")
        );
    }

    // ---------------------------------------------------------------- Rol

    @Test
    @DisplayName("No se permite registrar el rol Administrador desde el registro público")
    void testRolAdministradorNoPermitido() {
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> servicio.registrarUsuario("Maria", "Perez", "12345678",
                    Rol.ADMINISTRADOR, "mperez", CLAVE_VALIDA)
        );

        assertEquals("El rol seleccionado no es válido para registro público.", excepcion.getMessage());
        assertFalse(servicio.existe("mperez"));
    }

    @Test
    @DisplayName("No se permite registrar sin rol (nulo)")
    void testRolNuloNoPermitido() {
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> servicio.registrarUsuario("Maria", "Perez", "12345678", null, "mperez", CLAVE_VALIDA)
        );

        assertEquals("El rol seleccionado no es válido para registro público.", excepcion.getMessage());
    }

    // ---------------------------------------------------------------- Contraseña

    @ParameterizedTest
    @ValueSource(strings = {"A1", "Ab1", "Abc12"})
    @DisplayName("Contraseña con menos de 6 caracteres (límite: 5) es rechazada")
    void testContrasenaMuyCorta(String contrasena) {
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> servicio.registrarUsuario("Maria", "Perez", "12345678",
                    Rol.ESTUDIANTE, "mperez", contrasena)
        );

        assertEquals("La contraseña debe tener al menos 6 caracteres.", excepcion.getMessage());
        assertFalse(servicio.existe("mperez"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcdef", "CLAVESINNUMERO"})
    @DisplayName("Contraseña sin ningún número es rechazada")
    void testContrasenaSinNumero(String contrasena) {
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> servicio.registrarUsuario("Maria", "Perez", "12345678",
                    Rol.ESTUDIANTE, "mperez", contrasena)
        );

        assertEquals("La contraseña debe contener al menos un número.", excepcion.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc123", "clave123456"})
    @DisplayName("Contraseña sin ninguna letra mayúscula es rechazada")
    void testContrasenaSinMayuscula(String contrasena) {
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> servicio.registrarUsuario("Maria", "Perez", "12345678",
                    Rol.ESTUDIANTE, "mperez", contrasena)
        );

        assertEquals("La contraseña debe contener al menos una letra mayúscula.", excepcion.getMessage());
    }

    // ---------------------------------------------------------------- Usuario duplicado

    @ParameterizedTest
    @ValueSource(strings = {"mperez", "MPEREZ", "MPerez"})
    @DisplayName("Nombre de usuario ya registrado (sin distinguir mayúsculas) lanza UsuarioDuplicadoException")
    void testUsernameDuplicado(String usernameRepetido) {
        servicio.registrarUsuario("Maria", "Perez", "12345678", Rol.ESTUDIANTE, "mperez", CLAVE_VALIDA);

        assertThrows(
            UsuarioDuplicadoException.class,
            () -> servicio.registrarUsuario("Pedro", "Gomez", "87654321",
                    Rol.PROFESOR, usernameRepetido, "Otra1234")
        );

        // El usuario original no se modifica: sigue entrando con su contraseña original.
        assertEquals(Rol.ESTUDIANTE, servicio.iniciarSesion("mperez", CLAVE_VALIDA).getRol());
    }
}