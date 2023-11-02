public class Constants {

    public static final int DEFAULT_BUFFER_SIZE = 1016;
    public static final int HEADER_SIZE = 16;
    public static final int PAYLOAD_SIZE = DEFAULT_BUFFER_SIZE - HEADER_SIZE;

    public static final String GATEWAY_ADDRESS = "127.0.0.1";
    public static final int GATEWAY_PORT_UDP = 8888;
    public static final int GATEWAY_PORT_TCP = 8080;

    public static final int TRIES_UNTIL_TIMEOUT = 3;
    public static final int SERVER_TIMEOUT_MILISECONDS = 100;

    public static final String aux = System.getenv("SERVER_FILES_PATH");
    public static final String SERVER_FILES_PATH = aux != null ? aux : "..\\CC\\ServerFiles\\";

}
