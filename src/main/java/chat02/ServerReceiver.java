package chat02;

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
//    static Map<String, Integer> logoutLog = new HashMap<>();
    static Map<String, String> loginUserNow = new HashMap<>(); // 현재 접속자 리스트
    static Map<Integer, String> typeStorage = new HashMap<>(); // 대화라인별 타입저장소
    static Map<Integer, String> idStorage = new HashMap<>(); // 대화라인별 유저저장소
    static Map<Integer, String> conversationStorage = new HashMap<>(); // 대화라인 저장소

    static final String MESSAGE_CODE = "MSSG";
    static final String SYSTEM_CODE = "SYSM";
    static final String SYSTEM_CODE1 = "SYSL";
    static final String EMOJI_CODE = "EMOJ";
    static final String LOGOUT_CODE = "LOUT";
    static Integer conversationIndex = 0;

    static Map<String, Object> userInfo = new HashMap<>(); // 유저별 로그인 로그아웃 시점 저장소
    int[][] loginLogoutLog;
    int[][] loginLogoutLogBigger;

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
        String userId = "";
        String value;
        byte[] buffer;
        ArrayList<byte[]> tlvList;
        boolean isFinished = false;
        boolean loginAccess = false;
        boolean loginLogTf;

        try {

            // 현재 로그인유저 체크
            while (!loginAccess) {
                System.out.println("로그인 유저 체크");
//                buffer = receiveStreamDataToArrayWithSize1();
                buffer = receiveStreamDataToArrayWithSize2();
//                tlvList = splitTypeLengthValue(buffer);
                tlvList = splitTypeLengthValue1(buffer); // type, length, idValue, value

                // 1. 접속 중인지 확인, 접속 없으면 loginAcess true
                // 2. 접속 허용시, 접속명단 add, loginLog add, clients.put
                // 3. 접속 비허용 시, 접속중인 사람이 있습니다.
                type = new String(tlvList.get(0), 0, tlvList.get(0).length, "UTF-8");
                userId = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8");
//                System.out.println("type:" + type);
//                System.out.println("name:" + name);
//                System.out.println("[시스템]: 접속대기중..");
                loginAccess = loginCheck(userId);
            }
//            System.out.println("[시스템]: 접속중입니다.");
            clients.put(userId, bufferedOutputStream);
            loginUserNow.put(userId, userId);
            loginLogTf = loginLogCheck(userId);
            // 로그인 확인되었을 경우
            if (loginLogTf == true) { // 대화내역 불러오기
//                System.out.println("[시스템]: 대화내역 불러옵니다.");
                loginLogoutLog = saveAndReturnLoginIndex(userId, conversationIndex);
                int logSize = loginLogoutLog.length;
                for (int i = 0; i < logSize; i++) {
                    if (loginLogoutLog[i][1] != 0) {
                        int indexStart = loginLogoutLog[i][0];
                        int indexEnd = loginLogoutLog[i][1];
                        sendConversationStorage(indexStart, indexEnd);
//                        sendConversationStorage(indexStart, indexEnd, userId);
                    }

                    if (loginLogoutLog[i][1] == 0) {
                        i = logSize;
                    }
                }
            } else { // 인덱스 저장만 하기
//                System.out.println("[시스템]: 처음 접속하였습니다.");
                loginLog.put(userId, conversationIndex);
                saveAndReturnLoginIndex(userId, conversationIndex);
            }

//            sendToAll(SYSTEM_CODE, "님이 들어오셨습니다." + "/" + name);
            sendToAll(SYSTEM_CODE, "님이 들어오셨습니다.", userId);
//            System.out.println("[시스템]: 현재 서버접속자 수는 " + clients.size() + "입니다.");
//            System.out.println("loginUserNow출력:" + loginUserNow.toString());
            while (isFinished != true) {
//                buffer = receiveStreamDataToArrayWithSize1();
                buffer = receiveStreamDataToArrayWithSize2();
//                tlvList = splitTypeLengthValue(buffer);
                tlvList = splitTypeLengthValue1(buffer);
                type = new String(tlvList.get(0), 0, tlvList.get(0).length, "UTF-8");
                userId = new String(tlvList.get(2), 0, tlvList.get(2).length, "UTF-8"); // txt/id
                value = new String(tlvList.get(3), 0, tlvList.get(3).length, "UTF-8"); // txt/id
                System.out.println("Server type:" + type);
                System.out.println("Server value:" + value);

                if (type.equals("CHNG")) {
                    int strLine = value.indexOf("/");

                    String oldName = value.substring(0, strLine);
                    String newName = value.substring(strLine + 1);
                    System.out.println("oldName:" + oldName);
                    System.out.println("newName:" + newName);
                    changeName(oldName, newName);
                    userId = newName;
                }

                if (type.equals(MESSAGE_CODE)) {
//                    sendToAll(MESSAGE_CODE, value);
                    sendToAll(MESSAGE_CODE, value, userId);
                }

                if (type.equals(EMOJI_CODE)) {
                    int valueIndex = value.length();
                    String keyword = value.substring(7, 8);
                    String keyword1 = value.substring(7, valueIndex);
                    System.out.println("keyword출력:" + keyword);
                    System.out.println("keyword1출력:" + keyword1);

                    for (int i = 1; i < 10; i++) {
                        if (keyword.equals(String.valueOf(i))) {
//                            sendToAll(EMOJI_CODE, keyword1);
                            sendToAll(EMOJI_CODE, keyword1, userId);
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
//                sendToAll(SYSTEM_CODE, "님이 나가셨습니다." + "/" + userId);
                sendToAll(SYSTEM_CODE, "님이 나가셨습니다." ,userId);
//                logoutLog.put(name, conversationIndex);
                saveAndExtendLogoutIndex(userId, conversationIndex);
                userInfo.get(userId);
                loginUserNow.remove(userId);
                clients.remove(userId);
//                System.out.println("loginUserNow출력:" + loginUserNow.toString());
//                System.out.println("[시스템]: [" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "에서 접속을 종료하였습니다.");
//                System.out.println("[시스템]: 현재 서버접속자 수는 " + clients.size() + "입니다.");
            }
        }
    }

    private boolean loginLogCheck(String userId) {
        return loginLog.containsKey(userId);
    }

    private int[][] saveAndReturnLoginIndex(String userId, int conversationIndex) {
        if (userInfo.get(userId) == null) {
            userInfo.put(userId, new int[5][2]);
        }

        loginLogoutLog = (int[][]) userInfo.get(userId);
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

    private void saveAndExtendLogoutIndex(String userId, int conversationIndex) {
        loginLogoutLog = (int[][]) userInfo.get(userId);
        int logSize = loginLogoutLog.length;
        for (int i = 0; i < logSize; i++) {
            if (loginLogoutLog[i][1] == 0) {
                loginLogoutLog[i][1] = conversationIndex;
                System.out.println("====[시스템]: loginLogoutLog[" + i + "][1]:" + loginLogoutLog[i][1]);
                i = logSize;
            }

            if (loginLogoutLog[logSize - 1][1] != 0) {
                userInfo.put(userId, new int[logSize * 2][2]);
                loginLogoutLogBigger = (int[][]) userInfo.get(userId);
                System.out.println("[시스템]: 로그크기를 확장하였습니다." + loginLogoutLogBigger.length);
                for (int j = 0; j < loginLogoutLog.length; j++) {
                    loginLogoutLogBigger[j][0] = loginLogoutLog[j][0];
                    loginLogoutLogBigger[j][1] = loginLogoutLog[j][1];
                }
            }
        }
    }

    private boolean loginCheck(String userId) {
        boolean loginAccess = false;

        if (loginUserNow.containsKey(userId)) {

            String msgType = "DENI";
            String msg = "중복된 ID가 존재합니다.";

//            sendPacketOnce(msgType, msg);
            sendPacketOnce(msgType, msg, userId);

            loginAccess = false;

        } else {
            String msgType = "ACSS";
            String msg = "로그인합니다.";

//            sendPacketOnce(msgType, msg);
            sendPacketOnce(msgType, msg, userId);

            loginAccess = true;
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
                            String msgType = typeStorage.get(j);
                            String userId = idStorage.get(j);
                            String txtMsg = conversationStorage.get(j);
//                            String totalMsg = txtMsg + "/" + id;
                            BufferedOutputStream bufferedOutputStream = (BufferedOutputStream) clients.get(element);

                            sendPacketOnce(bufferedOutputStream, msgType, txtMsg, userId);

                            Thread.sleep(50);
                        }

                    }

                    if (loginLogoutLog[i][1] == 0) {
                        if (i == 0) {
                            int indexStart = loginLogoutLog[i][0];
                            int indexEnd = conversationIndex;

                            for (int j = indexStart; j < indexEnd; j++) {
                                String msgType = typeStorage.get(j);
                                String userId = idStorage.get(j);
                                String txtMsg = conversationStorage.get(j);
//                                String totalMsg = txtMsg + "/" + id;
                                BufferedOutputStream bufferedOutputStream = (BufferedOutputStream) clients.get(element);

                                sendPacketOnce(bufferedOutputStream, msgType, txtMsg, userId);

                                Thread.sleep(50);
                            }
                        } else {
                            i = logSize;
                        }
                    }
                }

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
        loadToAll();
//        sendToAll(SYSTEM_CODE1, oldName + "님이 " + newName + "으로 유저명을 변경하였습니다." + "/" + newName);
        sendToAll(SYSTEM_CODE1, oldName + "님이 " + newName + "으로 유저명을 변경하였습니다.", newName);
    }

    private byte[] receiveStreamDataToArrayWithSize1() {
        byte[] bufferTemp = new byte[16 + Const.PACKET_SIZE]; // 책갈피
        try {
            bufferedInputStream.read(bufferTemp);
            byte[] reqCount = new byte[4];
            System.arraycopy(bufferTemp, 12, reqCount, 0, 4);
            int messagePacketCount = byteArrayToInt(reqCount);
            System.out.println("서버쪽 메세지 패킷 totalCount : " + messagePacketCount);
            byte[] buffer = new byte[16 + messagePacketCount * Const.PACKET_SIZE];
            System.arraycopy(bufferTemp, 0, buffer, 0, 16);
            System.arraycopy(bufferTemp, 8, reqCount, 0, 4);
            int currentCount = byteArrayToInt(reqCount);
            System.out.println("서버쪽 메세지 현재 카운트 : " + currentCount + " / " + messagePacketCount);
            System.out.println("서버쪽 반복문 현재 카운트 : " + (1) + " / " + messagePacketCount);
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
                    System.out.println("서버쪽 메세지 현재 카운트 : " + currentCount + " / " + messagePacketCount);
                    System.out.println("서버쪽 반복문 현재 카운트 : " + (i + 2) + " / " + messagePacketCount);
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
            byte[] idLength = new byte[4];

            System.arraycopy(bufferTemp, 12, reqCount, 0, 4); // totalCnt 복사
            System.arraycopy(bufferTemp, 16, idLength, 0, 4); // id길이 복사
            int idValueLength = byteArrayToInt(idLength); // id길이 변환
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

        System.arraycopy(resArray, 0, type, 0, 4);
        System.arraycopy(resArray, 4, length, 0, 4);
        System.arraycopy(resArray, 16, value, 0, value.length);
        tlvList.add(type);
        tlvList.add(length);
        tlvList.add(value);

        return tlvList;
    }

    private ArrayList<byte[]> splitTypeLengthValue1(byte[] buffer) {
        // type(4), length(4), currentCnt(4), totalCnt(4), idLength(4), idValue(10), PACKET_SIZE
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

    public void sendConversationStorage(int indexStart, int indexEnd) {

        for (int i = indexStart; i < indexEnd; i++) {
            try {
                String msgType = typeStorage.get(i);
                String id = idStorage.get(i);
                String txtMsg = conversationStorage.get(i);
                String totalMsg = txtMsg + "/" + id;

//                sendPacketOnce(msgType, totalMsg);

                Thread.sleep(50);
            } catch (InterruptedException e) {

            }
        }

    }

    public void sendConversationStorage(int indexStart, int indexEnd, String userId) {

        for (int i = indexStart; i < indexEnd; i++) {
            try {
                String msgType = typeStorage.get(i);
                String id = idStorage.get(i);
                String txtMsg = conversationStorage.get(i);


                sendPacketOnce(msgType, txtMsg, id);

                Thread.sleep(50);
            } catch (InterruptedException e) {

            }
        }

    }

    private void sendToAll(String msgType, String msg) {
        Iterator it = clients.keySet().iterator();
        String id = "";
        String txtMsg = "";
        String totalMsg = "";
        if (msgType.equals(MESSAGE_CODE) || msgType.equals(SYSTEM_CODE) || msgType.equals(SYSTEM_CODE1) || msgType.equals(EMOJI_CODE)) {
            int strLine = msg.lastIndexOf("/");
            if (strLine < 0) {
                txtMsg = msg;
            } else {
                txtMsg = msg.substring(0, strLine);
                id = msg.substring(strLine + 1);
            }
        } else {
            txtMsg = msg;
        }
        typeStorage.put(conversationIndex, msgType);
        idStorage.put(conversationIndex, id);
        conversationStorage.put(conversationIndex, txtMsg);


        totalMsg = txtMsg + "/" + id;
        conversationIndex += 1;
        while (it.hasNext()) {

            String element = (String) it.next();
            BufferedOutputStream bufferedOutputStream = (BufferedOutputStream) clients.get(element);

//            sendPacketOnce(bufferedOutputStream, msgType, totalMsg);
        }
    }

    private void sendToAll(String msgType, String txtMsg, String userId) {
        Iterator it = clients.keySet().iterator();

        if (msgType.equals(MESSAGE_CODE) || msgType.equals(SYSTEM_CODE) || msgType.equals(SYSTEM_CODE1) || msgType.equals(EMOJI_CODE)) {


        }

        typeStorage.put(conversationIndex, msgType);
        idStorage.put(conversationIndex, userId);
        conversationStorage.put(conversationIndex, txtMsg);

        conversationIndex += 1;
        while (it.hasNext()) {

            String element = (String) it.next();
            BufferedOutputStream bufferedOutputStream = (BufferedOutputStream) clients.get(element);

            sendPacketOnce(bufferedOutputStream, msgType, txtMsg, userId);
        }
    }

    private void sendPacketOnce(BufferedOutputStream bufferedOutputStream, String msgType, String totalMsg) {
        try {
            byte[] req = tlvMessageWithSize(msgType, totalMsg);
            byte[] countMsg = new byte[4];
            System.arraycopy(req, 8, countMsg, 0, 4);
            int arrayCount = byteArrayToInt(countMsg); // totalCnt
            int restSize = (req.length - 12) % Const.PACKET_SIZE;
            System.out.println("sendToAll 메세지 패킷 totalCount : " + arrayCount);
            for (int i = 0; i < arrayCount; i++) {
                byte[] resultReq = new byte[16 + Const.PACKET_SIZE]; // type, length, currentCount, totalCount, Msg(PACKET_SIZE)
                byte[] currentCount = toBytes(i + 1);
                System.arraycopy(req, 0, resultReq, 0, 8);          // type, length
                System.arraycopy(currentCount, 0, resultReq, 8, 4); // 현재 메세지
                System.arraycopy(countMsg, 0, resultReq, 12, 4); // 현재 메세지
                int position = 12 + i * Const.PACKET_SIZE;

                if (i == arrayCount - 1) {
                    System.arraycopy(req, position, resultReq, 16, restSize); // 메세지
                } else {
                    System.arraycopy(req, position, resultReq, 16, Const.PACKET_SIZE); // 메세지
                }
                System.out.println("sendToAll 메세지 패킷 currentCount : " + (i + 1) + " / " + arrayCount);
                bufferedOutputStream.write(resultReq);
                bufferedOutputStream.flush();
            }
        } catch (IOException e) {

        }
    }

    // type(4), length(4), totalCnt(4), idLength(4), idValue(10), reqValue
    private void sendPacketOnce(BufferedOutputStream bufferedOutputStream, String msgType, String textLine, String userId) {
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
            //            byte[] idValue = userId.getBytes("UTF-8");
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

    private void sendPacketOnce(String msgType, String totalMsg) {
        try {
            byte[] req = tlvMessageWithSize(msgType, totalMsg);
//            byte[] reqLength = new byte[4];
            byte[] totalCount = new byte[4];
            System.arraycopy(req, 8, totalCount, 0, 4);
            int messagePacketCount = byteArrayToInt(totalCount);
            int restReqValueLength = (req.length - 12) % Const.PACKET_SIZE;
            System.out.println("sendToAll 메세지 패킷 totalCount : " + messagePacketCount);
            for (int i = 0; i < messagePacketCount; i++) {
                byte[] resultReq = new byte[16 + Const.PACKET_SIZE]; // type, length, currentCount, totalCount, Msg(PACKET_SIZE)
                byte[] currentCount = toBytes(i + 1);
//                System.arraycopy(req, 0, resultReq, 0, 4);          // type
                System.arraycopy(req, 0, resultReq, 0, 8);          // type, length
                System.arraycopy(currentCount, 0, resultReq, 8, 4); // 현재 패킷 횟수
                System.arraycopy(totalCount, 0, resultReq, 12, 4); // 총 패
                int position = 12 + i * Const.PACKET_SIZE;

                if (i == messagePacketCount - 1) {
//                    reqLength = toBytes(restReqValueLength);
//                    System.arraycopy(reqLength, 0, resultReq, 4, 4);
                    System.arraycopy(req, position, resultReq, 16, restReqValueLength); // 메세지
                } else {
//                    reqLength = toBytes(Const.PACKET_SIZE);
//                    System.arraycopy(reqLength, 0, resultReq, 4, 4);
                    System.arraycopy(req, position, resultReq, 16, Const.PACKET_SIZE); // 메세지
                }
                System.out.println("sendToAll 메세지 패킷 currentCount : " + (i + 1) + " / " + restReqValueLength);
                bufferedOutputStream.write(resultReq);
                bufferedOutputStream.flush();
            }
        } catch (IOException e) {

        }
    }

    // type(4), length(4), totalCnt(4), idLength(4), idValue(10), reqValue
    private void sendPacketOnce(String msgType, String textLine, String userId) {
        try {

            byte[] req = tlvMessageWithSize(msgType, textLine, userId);
//            byte[] reqLength = new byte[4];
            byte[] totalCount = new byte[4];
            System.arraycopy(req, 8, totalCount, 0, 4);
            byte[] idLength = new byte[4];
            System.arraycopy(req, 12, idLength, 0, 4);
            int idValueLength = byteArrayToInt(idLength);
            int messagePacketCount = byteArrayToInt(totalCount);
            int restReqValueLength = (req.length - 26) % Const.PACKET_SIZE;
            System.out.println("클라이언트쪽 메세지 패킷 totalCount : " + messagePacketCount);
            for (int i = 0; i < messagePacketCount; i++) {
                // type(4), length(4), currentCount(4), totalCount(4), idLength(4), idValue(10), Msg(PACKET_SIZE)
                byte[] resultReq = new byte[30 + Const.PACKET_SIZE]; // type, length, currentCount, totalCount, Msg(PACKET_SIZE)
                byte[] currentCount = toBytes(i + 1);
//                System.arraycopy(req, 0, resultReq, 0, 4);          // type
                System.arraycopy(req, 0, resultReq, 0, 8);          // type, length
                System.arraycopy(currentCount, 0, resultReq, 8, 4); // 현재 패킷 횟수
                System.arraycopy(req, 8, resultReq, 12, 18); // 총 패킷 횟수, ID 길이, 값

                int position = 26 + i * Const.PACKET_SIZE;

                if (i == messagePacketCount - 1) { // 마지막 패킷일경우
//                    reqLength = toBytes(restReqValueLength);
//                    System.arraycopy(reqLength, 0, resultReq, 4, 4);
                    System.arraycopy(req, position, resultReq, 30, restReqValueLength); // 메세지
                } else {
//                    reqLength = toBytes(Const.PACKET_SIZE);
//                    System.arraycopy(reqLength, 0, resultReq, 4, 4);
                    System.arraycopy(req, position, resultReq, 30, Const.PACKET_SIZE); // 메세지
                }

                System.out.println("클라이언트쪽 메세지 패킷 currentCount : " + (i + 1) + " / " + messagePacketCount);
                bufferedOutputStream.write(resultReq);
                bufferedOutputStream.flush();
            }
        } catch (IOException e) {

        }
    }

    // 이거쓰나?
    private byte[] tlvMessageWithSize(String msgType, String reqMsg) {
        try {
            System.out.println("메세지의 String 길이 : " + reqMsg.length());
            byte[] reqValue = reqMsg.getBytes("UTF-8"); // Msg 문자열을 바이트배열로 변환
            System.out.println("메세지의 byte[] 길이 : " + reqValue.length);
            byte[] reqType = msgType.getBytes(); // type 문자열을 바이트배열로 변환
            byte[] reqLength = toBytes(reqValue.length);
            int messagePacketCount = (int) Math.ceil((double) reqValue.length / Const.PACKET_SIZE); // 패킷 수
            byte[] totalCount = toBytes(messagePacketCount);
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

            byte[] reqLength = toBytes(reqValue.length);

//            byte[] reqCount = toBytes((reqValue.length / 10) + 1);
            int messageCount = (int) Math.ceil((double) (8 + reqValue.length) / Const.PACKET_SIZE); // 패킷 수
            byte[] reqCount = toBytes(messageCount); // TYPE, LENGTH 길이 포함

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
