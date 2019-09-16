package chat02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FrameId  extends JFrame implements ActionListener {

    static JTextField textField = new JTextField(8);
    static final String MESSAGE_CODE = "MSSG";
    static final String LOGOUT_CODE = "LOUT";
    JButton btnRegister = new JButton("가입");
    JButton btnInsert = new JButton("로그인");
    JButton btnExit = new JButton("닫기");
    JTextArea textArea = new JTextArea("아이디 입력하세요.");
    ClientSender clientSender;
    FrameChat frameChat;

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/chatappdb?useSSL=false";
    static final String USERNAME = "chatAdmin";
    static final String PASSWORD = "rhkswp!123";

    public FrameId(ClientSender clientSender, FrameChat frameChat) {
        super("아이디");
        this.clientSender = clientSender;
        this.frameChat = frameChat;

        setLayout(new FlowLayout());
        setBounds(300, 300, 450, 300);
        add(new JLabel("아이디"));

        add(textField);
        add(btnRegister);
        add(btnInsert);
        add(btnExit);
        add(textArea);
        setVisible(true);
        btnRegister.addActionListener(this);
        btnInsert.addActionListener(this);
        btnExit.addActionListener(this);
        textField.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnInsert) {
//            idCheck();
            login();
        } else if (e.getSource() == btnExit) {
            logout();
            System.exit(0);
        } else if (e.getSource() == btnRegister) {
            register();
        }
    }

    static public String getId() {
        return textField.getText();
    }

    public void login() {
        clientSender.sendMsg("LOGN", getId(), getId());
    }

    public void logout() {
        clientSender.sendMsg(LOGOUT_CODE, getId(), getId());
    }

    public void register() {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("forName=============");
            conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
            System.out.println("정상적으로 연결되었습니다.");
            System.out.println("\n- MySQL Connection");

            String sql = "insert into user (userid) values (?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, getId());

            int r = pstmt.executeUpdate();

            System.out.println("변경된 row : " + r);

            pstmt.close();
            conn.close();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            System.err.println(" !! <JDBC 오류> Driver load 오류: " + cnfe.getMessage());
        } catch (SQLException se) {
            System.err.println("conn 오류:" + se.getMessage());
            se.printStackTrace();
        } finally {
            try {
                if (conn!= null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
    }

    public void idCheck() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("forName=============");
            conn = DriverManager.getConnection(DB_URL,USERNAME,PASSWORD);
            System.out.println("정상적으로 연결되었습니다.");
            System.out.println("\n- MySQL Connection");

            String sql;
            sql = "SELECT userid, userpw FROM user";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while(rs.next()) {
                if (getId().equals(rs.getString("userid"))) {
                    System.out.println("id 조회 : " + rs.getString("userid"));
                    login();
                }
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            System.err.println(" !! <JDBC 오류> Driver load 오류: " + cnfe.getMessage());
        } catch (SQLException se) {
            System.err.println("conn 오류:" + se.getMessage());
            se.printStackTrace();
        } finally {
            try {
                if (conn!= null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

}
