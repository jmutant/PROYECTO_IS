package controlador;

import java.util.Map;
import java.util.Objects;
import modelo.TarifaServicio;
import modelo.Usuario;
import vista.ConfiguracionTarifaVista;

public final class TarifaControlador {

    private final ConfiguracionTarifaVista vista;
    private final TarifaServicio tarifaServicio;
    private final Usuario usuario;

    public TarifaControlador(ConfiguracionTarifaVista vista, TarifaServicio tarifaServicio, Usuario usuario) {
        this.vista = Objects.requireNonNull(vista);
        this.tarifaServicio = Objects.requireNonNull(tarifaServicio);
        this.usuario = Objects.requireNonNull(usuario);

        // Mapeo de eventos de la vista
        this.vista.setAccionGuardarTarifa(this::guardarTarifa);
        this.vista.setAccionRegresar(this::regresar);

        // Cargar la tarifa actual al abrir la ventana (si ya fue guardada antes)
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

            tarifaServicio.guardarTarifaBase(monto, pctEstudiante, pctEmpleado);

            // Cálculo dinámico de cobro solidario
            Map<String, Double> calculadas = Map.of(
                "Estudiante (" + pctEstudiante + "%)", tarifaServicio.calcularTarifaPorRol("estudiante", monto, pctEstudiante),
                "Empleado / Profesor (" + pctEmpleado + "%)", tarifaServicio.calcularTarifaPorRol("empleado", monto, pctEmpleado),
                "Público General (100%)", tarifaServicio.calcularTarifaPorRol("publico general", monto, 100.0)
            );

            vista.mostrarTarifasCalculadas(calculadas);
            vista.mostrarExito("¡Tarifa base configurada y guardada con éxito!");

        } catch (NumberFormatException e) {
            vista.mostrarError("El monto ingresado no tiene un formato numérico válido.");
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        } catch (Exception e) {
            vista.mostrarError("Error al guardar la tarifa: " + e.getMessage());
        }
    }

    private void cargarTarifaActual() {
        try {
            double tarifaActual = tarifaServicio.getTarifaBase();
            if (tarifaActual > 0) {
                Map<String, Double> calculadas = Map.of(
                    "Estudiante (15%)", tarifaServicio.calcularTarifaPorRol("estudiante", tarifaActual, 15.0),
                    "Empleado / Profesor (50%)", tarifaServicio.calcularTarifaPorRol("empleado", tarifaActual, 50.0),
                    "Público General (100%)", tarifaServicio.calcularTarifaPorRol("publico general", tarifaActual, 100.0)
                );
                vista.mostrarTarifasCalculadas(calculadas);
            }
        } catch (Exception e) {
            // Manejo por defecto si aún no hay registros previos
        }
    }

    private void regresar() {
        vista.cerrar();
    }
}
