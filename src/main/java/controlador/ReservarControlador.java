package controlador;

import java.util.Objects;

import vista.ReservarVista;

// Controlador de la ventana "reserva": por ahora solo maneja el botón para regresar.
public class ReservarControlador {

    private final ReservarVista vista;

    public ReservarControlador(ReservarVista vista) {
        this.vista = Objects.requireNonNull(vista);
        this.vista.setAccionRegresar(this::regresar);
    }

    private void regresar() {
        vista.cerrar();
    }
}
