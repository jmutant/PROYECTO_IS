package ve.ucv.campusexpress.controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ve.ucv.campusexpress.controlador.NavegadorFalso.Pantalla;

class PrincipalControladorTest {

    @Test
    @DisplayName("Cerrar sesión cierra la pantalla principal y vuelve al login")
    void cerrarSesionVuelveAlLogin() {
        PrincipalVistaFalsa vista = new PrincipalVistaFalsa();
        NavegadorFalso navegador = new NavegadorFalso();
        new PrincipalControlador(vista, navegador);

        vista.presionarCerrarSesion();

        assertEquals(Pantalla.LOGIN, navegador.pantalla);
        assertTrue(vista.cerrada);
    }
}
