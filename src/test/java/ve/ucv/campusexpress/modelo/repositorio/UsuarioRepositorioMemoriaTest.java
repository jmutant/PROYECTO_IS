package ve.ucv.campusexpress.modelo.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ve.ucv.campusexpress.modelo.Rol;
import ve.ucv.campusexpress.modelo.Usuario;

class UsuarioRepositorioMemoriaTest {

    private UsuarioRepositorio repositorio;
    private Usuario maria;

    @BeforeEach
    void preparar() {
        repositorio = new UsuarioRepositorioMemoria();
        maria = new Usuario("maria", "María Pérez", Rol.EMPLEADO, "hash");
    }

    @Test
    @DisplayName("Un usuario guardado se puede buscar por su nombre de usuario")
    void guardaYBuscaUnUsuario() {
        repositorio.guardar(maria);

        assertSame(maria, repositorio.buscarPorNombreUsuario("maria").orElseThrow());
    }

    @Test
    @DisplayName("La búsqueda no distingue mayúsculas ni espacios en los extremos")
    void busquedaSinDistinguirMayusculas() {
        repositorio.guardar(maria);

        assertTrue(repositorio.buscarPorNombreUsuario("  MARIA ").isPresent());
    }

    @Test
    void buscarUnUsuarioInexistenteDevuelveVacio() {
        assertEquals(Optional.empty(), repositorio.buscarPorNombreUsuario("nadie"));
    }

    @Test
    void buscarConNuloDevuelveVacio() {
        assertEquals(Optional.empty(), repositorio.buscarPorNombreUsuario(null));
    }

    @Test
    void indicaSiUnUsuarioExiste() {
        assertFalse(repositorio.existePorNombreUsuario("maria"));

        repositorio.guardar(maria);

        assertTrue(repositorio.existePorNombreUsuario("maria"));
        assertTrue(repositorio.existePorNombreUsuario("MARIA"));
    }
}
