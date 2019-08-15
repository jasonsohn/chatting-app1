package chat01;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class ClientSender extends Thread {
    Socket socket;
    BufferedOutputStream bufferedOutputStream;

    ClientSender(Socket socket) {
        this.socket = socket;
        try {
            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String type, String textLine) {
        try {
            bufferedOutputStream.write(messageSize(textLine));
            bufferedOutputStream.write(tlvMessage(type, textLine));
            bufferedOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private byte[] messageSize(String reqMsg) {
        try {

            byte[] reqValue = reqMsg.getBytes("UTF-8");

            byte[] reqLength =toBytes(reqValue.length);

            byte[] reqCount = toBytes((reqValue.length / 1024) + 1);

            byte[] req = new byte[8];
            System.arraycopy(reqCount, 0, req, 0, 4);
            System.arraycopy(reqLength, 0, req, 4, 4);
            return req;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] tlvMessage(String msgType, String reqMsg) {
        try {
            byte[] reqValue = reqMsg.getBytes("UTF-8"); // Msg 문자열을 바이트배열로 변환

            byte[] reqType = msgType.getBytes(); // type 문자열을 바이트배열로 변환

            byte[] reqLength = toBytes(reqValue.length); //

            byte[] req = new byte[reqValue.length + 8];
            //byte[] req = new byte[1024];
            System.arraycopy(reqType, 0, req, 0, 4);
            System.arraycopy(reqLength, 0, req, 4, 4);
            System.arraycopy(reqValue, 0, req, 8, reqValue.length);
            return req;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] toBytes(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) (i >> 24);
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i);
        return result;
    }

    public void run() {

    }
}
