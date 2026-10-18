package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;

//Reglas de negocio del monedero virtual y de las recargas
public class MonederoServicio {

    public static final BigDecimal SALDO_MAXIMO = new BigDecimal("9999999.99");
    public static final BigDecimal MONTO_RECARGA = new BigDecimal("1200.00");
    public static final String MENSAJE_TELEFONO_INVALIDO =
            "El teléfono debe tener un prefijo válido (0412, 0414, 0416, 0422, 0424 o 0426) seguido de 7 dígitos.";
    public static final String MENSAJE_REFERENCIA_INVALIDA =
            "La referencia debe tener exactamente 13 dígitos.";
    public static final String MENSAJE_BANCO_INVALIDO =
            "El banco es obligatorio.";
    public static final String MENSAJE_MONTO_INVALIDO =
            "El monto debe ser un número mayor que cero (solo dígitos, sin signos negativos).";
    public static final String MENSAJE_SALDO_MAXIMO =
            "La recarga supera el saldo máximo permitido de Bs 9.999.999,99.";

    public static final String MENSAJE_MONTO_COBRO_INVALIDO =
            "El monto a cobrar debe ser mayor que cero.";

    private final MonederoRepositorio repositorio;

    public MonederoServicio(MonederoRepositorio repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio es obligatorio.");
    }

    public BigDecimal consultarSaldo(Usuario usuario) {
        Objects.requireNonNull(usuario, "El usuario es obligatorio.");
        return repositorio.obtenerSaldo(usuario.getUsername());
    }


    public static BigDecimal parsearMonto(String texto) {
        String limpio = texto == null ? "" : texto.trim();
        if (!limpio.matches("[0-9]+([.,][0-9]{1,2})?")) {
            throw new IllegalArgumentException(MENSAJE_MONTO_INVALIDO);
        }
        BigDecimal monto = new BigDecimal(limpio.replace(',', '.')).setScale(2, RoundingMode.UNNECESSARY);
        if (monto.signum() <= 0) {
            throw new IllegalArgumentException(MENSAJE_MONTO_INVALIDO);
        }
        return monto;
    }

    public synchronized BigDecimal recargar(Usuario usuario, DatosRecarga datos) {
        Objects.requireNonNull(usuario, "El usuario es obligatorio.");
        validarDatosRecarga(datos);

        // Por el momento la verificación de pago es simulada: las reglas de formato representan la entrada válida del comprobante bancario.
        if (!verificarPago(datos)) {
            throw new IllegalArgumentException("No fue posible verificar el pago.");
        }

        BigDecimal saldoActual = repositorio.obtenerSaldo(usuario.getUsername());
        BigDecimal nuevoSaldo = saldoActual.add(parsearMonto(datos.monto()));

        if (nuevoSaldo.compareTo(SALDO_MAXIMO) > 0) {
            throw new IllegalArgumentException(MENSAJE_SALDO_MAXIMO);
        }

        repositorio.guardarSaldo(usuario.getUsername(), nuevoSaldo);
        return nuevoSaldo;
    }

    public synchronized BigDecimal debitar(Usuario usuario, BigDecimal monto) {
        Objects.requireNonNull(usuario, "El usuario es obligatorio.");
        if (monto == null || monto.signum() <= 0) {
            throw new IllegalArgumentException(MENSAJE_MONTO_COBRO_INVALIDO);
        }
        BigDecimal montoCobro = monto.setScale(2, RoundingMode.HALF_UP);
        BigDecimal saldoActual = repositorio.obtenerSaldo(usuario.getUsername());

        if (saldoActual.compareTo(montoCobro) < 0) {
            throw new IllegalArgumentException(String.format(Locale.US,
                    "Saldo insuficiente en el monedero virtual.%nSaldo disponible: Bs %,.2f%nMonto a pagar: Bs %,.2f%n"
                            + "Recargue su monedero para continuar.",
                    saldoActual, montoCobro));
        }

        BigDecimal nuevoSaldo = saldoActual.subtract(montoCobro);
        repositorio.guardarSaldo(usuario.getUsername(), nuevoSaldo);
        return nuevoSaldo;
    }

    public void validarDatosRecarga(DatosRecarga datos) {
        if (datos == null) {
            throw new IllegalArgumentException("Los datos de recarga son obligatorios.");
        }

        parsearMonto(datos.monto());

        String telefono = limpiar(datos.telefono());
        if (!telefono.matches("0(412|414|416|422|424|426)[0-9]{7}")) {
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

    //datos del pagomovillllllll
    public record DatosRecarga(String telefono, String cedula, String banco, String referencia, String monto) {

        //Constructor anterior: usa el monto fijo por defecto. 
        public DatosRecarga(String telefono, String cedula, String banco, String referencia) {
            this(telefono, cedula, banco, referencia, MONTO_RECARGA.toPlainString());
        }
    }
}
