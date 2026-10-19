package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Pruebas de la eliminación de unidades (botón Eliminar de Gestión de Flota). */
class FlotaServicioEliminarTest {

    private UnidadRepositorio repositorio;
    private FlotaServicio servicio;
    private Usuario admin;

    @BeforeEach
    void preparar() {
        repositorio = new UnidadRepositorioMemoria();
        servicio = new FlotaServicio(repositorio, new AutorizacionServicio());
        admin = new Usuario("admin", "Administrador Sistema", Rol.ADMINISTRADOR, "1234");
        servicio.registrar("ABC123", "Mercedes", 40, EstadoUnidad.ACTIVO, admin);
        servicio.registrar("PQR987", "Yutong", 30, EstadoUnidad.ACTIVO, admin);
    }

    @Test
    @DisplayName("FE-V01: el administrador elimina una unidad y ya no aparece en el listado")
    void eliminaUnidadExistente() {
        servicio.eliminarUnidad("ABC123", admin);

        assertFalse(repositorio.existePorPlaca("ABC123"));
        assertEquals(1, servicio.listar(admin).size());
        assertEquals("PQR987", servicio.listar(admin).get(0).getPlaca());
    }

    @Test
    @DisplayName("FE-V02: la placa se reconoce sin importar mayúsculas ni espacios")
    void placaSinDistinguirMayusculas() {
        servicio.eliminarUnidad("  abc123 ", admin);

        assertFalse(repositorio.existePorPlaca("ABC123"));
    }

    @Test
    @DisplayName("FE-V03: tras eliminar una unidad se puede registrar otra con la misma placa")
    void placaQuedaLibre() {
        servicio.eliminarUnidad("ABC123", admin);

        servicio.registrar("ABC123", "Volvo", 35, EstadoUnidad.ACTIVO, admin);

        assertTrue(repositorio.existePorPlaca("ABC123"));
    }

    @Test
    @DisplayName("FE-I01: eliminar una placa que no existe se rechaza y no afecta a las demás")
    void placaInexistente() {
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarUnidad("ZZZ999", admin));

        assertEquals(2, servicio.listar(admin).size());
    }

    @Test
    @DisplayName("FE-I02: eliminar con placa nula se rechaza")
    void placaNula() {
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarUnidad(null, admin));
    }

    @Test
    @DisplayName("FE-I03: eliminar dos veces la misma unidad falla la segunda vez")
    void eliminarDosVeces() {
        servicio.eliminarUnidad("ABC123", admin);

        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarUnidad("ABC123", admin));
    }

    @Test
    @DisplayName("FE-I04: un pasajero no puede eliminar unidades y la unidad se conserva")
    void soloAdministrador() {
        Usuario pasajero = new Usuario("Ana", "Perez", "12345678", Rol.ESTUDIANTE, "ana", "Clave123");

        assertThrows(AccesoDenegadoException.class, () -> servicio.eliminarUnidad("ABC123", pasajero));
        assertThrows(AccesoDenegadoException.class, () -> servicio.eliminarUnidad("ABC123", null));

        assertTrue(repositorio.existePorPlaca("ABC123"));
    }
}
