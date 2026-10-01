package modelo;

// Importaciones de JUnit 5 para estructurar y nombrar las pruebas
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// Importaciones estáticas de las aserciones para verificar los resultados
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnidadPlacaTest {


     // Prueba parametrizada: Evalua multiples placas que tienen menos de 6 caracteres.

    @ParameterizedTest
    @ValueSource(strings = {"A", "AB", "ABC12", "12345"}) // Lista de casos de prueba inválidos (1 a 5 caracteres)
    @DisplayName("Debe lanzar excepcion si la placa tiene menos de 6 caracteres") // Nombre descriptivo en el reporte
    void testPlacaConMenosDeSeisCaracteres(String placaInvalida) {
        
        // assertThrows verifica que al ejecutar la expresión Lambda () -> new Unidad(...) 
        // se lance exactamente la excepción IllegalArgumentException.
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> new Unidad(placaInvalida, "Volvo Marcopolo", 40, EstadoUnidad.ACTIVO)
        );

        // Verifica que el mensaje exacto almacenado dentro de la excepción coincida con la regla de negocio.
        assertEquals(
            "La placa debe ser alfanumérica y tener entre 6 y 7 caracteres.",
            excepcion.getMessage()
        );
    }


     // Prueba parametrizada: Evalúa múltiples placas que tienen mas de 6 caracteres.

    @ParameterizedTest
    @ValueSource(strings = {"ABC12345", "12345678", "AB1234567", "ABCDEFGHI"}) // Lista de casos inválidos (8 y 9 caracteres)
    @DisplayName("Debe lanzar excepcion si la placa tiene mas de 7 caracteres")
    void testPlacaConMasDeSeisCaracteres(String placaInvalida) {
        
        // Se espera de nuevo la excepción al intentar crear el objeto con más de 6 caracteres
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> new Unidad(placaInvalida, "Volvo Marcopolo", 40, EstadoUnidad.ACTIVO)
        );

        // Confirmamos que el mensaje de error de la excepción lanzada sea el esperado
        assertEquals(
            "La placa debe ser alfanumérica y tener entre 6 y 7 caracteres.",
            excepcion.getMessage()
        );
    }


     // Prueba unitaria simple: Valida el camino correcto.
     // Se prueba que una placa con 6 caracteres.

    @Test
    @DisplayName("Debe instanciar correctamente cuando la placa tiene 6 caracteres")
    void testPlacaValidaExactoSeisCaracteres() {
        
        // assertDoesNotThrow asegura que el bloque de código dentro NO lance ninguna excepción.
        assertDoesNotThrow(() -> {
            Unidad unidad = new Unidad("ABC123", "Volvo Marcopolo", 40, EstadoUnidad.ACTIVO);
            // Verificamos que el getter retorne el valor de la placa asignada correctamente
            assertEquals("ABC123", unidad.getPlaca());
        });
    }
}