package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MonederoTelefonoTest {

    private MonederoServicio crearServicio() {
        // Pasamos una implementación vacía (dummy) para cumplir con el constructor 
        // sin necesidad de instalar librerías externas de Mockito.
        MonederoRepositorio repoDummy = new MonederoRepositorio() {
            @Override public java.math.BigDecimal obtenerSaldo(String u) { return java.math.BigDecimal.ZERO; }
            @Override public void guardarSaldo(String u, java.math.BigDecimal s) {}
        };
        return new MonederoServicio(repoDummy);
    }

    @Test
    void testTelefonoInvalidoSinCeroInicial() {
        MonederoServicio servicio = crearServicio();
        
        MonederoServicio.DatosRecarga datos = new MonederoServicio.DatosRecarga(
            "41412345670", "12345678", "Banco", "1234567890123", "100.00"
        );
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.validarDatosRecarga(datos);
        });
        assertEquals(MonederoServicio.MENSAJE_TELEFONO_INVALIDO, ex.getMessage());
    }

    @Test
    void testTelefonoLongitudIncorrecta() {
        MonederoServicio servicio = crearServicio();
        
        MonederoServicio.DatosRecarga datos = new MonederoServicio.DatosRecarga(
            "0414123", "12345678", "Banco", "1234567890123", "100.00"
        );
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.validarDatosRecarga(datos);
        });
        assertEquals(MonederoServicio.MENSAJE_TELEFONO_INVALIDO, ex.getMessage());
    }
    
        @Test
    void testTelefonoValido() {
        MonederoServicio servicio = crearServicio();
        
        MonederoServicio.DatosRecarga datos = new MonederoServicio.DatosRecarga(
            "04141234567", "12345678", "Banco", "1234567890123", "100.00"
        );
        
        assertDoesNotThrow(() -> servicio.validarDatosRecarga(datos));
    }
}