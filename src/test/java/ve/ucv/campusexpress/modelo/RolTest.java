package ve.ucv.campusexpress.modelo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class RolTest {

    @Test
    @DisplayName("Solo ADMINISTRADOR es administrador")
    void soloAdministradorEsAdministrador() {
        assertTrue(Rol.ADMINISTRADOR.esAdministrador());
        assertFalse(Rol.ADMINISTRADOR.esPasajero());
    }

    @ParameterizedTest
    @EnumSource(value = Rol.class, names = "ADMINISTRADOR", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Todos los demás roles son pasajeros")
    void losDemasRolesSonPasajeros(Rol rol) {
        assertTrue(rol.esPasajero());
        assertFalse(rol.esAdministrador());
    }
}
