package chat01;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class FrameChat extends JFrame implements ActionListener {

    private Container contentPane;

    JTextArea textArea;
    JTextField textField = new JTextField(15);
    JButton btnTransfer = new JButton("전송");
    JButton btnEmoji = new JButton("이모티콘");
    JButton btnSetting = new JButton("설정");
    JButton btnExit = new JButton("닫기");
    //boolean isFirst = true;
    JPanel panel1;
    JPanel panel2;
    JPanel panel3;
    JScrollPane jScrollPane1;
    JScrollPane jScrollPane2;
    JScrollPane jScrollPane3;
    DefaultTableModel model1;
    DefaultTableModel model2;
    JTable table1;
    JTable table2;
    Socket socket;
    ClientSender clientSender;
    String textLine;
    static final String ENTER = "\n";
    static final String ID_CODE = "UUID";
    static final String MESSAGE_CODE = "MSSG";
    static final String LOGOUT_CODE = "LOUT";

    public FrameChat(Socket socket) {
        super("채팅 프로그램");
        this.socket = socket;
        textArea = new JTextArea();
        //----------------------------------------------------
        String colNames1[] = {"", ""};
        //       model1 = new DefaultTableModel(colNames1, 0); //columnCount, rowCount
        model1 = new DefaultTableModel(colNames1, 1) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1)
                    return ImageIcon.class;
                return Object.class;
            }
        };
        //----------------------------------------------------
        contentPane = getContentPane();
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();
        BorderLayout layout1 = new BorderLayout();
        BorderLayout layout2 = new BorderLayout();
        panel1.setLayout(layout1);
        panel2.setLayout(layout2);
//        panel3.setLayout(new FlowLayout());
        contentPane.add(panel1, BorderLayout.CENTER);
        contentPane.add(panel2, BorderLayout.SOUTH);
//        contentPane.add(panel3, BorderLayout.AFTER_LAST_LINE);
        clientSender = new ClientSender(socket);
        FrameId frameId = new FrameId(clientSender, this);
//        new ClientReceiver(socket, this, frameId).start();
        new ClientReceiver(socket, this, frameId).start();
//        jScrollPane1 = new JScrollPane(textArea);
//        panel1.add(jScrollPane1);


        //----------------------------------------------------
        table1 = new JTable(model1);
        table1.setShowHorizontalLines(false);
        table1.setShowVerticalLines(false);
        resizeColumnWidth(table1);
        jScrollPane2 = new JScrollPane(table1);
        jScrollPane2.getViewport().setBackground(Color.white);
        panel1.add(jScrollPane2, BorderLayout.CENTER);

        //----------------------------------------------------
//        String colNames2[]={"접속 아이디"};
//        model2=new DefaultTableModel(colNames2,0);
//        table2=new JTable(model2);
//        resizeColumnWidth(table2); // column size 내용 길이에 맞추기
//        jScrollPane3=new JScrollPane(table2);
        //panel1.add(jScrollPane3, BorderLayout.EAST);

        panel2.add(textField, BorderLayout.NORTH);
        panel2.add(btnTransfer, BorderLayout.WEST);
        panel2.add(btnEmoji, BorderLayout.CENTER);
        panel2.add(btnSetting, BorderLayout.EAST);
        panel2.add(btnExit, BorderLayout.SOUTH);

        btnTransfer.addActionListener(this);
        btnEmoji.addActionListener(this);
        btnSetting.addActionListener(this);
        btnExit.addActionListener(this);
        textField.addActionListener(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(300, 300, 650, 700);
        setVisible(false);
    }

    public JTable getTable1(){
        return table1;
    }


    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnTransfer) {//전송버튼 눌렀을 경우
            sendMessage();
        } else if (e.getSource() == btnEmoji) {
            new FrameEmoji(clientSender);
        } else if (e.getSource() == btnExit) {
            logout();
            System.exit(0);
        } else if (e.getSource() == textField) {
            sendMessage();
        } else if (e.getSource() == btnSetting) {
            new FrameSettings(clientSender);
        }
    }

    public void changeId() {

    }

    public void sendMessage() {
        String id = FrameId.getId();
        //메세지 입력없이 전송버튼만 눌렀을 경우
        if (textField.getText().equals("")) {
            return;
        }

//        textLine = "[" + id + "] " + textField.getText();
        textLine = textField.getText() + "/"+id;
        clientSender.sendMsg(MESSAGE_CODE, textLine);
        textField.setText("");
    }

    public void logout() {
        clientSender.sendMsg(LOGOUT_CODE, "LOG_OUT");
    }

    public void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width +1 , width);
                width = 100;
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }
    public void updateRowHeights(JTable table)
    {
        for (int row = 0; row < table.getRowCount(); row++)
        {
            int rowHeight = table.getRowHeight();

            for (int column = 0; column < table.getColumnCount(); column++)
            {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }

            table.setRowHeight(row, rowHeight);
        }
    }
}
