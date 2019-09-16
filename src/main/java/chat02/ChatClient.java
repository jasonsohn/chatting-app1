package chat02;

import java.net.ConnectException;
import java.net.Socket;

public class ChatClient {
    static final String PRINT_MSG_CONNECT = "서버에 연결되었습니다.";


    public static void main(String[] args) {
        try {
            Socket socket = new Socket(Const.SERVER_IP, Const.PORT_NUMBER);

            System.out.println(PRINT_MSG_CONNECT);
            new FrameChat(socket);

        } catch (ConnectException ce) {
            ce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
