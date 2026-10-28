package constants;

import java.awt.Color;

public interface IConstants {
    String SERVER_HOST = "172.18.136.2";       // host del servidor (máquina B, DESKTOP-JH0JGLG)
    int SERVER_PORT = 5050;                    // igual que el servidor
    int MATCH_CODE_LENGTH = 4;                 // largo del código de partida
    String MATCH_CODE_REGEX = "[A-Za-z0-9]{4}"; // validación local del código
    int NICKNAME_MIN_LENGTH = 3;               // igual que el servidor
    int NICKNAME_MAX_LENGTH = 16;              // igual que el servidor
    int MAP_COLUMNS = 4;                       // igual que el servidor
    int MAP_ROWS = 3;                          // igual que el servidor
    int ZONE_COUNT = MAP_COLUMNS * MAP_ROWS;   // zonas del mapa
    int ZONE_WIDTH = 800;                      // ancho de una zona (unidades del servidor)
    int ZONE_HEIGHT = 600;                     // alto de una zona (unidades del servidor)
    int SPECIES_COUNT = 7;                     // especies disponibles
    int MAX_PLAYER_ENERGY = 20;                // energía máxima de un animal del jugador
    long TICK_MS = 100;                        // cada cuánto llega un STATE_UPDATE
    double TELEPORT_THRESHOLD = 200;           // salto mayor a esto no se interpola (cambio de zona)
    int TARGET_FPS = 30;                       // cuadros por segundo del render
    int FRAME_DELAY_MS = 1000 / TARGET_FPS;    // delay del Timer de Swing
    int WINDOW_WIDTH = 1024;                   // ancho de la ventana
    int WINDOW_HEIGHT = 768;                   // alto de la ventana
    int HUD_HEIGHT = 60;                       // alto de la barra superior
    int SPRITE_SIZE = 32;                      // tamaño de un animal en pantalla
    int ENERGY_BAR_HEIGHT = 4;                 // alto de la barra de energía
    int CLICK_TOLERANCE = 20;                  // radio en píxeles para detectar clics
    int ATTACK_CLICK_TOLERANCE = 40;           // radio en píxeles para elegir un objetivo de ataque
    double ENCOUNTER_RADIUS = 40;              // igual que el servidor: distancia máxima para que ATTACK enganche
    double ATTACK_RANGE_MARGIN = 4;            // se pide el ataque un poco antes del borde (el snapshot llega atrasado)
    double CHASE_RESEND_DISTANCE = 30;         // cuánto se mueve el objetivo antes de volver a mandar a los perseguidores
    double ZONE_EDGE_MARGIN = 100;             // igual que el servidor: un destino más cerca del borde cruza a la zona vecina
    int MOVE_SNAP_DISTANCE = 80;              // un clic de movimiento fuera del camino se ajusta al camino más cercano a esta distancia
    String ASSETS_PATH = "/assets/svg/";       // carpeta de los SVG
    Color PLAYER_COLOR = new Color(46, 160, 67);    // verde
    Color OPPONENT_COLOR = new Color(214, 48, 49);  // rojo
    Color BOT_COLOR = new Color(120, 120, 120);     // gris
    Color SELECTION_COLOR = new Color(255, 214, 0); // amarillo
}
