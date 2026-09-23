package ve.ucv.campusexpress.modelo.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ve.ucv.campusexpress.modelo.Rol;
import ve.ucv.campusexpress.modelo.Usuario;
import ve.ucv.campusexpress.modelo.persistencia.AlmacenDatosLocal;
import ve.ucv.campusexpress.modelo.seguridad.ContrasenaHasher;

class UsuarioRepositorioArchivoTest {

    @TempDir
    Path directorio;

    @Test
    void guardaYRecuperaElUsuarioAlCrearOtroRepositorio() {
        AlmacenDatosLocal almacen = new AlmacenDatosLocal(directorio);
        Usuario original = new Usuario("Maria", "María Pérez", Rol.EMPLEADO,
                ContrasenaHasher.hashear("clave123".toCharArray()));

        UsuarioRepositorioArchivo primero = new UsuarioRepositorioArchivo(almacen);
        primero.guardar(original);

        UsuarioRepositorioArchivo segundo = new UsuarioRepositorioArchivo(almacen);
        Usuario recuperado = segundo.buscarPorNombreUsuario(" maria ").orElseThrow();

        assertEquals(original, recuperado);
        assertEquals(Rol.EMPLEADO, recuperado.getRol());
        assertTrue(ContrasenaHasher.verificar("clave123".toCharArray(), recuperado.getHashContrasena()));
    }
}
