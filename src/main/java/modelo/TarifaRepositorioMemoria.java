package modelo;

public class TarifaRepositorioMemoria {
    private final String ruta;
    private final double tarifaBase;
    private final double pctEstudiante;
    private final double pctEmpleado;

    public TarifaRepositorioMemoria(String ruta, double tarifaBase, double pctEstudiante, double pctEmpleado) {
        this.ruta = ruta;
        this.tarifaBase = tarifaBase;
        this.pctEstudiante = pctEstudiante;
        this.pctEmpleado = pctEmpleado;
    }

    public String getRuta() {
        return ruta;
    }

    public double getTarifaBase() {
        return tarifaBase;
    }

    public double getPctEstudiante() {
        return pctEstudiante;
    }

    public double getPctEmpleado() {
        return pctEmpleado;
    }
}
