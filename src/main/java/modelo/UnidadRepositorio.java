package modelo;

import java.util.List;
import java.util.Optional;

public interface UnidadRepositorio {
    Optional<Unidad> buscarPorPlaca(String placa);
    List<Unidad> listarTodos();
    void guardar(Unidad unidad);
    boolean existePorPlaca(String placa);
}
