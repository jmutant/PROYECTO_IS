package ve.ucv.campusexpress.vista;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import ve.ucv.campusexpress.modelo.Conductor;
import ve.ucv.campusexpress.modelo.Itinerario;
import ve.ucv.campusexpress.modelo.TipoRuta;
import ve.ucv.campusexpress.modelo.Unidad;

public interface GestionItinerariosVista {

    String getOrigen();

    String getDestino();

    DayOfWeek getDiaSemana();

    LocalTime getHoraSalida();

    TipoRuta getTipoRuta();

    Unidad getUnidad();

    Conductor getConductor();

    void setAccionRegistrar(Runnable accion);

    void mostrarItinerarios(List<Itinerario> itinerarios);

    void cargarUnidades(List<Unidad> unidades);

    void cargarConductores(List<Conductor> conductores);

    void mostrarExito(String mensaje);

    void mostrarError(String mensaje);

    void mostrar();
}
