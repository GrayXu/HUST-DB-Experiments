package GUI;

import IO.DataBase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainGui {

    JPanel jPanel;
    JFrame frame;

    String userName;
    JTextField textDialogName;
    JPasswordField textDialogPsw;
    JDialog dialogLogin;

    public static int authority;
    public DataBase dataBase;
    static int SCREEN_WIDTH;
    static int SCREEN_HEIGHT;
    static String PANEL_MODE = new String("");

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
//            dialogLogin.setIconImage(image);
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
            butLogin.addActionListener(butListener);
            dialogLogin.setResizable(false);

            setCenter(dialogLogin);

            return frame;

        } else {
            JOptionPane.showMessageDialog(frame, "数据库连接失败");
            return null;
        }

    }

    ActionListener butListener = (ActionEvent e) -> {
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
            i.addActionListener(butListener);
        }

        JMenu menuOp = new JMenu("操作");
        ArrayList<JMenuItem> itemsOp = new ArrayList<>();
        itemsOp.add(new JMenuItem("恢复初始"));
        itemsOp.add(new JMenuItem("保存更改"));
        for (JMenuItem i :
                itemsOp) {
            menuOp.add(i);
            i.addActionListener(butListener);
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
            i.addActionListener(butListener);
        }

        JMenu menuManage = new JMenu("管理");
        ArrayList<JMenuItem> itemManage = new ArrayList<>();
        itemManage.add(new JMenuItem("客户"));
        itemManage.add(new JMenuItem("车辆"));
        itemManage.add(new JMenuItem("员工"));
        itemManage.add(new JMenuItem("账户"));
        itemManage.add(new JMenuItem("财务"));



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

    private void setTablePanel(String[][] arrays, String[] columnNames){
        JTable jTable = new JTable(arrays, new String[]{"品牌","车牌号","租金","车况","押金"});
        JScrollPane scrollPane = new JScrollPane(jTable);
        jPanel.add(scrollPane, BorderLayout.NORTH);
        jPanel.updateUI();
    }

    ActionListener changeTableListener = e -> {
        String strClick = e.getActionCommand();
        System.out.println(strClick);
        if (strClick.equals("客户")) {
            if (!PANEL_MODE.equals(strClick)) {//界面如已加载过则不继续加载
                if (!PANEL_MODE.equals(strClick)) {//界面如已加载过则不继续加载
                    frame.getContentPane().removeAll();//移除原有的东西
                    try {
                        setTablePanel(DataBase.getInstance().getCustomerLists(),
                                new String[]{"姓名","年龄","信誉度","是否会员"});
                    } catch (SQLException e1) {
                        noticeMsg("数据库发生错误");
                    }
                }
            }
        } else if (strClick.equals("车辆")) {
            if (!PANEL_MODE.equals(strClick)) {//界面如已加载过则不继续加载
                frame.getContentPane().removeAll();//移除原有的东西
                try {
                    setTablePanel(DataBase.getInstance().getCarLists(),
                            new String[]{"品牌","车牌号","租金","车况","押金"});
                } catch (SQLException e1) {
                    noticeMsg("数据库发生错误");
                }
            }
        } else if (strClick.equals("员工")) {
            if (!PANEL_MODE.equals(strClick)) {//界面如已加载过则不继续加载
                if (!PANEL_MODE.equals(strClick)) {//界面如已加载过则不继续加载
                    frame.getContentPane().removeAll();//移除原有的东西
                    try {
                        setTablePanel(DataBase.getInstance().getStuffLists(),
                                new String[]{"姓名","年龄","工资"});
                    } catch (SQLException e1) {
                        noticeMsg("数据库发生错误");
                    }
                }
            }
        } else if (strClick.equals("账户")) {
            if (!PANEL_MODE.equals(strClick)) {//界面如已加载过则不继续加载
                if (!PANEL_MODE.equals(strClick)) {//界面如已加载过则不继续加载
                    frame.getContentPane().removeAll();//移除原有的东西
                    try {
                        setTablePanel(DataBase.getInstance().getUserLists(),
                                new String[]{"姓名","密码","权限等级"});
                    } catch (SQLException e1) {
                        noticeMsg("数据库发生错误");
                    }
                }
            }
        } else if (strClick.equals("财务")) {

        }
        PANEL_MODE = strClick;
    };
}
