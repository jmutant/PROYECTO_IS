package vista;

import java.awt.event.ItemListener;

public interface ConfiguracionTarifaVista {

    // Obtiene el monto ingresado para la Tarifa Base (T_base) en formato texto para su posterior parseo y validación.
    String getMontoTarifaBase();

    String getPctEstudiante();

    String getPctEmpleado();

    // Obtiene la ruta extraurbana seleccionada en la vista.
    String getRutaSeleccionada();

    // Registra el listener o acción para el botón de guardar/actualizar tarifa.
    void setAccionGuardarTarifa(Runnable accion);

    void setAccionEliminarTarifa(Runnable accion);

    void setAccionSeleccionarRuta(ItemListener listener);

    void limpiarCampos();

    // Registra el listener o acción para el botón de regresar al menú principal.
    void setAccionRegresar(Runnable accion);

    // Muestra/actualiza los cálculos de las tarifas por rol en la vista según el T_base configurado.

    public void mostrarTarifasCalculadas(double pctEstudiante, double pctEmpleado, double monto, double montoEstudiante, double montoEmpleado);

    void setMontoTarifaBase(String monto);

    void setPctEstudiante(String porcentaje);
    
    void setPctEmpleado(String porcentaje);
    // Despliega un mensaje emergente de confirmación o éxito.
    void mostrarExito(String mensaje);

    // Despliega un mensaje emergente de error de validación o negocio.
    void mostrarError(String mensaje);

    // Hace visible la ventana/pantalla.
    void mostrar();

    // Cierra o destruye la ventana/pantalla.
    void cerrar();
}