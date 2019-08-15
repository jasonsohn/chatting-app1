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
    static HashMap<Integer, String> conversationStorage = new HashMap<>();
    static final String ENTER = "\n";
    static final String ACCESS_CODE = "DENI";
    static final String MESSAGE_CODE = "MSSG";
    static final String SYSTEM_CODE = "SYSM";
    static final String SYSTEM_CODE1 = "SYSL";
    static final String EMOJI_CODE = "EMOJ";
    boolean isLoaded = false;
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

//                buffer = receiveStreamDataToArray();
                buffer = receiveStreamDataToArrayWithSize();
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

    private void typeCheckLoginAccess(String type) {
        if (type.equals(ACCESS_CODE)) {
//            frameId.
        } else {
            loginAccess = true;
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
                System.out.println("ddddddddddddddddddddd:"+value);
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

    private void loadConversation() {
        byte[] buffer = new byte[40];
        ArrayList<byte[]> tlvList;
        String type;
        String value;

        try {
            bufferedInputStream.read(buffer);
            tlvList = splitTypeLengthValue(buffer);
            type = new String(tlvList.get(0), 0, tlvList.get(0).length, "UTF-8");
            value = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8");
//            System.out.println("=================대화출력:" + value);
            typeCheckPrintMessage(tlvList, type);
            typeCheckPrintEmoji(tlvList, type);
            //System.out.println("conversationStorage:" + conversationStorage.toString());
            //isLoaded = true;
        } catch (IOException e) {

        }
    }

    private byte[] receiveStreamDataToArray() {
        byte[] buffer = new byte[1096]; // 책갈피. 여기 사이즈에 안맞게
        try {
            bufferedInputStream.read(buffer);
//            System.out.println("**************대화출력:" + buffer);
        } catch (IOException e) {

        }

        return buffer;
    }

    private byte[] receiveStreamDataToArrayWithSize() {
        byte[] bufferSize = new byte[8]; // 책갈피
        try {
            bufferedInputStream.read(bufferSize);
            byte[] reqCount = new byte[4];
            System.arraycopy(bufferSize, 0, reqCount, 0, 4);
            int arrayCount = byteArrayToInt(reqCount);
            byte[] buffer = new byte[arrayCount * 1024];
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
        System.arraycopy(resArray, 8, value, 0, value.length);
        tlvList.add(type);
        tlvList.add(length);
        tlvList.add(value);

        return tlvList;
    }

    public int byteArrayToInt(byte[] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }
}
