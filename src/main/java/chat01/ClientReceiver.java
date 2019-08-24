package chat01;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

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
    static final int PACKET_SIZE = 100;
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

//                buffer = receiveStreamDataToArrayWithSize();
                buffer = receiveStreamDataToArrayWithSize1();
                tlvList = splitTypeLengthValue(buffer);

                type = new String(tlvList.get(0), 0, tlvList.get(0).length, "UTF-8");
                value = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8");
//                System.out.println("Client type:" + type);
//                System.out.println("Client value:" + value);

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

//                    System.out.println("로그인acess false 출력중");
                } else {
                    if (type.equals("MSSG") || type.equals("EMOJ") || type.equals("SYSM") || type.equals("SYSL")) {
                        //typeCheckPrintUserList(tlvList, type);
//                        System.out.println("type11:" + type);
                        typeCheckPrintMessage(tlvList, type);
                        typeCheckPrintEmoji(tlvList, type);
//                    System.out.println("로그인acess true 출력중");
                        frameChat.resizeColumnWidth(frameChat.getTable1());
                        frameChat.updateRowHeights(frameChat.getTable1());
                        frameChat.jScrollPane2.getVerticalScrollBar().setValue(frameChat.jScrollPane2.getVerticalScrollBar().getMaximum());
                    }

                    if (type.equals("CLER")) {
                        System.out.println("get clear");
                        frameChat.model1.setNumRows(0);
                    }

                    if (type.equals("LOAD")) {

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void typeCheckPrintUserList(ArrayList<byte[]> tlvList, String type) {
        String arr[] = new String[1];
        if (type.equals("UUID")) {
            try {
                String value = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8");
                arr[0] = value;
                frameChat.model2.addRow(arr);
            } catch (UnsupportedEncodingException ue) {

            }
        }
    }

    private void typeCheckPrintMessage(ArrayList<byte[]> tlvList, String type) {

        try {
            String arr[] = new String[1];
            String value = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8");

            if (type.equals(MESSAGE_CODE)) {
                System.out.println("before substring:" + value);
                int strLine = value.lastIndexOf("/");
                String txtMsg = value.substring(0, strLine);
                String id = value.substring(strLine + 1);
                arr[0] = "[" + id + "] : " + txtMsg;
            }

            if (type.equals(SYSTEM_CODE)) {
                int strLine = value.lastIndexOf("/");
                String txtMsg = value.substring(0, strLine);
                String id = value.substring(strLine + 1);
                arr[0] = "#" + id + txtMsg;
            }

            if (type.equals(SYSTEM_CODE1)) {
                int strLine = value.lastIndexOf("/");
                String txtMsg = value.substring(0, strLine);
                String id = value.substring(strLine + 1);
                arr[0] = "#" + txtMsg;
                frameId.textField.setText(id);
            }
                frameChat.model1.addRow(arr);

        } catch (UnsupportedEncodingException ue) {

        }
    }

    private void typeCheckPrintEmoji(ArrayList<byte[]> tlvList, String type) {
        System.out.println("type출력:"+type);
        if (type.equals(EMOJI_CODE)) {
            try {
                ImageIcon icon = null;
                String value = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8");
                int strLine = value.lastIndexOf("/");
                String txtMsg = value.substring(0, strLine);
                String id = value.substring(strLine + 1);
                id = "[" + id + "] : ";
                System.out.println("value출력:"+value);
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
                Object[] rowData = {id, icon};
                frameChat.model1.addRow(rowData);
            } catch (IOException e) {

            }
        }
    }

    private byte[] receiveStreamDataToArrayWithSize1() {
        byte[] bufferTemp = new byte[16 + PACKET_SIZE]; // 책갈피
        try {
            bufferedInputStream.read(bufferTemp);
            byte[] reqCount = new byte[4];
            System.arraycopy(bufferTemp, 12, reqCount, 0, 4);
            int arrayCount = byteArrayToInt(reqCount); // totalCnt
            System.out.println("ClientReceiver 메세지 패킷 totalCount : " + arrayCount);
            byte[] buffer = new byte[16 + arrayCount * PACKET_SIZE];
            System.arraycopy(bufferTemp, 0, buffer, 0, 16);
            System.arraycopy(bufferTemp, 8, reqCount, 0, 4);
            int currentCount = byteArrayToInt(reqCount);
            System.out.println("ClientReceiver 메세지 현재 카운트 : " + currentCount + " / " + arrayCount);
            System.out.println("ClientReceiver 반복문 현재 카운트 : " + (1) + " / " + arrayCount);
            System.arraycopy(bufferTemp, 16, buffer, 16, PACKET_SIZE);
            if (arrayCount > 1) {
                // currentCount와 메세지만 변경
                for (int i = 0; i < arrayCount - 1; i++) {
                    byte[] bufferTemp_tail = new byte[16 + PACKET_SIZE]; //
                    bufferedInputStream.read(bufferTemp_tail);
                    int position = 16 + (i + 1) * PACKET_SIZE;
                    System.arraycopy(bufferTemp_tail, 16, buffer, position, PACKET_SIZE);
                    System.arraycopy(bufferTemp_tail, 8, reqCount, 0, 4);
                    currentCount = byteArrayToInt(reqCount);
                    System.out.println("ClientReceiver 메세지 현재 카운트 : " + currentCount + " / " + arrayCount);
                    System.out.println("ClientReceiver 반복문 현재 카운트 : " + (i + 2) + " / " + arrayCount);
                }
            }

            return buffer;
        } catch (IOException e) {
            return null;
        }

    }

    private byte[] receiveStreamDataToArrayWithSize() {
        byte[] bufferTemp = new byte[8]; // 책갈피
        try {
            bufferedInputStream.read(bufferTemp);
            byte[] reqCount = new byte[4];
            System.arraycopy(bufferTemp, 0, reqCount, 0, 4);
            int arrayCount = byteArrayToInt(reqCount);
            byte[] buffer = new byte[arrayCount * 10];
            bufferedInputStream.read(buffer);
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
//        byte[] value = new byte[resArray.length - 8];
//        byte[] resArray = new byte[arraySize + 8];
        // 전송원배열, 전송원시작위치, 전송처배열, 전송처 시작위치, 카피되는 배열요소의 길이
        System.arraycopy(resArray, 0, type, 0, 4);
        System.arraycopy(resArray, 4, length, 0, 4);
//        System.arraycopy(resArray, 8, value, 0, value.length);
        System.arraycopy(resArray, 16, value, 0, value.length);
        tlvList.add(type);
        tlvList.add(length);
        tlvList.add(value);

        return tlvList;
    }

    public int byteArrayToInt(byte[] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }
}
