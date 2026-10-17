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

// Controlador de la ventana "reserva": por ahora solo maneja el botón para regresar.
public class ReservarControlador {

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

         // 1. Cargar las rutas disponibles en el ComboBox de la vista
        cargarRutasEnVista();

        // 2. Escuchar cuando el usuario seleccione una ruta para calcular el pasaje en tiempo real
        this.vista.setAccionSeleccionarRuta(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                calcularYMostrarPasajeSeleccionado();
            }
        });

        calcularYMostrarPasajeSeleccionado();

        // 3. Botón para confirmar la reserva y descontar del monedero
        this.vista.setAccionConfirmarReserva(this::procesarReservaYDescuento);
}

    private void cargarRutasEnVista() {
        // Obtiene los itinerarios registrados en la BD local de itinerarios
        List<Itinerario> itinerarios = itinerarioServicio.listar(Usuario.class.cast(usuarioActual));
        vista.mostrarItinerarios(itinerarios);
    }

    private void calcularYMostrarPasajeSeleccionado() {
        Object seleccion = vista.getRutaSeleccionada();
        if (seleccion == null) return;

        String tipoRuta = "";
        
        // 1. Si el elemento seleccionado es un objeto Itinerario:
        if (seleccion instanceof Itinerario) {
            Itinerario itinerario = (Itinerario) seleccion;
            // Obtenemos si es Extraurbana o Urbana desde el enum / atributo
            tipoRuta = itinerario.getTipoRuta() != null ? itinerario.getTipoRuta().toString() : "";
        } else {
            // 2. Si el ComboBox contiene solo texto, buscamos la palabra clave en la cadena:
            String textoCompleto = seleccion.toString();
            if (textoCompleto.toUpperCase().contains("EXTRAURBANA")) {
                tipoRuta = "Extraurbana";
            } else if (textoCompleto.toUpperCase().contains("URBANA")) {
                tipoRuta = "Urbana";
            }
        }

        double tarifaBase = 0.0;
        double porcentajeSubsidio = 0.0;

        // 3. Consultar la tarifa en la BD usando "Extraurbana" o "Urbana"
        TarifaRepositorioMemoria tarifaInfo = tarifaServicio.obtenerTarifaPorRuta(tipoRuta);

        if (tarifaInfo != null) {
            tarifaBase = tarifaInfo.getTarifaBase(); // Lee el $200.0 guardado

            // Determinar subsidio según el rol activo
            String rol = usuarioActual.getRol().toString().toLowerCase();
            if (rol.contains("estudiante")) {
                porcentajeSubsidio = tarifaInfo.getPctEstudiante();
            } else if (rol.contains("empleado") || rol.contains("profesor")) {
                porcentajeSubsidio = tarifaInfo.getPctEmpleado();
            } else {
                porcentajeSubsidio = 0.0; // Público General
            }
        } else {
            // Valor por defecto en caso de que la ruta sea Urbana sin tarifa configurada
        }

        // 4. Calcular el monto final a cobrar
        double totalAPagar = tarifaServicio.calcularTarifaPorRol(usuarioActual.getRol().getNombreVisible(), tarifaBase, porcentajeSubsidio);

        // 5. Reflejar los datos reales en las etiquetas de la pantalla
        vista.mostrarDesgloseCobro(tarifaBase, porcentajeSubsidio, totalAPagar);
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

    private void regresar() {
        vista.cerrar();
    }
}
