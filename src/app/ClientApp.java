package app;

public class ClientApp {

    public static void main(String[] args) {
        // 1. SvgCache.getInstance().preload()
        // 2. crear GameModel y MessageDispatcher; registrar los 7 handlers
        // 3. ServerConnection.getInstance().connect(dispatcher)
        // 4. crear LobbyController, GameController y MainWindow (en SwingUtilities.invokeLater);
        //    MainWindow y GamePanel se suscriben al modelo
    }
}
