package modelo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Repositorio persistente de unidades. */
public class UnidadRepositorioArchivo implements UnidadRepositorio {

    private static final String ARCHIVO = "unidades.db";

    private final AlmacenDatosLocal almacen;
    private final Map<String, Unidad> unidades = new ConcurrentHashMap<>();

    public UnidadRepositorioArchivo(AlmacenDatosLocal almacen) {
        this.almacen = almacen;
        cargar();
    }

    @Override
    public synchronized void guardar(Unidad unidad) {
        unidades.put(Unidad.normalizarPlaca(unidad.getPlaca()), unidad);
        persistir();
    }

    @Override
    public synchronized void actualizar(String placaOriginal, Unidad unidadActualizada) {
        if (placaOriginal == null || unidadActualizada == null) {
            return;
        }

        String claveOriginal = Unidad.normalizarPlaca(placaOriginal);
        String claveNueva = Unidad.normalizarPlaca(unidadActualizada.getPlaca());

        // Si la placa cambió, eliminamos el registro previo bajo la clave anterior
        if (!claveOriginal.equals(claveNueva)) {
            unidades.remove(claveOriginal);
        }

        // Guardamos la unidad editada usando la clave normalizada
        unidades.put(claveNueva, unidadActualizada);

        // Guardamos los cambios inmediatamente en unidades.db
        persistir();
    }

    @Override
    public Optional<Unidad> buscarPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(unidades.get(Unidad.normalizarPlaca(placa)));
    }

    @Override
    public boolean existePorPlaca(String placa) {
        return buscarPorPlaca(placa).isPresent();
    }

    @Override
    public List<Unidad> listarTodos() {
        return unidades.values().stream()
                .sorted(Comparator.comparing(Unidad::getPlaca))
                .toList();
    }

    private void cargar() {
        for (String linea : almacen.leer(ARCHIVO)) {
            if (linea.isBlank()) {
                continue;
            }
            String[] partes = linea.split("\\|", -1);
            if (partes.length != 4) {
                throw new IllegalStateException("Registro de unidad inválido en " + ARCHIVO);
            }
            try {
                Unidad unidad = new Unidad(
                        TextoSeguro.decodificar(partes[0]),
                        TextoSeguro.decodificar(partes[1]),
                        Integer.parseInt(partes[2]),
                        EstadoUnidad.valueOf(partes[3]));
                
                // Normalizamos la clave para que la búsqueda/actualización posterior coincida siempre
                unidades.put(Unidad.normalizarPlaca(unidad.getPlaca()), unidad);
            } catch (RuntimeException e) {
                throw new IllegalStateException("No se pudo cargar una unidad desde " + ARCHIVO, e);
            }
        }
    }

    private void persistir() {
        List<String> lineas = unidades.values().stream()
                .sorted(Comparator.comparing(Unidad::getPlaca))
                .map(unidad -> String.join("|",
                        TextoSeguro.codificar(unidad.getPlaca()),
                        TextoSeguro.codificar(unidad.getModelo()),
                        Integer.toString(unidad.getCapacidad()),
                        unidad.getEstado().name()))
                .toList();
        almacen.reemplazar(ARCHIVO, lineas);
    }
}