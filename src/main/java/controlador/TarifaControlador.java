package controlador;

import java.util.Objects;

import modelo.TarifaRepositorioMemoria;
import modelo.TarifaServicio;
import modelo.Usuario;
import vista.ConfiguracionTarifaVista;

public final class TarifaControlador {

    private final ConfiguracionTarifaVista vista;
    private final TarifaServicio tarifaServicio;

    public TarifaControlador(ConfiguracionTarifaVista vista, TarifaServicio tarifaServicio, Usuario usuario) {
        this.vista = Objects.requireNonNull(vista);
        this.tarifaServicio = Objects.requireNonNull(tarifaServicio);

        // Mapeo de eventos de la vista
        this.vista.setAccionGuardarTarifa(this::guardarTarifa);
        this.vista.setAccionEliminarTarifa(this::eliminarTarifaRuta);
        this.vista.setAccionRegresar(this::regresar);

        this.vista.setAccionSeleccionarRuta(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                cargarTarifaActual();
            }
        });

        // Cargar la tarifa actual al abrir la ventana
        cargarTarifaActual();
    }

    private void guardarTarifa() {
        try {
            String ruta = vista.getRutaSeleccionada();
            String montoStr = vista.getMontoTarifaBase();
            String pctEstStr = vista.getPctEstudiante();
            String pctEmpStr = vista.getPctEmpleado();

            if (ruta == null || ruta.equals("Seleccionar...") || ruta.isBlank()) {
                vista.mostrarError("Debe seleccionar una ruta extraurbana.");
                return;
            }

            if (montoStr.isBlank() || pctEstStr.isBlank() || pctEmpStr.isBlank()) {
                vista.mostrarError("Todos los campos de monto y porcentajes son obligatorios.");
                return;
            }

            double monto = Double.parseDouble(montoStr.trim());
            double pctEstudiante = Double.parseDouble(pctEstStr.trim());
            double pctEmpleado = Double.parseDouble(pctEmpStr.trim());

            if (monto <= 0) {
                vista.mostrarError("La tarifa base debe ser mayor a cero.");
                return;
            }

            if (pctEstudiante < 10.0 || pctEstudiante > 20.0) {
                vista.mostrarError("El porcentaje para estudiantes debe estar entre el 10% y el 20%.");
                return;
            }
            if (pctEmpleado < 30.0 || pctEmpleado > 60.0) {
                vista.mostrarError("El porcentaje para empleados/profesores debe estar entre el 30% y el 60%.");
                return;
            }

            // Guardar o actualizar la configuración completa en la BD
            tarifaServicio.guardarTarifaBase(ruta, monto, pctEstudiante, pctEmpleado);

            // Calcular montos sin etiquetas (pasando solo los valores por posición)
            double subEstudiante = tarifaServicio.calcularTarifaPorRol("Estudiante", monto, pctEstudiante);
            double subEmpleado = tarifaServicio.calcularTarifaPorRol("Empleado", monto, pctEmpleado);
            double subPublico = tarifaServicio.calcularTarifaPorRol("Público General", monto, 100.0);
 
            // Reflejar en la tabla
            vista.mostrarTarifasCalculadas(pctEstudiante, subEstudiante, pctEmpleado, subEmpleado, subPublico);
            vista.mostrarExito("¡Tarifa base configurada y guardada con éxito!");

        } catch (NumberFormatException e) {
            vista.mostrarError("El monto ingresado o alguno de los porcentajes no tiene un formato numérico válido.");
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        } catch (Exception e) {
            vista.mostrarError("Error al guardar la tarifa: " + e.getMessage());
        }
    }

    private void cargarTarifaActual() {
        try {
            String rutaActual = vista.getRutaSeleccionada();
            if (rutaActual != null && !rutaActual.equals("Seleccionar...")) {
                TarifaRepositorioMemoria info = tarifaServicio.obtenerTarifaPorRuta(rutaActual);
                if (info != null) {
                    vista.setMontoTarifaBase(String.valueOf(info.getTarifaBase()));
                    vista.setPctEstudiante(String.valueOf(info.getPctEstudiante()));
                    vista.setPctEmpleado(String.valueOf(info.getPctEmpleado()));

                    double subEstudiante = tarifaServicio.calcularTarifaPorRol("Estudiante", info.getTarifaBase(), info.getPctEstudiante());
                    double subEmpleado = tarifaServicio.calcularTarifaPorRol("Empleado", info.getTarifaBase(), info.getPctEmpleado());
                    double subPublico = tarifaServicio.calcularTarifaPorRol("Público General", info.getTarifaBase(), 100.0);

                    vista.mostrarTarifasCalculadas(info.getPctEstudiante(), subEstudiante, info.getPctEmpleado(), subEmpleado, subPublico);
                } else {
                    vista.limpiarCampos();
                }
            }
        } catch (Exception e) {
            // Sin registros previos para esta ruta
        }
    }

    private void eliminarTarifaRuta() {
        try {
            String ruta = vista.getRutaSeleccionada();

            if (ruta == null || ruta.equals("Seleccionar...") || ruta.isBlank()) {
                vista.mostrarError("Debe seleccionar una ruta válida para eliminar.");
                return;
            }

            tarifaServicio.eliminarTarifa(ruta);
            vista.limpiarCampos();
            vista.mostrarExito("Tarifa eliminada correctamente para la ruta: " + ruta);
        } catch (Exception e) {
            vista.mostrarError(e.getMessage());
        }
    }

    private void regresar() {
        vista.cerrar();
    }
}