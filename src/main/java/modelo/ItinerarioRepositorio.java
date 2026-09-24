package modelo;

import java.util.List;

public interface ItinerarioRepositorio {

    void guardar(Itinerario itinerario);

    List<Itinerario> listarTodos();
}
