package ve.ucv.campusexpress.modelo;

import java.util.Locale;
import java.util.Objects;

/** Unidad física de transporte, parte del modelo de HU-002 y HU-003. */
public final class Unidad {

    private final String placa;
    private final String modelo;
    private final int capacidad;
    private final EstadoUnidad estado;

    public Unidad(String placa, String modelo, int capacidad, EstadoUnidad estado) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("El modelo es obligatorio");
        }
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a cero");
        }
        this.placa = normalizarPlaca(placa);
        this.modelo = modelo.trim();
        this.capacidad = capacidad;
        this.estado = Objects.requireNonNull(estado, "El estado es obligatorio");
    }

    public static String normalizarPlaca(String placa) {
        return placa.trim().toUpperCase(Locale.ROOT);
    }

    public String getPlaca() {
        return placa;
    }

    public String getModelo() {
        return modelo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public EstadoUnidad getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return placa + " - " + modelo + " - " + capacidad + " puestos - " + estado.getEtiqueta();
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Unidad)) {
            return false;
        }
        return placa.equals(((Unidad) otro).placa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placa);
    }
}
