package modelo;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

/** Itinerario semanal que relaciona una ruta, una unidad y un conductor. */
public final class Itinerario {

    private final String origen;
    private final String destino;
    private final DayOfWeek diaSemana;
    private final LocalTime horaSalida;
    private final TipoRuta tipoRuta;
    private final String placaUnidad;
    private final String licenciaConductor;

    public Itinerario(String origen, String destino, DayOfWeek diaSemana, LocalTime horaSalida,
                      TipoRuta tipoRuta, String placaUnidad, String licenciaConductor) {
        if (origen == null || origen.isBlank()) {
            throw new IllegalArgumentException("El origen es obligatorio");
        }
        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("El destino es obligatorio");
        }
        this.diaSemana = Objects.requireNonNull(diaSemana, "El día es obligatorio");
        this.horaSalida = Objects.requireNonNull(horaSalida, "La hora es obligatoria");
        this.tipoRuta = Objects.requireNonNull(tipoRuta, "El tipo de ruta es obligatorio");
        if (placaUnidad == null || placaUnidad.isBlank()) {
            throw new IllegalArgumentException("La unidad es obligatoria");
        }
        if (licenciaConductor == null || licenciaConductor.isBlank()) {
            throw new IllegalArgumentException("El conductor es obligatorio");
        }
        this.origen = origen.trim();
        this.destino = destino.trim();
        this.placaUnidad = Unidad.normalizarPlaca(placaUnidad);
        this.licenciaConductor = licenciaConductor.trim().toUpperCase();
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public TipoRuta getTipoRuta() {
        return tipoRuta;
    }

    public String getPlacaUnidad() {
        return placaUnidad;
    }

    public String getLicenciaConductor() {
        return licenciaConductor;
    }

    /** Dos itinerarios ocupan el mismo recurso si coinciden en día y hora de salida. */
    public boolean coincideHorario(Itinerario otro) {
        return diaSemana == otro.diaSemana && horaSalida.equals(otro.horaSalida);
    }

    @Override
    public String toString() {
        return diaSemana + " " + horaSalida + " | " + origen + " -> " + destino
                + " | " + tipoRuta.getEtiqueta() + " | " + placaUnidad;
    }
}
