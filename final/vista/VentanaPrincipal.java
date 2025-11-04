package vista;

import java.awt.*; //Importamos la clase que valida la cadena FEN
import javax.swing.*; //Librería para los componentes gráficos (botones,textos, etc.)
import modelo.FENParser; //Librería para manejar colores, fuentes y disposición de paneles

public class VentanaPrincipal extends JFrame {
    // Campo de texto donde el usuario escribe la cadena FEn
    private JTextField campoFEN;

    //Botón para validar y mostrar el tablero
    private JButton botonValidar;

    //Panel donde se dibuja el tablero de ajedrez
    private JPanel tableroPanel;

    private JButton botonLimpiar;

    //tablero con las etiquetas
    private JPanel tableroConEtiquetas;

    //Etiqueta para mostrar el turno actual
    private JLabel etiquetaTurno;

    //Variable para recordar el último turno mostrado
    private String ultimoTurno = ""; 

    //Diccionario para convertir letras FEN en simbolos Unicode de ajedrez
    private static final java.util.Map<Character, String> piezasUnicode = new java.util.HashMap<>();

    static {
        piezasUnicode.put('K', "♔"); // Rey blanco
        piezasUnicode.put('Q', "♕"); // Reina blanca
        piezasUnicode.put('R', "♖"); // Torre blanca
        piezasUnicode.put('B', "♗"); // Alfil blanco
        piezasUnicode.put('N', "♘"); // Caballo blanco
        piezasUnicode.put('P', "♙"); // Peón blanco

        piezasUnicode.put('k', "♚"); // Rey negro
        piezasUnicode.put('q', "♛"); // Reina negra
        piezasUnicode.put('r', "♜"); // Torre negra
        piezasUnicode.put('b', "♝"); // Alfil negro
        piezasUnicode.put('n', "♞"); // Caballo negro
        piezasUnicode.put('p', "♟"); // Peón negro
    }

    public VentanaPrincipal() {
        //Título de la ventana
        setTitle("Ajedrez");

        //permite que el usuario cambie el tamaño de la ventana
        setResizable(true);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        //Componentes
        campoFEN = new JTextField();
        botonValidar = new JButton("Validar y Mostrar");
        botonLimpiar = new JButton("Limpiar tablero");

        // 🔹 Panel principal vacío (sin tablero ni coordenadas al inicio)
        tableroPanel = new JPanel(new GridLayout(8, 8)); // tablero 8x8
        tableroConEtiquetas = new JPanel(new BorderLayout());
        add(tableroConEtiquetas, BorderLayout.CENTER); // se añade vacío

        //Panel superior con el texto y el botón
        JPanel superior = new JPanel(new BorderLayout(10, 10));
        superior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        superior.add(new JLabel("Ingrese cadena FEN:"), BorderLayout.WEST);
        superior.add(campoFEN, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.add(botonValidar);
        panelBotones.add(botonLimpiar);
        superior.add(panelBotones, BorderLayout.EAST);
        
        //Etiqueta que mostrará el turno actual
        etiquetaTurno = new JLabel("Turno: (sin definir)");
        etiquetaTurno.setFont(new Font("SansSerif", Font.BOLD, 18));
        etiquetaTurno.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaTurno.setOpaque(true); // permite cambiar el color de fondo
        etiquetaTurno.setBackground(Color.LIGHT_GRAY);

        //Se añade debajo del campo FEN
        superior.add(etiquetaTurno, BorderLayout.SOUTH);

        //paneles de la ventana
        add(superior, BorderLayout.NORTH);

        //Acción del botón: validar la cadena
        botonValidar.addActionListener(e -> validarYMostrar());
        botonLimpiar.addActionListener(e -> limpiarTablero());

        setVisible(true);

        //Mensaje inicial
        JOptionPane.showMessageDialog(this,
                "Bienvenida al tablero de ajedrez.\n" + "Ingresa una cadena FEN y presiona 'Validar y Mostrar'.",
                "Instrucciones", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Valida la cadena ingresada y, si es correcta, dibuja el tablero.
     */
    private void validarYMostrar() {
        String fen = campoFEN.getText();
        FENParser parser = new FENParser(fen);

        // Validamos la cadena FEN
        if (!parser.validar()) {
            JOptionPane.showMessageDialog(this,
                "Cadena FEN inválida.\nRevisa la consola para más detalles.",
                "Error de la validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] partes = parser.getPartes();
        String parteTablero = partes[0];
        String turnoActual = partes[1]; // 'w' o 'b'

        if (turnoActual.equals(ultimoTurno)) {
            JOptionPane.showMessageDialog(this,
                "No se puede repetir turno.\nDebe jugar el otro color.",
                "Turno inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ultimoTurno = turnoActual;
        if (turnoActual.equals("w")) {
            etiquetaTurno.setText("Turno: Blancas ♙");
            etiquetaTurno.setForeground(Color.BLACK);
            etiquetaTurno.setBackground(Color.WHITE);
        } else {
            etiquetaTurno.setText("Turno: Negras ♟");
            etiquetaTurno.setForeground(Color.WHITE);
            etiquetaTurno.setBackground(new Color(60, 60, 60));
        }

        // Si la cadena es válida, se pinta el tablero
        pintarTablero(parteTablero);
        mostrarTableroConEtiquetas();
    }

    /**
     * Dibuja el tablero según la cadena FEN validada.
     * Cada número representa casillas vacías y cada letra una pieza.
     */
    private void pintarTablero(String parteTablero) {
        tableroPanel.removeAll();
        String[] filas = parteTablero.split("/");

        boolean colorClaro; 

        for (int i = 0; i < filas.length; i++) {
            String fila = filas[i];
            colorClaro = (i % 2 == 0);

            for (char c : fila.toCharArray()) {
                if (Character.isDigit(c)) {
                    int vacias = Character.getNumericValue(c);
                    for (int j = 0; j < vacias; j++) {
                        tableroPanel.add(crearCasilla(" ", colorClaro));
                        colorClaro = !colorClaro; 
                    }
                } else {
                    String pieza = piezasUnicode.getOrDefault(c, String.valueOf(c));
                    tableroPanel.add(crearCasilla(pieza, colorClaro));
                    colorClaro = !colorClaro;
                }
            }
        }

        tableroPanel.revalidate();
        tableroPanel.repaint();
    }

    private void mostrarTableroConEtiquetas() {
        tableroConEtiquetas.removeAll();

        // Etiquetas superiores (A–H)
        JPanel etiquetasSuperiores = new JPanel(new GridLayout(1, 8));
        for (char c = 'A'; c <= 'H'; c++) {
            JLabel etiqueta = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            etiqueta.setFont(new Font("SansSerif", Font.BOLD, 16));
            etiquetasSuperiores.add(etiqueta);
        }

        // Etiquetas inferiores (A–H)
        JPanel etiquetasInferiores = new JPanel(new GridLayout(1, 8));
        for (char c = 'A'; c <= 'H'; c++) {
            JLabel etiqueta = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            etiqueta.setFont(new Font("SansSerif", Font.BOLD, 16));
            etiquetasInferiores.add(etiqueta);
        }

        // Etiquetas izquierda (8–1)
        JPanel etiquetasIzquierda = new JPanel(new GridLayout(8, 1));
        for (int i = 8; i >= 1; i--) {
            JLabel etiqueta = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            etiqueta.setFont(new Font("SansSerif", Font.BOLD, 16));
            etiquetasIzquierda.add(etiqueta);
        }

        // Etiquetas derecha (8–1)
        JPanel etiquetasDerecha = new JPanel(new GridLayout(8, 1));
        for (int i = 8; i >= 1; i--) {
            JLabel etiqueta = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            etiqueta.setFont(new Font("SansSerif", Font.BOLD, 16));
            etiquetasDerecha.add(etiqueta);
        }

        // Añadir las etiquetas alrededor del tablero
        tableroConEtiquetas.add(etiquetasSuperiores, BorderLayout.NORTH);
        tableroConEtiquetas.add(etiquetasInferiores, BorderLayout.SOUTH);
        tableroConEtiquetas.add(etiquetasIzquierda, BorderLayout.WEST);
        tableroConEtiquetas.add(etiquetasDerecha, BorderLayout.EAST);
        tableroConEtiquetas.add(tableroPanel, BorderLayout.CENTER);

        tableroConEtiquetas.revalidate();
        tableroConEtiquetas.repaint();
    }

    /**
     * Limpia solo las piezas del tablero (manteniendo los colores y coordenadas).
     */
    private void limpiarTablero() {
        campoFEN.setText(""); // Limpia el campo de texto

        // Elimina solo las piezas, pero deja el tablero y coordenadas
        tableroPanel.removeAll();
        tableroPanel.setLayout(new GridLayout(8, 8));

        for (int fila = 0; fila < 8; fila++) {
            boolean colorClaro = (fila % 2 == 0);
            for (int col = 0; col < 8; col++) {
                tableroPanel.add(crearCasilla(" ", colorClaro)); // Casilla vacía
                colorClaro = !colorClaro;
            }
        }

        tableroPanel.revalidate();
        tableroPanel.repaint();

        // Reinicia el turno
        ultimoTurno = "";
        etiquetaTurno.setText("Turno: (sin definir)");
        etiquetaTurno.setForeground(Color.BLACK);
        etiquetaTurno.setBackground(Color.LIGHT_GRAY);
    }

    /**
     * Crea una casilla individual del tablero.
     * @param texto símbolo de pieza (o vacío)
     * @param claro color de fondo (claro u oscuro)
     */
    private JLabel crearCasilla(String texto, boolean claro) {
        JLabel casilla = new JLabel(texto, SwingConstants.CENTER);
        casilla.setOpaque(true);
        casilla.setFont(new Font("Serif", Font.BOLD, 36));
        casilla.setBackground(claro ? new Color(240, 217, 181) : new Color(181, 136, 99));
        casilla.setForeground(Color.BLACK);
        return casilla;
    }
}
