package chat02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrameEmoji extends JFrame implements ActionListener {

    Container contentPane;
    ImageIcon imageIcon_1, imageIcon_2, imageIcon_3, imageIcon_4, imageIcon_5, imageIcon_6, imageIcon_7, imageIcon_8, imageIcon_9;
    JButton btnEmoji_1, btnEmoji_2, btnEmoji_3, btnEmoji_4, btnEmoji_5
            , btnEmoji_6, btnEmoji_7, btnEmoji_8, btnEmoji_9, btnExit;
    String emoji_1 = "/emoji_1";
    String emoji_2 = "/emoji_2";
    String emoji_3 = "/emoji_3";
    String emoji_4 = "/emoji_4";
    String emoji_5 = "/emoji_5";
    String emoji_6 = "/emoji_6";
    String emoji_7 = "/emoji_7";
    String emoji_8 = "/emoji_8";
    String emoji_9 = "/emoji_9";
    ClientSender clientSender;
    static final String EMOJI_CODE = "EMOJ";
    final int FRAME_WIDTH = 800;
    final int FRAME_HEIGHT = 600;

    public FrameEmoji(ClientSender clientSender) {
        super("이모티콘 창");
        this.clientSender = clientSender;
        contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(4, 3));

        imageIcon_1 = new ImageIcon("C:\\Users\\jason\\git\\chatting-app\\src\\main\\java\\images\\emoji_1.png");
        imageIcon_2 = new ImageIcon("C:\\Users\\jason\\git\\chatting-app\\src\\main\\java\\images\\emoji_2.png");
        imageIcon_3 = new ImageIcon("C:\\Users\\jason\\git\\chatting-app\\src\\main\\java\\images\\emoji_3.png");
        imageIcon_4 = new ImageIcon("C:\\Users\\jason\\git\\chatting-app\\src\\main\\java\\images\\emoji_4.png");
        imageIcon_5 = new ImageIcon("C:\\Users\\jason\\git\\chatting-app\\src\\main\\java\\images\\emoji_5.png");
        imageIcon_6 = new ImageIcon("C:\\Users\\jason\\git\\chatting-app\\src\\main\\java\\images\\emoji_6.png");
        imageIcon_7 = new ImageIcon("C:\\Users\\jason\\git\\chatting-app\\src\\main\\java\\images\\emoji_7.png");
        imageIcon_8 = new ImageIcon("C:\\Users\\jason\\git\\chatting-app\\src\\main\\java\\images\\emoji_8.png");
        imageIcon_9 = new ImageIcon("C:\\Users\\jason\\git\\chatting-app\\src\\main\\java\\images\\emoji_9.png");

        btnEmoji_1 = new JButton(imageIcon_1);
        btnEmoji_2 = new JButton(imageIcon_2);
        btnEmoji_3 = new JButton(imageIcon_3);
        btnEmoji_4 = new JButton(imageIcon_4);
        btnEmoji_5 = new JButton(imageIcon_5);
        btnEmoji_6 = new JButton(imageIcon_6);
        btnEmoji_7 = new JButton(imageIcon_7);
        btnEmoji_8 = new JButton(imageIcon_8);
        btnEmoji_9 = new JButton(imageIcon_9);
        btnExit = new JButton("닫기");

        contentPane.add(btnEmoji_1);
        contentPane.add(btnEmoji_2);
        contentPane.add(btnEmoji_3);
        contentPane.add(btnEmoji_4);
        contentPane.add(btnEmoji_5);
        contentPane.add(btnEmoji_6);
        contentPane.add(btnEmoji_7);
        contentPane.add(btnEmoji_8);
        contentPane.add(btnEmoji_9);
        contentPane.add(btnExit);

        btnEmoji_1.addActionListener(this);
        btnEmoji_2.addActionListener(this);
        btnEmoji_3.addActionListener(this);
        btnEmoji_4.addActionListener(this);
        btnEmoji_5.addActionListener(this);
        btnEmoji_6.addActionListener(this);
        btnEmoji_7.addActionListener(this);
        btnEmoji_8.addActionListener(this);
        btnEmoji_9.addActionListener(this);
        btnExit.addActionListener(this);

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String id = FrameId.getId();

        if (e.getSource() == btnExit) {
            this.dispose();
        }

        if (e.getSource() == btnEmoji_1) {
            clientSender.sendMsg(EMOJI_CODE, emoji_1,id);
        }

        if (e.getSource() == btnEmoji_2) {
            clientSender.sendMsg(EMOJI_CODE, emoji_2, id);
        }

        if (e.getSource() == btnEmoji_3) {
            clientSender.sendMsg(EMOJI_CODE, emoji_3, id);
        }

        if (e.getSource() == btnEmoji_4) {
            clientSender.sendMsg(EMOJI_CODE, emoji_4, id);
        }

        if (e.getSource() == btnEmoji_5) {
            clientSender.sendMsg(EMOJI_CODE, emoji_5, id);
        }

        if (e.getSource() == btnEmoji_6) {
            clientSender.sendMsg(EMOJI_CODE, emoji_6, id);
        }

        if (e.getSource() == btnEmoji_7) {
            clientSender.sendMsg(EMOJI_CODE, emoji_7, id);
        }

        if (e.getSource() == btnEmoji_8) {
            clientSender.sendMsg(EMOJI_CODE, emoji_8, id);
        }

        if (e.getSource() == btnEmoji_9) {
            clientSender.sendMsg(EMOJI_CODE, emoji_9, id);
        }
    }
}
