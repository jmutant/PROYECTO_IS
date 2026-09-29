package controlador;

import java.util.Objects;
import modelo.Conductor;
import modelo.ConflictoHorarioException;
import modelo.ItinerarioServicio;
import modelo.Unidad;
import modelo.Usuario;
import vista.GestionItinerariosVista;

public final class GestionItinerariosControlador {

    private final GestionItinerariosVista vista;
    private final ItinerarioServicio servicio;
    private final Usuario usuario;

    public GestionItinerariosControlador(GestionItinerariosVista vista, ItinerarioServicio servicio, Usuario usuario) {
        this.vista = Objects.requireNonNull(vista);
        this.servicio = Objects.requireNonNull(servicio);
        this.usuario = Objects.requireNonNull(usuario);

        // Mapeo de eventos de la vista
        this.vista.setAccionRegistrar(this::registrar);
        this.vista.setAccionRegresar(this::regresar);

        // Carga inicial de datos en combos y tablas
        cargarDatos();
    }

    private void registrar() {
        Unidad unidad = vista.getUnidad();
        Conductor conductor = vista.getConductor();

        if (unidad == null || conductor == null) {
            vista.mostrarError("Debe seleccionar una unidad y un conductor.");
            return;
        }

        if (vista.getHoraSalida() == null) {
            vista.mostrarError("La hora de salida no es válida (Ejemplo correcto: 08:00).");
            return;
        }

        try {
            // Nota: Si tu clase Conductor usa getLicencia(), cámbialo aquí
            String licencia = conductor.getNumeroLicencia(); 

            servicio.registrar(
                    vista.getOrigen(), 
                    vista.getDestino(), 
                    vista.getDiaSemana(), 
                    vista.getHoraSalida(),
                    vista.getTipoRuta(), 
                    unidad.getPlaca(), 
                    licencia, 
                    usuario
            );

            vista.mostrarExito("Itinerario registrado correctamente.");
            cargarDatos();

        } catch (ConflictoHorarioException e) {
            vista.mostrarError("Conflicto de horario: La unidad o conductor ya tienen un itinerario asignado.");
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        } catch (Exception e) {
            vista.mostrarError("Error al registrar itinerario: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        try {
            vista.cargarUnidades(servicio.getUnidadRepositorio().listarTodos());
            vista.cargarConductores(servicio.getConductorRepositorio().listarTodos());
            vista.mostrarItinerarios(servicio.listar(usuario));
        } catch (Exception e) {
            // Manejo silencioso o log si al iniciar aún no hay datos
        }
    }

    private void regresar() {
        vista.cerrar();
    }
}