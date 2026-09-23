package ve.ucv.campusexpress.modelo.repositorio;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import ve.ucv.campusexpress.modelo.Itinerario;
import ve.ucv.campusexpress.modelo.TipoRuta;
import ve.ucv.campusexpress.modelo.persistencia.AlmacenDatosLocal;

/** Repositorio persistente de itinerarios. */
public class ItinerarioRepositorioArchivo implements ItinerarioRepositorio {

    private static final String ARCHIVO = "itinerarios.db";

    private final AlmacenDatosLocal almacen;
    private final List<Itinerario> itinerarios = new ArrayList<>();

    public ItinerarioRepositorioArchivo(AlmacenDatosLocal almacen) {
        this.almacen = almacen;
        cargar();
    }

    @Override
    public synchronized void guardar(Itinerario itinerario) {
        itinerarios.add(itinerario);
        persistir();
    }

    @Override
    public synchronized List<Itinerario> listarTodos() {
        return itinerarios.stream()
                .sorted(Comparator.comparing(Itinerario::getDiaSemana)
                        .thenComparing(Itinerario::getHoraSalida))
                .toList();
    }

    private void cargar() {
        for (String linea : almacen.leer(ARCHIVO)) {
            if (linea.isBlank()) {
                continue;
            }
            String[] partes = linea.split("\\|", -1);
            if (partes.length != 7) {
                throw new IllegalStateException("Registro de itinerario inválido en " + ARCHIVO);
            }
            try {
                Itinerario itinerario = new Itinerario(
                        TextoSeguro.decodificar(partes[0]),
                        TextoSeguro.decodificar(partes[1]),
                        DayOfWeek.valueOf(partes[2]),
                        LocalTime.parse(partes[3]),
                        TipoRuta.valueOf(partes[4]),
                        TextoSeguro.decodificar(partes[5]),
                        TextoSeguro.decodificar(partes[6]));
                itinerarios.add(itinerario);
            } catch (RuntimeException e) {
                throw new IllegalStateException("No se pudo cargar un itinerario desde " + ARCHIVO, e);
            }
        }
    }

    private void persistir() {
        List<String> lineas = itinerarios.stream()
                .map(itinerario -> String.join("|",
                        TextoSeguro.codificar(itinerario.getOrigen()),
                        TextoSeguro.codificar(itinerario.getDestino()),
                        itinerario.getDiaSemana().name(),
                        itinerario.getHoraSalida().toString(),
                        itinerario.getTipoRuta().name(),
                        TextoSeguro.codificar(itinerario.getPlacaUnidad()),
                        TextoSeguro.codificar(itinerario.getLicenciaConductor())))
                .toList();
        almacen.reemplazar(ARCHIVO, lineas);
    }
}
