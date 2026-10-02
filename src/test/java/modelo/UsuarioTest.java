package modelo;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Pruebas unitarias de CAJA NEGRA para el Inicio de Sesión (HU-001).
 *
 * Solo se usa la interfaz pública de AutenticacionServicio.iniciarSesion(...):
 * se prueban entradas y salidas esperadas sin mirar la implementación interna.
 * Se usa el repositorio en memoria para no tocar archivos del disco.
 *
 * Particiones de equivalencia:
 *   - Válida:   usuario existente + contraseña correcta.
 *   - Inválida: usuario inexistente.
 *   - Inválida: contraseña incorrecta.
 *   - Inválida: usuario o contraseña nulos / vacíos / en blanco.
 */
class UsuarioTest {

    private static final String MENSAJE_CREDENCIALES = "Usuario o contraseña incorrectos";

    private AutenticacionServicio servicio;

    @BeforeEach
    void prepararUsuarioExistente() {
        UsuarioRepositorio repositorio = new UsuarioRepositorioMemoria();
        // Usuario de prueba ya registrado en el sistema.
        repositorio.guardar(new Usuario("Maria", "Perez", "12345678",
                Rol.ESTUDIANTE, "mperez", "Clave123"));
        servicio = new AutenticacionServicio(repositorio);
    }

    @Test
    @DisplayName("Credenciales correctas: devuelve el usuario autenticado")
    void testLoginConCredencialesValidas() {
        Usuario usuario = servicio.iniciarSesion("mperez", "Clave123");

        assertEquals("mperez", usuario.getUsername());
        assertEquals(Rol.ESTUDIANTE, usuario.getRol());
    }

    @ParameterizedTest(name = "usuario=''{0}'' contraseña=''{1}''")
    @MethodSource("credencialesIncorrectas")
    @DisplayName("Credenciales incorrectas: lanza excepcion con el mensaje del criterio de aceptación")
    void testLoginConCredencialesIncorrectas(String username, String password) {
        CredencialesInvalidasException excepcion = assertThrows(
            CredencialesInvalidasException.class,
            () -> servicio.iniciarSesion(username, password)
        );

        // El mensaje es el mismo en todos los casos para no revelar cuál dato falló.
        assertEquals(MENSAJE_CREDENCIALES, excepcion.getMessage());
    }

    static Stream<Arguments> credencialesIncorrectas() {
        return Stream.of(
            Arguments.of("noexiste", "Clave123"),   // usuario inexistente
            Arguments.of("mperez", "ClaveMala1"),   // contraseña incorrecta
            Arguments.of("mperez", "clave123"),     // contraseña con distinta capitalización
            Arguments.of("", "Clave123"),           // usuario vacío
            Arguments.of("   ", "Clave123"),        // usuario en blanco
            Arguments.of(null, "Clave123"),         // usuario nulo
            Arguments.of("mperez", ""),             // contraseña vacía
            Arguments.of("mperez", null),           // contraseña nula
            Arguments.of("", "")                    // ambos vacíos
        );
    }
}