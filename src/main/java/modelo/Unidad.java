package modelo;

import java.util.Locale;
import java.util.Objects;


 //Clase que representa la entidad de dominio de una Unidad
 
public final class Unidad {

    private final String placa;
    private final String modelo;
    private final int capacidad;
    private final EstadoUnidad estado;
    private static final String REGEX_PLACA = "^[a-zA-Z0-9]{6,7}$"; // 

 //Constructor con validación de restricciones de negocio para la creación de la entidad.

    public Unidad(String placa, String modelo, int capacidad, EstadoUnidad estado) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria");
        }

        String placaLimpia = placa.trim();
        if (!placaLimpia.matches(REGEX_PLACA)) {
            throw new IllegalArgumentException("La placa debe ser alfanumérica y tener entre 6 y 7 caracteres.");
        }

        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("El modelo es obligatorio");
        }

        if (capacidad < 1 || capacidad > 65 ) {
            throw new IllegalArgumentException("La cantidad de puestos debe estar entre 1 y 65.");
        }
        this.placa = normalizarPlaca(placa);
        this.modelo = modelo.trim();
        this.capacidad = capacidad;
        this.estado = Objects.requireNonNull(estado, "El estado es obligatorio");
    }


     // Método fábrica que retorna una nueva instancia con un nuevo estado manteniendo inmutabilidad.

    public Unidad conEstado(EstadoUnidad nuevoEstado) {
        return new Unidad(this.placa, this.modelo, this.capacidad, nuevoEstado);
    }


     // Formatea y estandariza la estructura textual de las placas vehiculares.

    public static String normalizarPlaca(String placa) {
        return placa.trim().toUpperCase(Locale.ROOT);
    }

    public String getPlaca() { return placa; }
    public String getModelo() { return modelo; }
    public int getCapacidad() { return capacidad; }
    public EstadoUnidad getEstado() { return estado; }

    @Override
    public String toString() {
        return placa + " - " + modelo + " - " + capacidad + " puestos - " + estado.getEtiqueta();
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Unidad)) return false;
        return placa.equals(((Unidad) otro).placa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placa);
    }
}