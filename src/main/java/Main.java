import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import controlador.NavegadorSwing;
import modelo.Conductor;
import modelo.EstadoUnidad;
import modelo.Rol;
import modelo.Unidad;
import modelo.AlmacenDatosLocal;
import modelo.ConductorRepositorio;
import modelo.ConductorRepositorioArchivo;
import modelo.ItinerarioRepositorio;
import modelo.ItinerarioRepositorioArchivo;
import modelo.UnidadRepositorio;
import modelo.UnidadRepositorioArchivo;
import modelo.UsuarioRepositorio;
import modelo.UsuarioRepositorioArchivo;
import modelo.AutenticacionServicio;
import modelo.AutorizacionServicio;
import modelo.FlotaServicio;
import modelo.ItinerarioServicio;
import java.nio.file.Path;

/** Punto de entrada de Campus Express (SGTU). Arma las piezas del modelo, servicios y vistas. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        /*
         * Persistencia de usuarios solicitada para el proyecto:
         * src/main/java/modelo/UsuariosData.txt
         */
        Path rutaUsuarios = Path.of("src", "main", "java", "modelo", "UsuariosData.txt");
        UsuarioRepositorio usuarios = new UsuarioRepositorioArchivo(rutaUsuarios);

        AlmacenDatosLocal almacen = AlmacenDatosLocal.porDefecto();
        UnidadRepositorio unidades = new UnidadRepositorioArchivo(almacen);
        ConductorRepositorio conductores = new ConductorRepositorioArchivo(almacen);
        ItinerarioRepositorio itinerarios = new ItinerarioRepositorioArchivo(almacen);

        AutenticacionServicio autenticacion = new AutenticacionServicio(usuarios);
        AutorizacionServicio autorizacion = new AutorizacionServicio();
        FlotaServicio flota = new FlotaServicio(unidades, autorizacion);
        ItinerarioServicio itinerario = new ItinerarioServicio(itinerarios, unidades, conductores, autorizacion);

        // Los usuarios demo se crean una sola vez. Nunca se reemplazan los que ya estén guardados.
        cargarUsuariosDemo(autenticacion);
        cargarDatosDemo(unidades, conductores);

        System.out.println("Datos locales: " + almacen.getDirectorio());

        SwingUtilities.invokeLater(() -> {
            usarAparienciaDelSistema();
            new NavegadorSwing(autenticacion, autorizacion, flota, itinerario).mostrarLogin();
        });
    }

    /** Usuarios de demostración (uno por rol) para conservar el acceso inicial del sistema. */
    private static void cargarUsuariosDemo(AutenticacionServicio autenticacion) {
        registrarSiNoExiste(autenticacion, "admin", "Administrador del Sistema",
                "admin123", Rol.ADMINISTRADOR);
        registrarSiNoExiste(autenticacion, "estudiante", "Estudiante de Prueba",
                "estudiante123", Rol.ESTUDIANTE);
        registrarSiNoExiste(autenticacion, "empleado", "Empleado de Prueba",
                "empleado123", Rol.EMPLEADO);
        registrarSiNoExiste(autenticacion, "profesor", "Profesor de Prueba",
                "profesor123", Rol.PROFESOR);
        registrarSiNoExiste(autenticacion, "publico", "Público General de Prueba",
                "publico123", Rol.PUBLICO_GENERAL);
    }

    private static void registrarSiNoExiste(AutenticacionServicio autenticacion, String usuario,
                                            String nombre, String contrasena, Rol rol) {
        if (!autenticacion.existe(usuario)) {
            autenticacion.registrar(usuario, nombre, contrasena.toCharArray(), rol);
        }
    }

    /** Datos mínimos para que HU-003 pueda probar asociaciones desde una instalación nueva. */
    private static void cargarDatosDemo(UnidadRepositorio unidades, ConductorRepositorio conductores) {
        if (!unidades.existePorPlaca("ABC123")) {
            unidades.guardar(new Unidad("ABC123", "Mercedes Benz", 40, EstadoUnidad.ACTIVO));
        }
        if (!conductores.existePorLicencia("LIC-001")) {
            conductores.guardar(new Conductor("Carlos Pérez", "LIC-001"));
        }
    }

    private static void usarAparienciaDelSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            // Si falla, Swing usa su apariencia por defecto.
        }
    }
}
