package modelo;

import java.util.List;
import java.util.Objects;


//Servicio de capa de negocio encargado del procesamiento de flota.

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

    /**
     * Valida permisos y actualiza todos los datos de la unidad, re-claveando en caso de cambio de placa.
     */
    public void actualizarUnidad(String placaOriginal, String nuevaPlaca, String modelo, int capacidad, EstadoUnidad estado, Usuario usuario) {
        autorizacion.exigirAdministrador(usuario);

        // Si la placa cambió, se verifica que la nueva placa no exista ya asignada a otra unidad
        if (!placaOriginal.equalsIgnoreCase(nuevaPlaca) && repositorio.existePorPlaca(nuevaPlaca)) {
            throw new IllegalArgumentException("La nueva placa " + nuevaPlaca + " ya pertenece a otra unidad registrada.");
        }

        // Se crea el nuevo objeto Unidad (ejecuta validaciones de placa de 6 caracteres y capacidad entre 10 y 65)
        Unidad unidadEditada = new Unidad(nuevaPlaca, modelo, capacidad, estado);

        // Se persiste la actualización utilizando la placa original para hacer el re-keying en el repositorio
        repositorio.actualizar(placaOriginal, unidadEditada);
    }

    public List<Unidad> listar(Usuario usuario) {
        autorizacion.exigirAdministrador(usuario);
        return repositorio.listarTodos();
    }

    public void actualizarEstadoUnidad(String placa, EstadoUnidad nuevoEstado, Usuario usuario) {
        autorizacion.exigirAdministrador(usuario);

        Unidad unidadExistente = repositorio.buscarPorPlaca(placa)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró ninguna unidad con la placa: " + placa));

        Unidad unidadActualizada = unidadExistente.conEstado(nuevoEstado);
        repositorio.guardar(unidadActualizada);
    }
}