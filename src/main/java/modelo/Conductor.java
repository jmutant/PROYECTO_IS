package modelo;

import java.util.Locale;
import java.util.Objects;

/** Conductor asociado a un itinerario. */
public final class Conductor {

    private final String nombreCompleto;
    private final String cedula;
    private final String numeroLicencia;

    public Conductor(String nombreCompleto, String cedula, String numeroLicencia) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException("El nombre del conductor es obligatorio");
        }
        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException("La cédula del conductor es obligatoria");
        }
        if (numeroLicencia == null || numeroLicencia.isBlank()) {
            throw new IllegalArgumentException("El número de licencia es obligatorio");
        }

        String cedulaTrim = cedula.trim().toUpperCase(Locale.ROOT);
        // Validar formato: debe iniciar con 'V' seguido de dígitos
        if (!cedulaTrim.matches("^V\\d+$")) {
            throw new IllegalArgumentException("La cédula debe iniciar con 'V' seguido del número (Ej: V12345678)");
        }

        this.nombreCompleto = nombreCompleto.trim();
        this.cedula = cedulaTrim;
        this.numeroLicencia = numeroLicencia.trim().toUpperCase(Locale.ROOT);
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    @Override
    public String toString() {
        return nombreCompleto + " - C.I.: " + cedula + " (" + numeroLicencia + ")";
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