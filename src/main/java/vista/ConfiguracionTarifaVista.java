package vista;

import java.util.Map;

public interface ConfiguracionTarifaVista {

    // Obtiene el monto ingresado para la Tarifa Base (T_base) en formato texto para su posterior parseo y validación.
    String getMontoTarifaBase();

    // Obtiene la ruta extraurbana seleccionada en la vista.
    String getRutaSeleccionada();

    // Registra el listener o acción para el botón de guardar/actualizar tarifa.
    void setAccionGuardarTarifa(Runnable accion);

    // Registra el listener o acción para el botón de regresar al menú principal.
    void setAccionRegresar(Runnable accion);

    // Muestra/actualiza los cálculos de las tarifas por rol en la vista según el T_base configurado.
    // @param tarifasCalculadas Mapa con el rol/tipo de usuario como clave y la tarifa subsidiada como valor.
    void mostrarTarifasCalculadas(Map<String, Double> tarifasCalculadas);

    // Despliega un mensaje emergente de confirmación o éxito.
    void mostrarExito(String mensaje);

    // Despliega un mensaje emergente de error de validación o negocio.
    void mostrarError(String mensaje);

    // Hace visible la ventana/pantalla.
    void mostrar();

    // Cierra o destruye la ventana/pantalla.
    void cerrar();
}