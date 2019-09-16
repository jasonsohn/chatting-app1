package chat02;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientReceiver extends Thread {

    Socket socket;
    BufferedInputStream bufferedInputStream;
    FrameChat frameChat;
    FrameId frameId;
    static final String ENTER = "\n";
    static final String ACCESS_CODE = "DENI";
    static final String MESSAGE_CODE = "MSSG";
    static final String SYSTEM_CODE = "SYSM";
    static final String SYSTEM_CODE1 = "SYSL";
    static final String EMOJI_CODE = "EMOJ";

    boolean loginAccess = false;

    ClientReceiver(Socket socket, FrameChat frameChat, FrameId frameId) {
        this.frameChat = frameChat;
        this.frameId = frameId;
        this.socket = socket;

        try {
            bufferedInputStream = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        byte[] buffer;
        ArrayList<byte[]> tlvList;
        String type;
        String value;
        String length;
        int count = 0;
        while (bufferedInputStream != null) {
            try {

//                buffer = receiveStreamDataToArrayWithSize1();
                buffer = receiveStreamDataToArrayWithSize2();
//                tlvList = splitTypeLengthValue(buffer);
                tlvList = splitTypeLengthValue1(buffer); // type, length, idValue, value

                type = new String(tlvList.get(0), 0, tlvList.get(0).length, "UTF-8");
                value = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8");


                if (!loginAccess) {
                    if (type.equals("DENI")) {
                        frameId.textArea.setText(null);
                        frameId.textArea.append("아이디 중복입니다.");
                    }

                    if (type.equals("DESE")) {
                        frameId.textArea.setText(null);
                        frameId.textArea.append("아이디 중복입니다.");
                    }

                    if (type.equals("ACSS")) {
                        loginAccess = true;
                        frameChat.setVisible(true);
                        frameId.dispose();
                    }

                } else {
                    if (type.equals("MSSG") || type.equals("EMOJ") || type.equals("SYSM") || type.equals("SYSL")) {
                        //typeCheckPrintUserList(tlvList, type);
//                        System.out.println("type11:" + type);
                        typeCheckPrintMessage(tlvList, type);
                        typeCheckPrintEmoji(tlvList, type);
                        frameChat.resizeColumnWidth(frameChat.getTable1());
                        frameChat.updateRowHeights(frameChat.getTable1());
                        frameChat.jScrollPane2.getVerticalScrollBar().setValue(frameChat.jScrollPane2.getVerticalScrollBar().getMaximum());
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void typeCheckPrintMessage(ArrayList<byte[]> tlvList, String type) {

        try {
            String arr[] = new String[1];
            String userId = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8");
            String value = new String(tlvList.get(3), 0, tlvList.get(3).length, "UTF-8");

            if (type.equals(MESSAGE_CODE)) {
                arr[0] = "[" + userId + "] : " + value;
            }

            if (type.equals(SYSTEM_CODE)) {
                arr[0] = "#" + userId + value;
            }

            if (type.equals(SYSTEM_CODE1)) {
                arr[0] = "#" + value;
                frameId.textField.setText(userId);
            }
            frameChat.model1.addRow(arr);

        } catch (UnsupportedEncodingException ue) {

        }
    }

    private void typeCheckPrintEmoji(ArrayList<byte[]> tlvList, String type) {

        if (type.equals(EMOJI_CODE)) {
            try {
                ImageIcon icon = null;
                String userId = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8");
                String txtMsg = new String(tlvList.get(3), 0, tlvList.get(3).length, "UTF-8");

//                int strLine = value.lastIndexOf("/");
//                String txtMsg = value.substring(0, strLine);
//                String id = value.substring(strLine + 1);
//
                userId = "[" + userId + "] : ";

                if (txtMsg.equals("1")) {
                    icon = new ImageIcon(ImageIO.read(new File("C:\\dev\\images\\emoji_" + (1) + ".png")));
                } else if (txtMsg.equals("2")) {
                    icon = new ImageIcon(ImageIO.read(new File("C:\\dev\\images\\emoji_" + (2) + ".png")));
                } else if (txtMsg.equals("3")) {
                    icon = new ImageIcon(ImageIO.read(new File("C:\\dev\\images\\emoji_" + (3) + ".png")));
                } else if (txtMsg.equals("4")) {
                    icon = new ImageIcon(ImageIO.read(new File("C:\\dev\\images\\emoji_" + (4) + ".png")));
                } else if (txtMsg.equals("5")) {
                    icon = new ImageIcon(ImageIO.read(new File("C:\\dev\\images\\emoji_" + (5) + ".png")));
                } else if (txtMsg.equals("6")) {
                    icon = new ImageIcon(ImageIO.read(new File("C:\\dev\\images\\emoji_" + (6) + ".png")));
                } else if (txtMsg.equals("7")) {
                    icon = new ImageIcon(ImageIO.read(new File("C:\\dev\\images\\emoji_" + (7) + ".png")));
                } else if (txtMsg.equals("8")) {
                    icon = new ImageIcon(ImageIO.read(new File("C:\\dev\\images\\emoji_" + (8) + ".png")));
                } else if (txtMsg.equals("9")) {
                    icon = new ImageIcon(ImageIO.read(new File("C:\\dev\\images\\emoji_" + (9) + ".png")));
                }
                Object[] rowData = {userId, icon};
                frameChat.model1.addRow(rowData);
            } catch (IOException e) {

            }
        }
    }

    private byte[] receiveStreamDataToArrayWithSize1() {
        byte[] bufferTemp = new byte[16 + Const.PACKET_SIZE]; // 책갈피
        try {
            bufferedInputStream.read(bufferTemp);
            byte[] reqCount = new byte[4];
            System.arraycopy(bufferTemp, 12, reqCount, 0, 4);
            int messagePacketCount = byteArrayToInt(reqCount); // totalCnt
            System.out.println("ClientReceiver 메세지 패킷 totalCount : " + messagePacketCount);
            byte[] buffer = new byte[16 + messagePacketCount * Const.PACKET_SIZE];
            System.arraycopy(bufferTemp, 0, buffer, 0, 16);
            System.arraycopy(bufferTemp, 8, reqCount, 0, 4);
            int currentCount = byteArrayToInt(reqCount);
            System.out.println("ClientReceiver 메세지 현재 카운트 : " + currentCount + " / " + messagePacketCount);
            System.out.println("ClientReceiver 반복문 현재 카운트 : " + (1) + " / " + messagePacketCount);
            System.arraycopy(bufferTemp, 16, buffer, 16, Const.PACKET_SIZE);
            if (messagePacketCount > 1) {
                // currentCount와 메세지만 변경
                for (int i = 0; i < messagePacketCount - 1; i++) {
                    byte[] bufferTemp_tail = new byte[16 + Const.PACKET_SIZE]; //
                    bufferedInputStream.read(bufferTemp_tail);
                    int position = 16 + (i + 1) * Const.PACKET_SIZE;
                    System.arraycopy(bufferTemp_tail, 16, buffer, position, Const.PACKET_SIZE);
                    System.arraycopy(bufferTemp_tail, 8, reqCount, 0, 4);
                    currentCount = byteArrayToInt(reqCount);
                    System.out.println("ClientReceiver 메세지 현재 카운트 : " + currentCount + " / " + messagePacketCount);
                    System.out.println("ClientReceiver 반복문 현재 카운트 : " + (i + 2) + " / " + messagePacketCount);
                }
            }

            return buffer;
        } catch (IOException e) {
            return null;
        }

    }

    private byte[] receiveStreamDataToArrayWithSize2() {
        // type(4), length(4), currentCnt(4), totalCnt(4), idLength(4), idValue(10), Msg(PACKET_SIZE)
        byte[] bufferTemp = new byte[30 + Const.PACKET_SIZE]; // 아이디 최대 10자
        try {
            bufferedInputStream.read(bufferTemp);
            byte[] reqCount = new byte[4];
//            byte[] idLength = new byte[4];

            System.arraycopy(bufferTemp, 12, reqCount, 0, 4); // totalCnt 복사
//            System.arraycopy(bufferTemp, 16, idLength, 0, 4); // id길이 복사
//            int idValueLength = byteArrayToInt(idLength); // id길이 변환
            int messagePacketCount = byteArrayToInt(reqCount);
            System.out.println("서버쪽 메세지 패킷 totalCount : " + messagePacketCount);
            byte[] buffer = new byte[30 + messagePacketCount * Const.PACKET_SIZE]; // 총 배열 초기화
            System.arraycopy(bufferTemp, 0, buffer, 0, 30); // idValue까지
            System.arraycopy(bufferTemp, 8, reqCount, 0, 4);
            int currentCount = byteArrayToInt(reqCount);
            System.out.println("서버쪽 메세지 현재 카운트 : " + currentCount + " / " + messagePacketCount);
            System.out.println("서버쪽 반복문 현재 카운트 : " + (1) + " / " + messagePacketCount);
            System.arraycopy(bufferTemp, 30, buffer, 30, Const.PACKET_SIZE);

            if (messagePacketCount > 1) { // 패킷이 1개 이상일경우
                // currentCount와 메세지만 변경
                for (int i = 0; i < messagePacketCount - 1; i++) {
                    byte[] bufferTemp_tail = new byte[30 + Const.PACKET_SIZE]; //
                    bufferedInputStream.read(bufferTemp_tail);
                    int position = 30 + (i + 1) * Const.PACKET_SIZE;
                    System.arraycopy(bufferTemp_tail, 30, buffer, position, Const.PACKET_SIZE);
                    System.arraycopy(bufferTemp_tail, 8, reqCount, 0, 4);
                    currentCount = byteArrayToInt(reqCount);
                    System.out.println("서버쪽 메세지 현재 카운트 : " + currentCount + " / " + messagePacketCount);
                    System.out.println("서버쪽 반복문 현재 카운트 : " + (i + 2) + " / " + messagePacketCount);
                }
            }

            return buffer;
        } catch (IOException e) {
            return null;
        }

    }

    private ArrayList<byte[]> splitTypeLengthValue(byte[] buffer) {
        ArrayList<byte[]> tlvList = new ArrayList<>();
        byte[] resArray = buffer;
        byte[] type = new byte[4];
        byte[] length = new byte[4];
        System.arraycopy(resArray, 4, length, 0, 4);
        int arraySize = byteArrayToInt(length);
        byte[] value = new byte[arraySize];
        // 전송원배열, 전송원시작위치, 전송처배열, 전송처 시작위치, 카피되는 배열요소의 길이
        System.arraycopy(resArray, 0, type, 0, 4);
        System.arraycopy(resArray, 4, length, 0, 4);
        System.arraycopy(resArray, 16, value, 0, value.length);
        tlvList.add(type);
        tlvList.add(length);
        tlvList.add(value);

        return tlvList;
    }

    private ArrayList<byte[]> splitTypeLengthValue1(byte[] buffer) {
        // type(4), length(4), currentCnt(4), totalCnt(4), idLength(4), idValue(), PACKET_SIZE
        ArrayList<byte[]> tlvList = new ArrayList<>();
        byte[] resArray = buffer;
        byte[] type = new byte[4];
        byte[] length = new byte[4];
        byte[] idLength = new byte[4];
        System.arraycopy(resArray, 4, length, 0, 4);
        int arraySize = byteArrayToInt(length);
        byte[] value = new byte[arraySize];

        System.arraycopy(resArray, 16, idLength, 0, 4);
        int idValueLength = byteArrayToInt(idLength);
        byte[] idValue = new byte[idValueLength];

        System.arraycopy(resArray, 0, type, 0, 4);
        System.arraycopy(resArray, 4, length, 0, 4);
        System.arraycopy(resArray, 20, idValue, 0, idValue.length);
        System.arraycopy(resArray, 30, value, 0, value.length);
        tlvList.add(type);
        tlvList.add(length);
        tlvList.add(idValue);
        tlvList.add(value);

        return tlvList;
    }

    public int byteArrayToInt(byte[] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }
}
