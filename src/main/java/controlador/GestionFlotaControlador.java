package controlador;

import vista.GestionFlotaVista;

public class GestionFlotaControlador {

    private final GestionFlotaVista vista;

    public GestionFlotaControlador(GestionFlotaVista vista) {
        this.vista = vista;
        this.vista.setAccionRegresar(this::regresar);
    }

    private void regresar() {
        vista.cerrar();
    }
}
/* package controlador;

import java.util.Objects;
import modelo.Usuario;
import modelo.UnidadDuplicadaException;
import modelo.FlotaServicio;
import vista.GestionFlotaVista;

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
} */
