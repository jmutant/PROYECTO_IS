package modelo;

import java.util.Objects;

public class TarifaServicio {
    private final TarifaRepositorio tarifaRepositorio;
    public TarifaServicio(TarifaRepositorio tarifaRepositorio) {
        this.tarifaRepositorio = Objects.requireNonNull(tarifaRepositorio);
    }

    public void guardarTarifaBase(String ruta, double monto, double pctEstudiante, double pctEmpleado) {
        if (ruta == null || ruta.isBlank() || ruta.equals("Seleccionar...")) {
            throw new IllegalArgumentException("La ruta no puede estar vacía.");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("La tarifa base debe ser mayor a cero.");
        }
        if (pctEstudiante < 10 || pctEstudiante > 20) {
            throw new IllegalArgumentException("El porcentaje para estudiantes debe estar entre 10% y 20%.");
        }
        if (pctEmpleado < 30 || pctEmpleado > 60) {
            throw new IllegalArgumentException("El porcentaje para empleados/profesores debe estar entre 30% y 60%.");
        }

        tarifaRepositorio.guardarOrActualizar(ruta, monto, pctEstudiante, pctEmpleado);
    }

    public double calcularTarifaPorRol(String rol, double tarifaBase, double porcentajePersonalizado) {
        switch (rol != null ? rol.toLowerCase() : "") {
            case "estudiante":
                return tarifaBase * (porcentajePersonalizado / 100.0);
            case "empleado":
            case "profesor":
                return tarifaBase * (porcentajePersonalizado / 100.0);
            case "publico general":
            default:
                return tarifaBase; // 100%
        }
    }
    
    public TarifaRepositorioMemoria obtenerTarifaPorRuta(String ruta) {
        return tarifaRepositorio.obtenerTarifaPorRuta(ruta);
    }

    public void eliminarTarifa(String ruta) {
        if (ruta == null || ruta.isBlank() || ruta.equals("Seleccionar...")) {
            throw new IllegalArgumentException("Debe seleccionar una ruta válida para eliminar.");
        }
        tarifaRepositorio.eliminar(ruta);
    }
}