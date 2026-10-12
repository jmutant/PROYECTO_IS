package modelo;

import java.util.List;

/** Repositorio persistente de la tarifa base. */
public class TarifaRepositorioArchivo implements TarifaRepositorio {

    private static final String ARCHIVO = "tarifas.db";
    private final AlmacenDatosLocal almacen;
    private double tarifaBase = 0.0;

    public TarifaRepositorioArchivo(AlmacenDatosLocal almacen) {
        this.almacen = almacen;
        cargar();
    }

    @Override
    public synchronized void guardar(double tarifaBase) {
        this.tarifaBase = tarifaBase;
        persistir();
    }

    @Override
    public synchronized double obtenerTarifa() {
        return tarifaBase;
    }

    private void cargar() {
        List<String> lineas = almacen.leer(ARCHIVO);
        if (!lineas.isEmpty() && !lineas.get(0).isBlank()) {
            try {
                tarifaBase = Double.parseDouble(lineas.get(0).trim());
            } catch (NumberFormatException e) {
                tarifaBase = 0.0;
            }
        }
    }

    private void persistir() {
        List<String> lineas = List.of(String.valueOf(tarifaBase));
        almacen.reemplazar(ARCHIVO, lineas);
    }
}