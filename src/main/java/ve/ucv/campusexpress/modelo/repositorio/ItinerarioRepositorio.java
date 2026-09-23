package ve.ucv.campusexpress.modelo.repositorio;

import java.util.List;
import ve.ucv.campusexpress.modelo.Itinerario;

public interface ItinerarioRepositorio {

    void guardar(Itinerario itinerario);

    List<Itinerario> listarTodos();
}
