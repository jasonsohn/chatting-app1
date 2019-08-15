package chat01;

import java.net.ConnectException;
import java.net.Socket;

public class TcpIpMultichatClient {
    static final String SERVER_IP = "127.0.0.1";
    static final String PRINT_MSG_CONNECT = "서버에 연결되었습니다.";
    static final int PORT_NUMBER = 7777;

    public static void main(String[] args) {

        try {
            Socket socket = new Socket(SERVER_IP, PORT_NUMBER);

            System.out.println(PRINT_MSG_CONNECT);
            new FrameChat(socket);

        } catch (ConnectException ce) {
            ce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
