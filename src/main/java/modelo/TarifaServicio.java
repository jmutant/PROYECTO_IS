package modelo;

import java.util.Objects;

public class TarifaServicio {

    private final TarifaRepositorio tarifaRepositorio;
    public TarifaServicio(TarifaRepositorio tarifaRepositorio) {
        this.tarifaRepositorio = Objects.requireNonNull(tarifaRepositorio);
    }
    
    // Podrías almacenar una tarifa base o usar un repositorio si se guarda en archivos (ej. tarifas.db)
    private double tarifaBase = 0.0;

    public void guardarTarifaBase(String ruta, double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("La tarifa ingresada debe ser un monto mayor a cero.");
        }
        // Lógica para guardar la tarifa base (en memoria o archivo)
        this.tarifaBase = monto;
    }

    public double calcularTarifaPorRol(String rol, double tarifaBase) {
        switch (rol != null ? rol.toLowerCase() : "") {
            case "estudiante":
                return tarifaBase * 0.20; // Ejemplo: 20% de la base
            case "empleado":
            case "profesor":
                return tarifaBase * 0.50; // Ejemplo: 50% de la base
            case "publico general":
            default:
                return tarifaBase;       // 100% de la base
        }
    }
    
    public double getTarifaBase() {
        return tarifaBase;
    }
}