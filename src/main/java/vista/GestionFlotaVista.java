package vista;

import java.util.List;

import modelo.EstadoUnidad;
import modelo.Unidad;


// Interfaz que define el contrato de comunicación entre la vista y el controlador.

public interface GestionFlotaVista {

    // Obtiene el texto ingresado en el campo Placa. */
    String getPlaca();

    // Obtiene el texto ingresado en el campo Modelo. */
    String getModelo();

    // Obtiene el valor numérico ingresado en el campo Capacidad. */
    int getCapacidad();

    // Obtiene el estado seleccionado del desplegable. */
    EstadoUnidad getEstado();

    // Asigna la acción a ejecutar al pulsar el botón Registrar. */
    void setAccionRegistrar(Runnable accion);

    // Asigna la acción a ejecutar al pulsar el botón Modificar Estado. */
    void setAccionModificarEstado(Runnable accion);

    // Asigna la acción a ejecutar al pulsar el botón Regresar. */
    void setAccionRegresar(Runnable accion);

    // Actualiza la tabla desplegada en la interfaz con la lista de unidades. */
    void mostrarUnidades(List<Unidad> unidades);

    // Muestra un mensaje flotante de éxito. */
    void mostrarExito(String mensaje);

    // Muestra un mensaje flotante de error. */
    void mostrarError(String mensaje);

    // Restablece los campos del formulario a su estado inicial. */
    void limpiarFormulario();

    // Muestra la ventana en pantalla. */
    void mostrar();

    // Cierra y destruye la ventana. */
    void cerrar();
}
