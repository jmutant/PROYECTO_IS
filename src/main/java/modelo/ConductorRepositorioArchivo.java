package modelo;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Repositorio persistente de conductores. */
public class ConductorRepositorioArchivo implements ConductorRepositorio {

    private static final String ARCHIVO = "conductores.db";

    private final AlmacenDatosLocal almacen;
    private final Map<String, Conductor> conductores = new ConcurrentHashMap<>();

    public ConductorRepositorioArchivo(AlmacenDatosLocal almacen) {
        this.almacen = almacen;
        cargar();
    }

    @Override
    public synchronized void guardar(Conductor conductor) {
        conductores.put(conductor.getNumeroLicencia(), conductor);
        persistir();
    }

    @Override
    public Optional<Conductor> buscarPorLicencia(String numeroLicencia) {
        if (numeroLicencia == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(conductores.get(numeroLicencia.trim().toUpperCase(Locale.ROOT)));
    }

    @Override
    public List<Conductor> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return List.of();
        }
        String criterio = nombre.trim().toLowerCase(Locale.ROOT);
        return conductores.values().stream()
                .filter(c -> c.getNombreCompleto().toLowerCase(Locale.ROOT).contains(criterio))
                .sorted(Comparator.comparing(Conductor::getNombreCompleto))
                .toList();
    }

    @Override
    public boolean existePorLicencia(String numeroLicencia) {
        return buscarPorLicencia(numeroLicencia).isPresent();
    }

    @Override
    public List<Conductor> listarTodos() {
        return conductores.values().stream()
                .sorted(Comparator.comparing(Conductor::getNombreCompleto))
                .toList();
    }

    private void cargar() {
        for (String linea : almacen.leer(ARCHIVO)) {
            if (linea.isBlank()) {
                continue;
            }
            String[] partes = linea.split("\\|", -1);
            if (partes.length != 2) {
                throw new IllegalStateException("Registro de conductor inválido en " + ARCHIVO);
            }
            try {
                Conductor conductor = new Conductor(
                        TextoSeguro.decodificar(partes[0]),
                        TextoSeguro.decodificar(partes[1]));
                conductores.put(conductor.getNumeroLicencia(), conductor);
            } catch (RuntimeException e) {
                throw new IllegalStateException("No se pudo cargar un conductor desde " + ARCHIVO, e);
            }
        }
    }

    private void persistir() {
        List<String> lineas = conductores.values().stream()
                .sorted(Comparator.comparing(Conductor::getNombreCompleto))
                .map(conductor -> String.join("|",
                        TextoSeguro.codificar(conductor.getNombreCompleto()),
                        TextoSeguro.codificar(conductor.getNumeroLicencia())))
                .toList();
        almacen.reemplazar(ARCHIVO, lineas);
    }
}
