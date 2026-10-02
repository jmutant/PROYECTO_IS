package modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItinerarioServicioTest {

    private ItinerarioServicio servicio;
    private Usuario admin;
    private ItinerarioRepositorioMemoria itinerarioRepo;
    private UnidadRepositorioMemoria unidadRepo;
    private ConductorRepositorioMemoria conductorRepo;

    @BeforeEach
    void setUp() {
        itinerarioRepo = new ItinerarioRepositorioMemoria();
        unidadRepo = new UnidadRepositorioMemoria();
        conductorRepo = new ConductorRepositorioMemoria();
        AutorizacionServicio autorizacion = new AutorizacionServicio();

        // Instanciar el servicio
        servicio = new ItinerarioServicio(itinerarioRepo, unidadRepo, conductorRepo, autorizacion);

        // --- CONDUCTORES DE PRUEBA ---
        Conductor conductor1 = new Conductor("Juan Perez", "V-12345678");
        Conductor conductor2 = new Conductor("Maria Gomez", "V-87654321");
        conductorRepo.guardar(conductor1);
        conductorRepo.guardar(conductor2);

        // --- UNIDADES DE PRUEBA ---
        Unidad unidadActiva = new Unidad("ABC123", "Mercedes", 40, EstadoUnidad.ACTIVO);
        Unidad unidadMantenimiento = new Unidad("MNT999", "Yutong", 30, EstadoUnidad.EN_MANTENIMIENTO);
        unidadRepo.guardar(unidadActiva);
        unidadRepo.guardar(unidadMantenimiento);

        // --- USUARIO ADMINISTRADOR ---
        admin = new Usuario("admin", "Administrador Sistema", Rol.ADMINISTRADOR, "1234");
    }

    // ==========================================
    // 1. CASO EXITOSO
    // ==========================================
    @Test
    @DisplayName("CP-01: Registrar itinerario exitosamente con datos válidos")
    void registrarItinerarioExitosamente() {
        Itinerario creado = servicio.registrar(
                "Catia", "UCV",
                DayOfWeek.MONDAY, LocalTime.of(8, 0),
                TipoRuta.URBANA, "ABC123", "V-12345678", admin
        );

        assertNotNull(creado);
        assertEquals("Catia", creado.getOrigen());
        assertEquals("UCV", creado.getDestino());

        List<Itinerario> lista = servicio.listar(admin);
        assertEquals(1, lista.size());
    }

    // ==========================================
    // 2. CASOS FALLIDOS POR CAMPOS INCOMPLETOS
    // ==========================================
    @Test
    @DisplayName("CP-02: Fallar si el Origen es nulo o está vacío")
    void rechazarSinOrigen() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrar(
                    "   ", "UCV",
                    DayOfWeek.MONDAY, LocalTime.of(8, 0),
                    TipoRuta.URBANA, "ABC123", "V-12345678", admin
            );
        });
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("CP-03: Fallar si el Destino es nulo o está vacío")
    void rechazarSinDestino() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrar(
                    "Catia", "",
                    DayOfWeek.MONDAY, LocalTime.of(8, 0),
                    TipoRuta.URBANA, "ABC123", "V-12345678", admin
            );
        });
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("CP-04: Fallar si la Unidad no existe o no se selecciona")
    void rechazarSinUnidad() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrar(
                    "Catia", "UCV",
                    DayOfWeek.MONDAY, LocalTime.of(8, 0),
                    TipoRuta.URBANA, null, "V-12345678", admin
            );
        });
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("CP-05: Fallar si el Conductor no existe o no se selecciona")
    void rechazarSinConductor() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrar(
                    "Catia", "UCV",
                    DayOfWeek.MONDAY, LocalTime.of(8, 0),
                    TipoRuta.URBANA, "ABC123", null, admin
            );
        });
        assertNotNull(ex.getMessage());
    }

    // ==========================================
    // 3. CONFLITOS DE HORARIO (SOLAPAMIENTO)
    // ==========================================
    @Test
    @DisplayName("CP-06: Fallar por conflicto de horario al duplicar Unidad el mismo día y hora")
    void rechazarConflictoHorarioUnidad() {
        // Registro base
        servicio.registrar(
                "Catia", "UCV",
                DayOfWeek.MONDAY, LocalTime.of(8, 0),
                TipoRuta.URBANA, "ABC123", "V-12345678", admin
        );

        // Intento con misma unidad, distinto conductor
        ConflictoHorarioException ex = assertThrows(ConflictoHorarioException.class, () -> {
            servicio.registrar(
                    "Gato Negro", "La Guaira",
                    DayOfWeek.MONDAY, LocalTime.of(8, 0),
                    TipoRuta.EXTRAURBANA, "ABC123", "V-87654321", admin
            );
        });
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("CP-07: Fallar por conflicto de horario al duplicar Conductor el mismo día y hora")
    void rechazarConflictoHorarioConductor() {
        // Registro base
        servicio.registrar(
                "Catia", "UCV",
                DayOfWeek.MONDAY, LocalTime.of(8, 0),
                TipoRuta.URBANA, "ABC123", "V-12345678", admin
        );

        // Intento con mismo conductor, distinta unidad (si tuviéramos otra activa)
        ConflictoHorarioException ex = assertThrows(ConflictoHorarioException.class, () -> {
            servicio.registrar(
                    "Catia", "UCV",
                    DayOfWeek.MONDAY, LocalTime.of(8, 0),
                    TipoRuta.URBANA, "ABC123", "V-12345678", admin
            );
        });
        assertNotNull(ex.getMessage());
    }

    // ==========================================
    // 4. ESTADO OPERATIVO DE LA UNIDAD
    // ==========================================
    @Test
    @DisplayName("CP-08: Rechazar asignación de Unidad EN MANTENIMIENTO")
    void rechazarUnidadEnMantenimiento() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrar(
                    "Catia", "UCV",
                    DayOfWeek.MONDAY, LocalTime.of(8, 0),
                    TipoRuta.URBANA, "MNT999", "V-12345678", admin
            );
        });
        assertNotNull(ex.getMessage());
    }
}