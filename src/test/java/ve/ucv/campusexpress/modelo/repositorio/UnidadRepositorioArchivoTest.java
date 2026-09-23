package ve.ucv.campusexpress.modelo.repositorio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ve.ucv.campusexpress.modelo.EstadoUnidad;
import ve.ucv.campusexpress.modelo.Unidad;
import ve.ucv.campusexpress.modelo.persistencia.AlmacenDatosLocal;

class UnidadRepositorioArchivoTest {

    @TempDir
    Path directorio;

    @Test
    void unaUnidadSigueDisponibleDespuesDeReabrirElRepositorio() {
        AlmacenDatosLocal almacen = new AlmacenDatosLocal(directorio);
        UnidadRepositorioArchivo primero = new UnidadRepositorioArchivo(almacen);
        primero.guardar(new Unidad("abc123", "Bus", 40, EstadoUnidad.ACTIVO));

        UnidadRepositorioArchivo segundo = new UnidadRepositorioArchivo(almacen);

        assertTrue(segundo.existePorPlaca("ABC123"));
        assertEquals(1, segundo.listarTodos().size());
        assertEquals(40, segundo.buscarPorPlaca("abc123").orElseThrow().getCapacidad());
    }
}
