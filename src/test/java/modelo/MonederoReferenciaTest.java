package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MonederoReferenciaTest {

    private MonederoServicio crearServicio() {
        MonederoRepositorio repoDummy = new MonederoRepositorio() {
            @Override public java.math.BigDecimal obtenerSaldo(String u) { return java.math.BigDecimal.ZERO; }
            @Override public void guardarSaldo(String u, java.math.BigDecimal s) {}
        };
        return new MonederoServicio(repoDummy);
    }

    @Test
    void testReferenciaLongitudIncorrecta() {
        MonederoServicio servicio = crearServicio();
        
        MonederoServicio.DatosRecarga datos = new MonederoServicio.DatosRecarga(
            "04141234567", "12345678", "Banco", "123456", "100.00"
        );
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.validarDatosRecarga(datos);
        });
        assertEquals(MonederoServicio.MENSAJE_REFERENCIA_INVALIDA, ex.getMessage());
    }

    @Test
    void testReferenciaConCaracteresInvalidos() {
        MonederoServicio servicio = crearServicio();
        
        MonederoServicio.DatosRecarga datos = new MonederoServicio.DatosRecarga(
            "04141234567", "12345678", "Banco", "1234567890ABC", "100.00"
        );
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.validarDatosRecarga(datos);
        });
        assertEquals(MonederoServicio.MENSAJE_REFERENCIA_INVALIDA, ex.getMessage());
    }
    
        @Test
    void testReferenciaValida() {
        MonederoServicio servicio = crearServicio();
        
        MonederoServicio.DatosRecarga datos = new MonederoServicio.DatosRecarga(
            "04141234567", "12345678", "Banco", "1234567890123", "100.00"
        );
        
        assertDoesNotThrow(() -> servicio.validarDatosRecarga(datos));
    }
}