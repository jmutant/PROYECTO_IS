package ve.ucv.campusexpress.modelo.repositorio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import ve.ucv.campusexpress.modelo.Unidad;

public class UnidadRepositorioMemoria implements UnidadRepositorio {

    private final Map<String, Unidad> unidades = new ConcurrentHashMap<>();

    @Override
    public void guardar(Unidad unidad) {
        unidades.put(unidad.getPlaca(), unidad);
    }

    @Override
    public Optional<Unidad> buscarPorPlaca(String placa) {
        if (placa == null) {
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
        return new ArrayList<>(unidades.values()).stream()
                .sorted(Comparator.comparing(Unidad::getPlaca))
                .toList();
    }
}
