package modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class TarifaServicioTest {

    private TarifaServicio tarifaServicio;
    private TarifaRepositorio tarifaRepositorioMock;

    @BeforeEach
    void setUp() {
        // Implementación en memoria ligera para aislar la prueba unitaria del sistema de archivos
        tarifaRepositorioMock = new TarifaRepositorio() {
            private double tarifa = 0.0;

            @Override
            public void guardar(double monto) {
                this.tarifa = monto;
            }

            @Override
            public double obtenerTarifa() {
                return this.tarifa;
            }
        };

        tarifaServicio = new TarifaServicio(tarifaRepositorioMock);
    }

    @Test
    @DisplayName("Debe guardar exitosamente la tarifa base y porcentajes válidos dentro del rango")
    void guardarTarifaBaseExitosa() {
        assertDoesNotThrow(() -> {
            tarifaServicio.guardarTarifaBase(100.0, 15.0, 45.0);
        });
        assertEquals(100.0, tarifaServicio.getTarifaBase(), 0.001);
    }

    @Test
    @DisplayName("Debe rechazar el registro si la tarifa base es menor o igual a cero")
    void tarifaBaseInvalida() {
        // Caso cero
        assertThrows(IllegalArgumentException.class, () -> {
            tarifaServicio.guardarTarifaBase(0.0, 15.0, 45.0);
        });

        // Caso negativo
        assertThrows(IllegalArgumentException.class, () -> {
            tarifaServicio.guardarTarifaBase(-25.5, 15.0, 45.0);
        });
    }

    @ParameterizedTest
    @DisplayName("Verificación de casos de borde para el porcentaje de Estudiantes (Rango válido: 10% - 20%)")
    @CsvSource({
        "9.9, false",   // Borde inferior fallido (menor a 10%)
        "10.0, true",   // Borde inferior exacto (válido)
        "15.0, true",   // Valor intermedio válido
        "20.0, true",   // Borde superior exacto (válido)
        "20.1, false"   // Borde superior fallido (mayor a 20%)
    })
    void validarBordesPorcentajeEstudiante(double porcentaje, boolean esValido) {
        if (esValido) {
            assertDoesNotThrow(() -> {
                tarifaServicio.guardarTarifaBase(100.0, porcentaje, 45.0);
            });
        } else {
            assertThrows(IllegalArgumentException.class, () -> {
                tarifaServicio.guardarTarifaBase(100.0, porcentaje, 45.0);
            });
        }
    }

    @ParameterizedTest
    @DisplayName("Verificación de casos de borde para Empleados/Profesores (Rango válido: 30% - 60%)")
    @CsvSource({
        "29.9, false",  // Borde inferior fallido (menor a 30%)
        "30.0, true",   // Borde inferior exacto (válido)
        "45.0, true",   // Valor intermedio válido
        "60.0, true",   // Borde superior exacto (válido)
        "60.1, false"   // Borde superior fallido (mayor a 60%)
    })
    void validarBordesPorcentajeEmpleado(double porcentaje, boolean esValido) {
        if (esValido) {
            assertDoesNotThrow(() -> {
                tarifaServicio.guardarTarifaBase(100.0, 15.0, porcentaje);
            });
        } else {
            assertThrows(IllegalArgumentException.class, () -> {
                tarifaServicio.guardarTarifaBase(100.0, 15.0, porcentaje);
            });
        }
    }

    @Test
    @DisplayName("Debe calcular de forma exacta el monto subsidiado según el rol y porcentaje configurado")
    double calcularTarifaPorRolCorrectamente() {
        double tarifaBase = 200.0;

        // Estudiante con 15% -> 200 * 0.15 = 30.0
        double resEstudiante = tarifaServicio.calcularTarifaPorRol("estudiante", tarifaBase, 15.0);
        assertEquals(30.0, resEstudiante, 0.001);

        // Empleado con 50% -> 200 * 0.50 = 100.0
        double resEmpleado = tarifaServicio.calcularTarifaPorRol("empleado", tarifaBase, 50.0);
        assertEquals(100.0, resEmpleado, 0.001);

        // Público general (100%) -> 200 * 1.0 = 200.0
        double resPublico = tarifaServicio.calcularTarifaPorRol("publico general", tarifaBase, 100.0);
        assertEquals(200.0, resPublico, 0.001);
        
        return tarifaBase;
    }
}