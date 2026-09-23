package ve.ucv.campusexpress.modelo.repositorio;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import ve.ucv.campusexpress.modelo.Rol;
import ve.ucv.campusexpress.modelo.Usuario;
import ve.ucv.campusexpress.modelo.persistencia.AlmacenDatosLocal;

/** Repositorio persistente de usuarios para el MVP local. */
public class UsuarioRepositorioArchivo implements UsuarioRepositorio {

    private static final String ARCHIVO = "usuarios.db";

    private final AlmacenDatosLocal almacen;
    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    public UsuarioRepositorioArchivo(AlmacenDatosLocal almacen) {
        this.almacen = almacen;
        cargar();
    }

    @Override
    public synchronized void guardar(Usuario usuario) {
        usuarios.put(usuario.getNombreUsuario(), usuario);
        persistir();
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

    private void cargar() {
        for (String linea : almacen.leer(ARCHIVO)) {
            if (linea.isBlank()) {
                continue;
            }
            String[] partes = linea.split("\\|", -1);
            if (partes.length != 4) {
                throw new IllegalStateException("Registro de usuario inválido en " + ARCHIVO);
            }
            try {
                Usuario usuario = new Usuario(
                        TextoSeguro.decodificar(partes[0]),
                        TextoSeguro.decodificar(partes[1]),
                        Rol.valueOf(partes[2]),
                        TextoSeguro.decodificar(partes[3]));
                usuarios.put(usuario.getNombreUsuario(), usuario);
            } catch (RuntimeException e) {
                throw new IllegalStateException("No se pudo cargar un usuario desde " + ARCHIVO, e);
            }
        }
    }

    private void persistir() {
        var lineas = usuarios.values().stream()
                .sorted((a, b) -> a.getNombreUsuario().compareTo(b.getNombreUsuario()))
                .map(usuario -> String.join("|",
                        TextoSeguro.codificar(usuario.getNombreUsuario()),
                        TextoSeguro.codificar(usuario.getNombreCompleto()),
                        usuario.getRol().name(),
                        TextoSeguro.codificar(usuario.getHashContrasena())))
                .toList();
        almacen.reemplazar(ARCHIVO, lineas);
    }
}
