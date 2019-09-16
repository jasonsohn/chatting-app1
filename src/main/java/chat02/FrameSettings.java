package chat02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrameSettings extends JFrame implements ActionListener {

    private Container contentPane;
    private ClientSender clientSender;
    JTextField textField = new JTextField(15);
    JTextArea textArea = new JTextArea("");
    JButton btnChange = new JButton("변경");
    JButton btnExit = new JButton("닫기");
    JPanel panel1;
    JPanel panel2;

    final int FRAME_WIDTH = 300;
    final int FRAME_HEIGHT = 200;

    public FrameSettings(ClientSender clientSender) {
        super("설정 변경");
        this.clientSender = clientSender;
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        panel1 = new JPanel();
        panel2 = new JPanel();
        BorderLayout layout1 = new BorderLayout();
        BorderLayout layout2 = new BorderLayout();
        panel1.setLayout(layout1);
        panel2.setLayout(layout2);
        contentPane.add(panel1, BorderLayout.CENTER);
        contentPane.add(panel2, BorderLayout.SOUTH);
        panel1.add(textField, BorderLayout.NORTH);
        panel1.add(textArea, BorderLayout.SOUTH);
        panel2.add(btnChange, BorderLayout.WEST);
        panel2.add(btnExit, BorderLayout.EAST);

        btnChange.addActionListener(this);
        btnExit.addActionListener(this);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnChange) {
            changeName();
            this.dispose();
        } else if(e.getSource() == btnExit) {
            this.dispose();
        }
    }

    public void changeName() {
//        clientSender.sendMsg("CHNG", getId()+"/"+textField.getText());
        clientSender.sendMsg("CHNG", getId()+"/"+textField.getText(), FrameId.getId());
    }

    public String getId() {
        return FrameId.textField.getText();
    }
}
