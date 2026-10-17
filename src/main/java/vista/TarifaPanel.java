package vista;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

//
//Pantalla "Configurar Tarifa". Se dibuja sobre ConfTarifaPantalla.png con las medidas de la maqueta.Aquí vive solo el diseño; la lógica está en ConfiguracionTarifaVentana y TarifaControlador.

public class TarifaPanel extends PanelFormularioTabla {
    private static final long serialVersionUID = 1L;

    private static final double ETIQUETA_DERECHA = 565;
    private static final double[] COMBO_RUTA = {615, 178, 652, 92};
    private static final double[] CAMPO_TARIFA = {615, 283, 652, 92};
    private static final double[] CAMPO_ESTUDIANTE = {615, 387, 652, 92};
    private static final double[] CAMPO_EMPLEADO = {615, 492, 652, 92};
    private static final double BOTONES_X = 1342;
    private static final double BOTONES_ANCHO = 470;
    private static final double BOTON_ELIMINAR_Y = 256;
    private static final double BOTON_GUARDAR_Y = 415;

    private final JComboBox<String> comboRuta =
            new JComboBox<>(new String[]{"Seleccionar...", "Urbana", "Extraurbana"});
    private final JTextField txtMontoTarifaBase = new CampoFondoImagen();
    private final JTextField txtPctEstudiante = new CampoFondoImagen();
    private final JTextField txtPctEmpleado = new CampoFondoImagen();
    private final BotonImagen botonEliminar = new BotonImagen("/imagenes/EliminarTarifa1.png",
            "/imagenes/EliminarTarifa2.png", "Eliminar Tarifa");
    private final BotonImagen botonGuardar = new BotonImagen("/imagenes/GuardarActu1.png",
            "/imagenes/GuardarActu2.png", "Guardar / Actualizar");
    private final double proporcionEliminar = proporcion("/imagenes/EliminarTarifa1.png");
    private final double proporcionGuardar = proporcion("/imagenes/GuardarActu1.png");

    public TarifaPanel() {
        super("/imagenes/ConfTarifaPantalla.png",
                new String[]{"Tipo de usuario", "Porcentaje de Subsidio/ Descuento", "Monto a Pagar"},
                new double[]{440, 700, 544}, // suman 1684 (ancho de las filas, sin barra)
                false, 36, 30);
        EstiloCombo.aplicar(comboRuta, true, false, null); // misma lista desplegable del resto del proyecto
        add(comboRuta);
        add(txtMontoTarifaBase);
        add(txtPctEstudiante);
        add(txtPctEmpleado);
        add(botonEliminar);
                add(botonGuardar);
        }

        public JComboBox<String> getComboRuta() { return comboRuta; }
        public JTextField getTxtMontoTarifaBase() { return txtMontoTarifaBase; }
        public JTextField getTxtPctEstudiante() { return txtPctEstudiante; }
        public JTextField getTxtPctEmpleado() { return txtPctEmpleado; }
        public BotonImagen getBotonEliminar() { return botonEliminar; }
        public BotonImagen getBotonGuardar() { return botonGuardar; }

        @Override
        protected void colocarFormulario() {
                Font fuente = new Font("Arial", Font.PLAIN, px(38));
                EmptyBorder margen = new EmptyBorder(0, px(40), 0, px(40));

                comboRuta.setFont(fuente);
                comboRuta.setBounds(rect(COMBO_RUTA[0], COMBO_RUTA[1], COMBO_RUTA[2], COMBO_RUTA[3]));
                colocarCampo(txtMontoTarifaBase, CAMPO_TARIFA, fuente, margen);
                colocarCampo(txtPctEstudiante, CAMPO_ESTUDIANTE, fuente, margen);
                colocarCampo(txtPctEmpleado, CAMPO_EMPLEADO, fuente, margen);

                botonEliminar.setBounds(rectBoton(BOTONES_X, BOTON_ELIMINAR_Y, BOTONES_ANCHO, proporcionEliminar));
                botonGuardar.setBounds(rectBoton(BOTONES_X, BOTON_GUARDAR_Y, BOTONES_ANCHO, proporcionGuardar));
        }

        private void colocarCampo(JTextField campo, double[] medidas, Font fuente, EmptyBorder margen) {
                campo.setFont(fuente);
                campo.setBorder(margen);
                campo.setBounds(rect(medidas[0], medidas[1], medidas[2], medidas[3]));
        }
//
        @Override
        protected void dibujarFormulario(Graphics2D g2) {
                dibujarTexto(g2, new String[]{"Tipo de Ruta"}, ETIQUETA_DERECHA, 224, 48, true, Color.BLACK);
                dibujarTexto(g2, new String[]{"Tarifa Base Municipal en Bs"}, ETIQUETA_DERECHA, 329, 36, true, Color.BLACK);
                dibujarTexto(g2, new String[]{"% Subsidio Estudiante", "(10% - 20%)"}, ETIQUETA_DERECHA, 433, 36, true,
                        Color.BLACK);
                dibujarTexto(g2, new String[]{"% Subsidio Empleado/Profesor", "(30% - 60%)"}, ETIQUETA_DERECHA, 538, 36,
                        true, Color.BLACK);
        }
        }
