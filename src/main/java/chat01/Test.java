package chat01;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class Test {
    static int[][] loginLogoutLog;
    static int[][] loginLogoutLogBigger;
    static Map<String, Object> userInfo = new HashMap<>();
    int logSize = 5;

    public static void main(String[] args) {
//        tlvMessage("MSSG", "가가가");
        tlvMessage("MSSG", "가가가가가가가가가가가");
        Scanner scanner = new Scanner(System.in);
        String str = scanner.nextLine();
        System.out.println(str.length());
    }


    private static byte[] tlvMessage(String msgType, String reqMsg) {
        try {
            System.out.println("String 길이 : " + reqMsg.length());
            byte[] reqValue = reqMsg.getBytes("UTF-8"); // Msg 문자열을 바이트배열로 변환
            System.out.println("byte[] 길이 : " + reqValue.length);

            byte[] reqType = msgType.getBytes(); // type 문자열을 바이트배열로 변환

            byte[] reqLength = toBytes(reqValue.length); //

            int count = (int) Math.ceil((double)reqValue.length / 10);
            System.out.println("count 수 : " + count);
            byte[] reqCount = toBytes(count);

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

    private static byte[] toBytes(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) (i >> 24); // 오른쪽으로 연산 위치 이동
        result[1] = (byte) (i >> 16);
        result[2] = (byte) (i >> 8);
        result[3] = (byte) (i);
        return result;
    }


    public static void testString() {
        String msg = "123";
        int strLine = msg.lastIndexOf("/");
        System.out.println(strLine);
    }

    public static void sortArr() {
        int[] sortArr = new int[5];
        int index = 20;

        for (int i = 0; i < sortArr.length; i++) {
            sortArr[i] = index;
            index -= 2;
        }
        System.out.println("출력전:" + Arrays.toString(sortArr));
        Arrays.sort(sortArr);
        System.out.println("출력:" + Arrays.toString(sortArr));
    }

    public static void makeMapArr_4() {
        if (userInfo.get("userNum") == null) {
            //System.out.println("NULL 출력");
        }
        userInfo.put("userNum", new int[5][2]);
        if (userInfo.get("userNum") != null) {
            //System.out.println("null 아님");
        }
        userInfo.put("userNum1", new int[5][2]);
        ((int[][]) userInfo.get("userNum"))[0][0] = 3;

        System.out.println(userInfo);
        userInfo.put("userNum2", userInfo.remove("userNum"));
        System.out.println(userInfo);


        for (int i = 0; i < 5; i++) {
            //expandLogSize(userInfo, "userNum", i);
        }


    }

    public static void makeMapArr_3() {
        Map<String, Object> userInfo = new HashMap<>();

        userInfo.put("userNum", new int[5][2]);
        userInfo.put("userNum1", new int[5][2]);
//        ((int[][])userInfo.get("userNum"))[0][0] = 3;
        loginLogoutLog = ((int[][]) userInfo.get("userNum"));
        System.out.println("loginLogoutLog.length:" + loginLogoutLog.length);
        userInfo.put("userNum", new int[10][2]);
        loginLogoutLog = ((int[][]) userInfo.get("userNum"));
        System.out.println("loginLogoutLog.length:" + loginLogoutLog.length);
        loginLogoutLog[0][0] = 3;
//        ((int[][])userInfo.get("userNum1"))[0][0] = 5;

        loginLogoutLog = ((int[][]) userInfo.get("userNum1"));
        loginLogoutLog[0][0] = 5;
        System.out.println("loginLogoutLog.length:" + loginLogoutLog.length);
        int result1 = ((int[][]) userInfo.get("userNum"))[0][0];
        int result2 = ((int[][]) userInfo.get("userNum1"))[0][0];
        System.out.println("출력1:" + result1);
        System.out.println("출력2:" + result2);

        // 유저 put 초기 설정
        // userInfo.get(name)


        // for length만큼 반복하는데, [i][1] == 0 일경우
        // 1) 로그인이면 [i][0] = index 대입
        // 2) 로그아웃이면 [i][1] = index 대입


//
//        loginLog[0] = 4;
//        loginLog[1] = 5;
//        userInfo.put("userNum1", new int[5]);
//        for (int i = 0; i < loginLog.length; i++) {
//            System.out.println(userInfo.get(""));
//        }
//        System.out.println("출력1:" + Arrays.toString((int[]) userInfo.get("userNum")));
//        System.out.println("출력2:" + Arrays.toString((int[]) userInfo.get("userNum1")));
//        result = ((int[]) userInfo.get("userNum")).length;

    }

    public static void expandLogSize(Map<String, Object> userInfo, String name, int conversationIndex) {
        loginLogoutLog = (int[][]) userInfo.get(name);

        for (int i = 0; i < loginLogoutLog.length; i++) {

            if (loginLogoutLog[i][1] == 0) {
                loginLogoutLog[i][1] = conversationIndex;
                System.out.println("loginLogoutLog[" + i + "][1]" + loginLogoutLog[i][1]);
            }

            if (loginLogoutLog[loginLogoutLog.length - 1][1] != 0) {
                userInfo.put(name, new int[loginLogoutLog.length * 2][2]);
                loginLogoutLogBigger = ((int[][]) userInfo.get("userNum"));
                for (int j = 0; j < loginLogoutLog.length; j++) {
                    loginLogoutLogBigger[j][0] = loginLogoutLog[j][0];
                    loginLogoutLogBigger[j][1] = loginLogoutLog[j][1];
                }
                System.out.println("loginLogoutLogBigger.length:" + loginLogoutLogBigger.length);
            }
        }
    }

    public static void checkIndex(int[][] loginLogoutLog) {
        int index = 0;
        for (int i = 0; i < loginLogoutLog.length; i++) {
            if (loginLogoutLog[i][1] == 0) {

            }
        }

    }

    public static void makeMapArr_0() {
        int[][] loginLog = new int[5][2];
        Map<String, Object> userInfo = new HashMap<>();
        System.out.println("loginLog.length:" + loginLog.length);
        System.out.println("loginLog[0].length:" + loginLog[0].length);
        int k = 0;
        for (int i = 0; i < loginLog.length; i++) {
            for (int j = 0; j < loginLog[i].length; j++) {
                loginLog[i][j] = k;
                k++;
                System.out.println("loginLog[" + i + "][" + j + "]:" + loginLog[i][j]);
            }
            userInfo.put("userNum" + k, loginLog[i]);
            System.out.println("111loginLog[" + i + "]:" + Arrays.toString(loginLog[i]));

        }
        System.out.println("출력:" + Arrays.toString((int[]) userInfo.get("userNum2")));

        for (int user : (int[]) userInfo.get("userNum2")) {
            System.out.println("user:" + user);
        }
    }

    public static void makeMapArr_1() {
        int[] a = new int[]{2, 7, 10};

        int[][] b = new int[][]{{1, 1}, {2, 3, 2}, {3, 3, 2, 1, 1}};

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("a", a);

        map.put("b", b);

        System.out.println("userInfo:" + map.get("b"));
    }

    public static void makeMapArr_2() {
        String valueArr[][] = {{"value1_1", "value1_2", "value1_3"},
                {"value2_1", "value2_2", "value2_3"},
                {"value3_1", "value3_2", "value3_3"}};

        Map<String, String[]> arrMap = new HashMap<>();
        arrMap.put("key01", valueArr[0]);
        arrMap.put("key02", valueArr[1]);
        arrMap.put("key03", valueArr[2]);

        LinkedList<String> valueList = new LinkedList<>();
        valueList.add("AAA");
        valueList.add("BBB");
        valueList.add("CCC");

        System.out.println("arrMap:" + (arrMap));
        System.out.println("arrMap.get(key01):" + Arrays.toString(arrMap.get("key01")));
    }
}
