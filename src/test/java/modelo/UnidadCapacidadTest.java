package modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnidadCapacidadTest {

    @ParameterizedTest
    @ValueSource(ints = {-50, -10, -1, 0, 4})
    @DisplayName("Debe lanzar excepcion si la capacidad es menor a 5 puestos (incluyendo negativos)")
    void testCapacidadInvalidaMenorADiez(int capacidadInvalida) {
        // La lambda () -> ... retrasa la instanciación para que assertThrows capture la excepción
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> new Unidad("ABC123", "Volvo Marcopolo", capacidadInvalida, EstadoUnidad.ACTIVO)
        );

        // Verificamos que el mensaje concuerde con la regla de negocio
        assertEquals(
            "La cantidad de puestos debe estar entre 5 y 65.",
            excepcion.getMessage()
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {66, 70, 100})
    @DisplayName("Debe lanzar excepcion si la capacidad es mayor a 65 puestos")
    void testCapacidadInvalidaMayorASesentaYCinco(int capacidadInvalida) {
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> new Unidad("ABC123", "Volvo Marcopolo", capacidadInvalida, EstadoUnidad.ACTIVO)
        );

        assertEquals(
            "La cantidad de puestos debe estar entre 5 y 65.",
            excepcion.getMessage()
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 30, 65})
    @DisplayName("Debe instanciar correctamente con capacidades validas en los limites (5, 30, 65)")
    void testCapacidadValidaLimites(int capacidadValida) {
        assertDoesNotThrow(() -> {
            Unidad unidad = new Unidad("ABC123", "Volvo Marcopolo", capacidadValida, EstadoUnidad.ACTIVO);
            assertEquals(capacidadValida, unidad.getCapacidad());
        });
    }
}