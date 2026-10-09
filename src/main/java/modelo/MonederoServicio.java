package modelo;

import java.math.BigDecimal;
import java.util.Objects;

/** Reglas de negocio del monedero virtual y de las recargas. */
public class MonederoServicio {

    public static final BigDecimal SALDO_MAXIMO = new BigDecimal("9999999.99");
    public static final BigDecimal MONTO_RECARGA = new BigDecimal("1200.00");
    public static final String MENSAJE_TELEFONO_INVALIDO =
            "El teléfono debe tener exactamente 11 dígitos y comenzar por 0.";
    public static final String MENSAJE_REFERENCIA_INVALIDA =
            "La referencia debe tener exactamente 13 dígitos.";
    public static final String MENSAJE_BANCO_INVALIDO =
            "El banco es obligatorio.";
    public static final String MENSAJE_SALDO_MAXIMO =
            "La recarga supera el saldo máximo permitido de Bs 9.999.999,99.";

    private final MonederoRepositorio repositorio;

    public MonederoServicio(MonederoRepositorio repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio es obligatorio.");
    }

    public BigDecimal consultarSaldo(Usuario usuario) {
        Objects.requireNonNull(usuario, "El usuario es obligatorio.");
        return repositorio.obtenerSaldo(usuario.getUsername());
    }

    public synchronized BigDecimal recargar(Usuario usuario, DatosRecarga datos) {
        Objects.requireNonNull(usuario, "El usuario es obligatorio.");
        validarDatosRecarga(datos);

        // Por el momento la verificación de pago es simulada: las reglas de formato
        // representan la entrada válida del comprobante bancario.
        if (!verificarPago(datos)) {
            throw new IllegalArgumentException("No fue posible verificar el pago.");
        }

        BigDecimal saldoActual = repositorio.obtenerSaldo(usuario.getUsername());
        BigDecimal nuevoSaldo = saldoActual.add(MONTO_RECARGA);

        if (nuevoSaldo.compareTo(SALDO_MAXIMO) > 0) {
            throw new IllegalArgumentException(MENSAJE_SALDO_MAXIMO);
        }

        repositorio.guardarSaldo(usuario.getUsername(), nuevoSaldo);
        return nuevoSaldo;
    }

    public void validarDatosRecarga(DatosRecarga datos) {
        if (datos == null) {
            throw new IllegalArgumentException("Los datos de recarga son obligatorios.");
        }

        String telefono = limpiar(datos.telefono());
        if (!telefono.matches("0[0-9]{10}")) {
            throw new IllegalArgumentException(MENSAJE_TELEFONO_INVALIDO);
        }

        // Se mantiene exactamente el mismo rango de cédula que el registro de usuarios.
        AutenticacionServicio.validarCedula(limpiar(datos.cedula()));

        if (datos.banco() == null || datos.banco().trim().isEmpty()) {
            throw new IllegalArgumentException(MENSAJE_BANCO_INVALIDO);
        }

        String referencia = limpiar(datos.referencia());
        if (!referencia.matches("[0-9]{13}")) {
            throw new IllegalArgumentException(MENSAJE_REFERENCIA_INVALIDA);
        }
    }

    private boolean verificarPago(DatosRecarga datos) {
        // Simulación de la verificación bancaria solicitada para el alcance actual.
        return datos != null;
    }

    private static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }

    public record DatosRecarga(String telefono, String cedula, String banco, String referencia) {
    }
}
