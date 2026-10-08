package controlador;

import modelo.FlotaServicio;
import modelo.Usuario;
import vista.GestionFlotaVentana;

/**
 * Controlador que orquesta la interacción de la interfaz Swing con los servicios de dominio.
 */
public class GestionFlotaControlador {

    private final GestionFlotaVentana vista;
    private final FlotaServicio flotaServicio;
    private final Usuario usuarioActual;

    public GestionFlotaControlador(GestionFlotaVentana vista, FlotaServicio flotaServicio, Usuario usuarioActual) {
        this.vista = vista;
        this.flotaServicio = flotaServicio;
        this.usuarioActual = usuarioActual;

        // Vínculo entre eventos de la vista y acciones del controlador
        this.vista.setAccionRegistrar(this::registrarUnidad);
        this.vista.setAccionModificarEstado(this::modificarUnidad);
        this.vista.setAccionRegresar(this.vista::cerrar);

        cargarUnidades();
    }

    
     // Procesa la modificación completa (placa, modelo, capacidad, estado) de la unidad seleccionada.
  
    private void modificarUnidad() {
        try {
            int filaSeleccionada = vista.getTablaUnidades().getSelectedRow();
            if (filaSeleccionada == -1) {
                vista.mostrarError("Seleccione una unidad de la tabla para modificar.");
                return;
            }

            // Obtiene la placa original de la fila seleccionada antes de la edición
            String placaOriginal = vista.getModeloTabla().getValueAt(filaSeleccionada, 0).toString();

            String nuevaPlaca = vista.getPlaca();
            String nuevoModelo = vista.getModelo();
            int nuevaCapacidad = vista.getCapacidad();
            var nuevoEstado = vista.getEstado();

            // Llama a la capa de servicio pasando la placa original y los nuevos valores
            flotaServicio.actualizarUnidad(placaOriginal, nuevaPlaca, nuevoModelo, nuevaCapacidad, nuevoEstado, usuarioActual);

            vista.mostrarExito("Unidad actualizada correctamente.");
            cargarUnidades();
            vista.limpiarFormulario();
        } catch (Exception e) {
            vista.mostrarError("Error al actualizar la unidad: " + e.getMessage());
        }
    }

    private void registrarUnidad() {
        try {
            flotaServicio.registrar(
                vista.getPlaca(),
                vista.getModelo(),
                vista.getCapacidad(),
                vista.getEstado(),
                usuarioActual
            );
            vista.mostrarExito("Unidad registrada con éxito.");
            cargarUnidades();
            vista.limpiarFormulario();
        } catch (Exception e) {
            vista.mostrarError("Error al registrar unidad: " + e.getMessage());
        }
    }

    private void cargarUnidades() { 
        vista.mostrarUnidades(flotaServicio.listar(usuarioActual)); 
    }
}