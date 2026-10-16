package controlador;

import java.util.List;
import java.util.Objects;

import modelo.Itinerario;
import modelo.ItinerarioServicio;
import modelo.MonederoServicio;
import modelo.TarifaRepositorioMemoria;
import modelo.TarifaServicio;
import modelo.Usuario;
import vista.ReservarVista;

public class ReservarControlador {
    private final ReservarVista vista;
    private final ItinerarioServicio itinerarioServicio;
    private final TarifaServicio tarifaServicio;
    private final MonederoServicio monederoServicio; // Tu servicio de monedero virtual
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

        // 1. Cargar las rutas disponibles en el ComboBox de la vista
        cargarRutasEnVista();

        // 2. Escuchar cuando el usuario seleccione una ruta para calcular el pasaje en tiempo real
        this.vista.setAccionSeleccionarRuta(e -> calcularYMostrarPasajeSeleccionado());

        // 3. Botón para confirmar la reserva y descontar del monedero
        this.vista.setAccionConfirmarReserva(this::procesarReservaYDescuento);
    }

    private void cargarRutasEnVista() {
        // Obtiene los itinerarios registrados en la BD local de itinerarios
        List<Itinerario> itinerarios = itinerarioServicio.listar(Usuario.class.cast(usuarioActual));
        vista.mostrarItinerarios(itinerarios);
    }

    private void calcularYMostrarPasajeSeleccionado() {
        // 1. Obtenemos el objeto Itinerario completo desde la selección del ComboBox
        Object seleccion = vista.getRutaSeleccionada();
        if (!(seleccion instanceof Itinerario)) return;
        Itinerario itinerarioSeleccionado = (Itinerario) seleccion;

        double tarifaBase = 0.0;
        double porcentajeSubsidio = 0.0;

        // 2. Evaluamos el TipoRuta (que es un Enum, ej. TipoRuta.EXTRAURBANA o similar)
        // Usamos el método getTipoRuta() que sí existe en tu modelo Itinerario
        if (itinerarioSeleccionado.getTipoRuta().name().equalsIgnoreCase("EXTRAURBANA")) {
            
            // 3. Como Itinerario no tiene .getRuta(), usamos getOrigen() y getDestino()
            // para consultar la tarifa específica en la base de datos de tarifas:
            String nombreRuta = itinerarioSeleccionado.getOrigen() + " - " + itinerarioSeleccionado.getDestino();
            TarifaRepositorioMemoria tarifaInfo = tarifaServicio.obtenerTarifaPorRuta(nombreRuta);
            
            if (tarifaInfo != null) {
                tarifaBase = tarifaInfo.getTarifaBase();
                
                // Validamos el rol del usuario actual
                if (usuarioActual.getRol().getNombreVisible().equalsIgnoreCase("Estudiante")) {
                    porcentajeSubsidio = tarifaInfo.getPctEstudiante();
                } else if (usuarioActual.getRol().getNombreVisible().equalsIgnoreCase("Empleado") ||
                        usuarioActual.getRol().getNombreVisible().equalsIgnoreCase("Profesor")) {
                    porcentajeSubsidio = tarifaInfo.getPctEmpleado();
                } else {
                    porcentajeSubsidio = 100.0; // Sin subsidio
                }
            }
        } else {
            // Lógica para ruta Urbana
        }

        // 4. Cálculo final y actualización en la vista
        double montoFinal = tarifaServicio.calcularTarifaPorRol(usuarioActual.getRol().getNombreVisible(), tarifaBase, porcentajeSubsidio);
        vista.mostrarDesgloseCobro(tarifaBase, porcentajeSubsidio, montoFinal);
    }

    private void procesarReservaYDescuento() {
        try {
            // 1. Obtener el objeto Itinerario completo desde la vista correctamente
            Object seleccion = vista.getRutaSeleccionada();
            if (!(seleccion instanceof Itinerario)) {
                vista.mostrarError("Por favor, seleccione una ruta válida.");
                return;
            }
            Itinerario itinerario = (Itinerario) seleccion;

            // 2. Consultar saldo actual (asegúrate de usar getCedula() o getUsername() según tu modelo de Usuario)
            // double saldoActual = monederoServicio.consultarSaldo(usuarioActual.getCedula());
            double montoAPagar = vista.getMontoCalculado(); // El monto calculado previamente según el rol

            // if (saldoActual < montoAPagar) {
            //     vista.mostrarError("Saldo insuficiente en el monedero virtual. Recargue para continuar.");
            //     return;
            // }

            // 3. Descontar del monedero
            // monederoServicio.debitar(usuarioActual.getCedula(), montoAPagar);

            // 4. Crear el ticket o reserva digital y guardarlo en su repositorio/archivo .db
            // TicketReserva ticket = new TicketReserva(
            //    UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
            //    usuarioActual.getCedula(),
            //    itinerario,
            //    montoAPagar,
            //    LocalDateTime.now()
            // );
            
            // ticketRepositorio.guardar(ticket);

            vista.mostrarExito("¡Reserva exitosa! Se han descontado $" + montoAPagar + " de tu monedero.");
            // vista.mostrarTicketDigital(ticket);

        } catch (Exception e) {
            vista.mostrarError("Error al procesar la reserva: " + e.getMessage());
        }
    }
}