package modelo;

/**
 * Clase encargada de analizar y validar cadenas FEN 
 * Se asegura de que la estructura del texto cumpla con las reglas del formato FEN.
 */

public class FENParser {
    private String fen;
    private String [] partes;

    /**
     * Constructor que recibe la cadena FEN y la separa por espacios.
     * Cada parte representa un campo distinto del formato FEN.
     */

    public FENParser (String fen) {
        this.fen = fen.trim();
        this.partes = fen.split(" ");
    }
    /**
     * Método principal de validación.
     * Comprueba que la cadena tenga la estructura y caracteres válidos.
     */

    public boolean validar() {
        //Debe haber exactamente 6 secciones separadas por espacios.
        if (partes.length !=6){
            System.out.println("Error: la cadena FEN debe tener 6 partes.");
            return false;

        }

        //Se valida cada parte individualmente.
        if (!validarTablero(partes[0])) return false; //Estructura del tablero
        if (!partes[1].matches("[wb]")) { // Turno
            System.out.println ("Error: el turno debe ser 'w' o 'b'.");
            return false;
        }

        if (!partes[2].matches("[-KQkq]+")) { //Enroque
            System.out.println("Error: formato de enroque inválido");
            return false;
        }

        if (!partes[3].matches("-|[a-h][1-8]")) { 
            System.out.println("Error: casilla al paso inválida.");
            return false;
        }

        if (!partes[4].matches ("\\d+") || !partes[5].matches("\\d+")) { //contadores 
            System.out.println("Error: los contadores deben ser numéricos.");
            return false;
        }
    

        return true;
    }

        /**
         * Valida que el tablero tenga exactamente 8 filas y 8 clumnas.
         * Cada fila puede contener piezas o numeros que representan casillas vacías.
         */

        private boolean validarTablero(String tablero) {
            String[] filas = tablero.split("/");
            if (filas.length !=8) {
                System.out.println("Error: el tablero debe tener 8 filas.");
                return false;
            }
        

        for (String fila: filas) {
            int conteo = 0;
            for (char c : fila.toCharArray()) {
                if (Character.isDigit(c)) conteo += Character.getNumericValue(c);
                else if ("prnbqkPRNBQK".indexOf(c) !=-1) conteo++;
                else {
                    System.out.println("Error: carácter no válido: " + c);
                    return false;
                }

            }
            if (conteo != 8 ) {
                System.out.println("Error: una fila no tiene 8 casillas exactas.");
                return false;
            }
        }
        return true;
    }
    // Devuelve las partes separadas de la cadena FEN.
    public String[] getPartes() {
        return partes;
    }
}
