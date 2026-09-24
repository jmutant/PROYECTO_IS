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
}
