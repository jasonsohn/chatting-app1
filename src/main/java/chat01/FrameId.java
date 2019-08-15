package chat01;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrameId  extends JFrame implements ActionListener {

    static JTextField textField = new JTextField(8);
    static final String MESSAGE_CODE = "MSSG";
    static final String LOGOUT_CODE = "LOUT";
    JButton btnInsert = new JButton("입력");
    JButton btnExit = new JButton("닫기");
    JTextArea textArea = new JTextArea("아이디 입력하세요.");
    ClientSender clientSender;
    FrameChat frameChat;

    public FrameId(ClientSender clientSender, FrameChat frameChat) {
        super("아이디");
        this.clientSender = clientSender;
        this.frameChat = frameChat;

        setLayout(new FlowLayout());
        setBounds(300, 300, 350, 300);
        add(new JLabel("아이디"));

        add(textField);
        add(btnInsert);
        add(btnExit);
        add(textArea);
        setVisible(true);
        btnInsert.addActionListener(this);
        btnExit.addActionListener(this);
        textField.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnInsert) {
            login();
        } else if (e.getSource() == btnExit) {
            logout();
            System.exit(0);
        } else if (e.getSource() == textField) {
            login();
        }

    }

    public void login() {
        clientSender.sendMsg("LOGN", getId());
        //frameChat.isFirst = false;
        //frameChat.setVisible(true);
        //this.dispose();
    }

    public void logout() {
        clientSender.sendMsg(LOGOUT_CODE, getId());
    }


    static public String getId() {
        return textField.getText();
    }
}
