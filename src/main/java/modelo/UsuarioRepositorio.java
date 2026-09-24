package modelo;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorio {
    Optional<Usuario> buscarPorUsername(String username);
    List<Usuario> listarTodos();
    void guardar(Usuario usuario);
    boolean existePorUsername(String username);

    // Alias para mantener compatibilidad con las versiones anteriores del proyecto.
    default Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return buscarPorUsername(nombreUsuario);
    }

    default boolean existePorNombreUsuario(String nombreUsuario) {
        return existePorUsername(nombreUsuario);
    }
}
