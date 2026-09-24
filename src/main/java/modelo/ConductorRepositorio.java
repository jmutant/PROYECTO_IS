package modelo;

import java.util.List;
import java.util.Optional;

public interface ConductorRepositorio {

    void guardar(Conductor conductor);

    Optional<Conductor> buscarPorLicencia(String numeroLicencia);

    List<Conductor> buscarPorNombre(String nombre);

    boolean existePorLicencia(String numeroLicencia);

    List<Conductor> listarTodos();
}
