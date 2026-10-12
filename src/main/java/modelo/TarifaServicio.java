package modelo;

import java.util.Objects;

public class TarifaServicio {
    private final TarifaRepositorio tarifaRepositorio;
    public TarifaServicio(TarifaRepositorio tarifaRepositorio) {
        this.tarifaRepositorio = Objects.requireNonNull(tarifaRepositorio);
    }

    public void guardarTarifaBase(double monto, double pctEstudiante, double pctEmpleado) {
        if (monto <= 0) {
            throw new IllegalArgumentException("La tarifa base debe ser mayor a cero.");
        }
        if (pctEstudiante < 10 || pctEstudiante > 20) {
            throw new IllegalArgumentException("El porcentaje para estudiantes debe estar entre 10% y 20%.");
        }
        if (pctEmpleado < 30 || pctEmpleado > 60) {
            throw new IllegalArgumentException("El porcentaje para empleados/profesores debe estar entre 30% y 60%.");
        }

        tarifaRepositorio.guardar(monto);
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
    
    public double getTarifaBase() {
        return tarifaRepositorio.obtenerTarifa();
    }
}