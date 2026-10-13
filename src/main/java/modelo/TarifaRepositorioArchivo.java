package modelo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Repositorio persistente de la tarifa base. */
public class TarifaRepositorioArchivo implements TarifaRepositorio {

    private static final String ARCHIVO = "tarifas.db";
    private final AlmacenDatosLocal almacen;
    private final Map<String, TarifaRepositorioMemoria> tarifasPorRuta = new HashMap<>();

    public TarifaRepositorioArchivo(AlmacenDatosLocal almacen) {
        this.almacen = almacen;
        cargar();
    }

    @Override
    public synchronized void guardarOrActualizar(String ruta, double tarifaBase, double pctEstudiante, double pctEmpleado) {
        tarifasPorRuta.put(ruta, new TarifaRepositorioMemoria(ruta, tarifaBase, pctEstudiante, pctEmpleado));
        persistir();
    }

    @Override
    public synchronized TarifaRepositorioMemoria obtenerTarifaPorRuta(String ruta) {
        return tarifasPorRuta.get(ruta);
    }

    @Override
    public synchronized void eliminar(String ruta) {
        if (tarifasPorRuta.containsKey(ruta)) {
            tarifasPorRuta.remove(ruta);
            persistir();
        }
    }

    @Override
    public synchronized Map<String, TarifaRepositorioMemoria> obtenerTodas() {
        return new HashMap<>(tarifasPorRuta);
    }

    private void cargar() {
        tarifasPorRuta.clear();
        List<String> lineas = almacen.leer(ARCHIVO);
        for (String linea : lineas) {
            if (linea == null || linea.isBlank()) continue;
            String[] partes = linea.split("\\|");
            if (partes.length >= 4) {
                try {
                    String ruta = partes[0].trim();
                    double tarifaBase = Double.parseDouble(partes[1].trim());
                    double pctEstudiante = Double.parseDouble(partes[2].trim());
                    double pctEmpleado = Double.parseDouble(partes[3].trim());
                    tarifasPorRuta.put(ruta, new TarifaRepositorioMemoria(ruta, tarifaBase, pctEstudiante, pctEmpleado));
                } catch (NumberFormatException e) {
                    // Si hay algun error de formato en el archivo, se ignora
                }
        }
    }
}

    private void persistir() {
        List<String> lineas = tarifasPorRuta.values().stream()
                .map(t -> t.getRuta() + "|" + t.getTarifaBase() + "|" + t.getPctEstudiante() + "|" + t.getPctEmpleado())
                .collect(Collectors.toList());
        almacen.reemplazar(ARCHIVO, lineas);
    }
}