package ve.ucv.campusexpress.modelo.repositorio;

import java.util.List;
import java.util.Optional;
import ve.ucv.campusexpress.modelo.Unidad;

public interface UnidadRepositorio {

    void guardar(Unidad unidad);

    Optional<Unidad> buscarPorPlaca(String placa);

    boolean existePorPlaca(String placa);

    List<Unidad> listarTodos();
}
