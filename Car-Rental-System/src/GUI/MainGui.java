package GUI;

import IO.DataBase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class MainGui {

    JPanel jPanel;
    JFrame frame;
    private JTable jTable;//当前界面的Table
    DefaultTableModel tableModel;

    private String userName;
    private JTextField textDialogName;
    private JPasswordField textDialogPsw;
    private JDialog dialogLogin;

    private static int authority;
    private DataBase dataBase;
    private static int SCREEN_WIDTH;
    private static int SCREEN_HEIGHT;

    private String PANEL_MODE = "";
    private int delete_row_id = -1;

    public static void main(String[] args) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = dim.width;
        SCREEN_HEIGHT = dim.height;

        JFrame getFrame = new MainGui().initDialog();

    }

    public JFrame initDialog() {
        dataBase = DataBase.getInstance();
        frame = new JFrame();

        if (dataBase.initConnect()) {

            dialogLogin = new JDialog();
            dialogLogin.setTitle("汽车租借信息系统");

            dialogLogin.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


            JLabel labelName = new JLabel("用户名:");
            JLabel labelPsw = new JLabel("密码 :");
            textDialogName = new JTextField(10);
            textDialogPsw = new JPasswordField(10);
            JButton butLogin = new JButton("登录");

            JPanel namePanel = new JPanel();
            namePanel.add(labelName, BorderLayout.WEST);
            namePanel.add(textDialogName, BorderLayout.EAST);

            JPanel pswPanel = new JPanel();
            pswPanel.add(labelPsw, BorderLayout.WEST);
            pswPanel.add(textDialogPsw, BorderLayout.EAST);

            dialogLogin.add(namePanel, BorderLayout.NORTH);
            dialogLogin.add(pswPanel, BorderLayout.CENTER);
            dialogLogin.add(butLogin, BorderLayout.SOUTH);
            butLogin.addActionListener(otherListener);
            dialogLogin.setResizable(false);

            setCenter(dialogLogin);

            return frame;

        } else {
            JOptionPane.showMessageDialog(frame, "数据库连接失败");
            return null;
        }

    }

    ActionListener otherListener = (ActionEvent e) -> {
        String strClick = e.getActionCommand();
        System.out.println(strClick);
        if (strClick.equals("登录")) {
            //check and set authority
            String nameInput = textDialogName.getText();
            String pswInput = textDialogPsw.getText();
            System.out.println("name:" + nameInput + "\npsw:" + pswInput);
            userName = nameInput;
            try {
                authority = dataBase.checkUser(nameInput, pswInput);
                if (authority != -1) {
                    dialogLogin.setVisible(false);
                    initMainFrame();

                } else {
                    noticeMsg("用户名或密码错误");
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
                noticeMsg("数据库连接失败");
            }
        } else if (strClick.equals("切换账户")) {
            frame.getContentPane().removeAll();
            frame.setVisible(false);
            dialogLogin.setVisible(true);
            PANEL_MODE = "";
        } else if (strClick.equals("说明")) {
            noticeMsg("本信息管理系统基于Java Swing进行界面开发，MySQL后台支持\n\t——Power by Gray");
        }
    };

    public void initMainFrame() {
        frame.setContentPane(jPanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setCenter(frame);
        frame.setTitle("汽车租借信息管理系统——欢迎" + userName);
        initMenu();
        createPopupMenu();
        frame.setSize(600, 500);
        frame.setVisible(true);

        JTextField field = new JTextField();
        field.setFont(new Font("宋体", Font.BOLD, 18));
        field.setText("汽车租借信息系统 数据库实验 2018春季");
        field.setEditable(false);

        frame.getContentPane().add(field, BorderLayout.CENTER);
        jPanel.updateUI();
    }


    /**
     * 初始化菜单
     */
    private void initMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu menuOther = new JMenu("其他");
        ArrayList<JMenuItem> itemsOther = new ArrayList<>();
        itemsOther.add(new JMenuItem("切换账户"));
        itemsOther.add(new JMenuItem("说明"));
        for (JMenuItem i :
                itemsOther) {
            menuOther.add(i);
            i.addActionListener(otherListener);
        }

        JMenu menuOp = new JMenu("操作");
        ArrayList<JMenuItem> itemsOp = new ArrayList<>();
        itemsOp.add(new JMenuItem("恢复初始"));
        itemsOp.add(new JMenuItem("保存更改"));
        for (JMenuItem i :
                itemsOp) {
            menuOp.add(i);
            i.addActionListener(otherListener);
        }

        JMenu menuReport = new JMenu("报表");
        ArrayList<JMenuItem> itemReport = new ArrayList<>();
        itemReport.add(new JMenuItem("日"));
        itemReport.add(new JMenuItem("月"));
        itemReport.add(new JMenuItem("季度"));
        itemReport.add(new JMenuItem("年"));
        for (JMenuItem i :
                itemReport) {
            menuReport.add(i);
            i.addActionListener(otherListener);
        }

        JMenu menuManage = new JMenu("管理");
        ArrayList<JMenuItem> itemManage = new ArrayList<>();
        itemManage.add(new JMenuItem("客户"));
        itemManage.add(new JMenuItem("车辆"));
        itemManage.add(new JMenuItem("员工"));
        if (authority == 1) {
            itemManage.add(new JMenuItem("管理员"));
        }
        itemManage.add(new JMenuItem("事件"));

        for (JMenuItem i :
                itemManage) {
            menuManage.add(i);
            i.addActionListener(changeTableListener);
        }
        mb.add(menuOp);
        mb.add(menuManage);
        mb.add(menuReport);
        mb.add(menuOther);
        frame.setJMenuBar(mb);

    }

    /**
     * 窗口放置桌面中央
     *
     * @param c
     */
    public void setCenter(Component c) {
        ((Window) c).pack();
        c.setLocation((SCREEN_WIDTH - c.getWidth()) / 2, (SCREEN_HEIGHT - c.getHeight()) / 2);
        c.setVisible(true);
    }

    public void noticeMsg(String in) {
        JOptionPane.showMessageDialog(frame, in);
    }

    /**
     * 加载全新的布局（包括表格）
     * @param vectors
     * @param columns
     */
    private void setTablePanel(Vector<Vector<String>> vectors, Vector<String> columns) {
        tableModel = new DefaultTableModel(vectors, columns){
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                super.setValueAt(aValue, row, column);//在这里做修改值的限定
            }
        };
        //利用列名来控制无法修改id
        if (!columns.get(0).equals("id")) {
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return true;
                }
            };
        } else {
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    if (column == 0) {
                        return false;
                    } else {
                        return true;
                    }
                }
            };
        }
        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.setModel(tableModel);
        jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseRightButtonClick(e, jTable);
            }

        });

        JScrollPane scrollPane = new JScrollPane(jTable);
        scrollPane.setPreferredSize(new Dimension(500, 350));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jPanel.add(scrollPane, BorderLayout.NORTH);
        jPanel.updateUI();
    }

    /**
     * 弹出右键窗口逻辑
     * @param evt
     * @param jTable
     */
    private void mouseRightButtonClick(MouseEvent evt, JTable jTable) {
        //判断是否为鼠标的BUTTON3按钮，BUTTON3为鼠标右键
        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            //通过点击位置找到点击为表格中的行
            int focusedRowIndex = jTable.rowAtPoint(evt.getPoint());
            if (focusedRowIndex == -1) {
                return;
            }
            delete_row_id = focusedRowIndex;
            //将表格所选项设为当前右键点击的行
            jTable.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
            //弹出菜单
            jPopupMenu.show(jTable, evt.getX(), evt.getY());
        }
    }

    ActionListener changeTableListener = e -> {
        String strClick = e.getActionCommand();
        System.out.println(strClick);
        if (!PANEL_MODE.equals(strClick)) {//界面如已加载过则不继续加载
            frame.getContentPane().removeAll();//移除原有的东西
            if (strClick.equals("客户")) {
                try {
                    Vector<String> columns = new Vector<>(Arrays.asList("id", "姓名", "年龄", "信誉度", "是否会员"));
                    Vector<Vector<String>> vectors = DataBase.getInstance().getCustomerLists();
                    setTablePanel(vectors, columns);
                } catch (SQLException e1) {
                    noticeMsg("数据库发生错误");
                }
            } else if (strClick.equals("车辆")) {
                try {
                    Vector<String> columns = new Vector<>(Arrays.asList("id", "品牌", "车牌号", "租金", "车况", "押金"));
                    Vector<Vector<String>> vectors = DataBase.getInstance().getCarLists();
                    setTablePanel(vectors, columns);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    noticeMsg("数据库发生错误");
                }
            } else if (strClick.equals("员工")) {
                try {
                    Vector<String> columns = new Vector<>(Arrays.asList("id", "姓名", "年龄"));
                    Vector<Vector<String>> vectors = DataBase.getInstance().getStuffLists();
                    setTablePanel(vectors, columns);
                } catch (SQLException e1) {
                    noticeMsg("数据库发生错误");
                }
            } else if (strClick.equals("管理员")) {
                try {
                    Vector<String> columns = new Vector<>(Arrays.asList("姓名", "密码", "权限等级"));
                    Vector<Vector<String>> vectors = DataBase.getInstance().getUserLists();
                    setTablePanel(vectors, columns);
                } catch (SQLException e1) {
                    noticeMsg("查询管理员数据时，数据库发生错误");
                }
            } else if (strClick.equals("事件")) {
                try {
                    Vector<String> columns = new Vector<>(Arrays.asList("id", "流水", "车牌","事件","备注","事件","经手员工"));
                    Vector<Vector<String>> vectors = DataBase.getInstance().getInfoLists();
                    setTablePanel(vectors, columns);
                } catch (SQLException e1) {
                    noticeMsg("查询事件数据时，数据库发生错误");
                }
            }
            PANEL_MODE = strClick;
        }
    };

    JPopupMenu jPopupMenu;

    private void createPopupMenu() {
        jPopupMenu = new JPopupMenu();

        JMenuItem delMenuItem = new JMenuItem();
        delMenuItem.setText("删除本行");
        delMenuItem.addActionListener(evt -> {
            int id = Integer.valueOf ((String) jTable.getValueAt(delete_row_id,0));
            try {
                DataBase.getInstance().deleteRow(PANEL_MODE, id);
                tableModel.removeRow(delete_row_id);
            } catch (SQLException e) {
                e.printStackTrace();
                noticeMsg("删除失败");
            }
        });

        JMenuItem addMenuItem = new JMenuItem();
        addMenuItem.setText("添加新行");
        delMenuItem.addActionListener(evt -> {
            HashMap<String, String> map = getNewRow();
            try {
                DataBase.getInstance().addRow(PANEL_MODE, map);
//                tableModel.addRow();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        jPopupMenu.add(delMenuItem);
    }

    private HashMap<String, String> getNewRow(){
        HashMap<String, String> map = new HashMap<>();

        //create a dialog

        return map;
    }

}
