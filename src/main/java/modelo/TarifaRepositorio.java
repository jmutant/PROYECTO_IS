package modelo;

public interface TarifaRepositorio {
    void guardar(double tarifaBase);
    double obtenerTarifa();
}