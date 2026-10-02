package controlador;

import java.util.List;
import java.util.Objects;
import modelo.Conductor;
import modelo.ConflictoHorarioException;
import modelo.ItinerarioServicio;
import modelo.Unidad;
import modelo.Usuario;
import vista.GestionItinerariosVista;

public final class GestionItinerariosControlador {

    private final GestionItinerariosVista vista;
    private final ItinerarioServicio servicio;
    private final Usuario usuario;

    public GestionItinerariosControlador(GestionItinerariosVista vista, ItinerarioServicio servicio, Usuario usuario) {
        this.vista = Objects.requireNonNull(vista);
        this.servicio = Objects.requireNonNull(servicio);
        this.usuario = Objects.requireNonNull(usuario);

        // Mapeo de eventos de la vista
        this.vista.setAccionRegistrar(this::registrar);
        this.vista.setAccionRegresar(this::regresar);
        // --- TAREA 3: Suscripción a Búsquedas Dinámicas ---
        this.vista.setAccionBuscarUnidad(this::filtrarUnidades);
        this.vista.setAccionBuscarConductor(this::filtrarConductores);

        // Carga inicial de datos en combos y tablas
        cargarDatos();
    }

    private void registrar() {
        Unidad unidad = vista.getUnidad();
        Conductor conductor = vista.getConductor();

        if (unidad == null || conductor == null) {
            vista.mostrarError("Debe seleccionar una unidad y un conductor.");
            return;
        }

        if (vista.getHoraSalida() == null) {
            vista.mostrarError("La hora de salida no es válida (Ejemplo correcto: 08:00).");
            return;
        }

        try {
            String licencia = conductor.getNumeroLicencia();

            servicio.registrar(
                    vista.getOrigen(),
                    vista.getDestino(),
                    vista.getDiaSemana(),
                    vista.getHoraSalida(),
                    vista.getTipoRuta(),
                    unidad.getPlaca(),
                    licencia,
                    usuario
            );

            vista.mostrarExito("Itinerario registrado correctamente.");
            cargarDatos();

        } catch (ConflictoHorarioException e) {
            vista.mostrarError("Conflicto de horario: La unidad o conductor ya tienen un itinerario asignado.");
        } catch (IllegalArgumentException e) {
            vista.mostrarError(e.getMessage());
        } catch (Exception e) {
            vista.mostrarError("Error al registrar itinerario: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        // 1. Cargar unidades
        try {
            vista.cargarUnidades(servicio.getUnidadRepositorio().listarTodos());
        } catch (Exception e) {
            System.err.println("Error o repositorio vacío al cargar unidades: " + e.getMessage());
        }

        // 2. Cargar conductores
        try {
            vista.cargarConductores(servicio.getConductorRepositorio().listarTodos());
        } catch (Exception e) {
            System.err.println("Error o repositorio vacío al cargar conductores: " + e.getMessage());
        }

        // 3. Cargar itinerarios en la tabla (HU-003)
        try {
            vista.mostrarItinerarios(servicio.listar(usuario));
        } catch (Exception e) {
            System.err.println("Error al cargar itinerarios en la tabla: " + e.getMessage());
        }
    }

    private void filtrarUnidades(String criterio) {
        if (criterio == null || criterio.isBlank()) {
            vista.cargarUnidades(servicio.getUnidadRepositorio().listarTodos());
        } else {
            String query = criterio.trim().toLowerCase();
            List<Unidad> filtrados = servicio.getUnidadRepositorio().listarTodos().stream().filter(u -> u.getPlaca().toLowerCase().contains(query)).toList();
            vista.cargarUnidades(filtrados);
        }
    }

    private void filtrarConductores(String criterio) {
    if (criterio == null || criterio.isBlank()) {
        vista.cargarConductores(servicio.getConductorRepositorio().listarTodos());
    } else {
        String query = criterio.trim().toLowerCase();
        List<Conductor> filtrados = servicio.getConductorRepositorio().listarTodos().stream().filter(c -> {
                    // 1. Busca en el método toString() (que es lo que el usuario ve en pantalla)
                    boolean coincideString = c.toString() != null && c.toString().toLowerCase().contains(query);
                    // 2. Busca en el nombre por si acaso
                    boolean coincideNombre = c.getNombreCompleto() != null && c.getNombreCompleto().toLowerCase().contains(query);
                    // 3. Busca en la cédula/licencia
                    boolean coincideLicencia = c.getNumeroLicencia() != null && c.getNumeroLicencia().toLowerCase().contains(query);
                    
                    return coincideString || coincideNombre || coincideLicencia;
                })
                .toList();
        vista.cargarConductores(filtrados);
    }
}

    private void regresar() {
        vista.cerrar();
    }
}