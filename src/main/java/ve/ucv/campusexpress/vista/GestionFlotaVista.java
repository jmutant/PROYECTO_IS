package ve.ucv.campusexpress.vista;

import java.util.List;
import ve.ucv.campusexpress.modelo.EstadoUnidad;
import ve.ucv.campusexpress.modelo.Unidad;

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
