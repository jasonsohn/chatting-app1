package chat01;

import java.util.Arrays;

public class LoginLogoutLog {
    int loginLogSize = 5;
//    private int[][] loginLogoutLog;
    private int[][] loginLogoutLog = new int[loginLogSize][2];

    public LoginLogoutLog(String msg, int conversationIndex) {
        if (msg.equals("init")) {
//            loginLogoutLog = new int[loginLogSize][2];
        }

        if (msg.equals("login")) {
            loginLogoutLog[loginLogSize][0] = conversationIndex;

        }

        if (msg.equals("logout")) {
            loginLogoutLog[loginLogSize][1] = conversationIndex;
        }

        if (msg.equals("print")) {
            System.out.println(Arrays.toString(loginLogoutLog));
        }
    }
}
