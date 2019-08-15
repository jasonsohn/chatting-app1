package chat01;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class JTableExample {
    JFrame jFrame = new JFrame("JTable 예제");

    String columnNames[] =
            {"상품번호", "상품이름", "상품가격", "상품설명"};

    Object rowData[][] =
            {
                    {1, "맛동산", 100, "오리온"},
                    {2, "아폴로", 200, "불량식품"},
                    {3, "칸쵸코", 300, "과자계의 레전드"}};


    //DefaultTableModel을 선언하고 데이터 담기
    DefaultTableModel defaultTableModel = new DefaultTableModel(rowData, columnNames) {
        @Override
        public Class<?> getColumnClass(int column){
            if(column==3)
                return ImageIcon.class;
            return Object.class;
        }
    };

    //JTable에 DefaultTableModel을 담기
    JTable jTable = new JTable(defaultTableModel);

    //JScrollPane에 JTable을 담기
    JScrollPane jScollPane = new JScrollPane(jTable);

    public JTableExample() {
        jFrame.add(jScollPane);

        //행 한줄 추가!
        Object [] temporaryObject = { 4, "초코송이", 500, "식품계의 절대강자" };
        defaultTableModel.addRow(temporaryObject);

        try {
            ImageIcon icon1 = null;
            ImageIcon icon2 = null;
            icon1=new ImageIcon(ImageIO.read(new File("C:\\dev\\images\\emoji_1.png")));
            icon2=new ImageIcon(ImageIO.read(new File("C:\\dev\\images\\emoji_11.jpg")));
            Object[] rowData1={"1", "2", "3", icon1};
            Object[] rowData2={"4", "5", "6", icon2};
            defaultTableModel.addRow(rowData1);
            defaultTableModel.addRow(rowData2);
        } catch (IOException e) {

        }
        resizeColumnWidth(jTable);
        updateRowHeights(jTable);

        //행과 열 갯수 구하기
        System.out.println(defaultTableModel.getRowCount());
        System.out.println(defaultTableModel.getColumnCount());

        //컬럼(열)의 index는 0부터 시작한다!!
        System.out.println(defaultTableModel.getColumnName(0));

        //0행을 삭제하면 제목행을 제외하고 첫째행을 삭제한다!!
        //defaultTableModel.removeRow(0);

        //값을 얻어올 때도 0부터 index가 시작된다는 것에 주의한다!!
        System.out.println(defaultTableModel.getValueAt(2, 2));

        //특정 좌표의 값을 바꾸는 것은 setValueAt()
        defaultTableModel.setValueAt("5000", 2, 2);

        //테이블에 Row를 미리 선택한 상태로 만들기!
        jTable.setRowSelectionInterval(1, 1);

        jFrame.setSize(500, 800);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width +1 , width);
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

    public static void main(String[] args) {
        new JTableExample();
    }
}
