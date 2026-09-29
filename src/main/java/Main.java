import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import controlador.NavegadorSwing;
import modelo.AlmacenDatosLocal;
import modelo.AutenticacionServicio;
import modelo.AutorizacionServicio;
import modelo.Conductor;
import modelo.ConductorRepositorio;
import modelo.ConductorRepositorioArchivo;
import modelo.EstadoUnidad;
import modelo.FlotaServicio;
import modelo.ItinerarioRepositorio;
import modelo.ItinerarioRepositorioArchivo;
import modelo.ItinerarioServicio;
import modelo.Unidad;
import modelo.UnidadRepositorio;
import modelo.UnidadRepositorioArchivo;
import modelo.UsuarioRepositorio;
import modelo.UsuarioRepositorioArchivo;


public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        
        // Es el inicializador del archivo local que almacen los datos de usuario
        AlmacenDatosLocal almacen = AlmacenDatosLocal.porDefecto();
        
        // buscar el archivo UsuariosData.txttttt
        Path rutaUsuarios = buscarArchivoUsuarios(almacen);
        UsuarioRepositorio usuarios = new UsuarioRepositorioArchivo(rutaUsuarios);

        UnidadRepositorio unidades = new UnidadRepositorioArchivo(almacen);
        ConductorRepositorio conductores = new ConductorRepositorioArchivo(almacen);
        ItinerarioRepositorio itinerarios = new ItinerarioRepositorioArchivo(almacen);

        AutenticacionServicio autenticacion = new AutenticacionServicio(usuarios);
        AutorizacionServicio autorizacion = new AutorizacionServicio();
        FlotaServicio flota = new FlotaServicio(unidades, autorizacion);
        ItinerarioServicio itinerario = new ItinerarioServicio(itinerarios, unidades, conductores, autorizacion);

        //cargarDatosDemo(unidades, conductores);

        System.out.println("Datos locales: " + almacen.getDirectorio());

        SwingUtilities.invokeLater(() -> {
            usarAparienciaDelSistema();
            new NavegadorSwing(autenticacion, autorizacion, flota, itinerario).mostrarLogin();
        });
    }

    //Busqueda y creacion del archivo UsuariosData.txt en caso de no existir o estar en una ruta distinta
    private static Path buscarArchivoUsuarios(AlmacenDatosLocal almacen) {
        // Opción 1: En la carpeta raíz del proyecto (muy común al ejecutar el .jar)
        Path enRaiz = Path.of("UsuariosData.txt");
        if (Files.exists(enRaiz)) {
            return enRaiz;
        }
        
        // Opción 2: Dentro de la carpeta de datos temporales del sistema (.campus-express/datos/)
        Path enAlmacen = almacen.getDirectorio().resolve("UsuariosData.txt");
        if (Files.exists(enAlmacen)) {
            return enAlmacen;
        }
        
        Path enModelo = Path.of("src", "main", "java", "modelo", "UsuariosData.txt");
        if (Files.exists(enModelo)) {
            return enModelo;
        }
        //Si no consigue el archivo o hay conflicto, lo crea alli 
        return enAlmacen;
    }

    /*Datos mínimos para que HU-003 pueda probar asociaciones desde una instalación nueva.
    private static void cargarDatosDemo(UnidadRepositorio unidades, ConductorRepositorio conductores) {
        if (!unidades.existePorPlaca("ABC123")) {
            unidades.guardar(new Unidad("ABC123", "Mercedes Benz", 40, EstadoUnidad.ACTIVO));
        }
        if (!conductores.existePorLicencia("LIC-001")) {
            conductores.guardar(new Conductor("Carlos Pérez", "LIC-001"));
        }
    }*/

    private static void usarAparienciaDelSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            // Si falla, Swing usa su apariencia por defecto.
        }
    }
}

/* public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        
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

        cargarDatosDemo(unidades, conductores);

        System.out.println("Datos locales: " + almacen.getDirectorio());

        SwingUtilities.invokeLater(() -> {
            usarAparienciaDelSistema();
            new NavegadorSwing(autenticacion, autorizacion, flota, itinerario).mostrarLogin();
        });
    }



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
} */
