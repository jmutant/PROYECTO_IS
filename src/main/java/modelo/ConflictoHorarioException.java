package modelo;

/** Indica que una unidad o conductor ya está asignado en el mismo espacio horario. */
public class ConflictoHorarioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConflictoHorarioException() {
        super("La unidad o el conductor ya tienen un itinerario en ese horario");
    }
}
