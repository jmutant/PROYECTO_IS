package modelo;

import java.util.List;
import java.util.Objects;


 // Servicio de capa de negocio encargado del procesamiento de flota (HU-002).

public class FlotaServicio {

    private final UnidadRepositorio repositorio;
    private final AutorizacionServicio autorizacion;


     // Constructor para inyección de dependencias del repositorio y el módulo de autorización.

    public FlotaServicio(UnidadRepositorio repositorio, AutorizacionServicio autorizacion) {
        this.repositorio = Objects.requireNonNull(repositorio);
        this.autorizacion = Objects.requireNonNull(autorizacion);
    }


     // Valida permisos, verifica duplicados y registra una nueva unidad en la base de datos.

    public Unidad registrar(String placa, String modelo, int capacidad, EstadoUnidad estado, Usuario usuario) {
        autorizacion.exigirAdministrador(usuario);
        Unidad unidad = new Unidad(placa, modelo, capacidad, estado);
        if (repositorio.existePorPlaca(unidad.getPlaca())) {
            throw new UnidadDuplicadaException(unidad.getPlaca());
        }
        repositorio.guardar(unidad);
        return unidad;
    }

     // Retorna la lista global de unidades registradas tras validar permisos de administrador.

    public List<Unidad> listar(Usuario usuario) {
        autorizacion.exigirAdministrador(usuario);
        return repositorio.listarTodos();
    }

     // Busca una unidad por placa, crea una versión actualizada con su nuevo estado

    public void actualizarEstadoUnidad(String placa, EstadoUnidad nuevoEstado, Usuario usuario) {
        autorizacion.exigirAdministrador(usuario);

        Unidad unidadExistente = repositorio.buscarPorPlaca(placa)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró ninguna unidad con la placa: " + placa));

        Unidad unidadActualizada = unidadExistente.conEstado(nuevoEstado);
        repositorio.guardar(unidadActualizada);
    }
}