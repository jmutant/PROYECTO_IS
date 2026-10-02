package controlador;

import modelo.FlotaServicio;
import modelo.Usuario;
import vista.GestionFlotaVista;


 // Controlador que orquesta la interacción de la interfaz Swing con los servicios de dominio.

public class GestionFlotaControlador {

    private final GestionFlotaVista vista;
    private final FlotaServicio flotaServicio;
    private final Usuario usuarioActual;


     // Registra listeners y carga los datos iniciales al instanciar el controlador.

    public GestionFlotaControlador(GestionFlotaVista vista, FlotaServicio flotaServicio, Usuario usuarioActual) {
        this.vista = vista;
        this.flotaServicio = flotaServicio;
        this.usuarioActual = usuarioActual;

        // Vínculo entre eventos de interfaz y métodos del controlador
        this.vista.setAccionRegistrar(this::registrarUnidad);
        this.vista.setAccionModificarEstado(this::modificarEstado);
        this.vista.setAccionRegresar(this.vista::cerrar);

        cargarUnidades();
    }


     // Procesa la solicitud de modificación de estado enviada desde la vista

    private void modificarEstado() {
        try {
            String placa = vista.getPlaca();
            var nuevoEstado = vista.getEstado();

            if (placa.isEmpty()) {
                vista.mostrarError("Seleccione una unidad de la tabla.");
                return;
            }

            flotaServicio.actualizarEstadoUnidad(placa, nuevoEstado, usuarioActual);
            
            vista.mostrarExito("Estado actualizado correctamente.");
            cargarUnidades();
            vista.limpiarFormulario(); // Limpia los inputs tras completar la acción
        } catch (Exception e) {
            vista.mostrarError("Error al actualizar el estado: " + e.getMessage());
        }
    }


     // Procesa y valida los campos recibidos para la creación de una nueva unidad.

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
            vista.limpiarFormulario(); // Limpia los inputs tras completar la acción
        } catch (Exception e) {
            vista.mostrarError("Error al registrar unidad: " + e.getMessage());
        }
    }


     // Consulta las unidades vigentes a la capa de servicio y re-dibuja la tabla en la vista.

    private void cargarUnidades() { 
        vista.mostrarUnidades(flotaServicio.listar(usuarioActual)); 
    }
}