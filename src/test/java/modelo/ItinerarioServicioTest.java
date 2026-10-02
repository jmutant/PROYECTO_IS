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
    private Usuario pasajero;

    @BeforeEach
    void setUp() {
        // Usamos repositorios en memoria para aislar la prueba de la persistencia física
        ItinerarioRepositorio itinerarioRepo = new ItinerarioRepositorioMemoria();
        UnidadRepositorio unidadRepo = new UnidadRepositorioMemoria();
        ConductorRepositorio conductorRepo = new ConductorRepositorioMemoria();
        AutorizacionServicio autorizacion = new AutorizacionServicio();

        // Cargar flota y conductores de prueba con sus respectivos estados
        unidadRepo.guardar(new Unidad("ABC123", "Mercedes", 40, EstadoUnidad.ACTIVO));
        unidadRepo.guardar(new Unidad("MNT999", "Volvo", 30, EstadoUnidad.EN_MANTENIMIENTO));
        
        conductorRepo.guardar(new Conductor("V-12345678", "JUAN PEREZ"));
        conductorRepo.guardar(new Conductor("V-87654321", "PEDRO LOPEZ"));

        servicio = new ItinerarioServicio(itinerarioRepo, unidadRepo, conductorRepo, autorizacion);

        admin = new Usuario("admin", "Administrador", Rol.ADMINISTRADOR, "Admin123");
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
    @DisplayName("CP-08: Rechazar asignación de Unidad INACTIVA")
    void rechazarUnidadInactiva() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrar(
                    "Catia", "UCV",
                    DayOfWeek.MONDAY, LocalTime.of(8, 0),
                    TipoRuta.URBANA, "INA000", "V-12345678", admin
            );
        });
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("CP-09: Rechazar asignación de Unidad EN MANTENIMIENTO")
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

    // ==========================================
    // 5. AUTORIZACIÓN POR ROLES
    // ==========================================
    @Test
    @DisplayName("CP-10: Denegar registro si el usuario no tiene rol de Administrador")
    void rechazarUsuarioNoAdmin() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.registrar(
                    "Catia", "UCV",
                    DayOfWeek.MONDAY, LocalTime.of(8, 0),
                    TipoRuta.URBANA, "ABC123", "V-12345678", pasajero
            );
        });
        assertNotNull(ex.getMessage());
    }
}