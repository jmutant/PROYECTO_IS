package controlador;

import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import modelo.Itinerario;
import modelo.ItinerarioServicio;
import modelo.MonederoServicio;
import modelo.Rol;
import modelo.TarifaRepositorioMemoria;
import modelo.TarifaServicio;
import modelo.Usuario;
import vista.ReservarVista;

// Controlador de la ventana "reserva": muestra el desglose de la tarifa y cobra el pasaje del monedero.
public class ReservarControlador {

    // Resultado del cálculo de una tarifa para el rol del usuario
    private record Cobro(double tarifaBase, double porcentajeSubsidio, double total) { }

    private final ReservarVista vista;
    private final ItinerarioServicio itinerarioServicio;
    private final TarifaServicio tarifaServicio;
    private final MonederoServicio monederoServicio;
    private final Usuario usuarioActual;

    public ReservarControlador(ReservarVista vista,
                                ItinerarioServicio itinerarioServicio,
                                TarifaServicio tarifaServicio,
                                MonederoServicio monederoServicio,
                                Usuario usuarioActual) {
        this.vista = Objects.requireNonNull(vista);
        this.itinerarioServicio = Objects.requireNonNull(itinerarioServicio);
        this.tarifaServicio = Objects.requireNonNull(tarifaServicio);
        this.monederoServicio = Objects.requireNonNull(monederoServicio);
        this.usuarioActual = Objects.requireNonNull(usuarioActual);
        this.vista.setAccionRegresar(this::regresar);

        // 1. Cargar las rutas disponibles en la lista desplegable de la vista
        cargarRutasEnVista();

        // 2. Al seleccionar una ruta se recalcula el pasaje en tiempo real
        this.vista.setAccionSeleccionarRuta(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                mostrarDesgloseSeleccionado();
            }
        });
        mostrarDesgloseSeleccionado();

        // 3. Botón Reservar: verifica el saldo y cobra del monedero
        this.vista.setAccionConfirmarReserva(this::procesarReservaYDescuento);
    }

    private void cargarRutasEnVista() {
        List<Itinerario> itinerarios = itinerarioServicio.listar(usuarioActual);
        vista.mostrarItinerarios(itinerarios);
    }

    /** Calcula la tarifa de la ruta según el rol; null si el tipo de ruta no tiene tarifa configurada. */
    private Cobro calcularCobro(Itinerario itinerario) {
        // La tarifa se guarda con la etiqueta del tipo de ruta ("Urbana" / "Extraurbana")
        String tipoRuta = itinerario.getTipoRuta() != null ? itinerario.getTipoRuta().getEtiqueta() : "";
        TarifaRepositorioMemoria tarifa = tarifaServicio.obtenerTarifaPorRuta(tipoRuta);
        if (tarifa == null) {
            return null;
        }

        Rol rol = usuarioActual.getRol();
        double porcentaje = switch (rol) {
            case ESTUDIANTE -> tarifa.getPctEstudiante();
            case EMPLEADO, PROFESOR -> tarifa.getPctEmpleado();
            default -> 0.0; // Público General: paga el 100 %
        };
        double total = tarifaServicio.calcularTarifaPorRol(rol.getNombreVisible(), tarifa.getTarifaBase(), porcentaje);
        return new Cobro(tarifa.getTarifaBase(), porcentaje, total);
    }

    private void mostrarDesgloseSeleccionado() {
        Object seleccion = vista.getRutaSeleccionada();
        Cobro cobro = seleccion instanceof Itinerario it ? calcularCobro(it) : null;
        if (cobro == null) {
            vista.mostrarDesgloseCobro(0.0, 0.0, 0.0);
            return;
        }
        vista.mostrarDesgloseCobro(cobro.tarifaBase(), cobro.porcentajeSubsidio(), cobro.total());
    }

    private void procesarReservaYDescuento() {
        try {
            Object seleccion = vista.getRutaSeleccionada();
            if (!(seleccion instanceof Itinerario itinerario)) {
                vista.mostrarError("Por favor, seleccione una ruta válida.");
                return;
            }

            // Se recalcula con la tarifa vigente (el administrador pudo cambiarla mientras la ventana estaba abierta)
            Cobro cobro = calcularCobro(itinerario);
            if (cobro == null || cobro.total() <= 0) {
                vista.mostrarError("No hay una tarifa configurada para este tipo de ruta.\n"
                        + "Comuníquese con el administrador.");
                return;
            }
            vista.mostrarDesgloseCobro(cobro.tarifaBase(), cobro.porcentajeSubsidio(), cobro.total());

            // Verifica que el saldo alcance y, si es así, lo descuenta. Si no alcanza lanza IllegalArgumentException
            BigDecimal monto = BigDecimal.valueOf(cobro.total()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal nuevoSaldo = monederoServicio.debitar(usuarioActual, monto);

            vista.mostrarExito(String.format(java.util.Locale.US,
                    "¡Reserva exitosa!%nSe descontaron Bs %,.2f de tu monedero.%nSaldo restante: Bs %,.2f",
                    monto, nuevoSaldo));
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        } catch (RuntimeException e) {
            vista.mostrarError("Error al procesar la reserva: " + e.getMessage());
        }
    }

    private void regresar() {
        vista.cerrar();
    }
}
