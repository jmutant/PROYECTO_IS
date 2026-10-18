package vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

//Pantalla "Registrar Conductor". Se dibuja sobre RegisConductorPantalla.png con las medidas de la maqueta
//
public class ConductoresPanel extends PanelFormularioTabla {
    private static final long serialVersionUID = 1L;

    private static final double ETIQUETA_DERECHA = 555;
    private static final double[] CAMPO_NOMBRE = {590, 170, 1170, 90};
    private static final double[] CAMPO_CEDULA = {590, 270, 651, 90};
    private static final double[] CAMPO_LICENCIA = {590, 370, 651, 90};
    private static final double AYUDA_X = 1288;
    private static final double BOTONES_ANCHO = 470;
    private static final double BOTONES_Y = 477;
    private static final double BOTON_REGISTRAR_X = 515;
    private static final double BOTON_ELIMINAR_X = 1002;
    private static final Color GRIS_AYUDA = new Color(140, 140, 140);

    private final JTextField txtNombreCompleto = new CampoFondoImagen("/imagenes/BarButton.png");
    private final JTextField txtCedula = new CampoFondoImagen();
    private final JTextField txtNumeroLicencia = new CampoFondoImagen();
    private final BotonImagen botonRegistrar = new BotonImagen("/imagenes/RegisConductor1.png",
            "/imagenes/RegisConductor2.png", "Registrar Conductor");
    private final BotonImagen botonEliminar = new BotonImagen("/imagenes/Erase1.png",
            "/imagenes/Erase2.png", "Eliminar");
    private final double proporcionRegistrar = proporcion("/imagenes/RegisConductor1.png");
    private final double proporcionEliminar = proporcion("/imagenes/Erase1.png");

    public ConductoresPanel() {
        super("/imagenes/RegisConductorPantalla.png",
                new String[]{"Nombre Completo", "Cédula", "Licencia"},
                new double[]{554.5, 501, 540.5}, // suman 1596 (ancho de las filas junto a la barra)
                true, 44, 27);
        setTitulo(new TituloFlotante("/imagenes/GESTIONCONDUCTORTEXTO.png", 15, 8, 1673, 151));
        add(txtNombreCompleto);
        add(txtCedula);
        add(txtNumeroLicencia);
        add(botonRegistrar);
        add(botonEliminar);
    }

    public JTextField getTxtNombreCompleto() { return txtNombreCompleto; }
    public JTextField getTxtCedula() { return txtCedula; }
    public JTextField getTxtNumeroLicencia() { return txtNumeroLicencia; }
    public BotonImagen getBotonRegistrar() { return botonRegistrar; }
    public BotonImagen getBotonEliminar() { return botonEliminar; }
//
    @Override
    protected void colocarFormulario() {
        Font fuenteCampo = new Font("Arial", Font.PLAIN, px(38));
        EmptyBorder margen = new EmptyBorder(0, px(40), 0, px(40));
        colocarCampo(txtNombreCompleto, CAMPO_NOMBRE, fuenteCampo, margen);
        colocarCampo(txtCedula, CAMPO_CEDULA, fuenteCampo, margen);
        colocarCampo(txtNumeroLicencia, CAMPO_LICENCIA, fuenteCampo, margen);

        botonRegistrar.setBounds(rectBoton(BOTON_REGISTRAR_X, BOTONES_Y, BOTONES_ANCHO, proporcionRegistrar));
        botonEliminar.setBounds(rectBoton(BOTON_ELIMINAR_X, BOTONES_Y, BOTONES_ANCHO, proporcionEliminar));
    }

    private void colocarCampo(JTextField campo, double[] medidas, Font fuente, EmptyBorder margen) {
        campo.setFont(fuente);
        campo.setBorder(margen);
        Rectangle r = rect(medidas[0], medidas[1], medidas[2], medidas[3]);
        campo.setBounds(r);
    }

    @Override
    protected void dibujarFormulario(Graphics2D g2) {
        dibujarTexto(g2, new String[]{"Nombre Completo"}, ETIQUETA_DERECHA, 215, 48, true, Color.BLACK);
        dibujarTexto(g2, new String[]{"Cédula"}, ETIQUETA_DERECHA, 315, 48, true, Color.BLACK);
        dibujarTexto(g2, new String[]{"N° de Licencia"}, ETIQUETA_DERECHA, 415, 48, true, Color.BLACK);
        dibujarTexto(g2, new String[]{"Ejemplo: V12345678"}, AYUDA_X, 316, 40, false, GRIS_AYUDA);
        dibujarTexto(g2, new String[]{"10 Dígitos exactos"}, AYUDA_X, 417, 40, false, GRIS_AYUDA);
    }
}




















// :333333333