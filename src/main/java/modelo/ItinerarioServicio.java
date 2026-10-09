package modelo;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/** Reglas de negocio de HU-003. */
public class ItinerarioServicio {

    private final ItinerarioRepositorio itinerarioRepositorio;
    private final UnidadRepositorio unidadRepositorio;
    private final ConductorRepositorio conductorRepositorio;
    private final AutorizacionServicio autorizacion;

    public ItinerarioServicio(ItinerarioRepositorio itinerarioRepositorio,
                            UnidadRepositorio unidadRepositorio,
                            ConductorRepositorio conductorRepositorio,
                            AutorizacionServicio autorizacion) {
        this.itinerarioRepositorio = Objects.requireNonNull(itinerarioRepositorio);
        this.unidadRepositorio = Objects.requireNonNull(unidadRepositorio);
        this.conductorRepositorio = Objects.requireNonNull(conductorRepositorio);
        this.autorizacion = Objects.requireNonNull(autorizacion);
    }

    public Itinerario registrar(String origen, String destino, DayOfWeek dia, LocalTime hora, TipoRuta tipoRuta, String placaUnidad, String licenciaConductor, Usuario usuario) {
        autorizacion.exigirAdministrador(usuario);
        if (unidadRepositorio.buscarPorPlaca(placaUnidad).isEmpty()) {
            throw new IllegalArgumentException("La unidad seleccionada no existe");
        }
        if (unidadRepositorio.buscarPorPlaca(placaUnidad).orElseThrow().getEstado()
                != EstadoUnidad.ACTIVO) {
            throw new IllegalArgumentException("La unidad seleccionada no está activa");
        }
        if (conductorRepositorio.buscarPorLicencia(licenciaConductor).isEmpty()) {
            throw new IllegalArgumentException("El conductor seleccionado no existe");
        }

        Itinerario nuevo = new Itinerario(origen, destino, dia, hora, tipoRuta, placaUnidad, licenciaConductor);
        boolean conflicto = itinerarioRepositorio.listarTodos().stream().anyMatch(actual -> actual.coincideHorario(nuevo) && (actual.getPlacaUnidad().equals(nuevo.getPlacaUnidad()) || actual.getLicenciaConductor().equals(nuevo.getLicenciaConductor())));
        if (conflicto) {
            throw new ConflictoHorarioException();
        }
        itinerarioRepositorio.guardar(nuevo);
        return nuevo;
    }
    public List<Itinerario> listar(Usuario usuario) {
        autorizacion.exigirAdministrador(usuario);
        return itinerarioRepositorio.listarTodos();
    }

    public UnidadRepositorio getUnidadRepositorio() {
        return unidadRepositorio;
    }

    public ConductorRepositorio getConductorRepositorio() {
        return conductorRepositorio;
    }

    //Mejoras con respecto a Sprint 1
    // --- MODIFICAR ITINERARIO ---
    public void modificar(String id, String origen, String destino, DayOfWeek dia, LocalTime hora, TipoRuta tipoRuta, String placaUnidad, String licenciaConductor, Usuario usuarioActual) {
        
        // 1. Validar permisos de Administrador
        autorizacion.exigirAdministrador(usuarioActual);

        // 2. Buscar itinerario existente
        Itinerario itinerarioExistente = itinerarioRepositorio.listarTodos().stream()
        .filter(it -> it.getPlacaUnidad().equals(placaUnidad) && it.getDiaSemana() == dia && it.getHoraSalida().equals(hora))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("El itinerario a modificar no existe."));

        // 3. Validar existencia y estado de la Unidad y Conductor
        Unidad unidad = unidadRepositorio.buscarPorPlaca(placaUnidad).orElse(null);
        if (unidad == null || unidad.getEstado() != EstadoUnidad.ACTIVO) {
            throw new IllegalArgumentException("La unidad seleccionada no está disponible o no existe.");
        }

        Conductor conductor = conductorRepositorio.buscarPorLicencia(licenciaConductor).orElse(null);
        if (conductor == null) {
            throw new IllegalArgumentException("El conductor seleccionado no existe.");
        }

        // 4. Validar conflicto de horario (excluyendo el propio itinerario que estamos modificando)
        boolean conflicto = itinerarioRepositorio.listarTodos().stream()
        // Excluimos el itinerario original usando los datos de itinerarioExistente:
        .filter(i -> !(i.getDiaSemana().equals(itinerarioExistente.getDiaSemana()) && i.getHoraSalida().equals(itinerarioExistente.getHoraSalida())
        && (i.getPlacaUnidad().equals(itinerarioExistente.getPlacaUnidad()) || i.getLicenciaConductor().equals(itinerarioExistente.getLicenciaConductor()))))
        // Validamos si alguno de los DEMÁS entra en conflicto con las NUEVAS variables/datos:
        .anyMatch(i -> i.getDiaSemana().equals(dia) && i.getHoraSalida().equals(hora) &&
        (i.getPlacaUnidad().equals(placaUnidad) || i.getLicenciaConductor().equals(licenciaConductor)));

        if (conflicto) {
            throw new ConflictoHorarioException();
        }

        // 5. Crear el nuevo itinerario actualizado
        Itinerario itinerarioActualizado = new Itinerario(
            origen,
            destino,
            dia,
            hora,
            tipoRuta,
            placaUnidad,
            licenciaConductor
        );

        // Eliminar el viejo antes de guardar el nuevo:
        itinerarioRepositorio.eliminar(itinerarioExistente);
        itinerarioRepositorio.guardar(itinerarioActualizado);
    }

    // --- ELIMINAR ITINERARIO ---
    public void eliminar(String placaUnidad, DayOfWeek dia, LocalTime hora, Usuario usuarioActual) {
        // Validar permisos
        autorizacion.exigirAdministrador(usuarioActual);

        // Buscar itinerario a eliminar por sus claves
        Itinerario itinerarioAEliminar = itinerarioRepositorio.listarTodos().stream().filter(it -> it.getPlacaUnidad().equals(placaUnidad) && it.getDiaSemana().equals(dia) && it.getHoraSalida().equals(hora))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("El itinerario a eliminar no existe."));

        // Eliminar el objeto del repositorio
        itinerarioRepositorio.eliminar(itinerarioAEliminar);
    }
}
