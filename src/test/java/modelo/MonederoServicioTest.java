package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MonederoServicioTest {

    private MonederoServicio servicio;
    private Usuario usuario;

    @BeforeEach
    void preparar() throws Exception {
        Path directorio = Files.createTempDirectory("monedero-test-");
        AlmacenDatosLocal almacen = new AlmacenDatosLocal(directorio);
        servicio = new MonederoServicio(new MonederoRepositorioArchivo(almacen));
        usuario = new Usuario("Ana", "Perez", "12345678", Rol.ESTUDIANTE, "ana", "Clave123");
    }

    @Test
    void saldoInicialEsCeroYRecargaSuma1200() {
        assertEquals(new BigDecimal("0.00"), servicio.consultarSaldo(usuario));

        BigDecimal nuevo = servicio.recargar(usuario,
                new MonederoServicio.DatosRecarga(
                        "04123456789", "12345678", "Banco Nacional", "1234567890123"));

        assertEquals(new BigDecimal("1200.00"), nuevo);
        assertEquals(new BigDecimal("1200.00"), servicio.consultarSaldo(usuario));
    }

    @Test
    void rechazaTelefonoQueNoEmpiezaPorCero() {
        assertThrows(IllegalArgumentException.class, () -> servicio.recargar(usuario,
                new MonederoServicio.DatosRecarga(
                        "41123456789", "12345678", "Banco Nacional", "1234567890123")));
    }

    @Test
    void rechazaReferenciaDistintaDe13Digitos() {
        assertThrows(IllegalArgumentException.class, () -> servicio.recargar(usuario,
                new MonederoServicio.DatosRecarga(
                        "04123456789", "12345678", "Banco Nacional", "123456789012")));
    }

    @Test
    void rechazaCedulaFueraDelRangoExistente() {
        assertThrows(IllegalArgumentException.class, () -> servicio.recargar(usuario,
                new MonederoServicio.DatosRecarga(
                        "04123456789", "1234567", "Banco Nacional", "1234567890123")));
    }
}
