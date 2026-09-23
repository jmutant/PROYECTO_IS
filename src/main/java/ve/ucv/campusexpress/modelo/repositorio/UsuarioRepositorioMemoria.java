package ve.ucv.campusexpress.modelo.repositorio;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import ve.ucv.campusexpress.modelo.Usuario;

/**
 * Implementación en memoria: los datos se pierden al cerrar la aplicación.
 * Suficiente para el MVP del Sprint 1.
 */
public class UsuarioRepositorioMemoria implements UsuarioRepositorio {

    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    @Override
    public void guardar(Usuario usuario) {
        usuarios.put(usuario.getNombreUsuario(), usuario);
    }

    @Override
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(usuarios.get(Usuario.normalizar(nombreUsuario)));
    }

    @Override
    public boolean existePorNombreUsuario(String nombreUsuario) {
        return buscarPorNombreUsuario(nombreUsuario).isPresent();
    }
}
