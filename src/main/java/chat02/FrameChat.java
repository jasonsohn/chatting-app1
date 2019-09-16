package chat02;

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

    JPanel panel1;
    JPanel panel2;
    JScrollPane jScrollPane2;
    DefaultTableModel model1;
    JTable table1;
    Socket socket;
    ClientSender clientSender;
    String textLine;
    static final String MESSAGE_CODE = "MSSG";
    static final String LOGOUT_CODE = "LOUT";

    public FrameChat(Socket socket) {
        super("채팅 프로그램");
        this.socket = socket;
        textArea = new JTextArea();
        //----------------------------------------------------
        String colNames1[] = {"", ""};
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
        BorderLayout layout1 = new BorderLayout();
        BorderLayout layout2 = new BorderLayout();
        panel1.setLayout(layout1);
        panel2.setLayout(layout2);
        contentPane.add(panel1, BorderLayout.CENTER);
        contentPane.add(panel2, BorderLayout.SOUTH);
        clientSender = new ClientSender(socket);
        FrameId frameId = new FrameId(clientSender, this);
        new ClientReceiver(socket, this, frameId).start();


        //----------------------------------------------------
        table1 = new JTable(model1);
        table1.setShowHorizontalLines(false);
        table1.setShowVerticalLines(false);
        resizeColumnWidth(table1);
        jScrollPane2 = new JScrollPane(table1);
        jScrollPane2.getViewport().setBackground(Color.white);
        panel1.add(jScrollPane2, BorderLayout.CENTER);

        //----------------------------------------------------

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

    public void sendMessage() {
        String id = FrameId.getId();
        //메세지 입력없이 전송버튼만 눌렀을 경우
        if (textField.getText().equals("")) {
            return;
        }

        textLine = textField.getText();
        clientSender.sendMsg(MESSAGE_CODE, textLine, id);
        textField.setText("");
    }

    public void logout() {
        clientSender.sendMsg(LOGOUT_CODE, "LOG_OUT", FrameId.getId());

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
