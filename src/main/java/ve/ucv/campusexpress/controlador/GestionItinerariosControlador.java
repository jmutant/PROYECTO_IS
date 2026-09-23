package ve.ucv.campusexpress.controlador;

import java.util.Objects;
import ve.ucv.campusexpress.modelo.Conductor;
import ve.ucv.campusexpress.modelo.Itinerario;
import ve.ucv.campusexpress.modelo.Unidad;
import ve.ucv.campusexpress.modelo.Usuario;
import ve.ucv.campusexpress.modelo.excepcion.ConflictoHorarioException;
import ve.ucv.campusexpress.modelo.servicio.ItinerarioServicio;
import ve.ucv.campusexpress.vista.GestionItinerariosVista;

/** Controlador de HU-003: valida asociaciones y conflictos antes de guardar el itinerario. */
public final class GestionItinerariosControlador {

    private final GestionItinerariosVista vista;
    private final ItinerarioServicio servicio;
    private final Usuario usuario;

    public GestionItinerariosControlador(GestionItinerariosVista vista, ItinerarioServicio servicio,
                                         Usuario usuario) {
        this.vista = Objects.requireNonNull(vista);
        this.servicio = Objects.requireNonNull(servicio);
        this.usuario = Objects.requireNonNull(usuario);
        vista.setAccionRegistrar(this::registrar);
        cargarDatos();
    }

    private void registrar() {
        Unidad unidad = vista.getUnidad();
        Conductor conductor = vista.getConductor();
        if (unidad == null || conductor == null) {
            vista.mostrarError("Debe seleccionar una unidad y un conductor.");
            return;
        }
        try {
            servicio.registrar(vista.getOrigen(), vista.getDestino(), vista.getDiaSemana(), vista.getHoraSalida(),
                    vista.getTipoRuta(), unidad.getPlaca(), conductor.getNumeroLicencia(), usuario);
            vista.mostrarExito("Itinerario registrado correctamente.");
            cargarDatos();
        } catch (ConflictoHorarioException | IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void cargarDatos() {
        vista.cargarUnidades(servicio.getUnidadRepositorio().listarTodos());
        vista.cargarConductores(servicio.getConductorRepositorio().listarTodos());
        vista.mostrarItinerarios(servicio.listar(usuario));
    }
}
