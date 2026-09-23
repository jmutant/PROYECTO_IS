package ve.ucv.campusexpress.modelo.repositorio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import ve.ucv.campusexpress.modelo.Conductor;

public class ConductorRepositorioMemoria implements ConductorRepositorio {

    private final Map<String, Conductor> conductores = new ConcurrentHashMap<>();

    @Override
    public void guardar(Conductor conductor) {
        conductores.put(conductor.getNumeroLicencia(), conductor);
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
        return new ArrayList<>(conductores.values()).stream()
                .sorted(Comparator.comparing(Conductor::getNombreCompleto))
                .toList();
    }
}
