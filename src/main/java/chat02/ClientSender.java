package chat02;

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

    public void sendMsg(String msgType, String textLine, String userId) {
        sendPacketOnce(msgType, textLine, userId);
    }

    // type(4), length(4), totalCnt(4), idLength(4), idValue(10), reqValue
    private void sendPacketOnce(String msgType, String textLine, String userId) {
        try {

            byte[] req = tlvMessageWithSize(msgType, textLine, userId);
            byte[] totalCount = new byte[4];
            System.arraycopy(req, 8, totalCount, 0, 4);
            int messagePacketCount = byteArrayToInt(totalCount);
            int restReqValueLength = (req.length - 26) % Const.PACKET_SIZE;
            System.out.println("클라이언트쪽 메세지 패킷 totalCount : " + messagePacketCount);
            for (int i = 0; i < messagePacketCount; i++) {
                // type(4), length(4), currentCount(4), totalCount(4), idLength(4), idValue(10), Msg(PACKET_SIZE)
                byte[] resultReq = new byte[30 + Const.PACKET_SIZE];
                byte[] currentCount = toBytes(i + 1);
                System.arraycopy(req, 0, resultReq, 0, 8);          // type, length
                System.arraycopy(currentCount, 0, resultReq, 8, 4); // 현재 패킷 횟수
                System.arraycopy(req, 8, resultReq, 12, 18); // 총 패킷 횟수, ID 길이, 값

                int position = 26 + i * Const.PACKET_SIZE;

                if (i == messagePacketCount - 1) { // 마지막 패킷일경우
                    System.arraycopy(req, position, resultReq, 30, restReqValueLength); // 메세지
                } else {
                    System.arraycopy(req, position, resultReq, 30, Const.PACKET_SIZE); // 메세지
                }

                System.out.println("클라이언트쪽 메세지 패킷 currentCount : " + (i + 1) + " / " + messagePacketCount);
                bufferedOutputStream.write(resultReq);
                bufferedOutputStream.flush();
            }
        } catch (IOException e) {

        }
    }

    // type(4), length(4), totalCnt(4), idLength(4), idValue(10), reqValue
    private byte[] tlvMessageWithSize(String msgType, String reqMsg, String userId) {
        try {
            System.out.println("메세지의 String 길이 : " + reqMsg.length());
            byte[] reqValue = reqMsg.getBytes("UTF-8"); // Msg 문자열을 바이트배열로 변환
            byte[] idValue = new byte[10];
            byte[] idValueTemp = userId.getBytes("UTF-8");
            System.arraycopy(idValueTemp, 0, idValue, 0, idValueTemp.length);
            System.out.println("메세지의 byte[] 길이 : " + reqValue.length);
            byte[] reqType = msgType.getBytes(); // type 문자열을 바이트배열로 변환
            byte[] reqLength = toBytes(reqValue.length);
            byte[] idLength = toBytes(idValueTemp.length);
            int messagePacketCount = (int) Math.ceil((double) reqValue.length / Const.PACKET_SIZE); // 패킷 수
            byte[] totalCount = toBytes(messagePacketCount);
            byte[] req = new byte[reqValue.length + 26];

            System.arraycopy(reqType, 0, req, 0, 4);
            System.arraycopy(reqLength, 0, req, 4, 4);
            System.arraycopy(totalCount, 0, req, 8, 4);
            System.arraycopy(idLength, 0, req, 12, 4);
            System.arraycopy(idValue, 0, req, 16, 10);
            System.arraycopy(reqValue, 0, req, 26, reqValue.length);
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
