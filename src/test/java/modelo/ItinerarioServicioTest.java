package modelo;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        Unidad unidadActiva2 = new Unidad("PQR987", "Volvo", 35, EstadoUnidad.ACTIVO);
        Unidad unidadMantenimiento = new Unidad("MNT999", "Yutong", 30, EstadoUnidad.EN_MANTENIMIENTO);
        unidadRepo.guardar(unidadActiva);
        unidadRepo.guardar(unidadActiva2);
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

    // =========================================================================
    // PRUEBAS UNITARIAS: MODIFICACIÓN DE ITINERARIOS
    // =========================================================================

    @Test
    @DisplayName("Debe modificar un itinerario exitosamente")
    void testModificarItinerarioExitoso() {
        // 1. Datos originales del itinerario a registrar
        String placaOriginal = "ABC123";
        DayOfWeek diaOriginal = DayOfWeek.MONDAY;
        LocalTime horaOriginal = LocalTime.of(8, 0);

        // Registrar itinerario base
        servicio.registrar(
            "Catia", "UCV", diaOriginal, horaOriginal, TipoRuta.URBANA, placaOriginal, "V-12345678", admin
        );

        // 2. Modificar invocando al servicio con la clave ORIGINAL y los NUEVOS valores
        LocalTime nuevaHora = LocalTime.of(10, 0);
        String nuevoDestino = "La Guaira";

        servicio.modificar(
            placaOriginal, diaOriginal, horaOriginal, // Clave de búsqueda original
            "Catia", nuevoDestino, diaOriginal, nuevaHora, TipoRuta.URBANA, placaOriginal, "V-12345678", admin
        );

        // 3. Verificación buscando en la lista completa obtenida del servicio
        Itinerario modificado = servicio.listar(admin).stream()
            .filter(i -> i.getPlacaUnidad().equalsIgnoreCase(placaOriginal) && i.getDiaSemana() == diaOriginal && i.getHoraSalida().equals(nuevaHora))
            .findFirst()
            .orElse(null);

        assertNotNull(modificado, "El itinerario modificado debe existir en la lista");
        assertEquals(nuevoDestino, modificado.getDestino());
        assertEquals(nuevaHora, modificado.getHoraSalida());
    }

    @Test
    @DisplayName("Rechazar modificación por conflicto de horario")
    void testModificarItinerarioConConflictoHorario() {
        // 1. Crear un itinerario A de 8:00 a 9:00 en el día MONDAY con la unidad ABC123
        servicio.registrar("Caracas", "UCV", DayOfWeek.MONDAY, LocalTime.of(8, 0), TipoRuta.URBANA, "ABC123", "V-12345678", admin);

        // 2. Crear un itinerario B a las 10:00 con la misma unidad
        servicio.registrar("Caracas", "Guarenas", DayOfWeek.MONDAY, LocalTime.of(10, 0), TipoRuta.URBANA, "ABC123", "V-12345678", admin);

        // 3. Intentar modificar el itinerario B para moverlo a las 8:00 (mismo día y unidad que el itinerario A)
        assertThrows(ConflictoHorarioException.class, () -> {
            servicio.modificar(
                "ABC123", DayOfWeek.MONDAY, LocalTime.of(10, 0), // Datos del itinerario B a cambiar
                "Caracas", "Guarenas", DayOfWeek.MONDAY, LocalTime.of(8, 0), // Intentar mover a las 8:00
                TipoRuta.URBANA, "ABC123", "V-12345678", admin
            );
        });
    }

    // =========================================================================
    // PRUEBAS UNITARIAS: ELIMINACIÓN DE ITINERARIOS
    // =========================================================================

    @Test
    @DisplayName("Debe eliminar un itinerario exitosamente por sus campos clave")
    void testEliminarItinerarioExitoso() {
        // 1. Datos del itinerario a registrar
        String placa = "ABC123";
        DayOfWeek dia = DayOfWeek.MONDAY;
        LocalTime hora = LocalTime.of(8, 0);

        // Registrar el itinerario base
        servicio.registrar(
            "Catia", "UCV", dia, hora, TipoRuta.URBANA, placa, "V-12345678", admin
        );

        // 2. Invocar al método eliminar del servicio pasando la clave/parámetros reales
        servicio.eliminar(placa, dia, hora, admin);

        // 3. Verificar que el itinerario ya no existe en la lista del sistema
        List<Itinerario> lista = servicio.listar(admin);
        
        assertFalse(
            lista.stream().anyMatch(i -> i.getPlacaUnidad().equals(placa)
                && i.getDiaSemana() == dia
                && i.getHoraSalida().equals(hora)
            ),
            "El itinerario eliminado no debería figurar en la lista del servicio"
        );
    }
}