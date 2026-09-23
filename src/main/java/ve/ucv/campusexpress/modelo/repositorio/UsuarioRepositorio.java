package ve.ucv.campusexpress.modelo.repositorio;

import java.util.Optional;
import ve.ucv.campusexpress.modelo.Usuario;

/**
 * Acceso a los datos de los usuarios (capa de datos).
 *
 * <p>Es una interfaz para que el resto del sistema no dependa de dónde se guardan los datos:
 * hoy hay una implementación en memoria y mañana podría haber una en archivo o base de datos
 * sin cambiar servicios, controladores ni pruebas.</p>
 */
public interface UsuarioRepositorio {

    /** Guarda el usuario; si ya existía uno con el mismo nombre de usuario, lo reemplaza. */
    void guardar(Usuario usuario);

    /** Busca por nombre de usuario sin distinguir mayúsculas ni espacios en los extremos. */
    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);

    boolean existePorNombreUsuario(String nombreUsuario);
}
