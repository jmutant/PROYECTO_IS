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

    public Itinerario registrar(String origen, String destino, DayOfWeek dia, LocalTime hora,
                                TipoRuta tipoRuta, String placaUnidad, String licenciaConductor,
                                Usuario usuario) {
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
        boolean conflicto = itinerarioRepositorio.listarTodos().stream().anyMatch(actual ->
                actual.coincideHorario(nuevo)
                        && (actual.getPlacaUnidad().equals(nuevo.getPlacaUnidad())
                        || actual.getLicenciaConductor().equals(nuevo.getLicenciaConductor())));
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
}
