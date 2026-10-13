package controlador;

//import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Locale;
//import java.util.function.Consumer;

import javax.swing.JOptionPane;

import modelo.MonederoServicio;
import modelo.Usuario;
import vista.MonederoRecargaDatos;
import vista.MonederoVista;

// Controlador de la pantalla del monedero y de las recargas.
public class MonederoControlador {

    private final MonederoVista vista;
    private final MonederoServicio servicio;
    private final Usuario usuario;

    public MonederoControlador(MonederoVista vista, MonederoServicio servicio,
                            Usuario usuario) {
        this.vista = vista;
        this.servicio = servicio;
        this.usuario = usuario;

        this.vista.setAccionAceptarRecarga(this::recargarSaldo);
        this.vista.setAccionCerrar(this::cerrar);
        actualizarSaldo();
    }

    private void actualizarSaldo() {
        BigDecimal saldo = servicio.consultarSaldo(usuario);
        vista.mostrarSaldo(formatearSaldo(saldo));
    }

    private void recargarSaldo(MonederoRecargaDatos datos) {
        try {
            MonederoServicio.DatosRecarga datosModelo = new MonederoServicio.DatosRecarga(
                    datos.telefono(), datos.cedula(), datos.banco(), datos.referencia(), datos.monto());
            BigDecimal montoRecargado = MonederoServicio.parsearMonto(datos.monto());
            BigDecimal nuevoSaldo = servicio.recargar(usuario, datosModelo);
            vista.mostrarSaldo(formatearSaldo(nuevoSaldo));
            vista.mostrarPantallaMonedero();
            vista.mostrarMensaje(
                    "Pago verificado correctamente.\nSe agregaron Bs " + formatearSaldo(montoRecargado)
                            + " a tu monedero.",
                    "Recarga exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    private void cerrar() {
        vista.cerrar();
    }

    private String formatearSaldo(BigDecimal saldo) {
        return String.format(Locale.US, "%,.2f", saldo.doubleValue());
    }


    public Usuario getUsuarioActual() {
        return usuario;
    }

}


