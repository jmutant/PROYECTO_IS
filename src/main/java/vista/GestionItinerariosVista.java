package vista;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import modelo.Conductor;
import modelo.Itinerario;
import modelo.TipoRuta;
import modelo.Unidad;

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
