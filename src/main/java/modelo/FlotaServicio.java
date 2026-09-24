package modelo;

import java.util.List;
import java.util.Objects;

/** Reglas de negocio de HU-002. */
public class FlotaServicio {

    private final UnidadRepositorio repositorio;
    private final AutorizacionServicio autorizacion;

    public FlotaServicio(UnidadRepositorio repositorio, AutorizacionServicio autorizacion) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.autorizacion = Objects.requireNonNull(autorizacion);
    }

    public Unidad registrar(String placa, String modelo, int capacidad, EstadoUnidad estado, Usuario usuario) {
        autorizacion.exigirAdministrador(usuario);
        Unidad unidad = new Unidad(placa, modelo, capacidad, estado);
        if (repositorio.existePorPlaca(unidad.getPlaca())) {
            throw new UnidadDuplicadaException(unidad.getPlaca());
        }
        repositorio.guardar(unidad);
        return unidad;
    }

    public List<Unidad> listar(Usuario usuario) {
        autorizacion.exigirAdministrador(usuario);
        return repositorio.listarTodos();
    }
}
