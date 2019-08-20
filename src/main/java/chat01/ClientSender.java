package chat01;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class ClientSender extends Thread {
    Socket socket;
    BufferedOutputStream bufferedOutputStream;
    static final int PACKET_SIZE = 10;

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
//            bufferedOutputStream.write(messageSize(textLine));
//            bufferedOutputStream.write(tlvMessage(type, textLine));
//            bufferedOutputStream.flush();

            byte[] req = tlvMessageWithSize(type, textLine);
            byte[] countMsg = new byte[4];
            System.arraycopy(req, 8, countMsg, 0, 4);
            int arrayCount = byteArrayToInt(countMsg);
            System.out.println("클라이언트쪽 메세지 패킷 totalCount : " + arrayCount);
            for (int i = 0; i < arrayCount; i++) {
                byte[] resultReq = new byte[16 + PACKET_SIZE]; // type, length, currentCount, totalCount, Msg(PACKET_SIZE)
                byte[] currentCount = toBytes(i + 1);
                System.arraycopy(req, 0, resultReq, 0, 8);          // type, length
                System.arraycopy(currentCount, 0, resultReq, 8, 4); // 현재 메세지
                System.arraycopy(countMsg, 0, resultReq, 12, 4); // 현재 메세지
                int position = 12 + i * PACKET_SIZE;
                System.arraycopy(req, position, resultReq, 16, req.length - (position)); // 메세지
                System.out.println("클라이언트쪽 메세지 패킷 currentCount : " + (i + 1) + " / " + arrayCount);
                bufferedOutputStream.write(resultReq);
                bufferedOutputStream.flush();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void sendPacketOnce() {

    }

    private void sendPacketTwice() {

    }

    private byte[] tlvMessageWithSize(String msgType, String reqMsg) {
        try {
            System.out.println("메세지의 String 길이 : " + reqMsg.length());
            byte[] reqValue = reqMsg.getBytes("UTF-8"); // Msg 문자열을 바이트배열로 변환
            System.out.println("메세지의 byte[] 길이 : " + reqValue.length);

            byte[] reqType = msgType.getBytes(); // type 문자열을 바이트배열로 변환

            byte[] reqLength = toBytes(reqValue.length);

            int messageCount = (int) Math.ceil((double) reqValue.length / PACKET_SIZE); // 패킷 수
            byte[] totalCount = toBytes(messageCount);

            byte[] req = new byte[reqValue.length + 12];

            System.arraycopy(reqType, 0, req, 0, 4);
            System.arraycopy(reqLength, 0, req, 4, 4);
            System.arraycopy(totalCount, 0, req, 8, 4);
            System.arraycopy(reqValue, 0, req, 12, reqValue.length);
            return req;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] messageSize(String reqMsg) {
        try {
            byte[] reqValue = reqMsg.getBytes("UTF-8");

//            byte[] reqLength =toBytes(reqValue.length);

//            byte[] reqCount = toBytes((8 + reqValue.length / 10) + 1);
            int messageCount = (int) Math.ceil((double) (8 + reqValue.length) / PACKET_SIZE); // 패킷 수
            byte[] reqCount = toBytes(messageCount); // TYPE, LENGTH 길이 포함

//            byte[] req = new byte[8];
            byte[] req = new byte[4];
            System.arraycopy(reqCount, 0, req, 0, 4);
//            System.arraycopy(reqLength, 0, req, 4, 4);
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

            byte[] reqLength = toBytes(reqValue.length);

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

    public int byteArrayToInt(byte[] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }

}
