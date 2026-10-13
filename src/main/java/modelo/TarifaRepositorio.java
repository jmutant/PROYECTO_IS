package modelo;

import java.util.Map;

public interface TarifaRepositorio {
    void guardarOrActualizar(String ruta,double tarifaBase, double pctEstudiante, double pctEmpleado);
    TarifaRepositorioMemoria obtenerTarifaPorRuta(String ruta);
    void eliminar(String ruta);
    Map<String, TarifaRepositorioMemoria> obtenerTodas();
}