package ve.ucv.campusexpress.controlador;

import java.util.Objects;
import ve.ucv.campusexpress.modelo.Usuario;
import ve.ucv.campusexpress.modelo.excepcion.UnidadDuplicadaException;
import ve.ucv.campusexpress.modelo.servicio.FlotaServicio;
import ve.ucv.campusexpress.vista.GestionFlotaVista;

/** Controlador de HU-002: registra y lista unidades sin exponer detalles de persistencia a la vista. */
public final class GestionFlotaControlador {

    private final GestionFlotaVista vista;
    private final FlotaServicio servicio;
    private final Usuario usuario;

    public GestionFlotaControlador(GestionFlotaVista vista, FlotaServicio servicio, Usuario usuario) {
        this.vista = Objects.requireNonNull(vista);
        this.servicio = Objects.requireNonNull(servicio);
        this.usuario = Objects.requireNonNull(usuario);
        vista.setAccionRegistrar(this::registrar);
        actualizarTabla();
    }

    private void registrar() {
        try {
            servicio.registrar(vista.getPlaca(), vista.getModelo(), vista.getCapacidad(),
                    vista.getEstado(), usuario);
            vista.mostrarExito("Unidad registrada correctamente.");
            actualizarTabla();
        } catch (UnidadDuplicadaException | IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void actualizarTabla() {
        vista.mostrarUnidades(servicio.listar(usuario));
    }
}
