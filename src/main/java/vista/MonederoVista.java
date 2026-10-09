package vista;

import modelo.Usuario;

/** Operaciones que necesita el controlador del monedero virtual. */
public interface MonederoVista {
    void setAccionRecargarSaldo(Runnable accion);
    void setAccionCerrar(Runnable accion);
    void mostrarSaldo(String saldo);
    MonederoRecargaDatos solicitarDatosRecarga(Usuario usuario);
    void mostrarMensaje(String mensaje, String titulo, int tipo);
    void mostrarError(String mensaje);
    void mostrar();
    void cerrar();
}
