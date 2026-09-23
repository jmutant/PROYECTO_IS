package ve.ucv.campusexpress.modelo.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ve.ucv.campusexpress.modelo.Conductor;
import ve.ucv.campusexpress.modelo.EstadoUnidad;
import ve.ucv.campusexpress.modelo.Rol;
import ve.ucv.campusexpress.modelo.TipoRuta;
import ve.ucv.campusexpress.modelo.Unidad;
import ve.ucv.campusexpress.modelo.Usuario;
import ve.ucv.campusexpress.modelo.excepcion.ConflictoHorarioException;
import ve.ucv.campusexpress.modelo.repositorio.ConductorRepositorioMemoria;
import ve.ucv.campusexpress.modelo.repositorio.ItinerarioRepositorioMemoria;
import ve.ucv.campusexpress.modelo.repositorio.UnidadRepositorioMemoria;

class ItinerarioServicioTest {

    private ItinerarioServicio servicio;
    private Usuario admin;

    @BeforeEach
    void preparar() {
        UnidadRepositorioMemoria unidades = new UnidadRepositorioMemoria();
        unidades.guardar(new Unidad("ABC123", "Bus", 40, EstadoUnidad.ACTIVO));

        ConductorRepositorioMemoria conductores = new ConductorRepositorioMemoria();
        conductores.guardar(new Conductor("Carlos Pérez", "LIC-001"));

        servicio = new ItinerarioServicio(new ItinerarioRepositorioMemoria(), unidades, conductores,
                new AutorizacionServicio());
        admin = new Usuario("admin", "Administrador", Rol.ADMINISTRADOR, "hash");
    }

    @Test
    void registraItinerarioConRecursosDisponibles() {
        servicio.registrar("UCV", "La Bandera", DayOfWeek.MONDAY, LocalTime.of(7, 30),
                TipoRuta.URBANA, "ABC123", "LIC-001", admin);

        assertEquals(1, servicio.listar(admin).size());
    }

    @Test
    void impideReutilizarUnidadOConductorEnElMismoHorario() {
        servicio.registrar("UCV", "La Bandera", DayOfWeek.MONDAY, LocalTime.of(7, 30),
                TipoRuta.URBANA, "ABC123", "LIC-001", admin);

        assertThrows(ConflictoHorarioException.class, () -> servicio.registrar(
                "UCV", "Petare", DayOfWeek.MONDAY, LocalTime.of(7, 30),
                TipoRuta.URBANA, "ABC123", "LIC-001", admin));
    }
}
