package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UsuarioRepositorioMemoria implements UsuarioRepositorio {
    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    @Override
    public void guardar(Usuario usuario) {
        if (existePorUsername(usuario.getUsername())) {
            throw new UsuarioDuplicadoException("El nombre de usuario '" + usuario.getUsername() + "' ya está registrado.");
        }
        usuarios.put(AutenticacionServicio.normalizar(usuario.getUsername()), usuario);
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        if (username == null) return Optional.empty();
        return Optional.ofNullable(usuarios.get(AutenticacionServicio.normalizar(username)));
    }

    @Override
    public boolean existePorUsername(String username) {
        return buscarPorUsername(username).isPresent();
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios.values());
    }
}
