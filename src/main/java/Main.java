import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import controlador.NavegadorSwing;
import modelo.AlmacenDatosLocal;
import modelo.AutenticacionServicio;
import modelo.AutorizacionServicio;
import modelo.ConductorRepositorio;
import modelo.ConductorRepositorioArchivo;
import modelo.FlotaServicio;
import modelo.ItinerarioRepositorio;
import modelo.ItinerarioRepositorioArchivo;
import modelo.ItinerarioServicio;
import modelo.UnidadRepositorio;
import modelo.UnidadRepositorioArchivo;
import modelo.UsuarioRepositorio;
import modelo.UsuarioRepositorioArchivo;

<<<<<<< HEAD

=======
>>>>>>> 68fb3b463b634071a7ff8b7e1378599cde9ee71c
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        // Todos los datos persistentes usan una única carpeta: data/.
        // Esto evita que el login lea un archivo diferente según cómo se ejecute el programa.
        AlmacenDatosLocal almacen = AlmacenDatosLocal.porDefecto();

        UsuarioRepositorio usuarios = new UsuarioRepositorioArchivo(almacen);
        UnidadRepositorio unidades = new UnidadRepositorioArchivo(almacen);
        ConductorRepositorio conductores = new ConductorRepositorioArchivo(almacen);
        ItinerarioRepositorio itinerarios = new ItinerarioRepositorioArchivo(almacen);

        AutenticacionServicio autenticacion = new AutenticacionServicio(usuarios);
        AutorizacionServicio autorizacion = new AutorizacionServicio();
        FlotaServicio flota = new FlotaServicio(unidades, autorizacion);
        ItinerarioServicio itinerario = new ItinerarioServicio(
                itinerarios, unidades, conductores, autorizacion);

        System.out.println("Datos persistentes: " + almacen.getDirectorio().toAbsolutePath());

        SwingUtilities.invokeLater(() -> {
            usarAparienciaDelSistema();
            new NavegadorSwing(autenticacion, autorizacion, flota, itinerario).mostrarLogin();
        });
    }

    private static void usarAparienciaDelSistema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            // Si falla, Swing usa su apariencia por defecto.
        }
    }
}
