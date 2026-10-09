package controlador;

import java.math.BigDecimal;
import java.util.Locale;

import javax.swing.JOptionPane;

import modelo.MonederoServicio;
import modelo.Usuario;
import vista.MonederoRecargaDatos;
//import vista.MonederoVentana;
import vista.MonederoVista;

/** Controlador de la pantalla del monedero y de las recargas. */
public class MonederoControlador {

    private final MonederoVista vista;
    private final MonederoServicio servicio;
    private final Usuario usuario;

    public MonederoControlador(MonederoVista vista, MonederoServicio servicio,
                            Usuario usuario) {
        this.vista = vista;
        this.servicio = servicio;
        this.usuario = usuario;

        this.vista.setAccionRecargarSaldo(this::recargarSaldo);
        this.vista.setAccionCerrar(this::cerrar);
        actualizarSaldo();
    }

    private void actualizarSaldo() {
        BigDecimal saldo = servicio.consultarSaldo(usuario);
        vista.mostrarSaldo(formatearSaldo(saldo));
    }

    private void recargarSaldo() {
        MonederoRecargaDatos datos = vista.solicitarDatosRecarga(usuario);
        if (datos == null) {
            return;
        }

        try {
            MonederoServicio.DatosRecarga datosModelo = new MonederoServicio.DatosRecarga(
                    datos.telefono(), datos.cedula(), datos.banco(), datos.referencia());
            BigDecimal nuevoSaldo = servicio.recargar(usuario, datosModelo);
            vista.mostrarSaldo(formatearSaldo(nuevoSaldo));
            vista.mostrarMensaje(
                    "Pago verificado correctamente.\nSe agregaron Bs 1,200.00 a tu monedero.",
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


