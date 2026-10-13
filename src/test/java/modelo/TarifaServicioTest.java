package modelo;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TarifaServicioTest {

    private TarifaServicio tarifaServicio;
    private TarifaRepositorio tarifaRepositorioMock;

    @BeforeEach
    void setUp() {
        // Mock en memoria del repositorio adaptado al manejo por rutas
        tarifaRepositorioMock = new TarifaRepositorio() {
            private final Map<String, TarifaRepositorioMemoria> almacenamiento = new HashMap<>();

            @Override
            public void guardarOrActualizar(String ruta, double tarifaBase, double pctEstudiante, double pctEmpleado) {
                almacenamiento.put(ruta, new TarifaRepositorioMemoria(ruta, tarifaBase, pctEstudiante, pctEmpleado));
            }

            @Override
            public TarifaRepositorioMemoria obtenerTarifaPorRuta(String ruta) {
                return almacenamiento.get(ruta);
            }

            @Override
            public void eliminar(String ruta) {
                almacenamiento.remove(ruta);
            }

            @Override
            public Map<String, TarifaRepositorioMemoria> obtenerTodas() {
                return new HashMap<>(almacenamiento);
            }
        };

        tarifaServicio = new TarifaServicio(tarifaRepositorioMock);
    }

    @Test
    @DisplayName("HU-004: Debe guardar exitosamente la tarifa base y porcentajes válidos para una ruta")
    void guardarConfiguracionTarifaExitosa() {
        assertDoesNotThrow(() -> {
            tarifaServicio.guardarTarifaBase("Ruta Extraurbana", 500.0, 15.0, 45.0);
        });

        TarifaRepositorioMemoria info = tarifaServicio.obtenerTarifaPorRuta("Ruta Extraurbana");
        assertNotNull(info);
        assertEquals(500.0, info.getTarifaBase(), 0.001);
        assertEquals(15.0, info.getPctEstudiante(), 0.001);
        assertEquals(45.0, info.getPctEmpleado(), 0.001);
    }

    @Test
    @DisplayName("HU-004: Debe rechazar el registro si la tarifa base es menor o igual a cero")
    void tarifaBaseInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            tarifaServicio.guardarTarifaBase("Ruta Caracas - Maracay", 0.0, 15.0, 45.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            tarifaServicio.guardarTarifaBase("Ruta Caracas - Maracay", -100.0, 15.0, 45.0);
        });
    }

    @Test
    @DisplayName("HU-004: Debe rechazar el registro si la ruta es nula, vacía o inválida")
    void rutaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            tarifaServicio.guardarTarifaBase("", 500.0, 15.0, 45.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            tarifaServicio.guardarTarifaBase("Seleccionar...", 500.0, 15.0, 45.0);
        });
    }

    @ParameterizedTest
    @DisplayName("HU-004: Verificación de casos de borde para el porcentaje de Estudiantes (Rango válido: 10% - 20%)")
    @CsvSource({
        "9.9, false",   // Borde inferior fallido (menor al 10%)
        "10.0, true",   // Borde inferior exacto (válido)
        "15.0, true",   // Valor intermedio válido
        "20.0, true",   // Borde superior exacto (válido)
        "20.1, false"   // Borde superior fallido (mayor al 20%)
    })
    void validarBordesPorcentajeEstudiante(double porcentaje, boolean esValido) {
        if (esValido) {
            assertDoesNotThrow(() -> {
                tarifaServicio.guardarTarifaBase("Ruta Test", 500.0, porcentaje, 45.0);
            });
        } else {
            assertThrows(IllegalArgumentException.class, () -> {
                tarifaServicio.guardarTarifaBase("Ruta Test", 500.0, porcentaje, 45.0);
            });
        }
    }

    @ParameterizedTest
    @DisplayName("HU-004: Verificación de casos de borde para Empleados/Profesores (Rango válido: 30% - 60%)[cite: 10, 11]")
    @CsvSource({
        "29.9, false",  // Borde inferior fallido (menor al 30%)[cite: 10, 11]
        "30.0, true",   // Borde inferior exacto (válido)[cite: 10, 11]
        "45.0, true",   // Valor intermedio válido
        "60.0, true",   // Borde superior exacto (válido)[cite: 10, 11]
        "60.1, false"   // Borde superior fallido (mayor al 60%)[cite: 10, 11]
    })
    void validarBordesPorcentajeEmpleado(double porcentaje, boolean esValido) {
        if (esValido) {
            assertDoesNotThrow(() -> {
                tarifaServicio.guardarTarifaBase("Ruta Test", 500.0, 15.0, porcentaje);
            });
        } else {
            assertThrows(IllegalArgumentException.class, () -> {
                tarifaServicio.guardarTarifaBase("Ruta Test", 500.0, 15.0, porcentaje);
            });
        }
    }

    @Test
    @DisplayName("HU-004: Debe calcular de forma exacta el monto subsidiado según el rol y porcentaje configurado[cite: 10, 11]")
    void calcularTarifaPorRolCorrectamente() {
        double tarifaBase = 500.0;

        // Estudiante con 15% -> 500 * (15 / 100) = 75.0[cite: 10, 11]
        double resEstudiante = tarifaServicio.calcularTarifaPorRol("estudiante", tarifaBase, 15.0);
        assertEquals(75.0, resEstudiante, 0.001);

        // Empleado con 40% -> 500 * (40 / 100) = 200.0[cite: 10, 11]
        double resEmpleado = tarifaServicio.calcularTarifaPorRol("empleado", tarifaBase, 40.0);
        assertEquals(200.0, resEmpleado, 0.001);

        // Público general (100%) -> 500 * (100 / 100) = 500.0[cite: 10, 11]
        double resPublico = tarifaServicio.calcularTarifaPorRol("publico general", tarifaBase, 100.0);
        assertEquals(500.0, resPublico, 0.001);
    }
}