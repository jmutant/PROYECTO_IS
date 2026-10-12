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
            String montoStr = vista.getMontoTarifaBase();
            if (montoStr == null || montoStr.isBlank()) {
                vista.mostrarError("Debe ingresar un monto para la tarifa base.");
                return;
            }

            String ruta = vista.getRutaSeleccionada();

            double monto = Double.parseDouble(montoStr.trim());
            if (monto <= 0) {
                vista.mostrarError("La tarifa base debe ser mayor a cero.");
                return;
            }

            // Guardar mediante el servicio (lo persiste en el archivo .db)
            tarifaServicio.guardarTarifaBase(ruta, monto);

            // Calcular y reflejar los montos subsidiados por rol en la vista (HU-004)
            Map<String, Double> calculadas = Map.of(
                "Estudiante", tarifaServicio.calcularTarifaPorRol("estudiante", monto),
                "Empleado / Profesor", tarifaServicio.calcularTarifaPorRol("empleado", monto),
                "Público General", tarifaServicio.calcularTarifaPorRol("publico general", monto)
            );

            vista.mostrarTarifasCalculadas(calculadas);
            vista.mostrarExito("¡Tarifa base configurada y guardada con éxito!");

        } catch (NumberFormatException e) {
            vista.mostrarError("El monto ingresado no tiene un formato numérico válido.");
        } catch (Exception e) {
            vista.mostrarError("Error al guardar la tarifa: " + e.getMessage());
        }
    }

    private void cargarTarifaActual() {
        try {
            double tarifaActual = tarifaServicio.getTarifaBase();
            if (tarifaActual > 0) {
                Map<String, Double> calculadas = Map.of(
                    "Estudiante", tarifaServicio.calcularTarifaPorRol("estudiante", tarifaActual),
                    "Empleado / Profesor", tarifaServicio.calcularTarifaPorRol("empleado", tarifaActual),
                    "Público General", tarifaServicio.calcularTarifaPorRol("publico general", tarifaActual)
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
