package chat01;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;

public class TcpIpMultichatServer {

    HashMap clients;
    static final String BRACKET_START = "[11시스템]: [";
    static final String BRACKET_END = "]";
    static final String COLON = ":";
    static final String PRINT_MSG_INIT = "[시스템]: 서버가 시작되었습니다.";
    static final String PRINT_MSG_LOGIN = "에서 접속하였습니다.";
    static final int PORT_NUMBER = 7777;

    TcpIpMultichatServer() {
        clients = new HashMap();
        Collections.synchronizedMap(clients);
    }

    public void start() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(PORT_NUMBER);
            System.out.println(PRINT_MSG_INIT);

            while(true) {
                socket = serverSocket.accept();
                System.out.println(BRACKET_START + socket.getInetAddress() + COLON + socket.getPort() + BRACKET_END + PRINT_MSG_LOGIN);
                ServerReceiver thread = new ServerReceiver(socket, clients);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TcpIpMultichatServer().start();
    }
}
