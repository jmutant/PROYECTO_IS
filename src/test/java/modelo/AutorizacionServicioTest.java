package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AutorizacionServicioTest {

    private final AutorizacionServicio autorizacion = new AutorizacionServicio();

    private static Usuario usuarioConRol(Rol rol) {
        return new Usuario("usuario", "Usuario de Prueba", rol, "hash");
    }

    @Test
    @DisplayName("Un usuario con rol Administrador es administrador y no es pasajero")
    void administrador() {
        Usuario admin = usuarioConRol(Rol.ADMINISTRADOR);

        assertTrue(autorizacion.esAdministrador(admin));
        assertFalse(autorizacion.esPasajero(admin));
    }

    @ParameterizedTest
    @EnumSource(value = Rol.class, names = "ADMINISTRADOR", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Cualquier otro rol es pasajero y no es administrador")
    void pasajeros(Rol rol) {
        Usuario pasajero = usuarioConRol(rol);

        assertTrue(autorizacion.esPasajero(pasajero));
        assertFalse(autorizacion.esAdministrador(pasajero));
    }

    @Test
    @DisplayName("Sin usuario autenticado no hay ningún permiso")
    void usuarioNulo() {
        assertFalse(autorizacion.esAdministrador(null));
        assertFalse(autorizacion.esPasajero(null));
    }

    @Test
    void exigirAdministradorPermiteAlAdministrador() {
        autorizacion.exigirAdministrador(usuarioConRol(Rol.ADMINISTRADOR));
    }

    @ParameterizedTest
    @EnumSource(value = Rol.class, names = "ADMINISTRADOR", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("exigirAdministrador deniega el acceso a los pasajeros")
    void exigirAdministradorDeniegaAPasajeros(Rol rol) {
        assertThrows(AccesoDenegadoException.class, () -> autorizacion.exigirAdministrador(usuarioConRol(rol)));
    }

    @Test
    void exigirAdministradorDeniegaSinUsuario() {
        AccesoDenegadoException error = assertThrows(AccesoDenegadoException.class,
                () -> autorizacion.exigirAdministrador(null));

        assertEquals("Acceso denegado: esta funcionalidad es exclusiva del rol Administrador", error.getMessage());
    }
}
