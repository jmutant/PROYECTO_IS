package ve.ucv.campusexpress.modelo.repositorio;

import java.util.ArrayList;
import java.util.List;
import ve.ucv.campusexpress.modelo.Itinerario;

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
