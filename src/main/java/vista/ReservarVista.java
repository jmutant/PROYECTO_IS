package vista;

import java.util.List;

/** Lo que el controlador necesita de la ventana "reserva" (por ahora solo regresar). */
public interface ReservarVista {

    // Métodos para la manipulación de elementos en la interfaz
    void mostrarItinerarios(List<?> itinerarios);

    Object getRutaSeleccionada();
    
    void mostrarDesgloseCobro(double tarifaBase, double porcentajeSubsidio, double montoFinal);

    double getMontoCalculado();
    
    void mostrarExito(String mensaje);

    void mostrarError(String mensaje);

    void mostrarTicketDigital(Object ticket);

    // Registro de eventos/acciones para el Controlador
    void setAccionSeleccionarRuta(java.awt.event.ItemListener listener);

    void setAccionConfirmarReserva(Runnable accion);
    
    void setAccionRegresar(Runnable accion);

    void mostrar();

    void cerrar();
}
