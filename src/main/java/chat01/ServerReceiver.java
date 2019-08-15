package chat01;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServerReceiver extends Thread {

    HashMap clients;
    Socket socket;

    BufferedInputStream bufferedInputStream;
    BufferedOutputStream bufferedOutputStream;

    static Map<String, Integer> loginLog = new HashMap<>();
    static Map<String, Integer> logoutLog = new HashMap<>();
    //    static ArrayList<String> loginUserNow = new ArrayList<>(); // 현재 접속자 리스트
    static Map<String, String> loginUserNow = new HashMap<>(); // 현재 접속자 리스트
//    static Map<Integer, byte[]> idStorage = new HashMap<>(); // 대화라인별 유저저장소
//    static Map<Integer, byte[]> conversationStorage = new HashMap<>(); // 대화라인 저장소


    static Map<Integer, String> typeStorage = new HashMap<>(); // 대화라인별 타입저장소
    static Map<Integer, String> idStorage = new HashMap<>(); // 대화라인별 유저저장소
    static Map<Integer, String> conversationStorage = new HashMap<>(); // 대화라인 저장소

    static final String ENTER = "\n";
    static final String MESSAGE_CODE = "MSSG";
    static final String SYSTEM_CODE = "SYSM";
    static final String SYSTEM_CODE1 = "SYSL";
    static final String EMOJI_CODE = "EMOJ";
    static final String LOGOUT_CODE = "LOUT";
    static Integer conversationIndex = 0;
    boolean isNextstep = false;
    int loginLogSize = 5;
    static Map<String, Object> userInfo = new HashMap<>(); // 유저별 로그인 로그아웃 시점 저장소
    int[][] loginLogoutLog;
    int[][] loginLogoutLogBigger;
    int loginLogIndex = 0;

    ServerReceiver(Socket socket, HashMap clients) {
        this.socket = socket;
        this.clients = clients;

        try {
            bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {

        }
    }

    public void run() {
        String type;
        String value;
        byte[] buffer;
        String name = "";
        ArrayList<byte[]> tlvList;
        boolean isFinished = false;
        boolean loginAccess = false;
        boolean loginLogTf;

        try {

            // 현재 로그인유저 체크
            while (!loginAccess) {

//                buffer = receiveStreamDataToArray();
                buffer = receiveStreamDataToArrayWithSize();
                tlvList = splitTypeLengthValue(buffer);

                // 1. 접속 중인지 확인, 접속 없으면 loginAcess true
                // 2. 접속 허용시, 접속명단 add, loginLog add, clients.put
                // 3. 접속 비허용 시, 접속중인 사람이 있습니다.
                type = new String(tlvList.get(0), 0, tlvList.get(0).length, "UTF-8");
                name = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8");
//                System.out.println("type:" + type);
//                System.out.println("name:" + name);
//                System.out.println("[시스템]: 접속대기중..");
                loginAccess = loginCheck(name);
            }
//            System.out.println("[시스템]: 접속중입니다.");
            clients.put(name, bufferedOutputStream);
            loginUserNow.put(name, name);
            loginLogTf = loginLogCheck(name);
            // 로그인 확인되었을 경우
            if (loginLogTf == true) { // 대화내역 불러오기
//                System.out.println("[시스템]: 대화내역 불러옵니다.");
                loginLogoutLog = saveAndReturnLoginIndex(name, conversationIndex);
                int logSize = loginLogoutLog.length;
                for (int i = 0; i < logSize; i++) {
                    if (loginLogoutLog[i][1] != 0) {
                        int indexStart = loginLogoutLog[i][0];
                        int indexEnd = loginLogoutLog[i][1];
                        sendConversationStorage(indexStart, indexEnd);

                    }

                    if (loginLogoutLog[i][1] == 0) {
                        i = logSize;
                    }
                }
            } else { // 인덱스 저장만 하기
//                System.out.println("[시스템]: 처음 접속하였습니다.");
                loginLog.put(name, conversationIndex);
                saveAndReturnLoginIndex(name, conversationIndex);
            }

            sendToAll(SYSTEM_CODE, "님이 들어오셨습니다." + "/" + name);
//            System.out.println("[시스템]: 현재 서버접속자 수는 " + clients.size() + "입니다.");
//            System.out.println("loginUserNow출력:" + loginUserNow.toString());
            while (isFinished != true) {
//                buffer = receiveStreamDataToArray();
                buffer = receiveStreamDataToArrayWithSize();
                tlvList = splitTypeLengthValue(buffer);
                type = new String(tlvList.get(0), 0, tlvList.get(0).length, "UTF-8");
                value = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8"); // txt/id
                System.out.println("Server type:" + type);
                System.out.println("Server value:" + value);
                if (type.equals("CLER")) {
                    System.out.println("clearrrrrrrrrrr");
                    clearToAll("CLER", value);
                }

                if (type.equals("LOAD")) {
                    loadToAll(1, 5);
                }

                if (type.equals("CHNG")) {
                    int strLine = value.indexOf("/");

                    String oldName = value.substring(0, strLine);
                    String newName = value.substring(strLine + 1);
                    System.out.println("oldName:" + oldName);
                    System.out.println("newName:" + newName);
                    changeName(oldName, newName);
                    name = newName;
                }

                if (type.equals(MESSAGE_CODE)) {
                    sendToAll(MESSAGE_CODE, value);
                }

                if (type.equals(EMOJI_CODE)) {
                    int valueIndex = value.length();
                    String keyword2 = value.substring(valueIndex - 1, valueIndex);
                    String keyword3 = value.substring(valueIndex - 2, valueIndex - 1);
                    String keyword1 = value.substring(7, valueIndex);
                    String keyword = value.substring(7, 8);
                    System.out.println("keyword출력:"+keyword);
                    System.out.println("keyword1출력:"+keyword1);
                    System.out.println("keyword2출력:"+keyword2);

                    for (int i = 1; i < 10; i++) {
                        if (keyword.equals(String.valueOf(i))) {
//                            sendToAll(EMOJI_CODE, String.valueOf(i));
                            sendToAll(EMOJI_CODE, keyword1);
                        }
                    }
                }

                if (type.equals(LOGOUT_CODE)) {
                    isFinished = true;
                }
            }

        } catch (IOException e) {

        } finally {
            if (loginAccess == true) {
                sendToAll(SYSTEM_CODE, "님이 나가셨습니다." + "/" + name);
                logoutLog.put(name, conversationIndex);
                saveAndExtendLogoutIndex(name, conversationIndex);
                userInfo.get(name);
                loginUserNow.remove(name);
                clients.remove(name);
//                System.out.println("loginUserNow출력:" + loginUserNow.toString());
//                System.out.println("[시스템]: [" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "에서 접속을 종료하였습니다.");
//                System.out.println("[시스템]: 현재 서버접속자 수는 " + clients.size() + "입니다.");
            }
        }
    }


    private boolean loginNowCheck(String name) {
        return loginUserNow.containsKey(name);
    }

    private boolean loginLogCheck(String name) {
        return loginLog.containsKey(name);
    }

    private int[][] saveAndReturnLoginIndex(String name, int conversationIndex) {
        if (userInfo.get(name) == null) {
            userInfo.put(name, new int[5][2]);
        }

        loginLogoutLog = (int[][]) userInfo.get(name);
        int logSize = loginLogoutLog.length;
        for (int i = 0; i < logSize; i++) {
            if (loginLogoutLog[i][1] == 0) {
                loginLogoutLog[i][0] = conversationIndex;
                System.out.println("====[시스템]: loginLogoutLog[" + i + "][0]:" + loginLogoutLog[i][0]);
                i = logSize;
            }
        }
        return loginLogoutLog;
    }

    private void saveAndExtendLogoutIndex(String name, int conversationIndex) {
        loginLogoutLog = (int[][]) userInfo.get(name);
        int logSize = loginLogoutLog.length;
        for (int i = 0; i < logSize; i++) {
            if (loginLogoutLog[i][1] == 0) {
                loginLogoutLog[i][1] = conversationIndex;
                System.out.println("====[시스템]: loginLogoutLog[" + i + "][1]:" + loginLogoutLog[i][1]);
                i = logSize;
            }

            if (loginLogoutLog[logSize - 1][1] != 0) {
                userInfo.put(name, new int[logSize * 2][2]);
                loginLogoutLogBigger = (int[][]) userInfo.get(name);
                System.out.println("[시스템]: 로그크기를 확장하였습니다." + loginLogoutLogBigger.length);
                for (int j = 0; j < loginLogoutLog.length; j++) {
                    loginLogoutLogBigger[j][0] = loginLogoutLog[j][0];
                    loginLogoutLogBigger[j][1] = loginLogoutLog[j][1];
                }
            }
        }
    }

    private boolean loginCheck(String name) {
        boolean loginAccess = false;

        if (loginUserNow.containsKey(name)) {
            try {
                String type = "DENI";
                String msg = "중복된 ID가 존재합니다.";
                bufferedOutputStream.write(messageSize(msg));
                bufferedOutputStream.write(tlvMessage(type, msg));
                bufferedOutputStream.flush();
                loginAccess = false;
            } catch (IOException e) {

            }
        } else {

            try {
                String type = "ACSS";
                String msg = "로그인합니다.";
                bufferedOutputStream.write(messageSize(msg));
                bufferedOutputStream.write(tlvMessage(type, msg));
                bufferedOutputStream.flush();
                loginAccess = true;
            } catch (IOException e) {

            }

        }

        return loginAccess;
    }

    private void loadToAll() {
        Iterator it = clients.keySet().iterator();

        while (it.hasNext()) {
            try {
                String element = (String) it.next();
                System.out.println("element ID:" + element);
                loginLogoutLog = (int[][]) userInfo.get(element);
                int logSize = loginLogoutLog.length;
                for (int i = 0; i < logSize; i++) {
                    if (loginLogoutLog[i][1] != 0) {
                        int indexStart = loginLogoutLog[i][0];
                        int indexEnd = loginLogoutLog[i][1];

                        for (int j = indexStart; j < indexEnd; j++) {
                            String type = typeStorage.get(j);
                            String id = idStorage.get(j);
                            String txtMsg = conversationStorage.get(j);
                            String totalMsg = txtMsg + "/" + id;
                            BufferedOutputStream bufferedOutputStream = (BufferedOutputStream) clients.get(element);
                            bufferedOutputStream.write(messageSize(totalMsg));
                            bufferedOutputStream.write(tlvMessage(type, totalMsg));
                            bufferedOutputStream.flush();
                            Thread.sleep(50);
                        }

                    }


                    if (loginLogoutLog[i][1] == 0) {
                        if (i == 0) {
                            int indexStart = loginLogoutLog[i][0];
                            int indexEnd = conversationIndex;

                            for (int j = indexStart; j < indexEnd; j++) {
                                String type = typeStorage.get(j);
                                String id = idStorage.get(j);
                                String txtMsg = conversationStorage.get(j);
                                String totalMsg = txtMsg + "/" + id;
                                BufferedOutputStream bufferedOutputStream = (BufferedOutputStream) clients.get(element);
                                bufferedOutputStream.write(messageSize(totalMsg));
                                bufferedOutputStream.write(tlvMessage(type, totalMsg));
                                bufferedOutputStream.flush();
                                Thread.sleep(50);
                            }

                        } else {
                            i = logSize;
                        }

                    }
                }

            } catch (IOException e) {

            } catch (InterruptedException ie) {

            }
        }
    }

    private void changeName(String oldName, String newName) {
        System.out.println("loginUserNow:" + loginUserNow);
        System.out.println("userInfo:" + userInfo);
        System.out.println("clients:" + clients);
        System.out.println("loginLog:" + loginLog);
        loginUserNow.put(newName, loginUserNow.remove(oldName));
        userInfo.put(newName, userInfo.remove(oldName));
        clients.put(newName, clients.remove(oldName));
        loginLog.put(newName, loginLog.remove(oldName));
        changeUserIdValue(oldName, newName);
        System.out.println("new loginUserNow:" + loginUserNow);
        System.out.println("new userInfo:" + userInfo);
        System.out.println("new clients:" + clients);
        System.out.println("new loginLog:" + loginLog);
        clearToAll("CLER", "CLER");
        loadToAll();
        sendToAll(SYSTEM_CODE1, oldName + "님이 " + newName + "으로 유저명을 변경하였습니다." + "/" + newName);
    }


    private byte[] receiveStreamDataToArray() {
        byte[] buffer = new byte[1096]; // 책갈피
        try {
            bufferedInputStream.read(buffer);

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

        System.arraycopy(resArray, 0, type, 0, 4);
        System.arraycopy(resArray, 4, length, 0, 4);
        System.arraycopy(resArray, 8, value, 0, value.length);
        tlvList.add(type);
        tlvList.add(length);
        tlvList.add(value);

        return tlvList;
    }

    public void sendConversationStorage(int indexStart, int indexEnd) {

        for (int i = indexStart; i < indexEnd; i++) {
            try {
                String type = typeStorage.get(i);
                String id = idStorage.get(i);
                String txtMsg = conversationStorage.get(i);
                String totalMsg = txtMsg + "/" + id;

                bufferedOutputStream.write(messageSize(totalMsg));
                bufferedOutputStream.write(tlvMessage(type, totalMsg));
                bufferedOutputStream.flush();
                Thread.sleep(50);
            } catch (IOException e) {

            } catch (InterruptedException e) {

            }
        }

    }

    private void sendToAll(String type, String msg) {
        Iterator it = clients.keySet().iterator();
        String id = "";
        String txtMsg = "";
        String totalMsg = "";
        if (type.equals(MESSAGE_CODE) || type.equals(SYSTEM_CODE) || type.equals(SYSTEM_CODE1) || type.equals(EMOJI_CODE)) {
            int strLine = msg.lastIndexOf("/");
            txtMsg = msg.substring(0, strLine);
            id = msg.substring(strLine + 1);

        } else {
            txtMsg = msg;
        }
//        idStorage.put(conversationIndex, tlvMessage(type, id));
//        conversationStorage.put(conversationIndex, tlvMessage(type, txtMsg));
        typeStorage.put(conversationIndex, type);
        idStorage.put(conversationIndex, id);
        conversationStorage.put(conversationIndex, txtMsg);


        totalMsg = txtMsg + "/" + id;
        System.out.println("conversationIndex:" + conversationIndex + ":msg:" + msg);
        conversationIndex += 1;
        //System.out.println("Storage 출력:" + conversationStorage.toString().replace(", ", ""));
        while (it.hasNext()) {
            try {
                String element = (String) it.next();
                BufferedOutputStream bufferedOutputStream = (BufferedOutputStream) clients.get(element);
//                System.out.println("element:" + element);
                bufferedOutputStream.write(messageSize(totalMsg));
                bufferedOutputStream.write(tlvMessage(type, totalMsg));
                bufferedOutputStream.flush();

            } catch (IOException e) {

            }
        }
    }

    private byte[] messageSize(String reqMsg) {
        try {

            byte[] reqValue = reqMsg.getBytes("UTF-8");

            byte[] reqLength =toBytes(reqValue.length);

            byte[] reqCount = toBytes((reqValue.length / 10) + 1);

            byte[] req = new byte[8];
            System.arraycopy(reqCount, 0, req, 0, 4);
            System.arraycopy(reqLength, 0, req, 4, 4);
            return req;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void changeUserIdValue(String oldName, String newName) {
        for (int i = 0; i < idStorage.size(); i++) {
            if (idStorage.get(i).equals(oldName)) {
                idStorage.put(i, newName);
            }
        }
    }

    private void clearToAll(String type, String msg) {
        Iterator it = clients.keySet().iterator();

        while (it.hasNext()) {
            try {
                String element = (String) it.next();
                BufferedOutputStream bufferedOutputStream = (BufferedOutputStream) clients.get(element);
//                System.out.println("element:" + element);
                bufferedOutputStream.write(messageSize(msg));
                bufferedOutputStream.write(tlvMessage(type, msg));
                bufferedOutputStream.flush();

            } catch (IOException e) {

            }
        }
    }

    private void loadToAll(int indexStart, int indexEnd) {
        Iterator it = clients.keySet().iterator();

        while (it.hasNext()) {
            try {
                String element = (String) it.next();
                BufferedOutputStream bufferedOutputStream = (BufferedOutputStream) clients.get(element);
//                System.out.println("element:" + element);
                for (int i = indexStart; i < indexEnd; i++) {
                    String type = typeStorage.get(i);
                    String id = idStorage.get(i);
                    String txtMsg = conversationStorage.get(i);
                    String totalMsg = txtMsg + "/" + id;

                    bufferedOutputStream.write(messageSize(totalMsg));
                    bufferedOutputStream.write(tlvMessage(type, totalMsg));
                    bufferedOutputStream.flush();
                    Thread.sleep(50);
                }

            } catch (IOException e) {

            } catch (InterruptedException ie) {

            }
        }
    }

    private byte[] tlvMessage(String msgType, String reqMsg) {
        try {
//            System.out.println("reqMsg 아이디 받아온거:" + reqMsg);
            //reqMsg = "#11님이 들어오셨습니다.";
            //System.out.println("reqMsg 하드코딩:" + reqMsg);
            byte[] reqValue = reqMsg.getBytes("UTF-8");
            for (int i = 0; i < reqValue.length; i++) {
//                System.out.println(reqValue[i] + " ");
            }

            byte[] reqType = msgType.getBytes();

            byte[] reqLength = toBytes(reqValue.length);

            byte[] req = new byte[reqValue.length + 8];
            System.arraycopy(reqType, 0, req, 0, 4);
            System.arraycopy(reqLength, 0, req, 4, 4);
            System.arraycopy(reqValue, 0, req, 8, reqValue.length);

            return req;

        } catch (Exception e) {
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

    public byte[] intToByteArray(int value) {
        return new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value};
    }

    public int byteArrayToInt(byte[] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }
}
