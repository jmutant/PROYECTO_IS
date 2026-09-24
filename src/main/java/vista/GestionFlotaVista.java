package vista;

import java.util.List;
import modelo.EstadoUnidad;
import modelo.Unidad;

public interface GestionFlotaVista {

    String getPlaca();

    String getModelo();

    int getCapacidad();

    EstadoUnidad getEstado();

    void setAccionRegistrar(Runnable accion);

    void mostrarUnidades(List<Unidad> unidades);

    void mostrarExito(String mensaje);

    void mostrarError(String mensaje);

    void mostrar();

    void cerrar();
}
