package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UsuarioTest {

    private static final String HASH = "hash-de-prueba";

    @Test
    @DisplayName("Normaliza el nombre de usuario: sin espacios en los extremos y en minúsculas")
    void normalizaElNombreDeUsuario() {
        Usuario usuario = new Usuario("  Jesus.H  ", "Jesús Hiraola", Rol.ESTUDIANTE, HASH);

        assertEquals("jesus.h", usuario.getNombreUsuario());
    }

    @Test
    @DisplayName("Conserva el nombre completo, el rol y el hash recibidos")
    void conservaLosDatos() {
        Usuario usuario = new Usuario("admin", "  Administrador  ", Rol.ADMINISTRADOR, HASH);

        assertEquals("Administrador", usuario.getNombreCompleto());
        assertEquals(Rol.ADMINISTRADOR, usuario.getRol());
        assertEquals(HASH, usuario.getHashContrasena());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Rechaza un nombre de usuario vacío o en blanco")
    void rechazaNombreUsuarioEnBlanco(String nombreUsuario) {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario(nombreUsuario, "Nombre", Rol.ESTUDIANTE, HASH));
    }

    @Test
    void rechazaNombreUsuarioNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario(null, "Nombre", Rol.ESTUDIANTE, HASH));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Rechaza un nombre completo vacío o en blanco")
    void rechazaNombreCompletoEnBlanco(String nombreCompleto) {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("usuario", nombreCompleto, Rol.ESTUDIANTE, HASH));
    }

    @Test
    void rechazaRolNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("usuario", "Nombre", null, HASH));
    }

    @Test
    void rechazaHashVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("usuario", "Nombre", Rol.ESTUDIANTE, " "));
    }

    @Test
    @DisplayName("Dos usuarios con el mismo nombre de usuario (sin distinguir mayúsculas) son iguales")
    void igualdadPorNombreDeUsuario() {
        Usuario a = new Usuario("Maria", "María Pérez", Rol.EMPLEADO, HASH);
        Usuario b = new Usuario("maria ", "Otra Persona", Rol.PROFESOR, HASH);
        Usuario c = new Usuario("pedro", "Pedro", Rol.EMPLEADO, HASH);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("toString no expone el hash de la contraseña")
    void toStringNoExponeElHash() {
        Usuario usuario = new Usuario("admin", "Administrador", Rol.ADMINISTRADOR, "hash-secreto");

        assertFalse(usuario.toString().contains("hash-secreto"));
    }
}
