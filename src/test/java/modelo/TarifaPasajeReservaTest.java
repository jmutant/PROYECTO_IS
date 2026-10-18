package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;


// Pruebas de caja negra del cálculo del pasaje que se muestra y se cobra en la reserva, (TarifaServicio.calcularTarifaPorRol y consulta de la tarifa de una ruta).

class TarifaPasajeReservaTest {

    private TarifaServicio servicio;

    @BeforeEach
    void preparar() {
        TarifaRepositorio repositorio = new TarifaRepositorio() {
            private final Map<String, TarifaRepositorioMemoria> datos = new HashMap<>();

            @Override
            public void guardarOrActualizar(String ruta, double tarifaBase, double pctEstudiante, double pctEmpleado) {
                datos.put(ruta, new TarifaRepositorioMemoria(ruta, tarifaBase, pctEstudiante, pctEmpleado));
            }

            @Override
            public TarifaRepositorioMemoria obtenerTarifaPorRuta(String ruta) {
                return datos.get(ruta);
            }

            @Override
            public void eliminar(String ruta) {
                datos.remove(ruta);
            }

            @Override
            public Map<String, TarifaRepositorioMemoria> obtenerTodas() {
                return new HashMap<>(datos);
            }
        };
        servicio = new TarifaServicio(repositorio);
    }

    // ------------------------------------------------------------------ casos válidos y bordes del porcentaje

    @ParameterizedTest
    @DisplayName("TP-V01: pasaje por rol con los límites de los rangos permitidos (estudiante 10-20, empleado/profesor 30-60)")
    @CsvSource({
        "estudiante,       500.0, 10.0, 50.0",   // límite inferior estudiante
        "estudiante,       500.0, 15.0, 75.0",   // valor intermedio
        "estudiante,       500.0, 20.0, 100.0",  // límite superior estudiante
        "empleado,         500.0, 30.0, 150.0",  // límite inferior empleado
        "empleado,         500.0, 60.0, 300.0",  // límite superior empleado
        "profesor,         500.0, 30.0, 150.0",  // el profesor se calcula igual que el empleado
        "profesor,         500.0, 60.0, 300.0"
    })
    void pasajeSegunRolYPorcentaje(String rol, double base, double porcentaje, double esperado) {
        assertEquals(esperado, servicio.calcularTarifaPorRol(rol, base, porcentaje), 0.001);
    }

    @ParameterizedTest
    @DisplayName("TP-V02: las etiquetas visibles de los roles (Rol.getNombreVisible) calculan el pasaje correcto")
    @CsvSource({
        "ESTUDIANTE,      100.0, 15.0, 15.0",
        "EMPLEADO,        100.0, 40.0, 40.0",
        "PROFESOR,        100.0, 40.0, 40.0",
        "PUBLICO_GENERAL, 100.0, 15.0, 100.0"   // el público general paga el 100 %, sin importar el porcentaje
    })
    void pasajeConLaEtiquetaDelRol(Rol rol, double base, double porcentaje, double esperado) {
        assertEquals(esperado, servicio.calcularTarifaPorRol(rol.getNombreVisible(), base, porcentaje), 0.001);
    }

    @Test
    @DisplayName("TP-V03: el rol se reconoce sin importar mayúsculas o minúsculas")
    void rolSinDistinguirMayusculas() {
        assertEquals(75.0, servicio.calcularTarifaPorRol("ESTUDIANTE", 500.0, 15.0), 0.001);
        assertEquals(75.0, servicio.calcularTarifaPorRol("Estudiante", 500.0, 15.0), 0.001);
    }

    @Test
    @DisplayName("TP-V04: el público general siempre paga la tarifa base completa")
    void publicoGeneralPagaTodo() {
        assertEquals(500.0, servicio.calcularTarifaPorRol("publico general", 500.0, 15.0), 0.001);
        assertEquals(500.0, servicio.calcularTarifaPorRol("Público General", 500.0, 0.0), 0.001);
    }

    // ------------------------------------------------------------------ casos borde de los valores

    @ParameterizedTest
    @DisplayName("TP-B01: bordes numéricos (porcentaje 0 y 100, tarifa base 0 y mínima)")
    @CsvSource({
        "estudiante, 500.0, 0.0,   0.0",    // porcentaje 0: no paga nada
        "estudiante, 500.0, 100.0, 500.0",  // porcentaje 100: paga todo
        "estudiante, 0.0,   15.0,  0.0",    // tarifa base 0
        "estudiante, 0.01,  100.0, 0.01",   // tarifa mínima de un centavo al 100 %
        "empleado,   1000000.0, 60.0, 600000.0"  // tarifa muy alta
    })
    void bordesNumericos(String rol, double base, double porcentaje, double esperado) {
        assertEquals(esperado, servicio.calcularTarifaPorRol(rol, base, porcentaje), 0.001);
    }

    // ------------------------------------------------------------------ casos inválidos

    @Test
    @DisplayName("TP-I01: un rol nulo se trata como público general (paga el 100 %)")
    void rolNulo() {
        assertEquals(500.0, servicio.calcularTarifaPorRol(null, 500.0, 15.0), 0.001);
    }

    @ParameterizedTest
    @DisplayName("TP-I02: un rol desconocido se trata como público general (paga el 100 %)")
    @CsvSource({"visitante", "administrador", "xyz", "123"})
    void rolDesconocido(String rol) {
        assertEquals(500.0, servicio.calcularTarifaPorRol(rol, 500.0, 15.0), 0.001);
    }

    @Test
    @DisplayName("TP-I03: consultar la tarifa de una ruta que no existe o es nula devuelve null (sin excepción)")
    void rutaSinTarifa() {
        assertNull(servicio.obtenerTarifaPorRuta("Urbana"));
        assertNull(servicio.obtenerTarifaPorRuta("Ruta inexistente"));
        assertNull(servicio.obtenerTarifaPorRuta(null));
    }

    @Test
    @DisplayName("TP-I04: tras eliminar la tarifa de una ruta ya no se puede consultar")
    void tarifaEliminada() {
        servicio.guardarTarifaBase("Urbana", 100.0, 15.0, 40.0);
        assertEquals(100.0, servicio.obtenerTarifaPorRuta("Urbana").getTarifaBase(), 0.001);

        servicio.eliminarTarifa("Urbana");

        assertNull(servicio.obtenerTarifaPorRuta("Urbana"));
    }

    @Test
    @DisplayName("TP-I05: eliminar con una ruta vacía, nula o 'Seleccionar...' se rechaza")
    void eliminarRutaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarTarifa(null));
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarTarifa(""));
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarTarifa("   "));
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarTarifa("Seleccionar..."));
    }

    @Test
    @DisplayName("TP-I06: el constructor rechaza un repositorio nulo")
    void repositorioNulo() {
        assertThrows(NullPointerException.class, () -> new TarifaServicio(null));
    }

    // ------------------------------------------------------------------ defecto detectado

    @ParameterizedTest
    @Disabled("DEFECTO DETECTADO: guardarTarifaBase acepta NaN e Infinity como tarifa base "
            + "(NaN <= 0 es falso), y luego esa ruta no se puede reservar. "
            + "Deberían rechazarse con IllegalArgumentException. Quitar @Disabled para verlo fallar.")
    @DisplayName("TP-D01: la tarifa base no puede ser NaN ni infinita")
    @CsvSource({"NaN", "Infinity"})
    void tarifaBaseNoNumerica(double tarifaBase) {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.guardarTarifaBase("Urbana", tarifaBase, 15.0, 40.0));
    }
}
