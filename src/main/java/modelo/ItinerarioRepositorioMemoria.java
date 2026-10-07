package modelo;

import java.util.ArrayList;
import java.util.List;

public class ItinerarioRepositorioMemoria implements ItinerarioRepositorio {

    private final List<Itinerario> itinerarios = new ArrayList<>();

    @Override
    public synchronized void guardar(Itinerario itinerario) {
        itinerarios.add(itinerario);
    }

    @Override
    public synchronized List<Itinerario> listarTodos() {
        return List.copyOf(itinerarios);
    }

    @Override
    public synchronized void actualizar(Itinerario antiguo, Itinerario nuevo) {
        eliminar(antiguo);
        guardar(nuevo);
    }
    @Override
    public synchronized void eliminar(Itinerario itinerario) {
        itinerarios.removeIf(it -> it.getPlacaUnidad().equals(itinerario.getPlacaUnidad())
            && it.getDiaSemana().equals(itinerario.getDiaSemana())
            && it.getHoraSalida().equals(itinerario.getHoraSalida()));
    }
}
