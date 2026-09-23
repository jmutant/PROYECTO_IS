package ve.ucv.campusexpress.modelo;

import java.util.Locale;
import java.util.Objects;

/** Conductor asociado a un itinerario. */
public final class Conductor {

    private final String nombreCompleto;
    private final String numeroLicencia;

    public Conductor(String nombreCompleto, String numeroLicencia) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException("El nombre del conductor es obligatorio");
        }
        if (numeroLicencia == null || numeroLicencia.isBlank()) {
            throw new IllegalArgumentException("El número de licencia es obligatorio");
        }
        this.nombreCompleto = nombreCompleto.trim();
        this.numeroLicencia = numeroLicencia.trim().toUpperCase(Locale.ROOT);
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    @Override
    public String toString() {
        return nombreCompleto + " (" + numeroLicencia + ")";
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Conductor)) {
            return false;
        }
        return numeroLicencia.equals(((Conductor) otro).numeroLicencia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroLicencia);
    }
}
