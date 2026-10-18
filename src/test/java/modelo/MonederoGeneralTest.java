package modelo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

class MonederoGeneralTest {

    @Test
    void testFlujoCompletoRecargaYConsulta() {
        // 1. Cream un repositorio simulado en memoria usando un Map para guardar los saldos
        MonederoRepositorio repositorioEnMemoria = new MonederoRepositorio() {
            private final Map<String, BigDecimal> saldos = new HashMap<>();

            @Override
            public BigDecimal obtenerSaldo(String username) {
                // Si no existe saldo previo, retorna 0.00
                return saldos.getOrDefault(username, new BigDecimal("0.00"));
            }

            @Override
            public void guardarSaldo(String username, BigDecimal nuevoSaldo) {
                saldos.put(username, nuevoSaldo);
            }
        };

        // instancia el servicio con nuestro repositorio de prueba
        MonederoServicio servicio = new MonederoServicio(repositorioEnMemoria);
        Usuario usuarioPrueba = new Usuario("testuser", "Usuario Prueba", Rol.ESTUDIANTE, "123456");

        // verifica el saldo inicial
        BigDecimal saldoInicial = servicio.consultarSaldo(usuarioPrueba);
        assertEquals(new BigDecimal("0.00"), saldoInicial);

        // Creamos datos de recarga válidos (cumpliendo las reglas de 11 dígitos, 13 de referencia, etc.)
        MonederoServicio.DatosRecarga datosValidos = new MonederoServicio.DatosRecarga(
            "04141234567", "12345678", "Banco", "1234567890123", "500.00"
        );

        //  ejecuta la recarga
        BigDecimal saldoDespuesRecarga = servicio.recargar(usuarioPrueba, datosValidos);

        // valida que el saldo haya aumentado correctamente a 500.00
        assertEquals(new BigDecimal("500.00"), saldoDespuesRecarga);
        
        // comprueba que la consulta del saldo también refleje el nuevo monto
        assertEquals(new BigDecimal("500.00"), servicio.consultarSaldo(usuarioPrueba));
    }
}