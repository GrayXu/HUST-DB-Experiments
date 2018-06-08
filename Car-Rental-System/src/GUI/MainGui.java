package GUI;

import IO.DataBase;
import Support.DataNameUtils;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class MainGui {

    JPanel jMainPanel;
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

    private String PANEL_MODE = "";//users, stuff, car ....
    private int delete_row_id = -1;

    public static void main(String[] args) {

        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIManager.put("RootPane.setupButtonVisible", false);
        setFontForBeautyEye();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = dim.width;
        SCREEN_HEIGHT = dim.height;

        JFrame getFrame = new MainGui().initDialog();

    }

    private static void setFontForBeautyEye() {
        String[] DEFAULT_FONT = new String[]{
                "Table.font"
                , "TableHeader.font"
                , "CheckBox.font"
                , "Tree.font"
                , "Viewport.font"
                , "ProgressBar.font"
                , "RadioButtonMenuItem.font"
                , "ToolBar.font"
                , "ColorChooser.font"
                , "ToggleButton.font"
                , "Panel.font"
                , "TextArea.font"
                , "Menu.font"
                , "TableHeader.font"
                , "OptionPane.font"
                , "MenuBar.font"
                , "Button.font"
                , "Label.font"
                , "PasswordField.font"
                , "ScrollPane.font"
                , "MenuItem.font"
                , "ToolTip.font"
                , "List.font"
                , "EditorPane.font"
                , "Table.font"
                , "TabbedPane.font"
                , "RadioButton.font"
                , "CheckBoxMenuItem.font"
                , "TextPane.font"
                , "PopupMenu.font"
                , "TitledBorder.font"
                , "ComboBox.font"
        };

        for (int i = 0; i < DEFAULT_FONT.length; i++) {
            UIManager.put(DEFAULT_FONT[i], new Font("微软雅黑", Font.PLAIN, 12));
        }
    }

    public JFrame initDialog() {
        dataBase = DataBase.getInstance();
        frame = new JFrame();//顺便初始化一下父容器

        if (dataBase.initConnect()) {

            dialogLogin = new JDialog();
            try {
                dialogLogin.setContentPane(new BackgrouPanel("res/loginBackground.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            dialogLogin.setTitle("汽车租借信息系统");

            dialogLogin.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


            JLabel labelName = new JLabel("用户名:");
            JLabel labelPsw = new JLabel("密码 :");
            labelPsw.setForeground(Color.white);
            labelName.setForeground(Color.white);

            textDialogName = new JTextField(17);
            textDialogPsw = new JPasswordField(10);
            JButton butLogin = new JButton("登录");

            JPanelOpen namePanel = new JPanelOpen();
            namePanel.add(labelName, BorderLayout.WEST);
            namePanel.add(textDialogName, BorderLayout.EAST);

            JPanelOpen pswPanel = new JPanelOpen();
            pswPanel.add(labelPsw, BorderLayout.WEST);
            pswPanel.add(textDialogPsw, BorderLayout.EAST);

            dialogLogin.getContentPane().setLayout(new BorderLayout());
            dialogLogin.getContentPane().add(namePanel, BorderLayout.NORTH);
            dialogLogin.getContentPane().add(pswPanel, BorderLayout.CENTER);
            dialogLogin.getContentPane().add(butLogin, BorderLayout.SOUTH);
            butLogin.addActionListener(otherListener);
            dialogLogin.setSize(new Dimension(270, 200));
            dialogLogin.setResizable(false);
            setCenter(dialogLogin);
            return frame;

        } else {
            JOptionPane.showMessageDialog(frame, "数据库连接失败");
            return null;
        }

    }

    /**
     * 菜单栏中“其他”的监听器
     */
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

    /**
     * 初始化主框架
     */
    public void initMainFrame() {
        try {
            jMainPanel = new BackgrouPanel("res/mainBack2.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setContentPane(jMainPanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setCenter(frame);
        frame.setTitle("汽车租借信息管理系统——欢迎" + userName);
        initMenu();
        createPopupMenu();
        frame.setSize(1025, 635);
        frame.setVisible(true);

        JTextFiledOpen field = new JTextFiledOpen();
        field.setFont(new Font("宋体", Font.BOLD, 30));
        field.setText("汽车租借信息系统 数据库实验 2018春季");
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(null);
        field.setEditable(false);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(field, BorderLayout.CENTER);
        jMainPanel.updateUI();
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

        JMenu menuOp = new JMenu("文件");
        ArrayList<JMenuItem> itemsOp = new ArrayList<>();
        itemsOp.add(new JMenuItem("导出"));
        itemsOp.add(new JMenuItem("导入"));
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
        itemManage.add(new JMenuItem("车辆"));//普通用户不能修改车辆表, we define this logic in updateData function
        if (authority != 3) {
            itemManage.add(new JMenuItem("顾客"));
            itemManage.add(new JMenuItem("员工"));
        }
        //以下两张表普通用户只能看到与他相关的
        itemManage.add(new JMenuItem("用户"));
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
//        ((Window) c).pack();
        c.setLocation((SCREEN_WIDTH - c.getWidth()) / 2, (SCREEN_HEIGHT - c.getHeight()) / 2);
        c.setVisible(true);
    }

    public void noticeMsg(String in) {//make code elegant
        JOptionPane.showMessageDialog(frame, in);
    }

    /**
     * initialize search panel
     * TODO: 搜索框也要做权限设置
     */
    private void setSearchPanel(JPanelOpen jPanelSearch) {
        jPanelSearch.setLayout(new FlowLayout());
        if (PANEL_MODE.equals("车辆")) {
            setBlankInSearchPanel(jPanelSearch, DataNameUtils.carColumns);
        } else if (PANEL_MODE.equals("顾客")) {
            setBlankInSearchPanel(jPanelSearch, DataNameUtils.customerColumns);
        }
        if (PANEL_MODE.equals("用户")) {
            setBlankInSearchPanel(jPanelSearch, DataNameUtils.usersColumns);
        }
        if (PANEL_MODE.equals("事件")) {
            setBlankInSearchPanel(jPanelSearch, DataNameUtils.infoColumns);
        }
        if (PANEL_MODE.equals("员工")) {
            setBlankInSearchPanel(jPanelSearch, DataNameUtils.stuffColumns);
        }
        JButton jButton = new JButton("搜索");
        jButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        jPanelSearch.add(jButton);
    }

    private void setBlankInSearchPanel(JPanelOpen jPanelSearch, String names[]) {
        for (int i = 0; i < names.length; i++) {
            JPanelOpen jPanelO = new JPanelOpen();
            JLabelOpen jLO = new JLabelOpen();
            jLO.setText(names[i]);
            JTextField jTextField = new JTextField();
            jTextField.setColumns(11);
            jPanelO.add(jLO);
            jPanelO.add(jTextField);
            jPanelSearch.add(jPanelO);
        }
    }

    /**
     * 加载全新的布局（包括表格）
     *
     * @param vectors
     * @param columns
     */
    private void setTablePanel(Vector<Vector<String>> vectors, Vector<String> columns) {
        tableModel = new DefaultTableModel(vectors, columns) {
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (updateData(aValue, row, column)) {
                    super.setValueAt(aValue, row, column);//在这里做修改值的限定
                }
            }
        };

        JPanelOpen jPanelSearch = new JPanelOpen();
        setSearchPanel(jPanelSearch);

        /**
         * 修改权限的体现
         * */
        if ((PANEL_MODE.equals("车辆") || PANEL_MODE.equals("事件")) & authority == 3) {//顾客不能修改车辆表和事件表
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        } else if (PANEL_MODE.equals("用户") & authority != 1) {//除了超级管理员，不能修改权限和绑定顾客
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column != 2 && column != 3;
                }
            };
        } else if (PANEL_MODE.equals("事件") && authority != 3) {//修改事件表的时候在顾客和经手员工的地方，只能修改对应id
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column != 4 && column != 9 && column != 0;
                }
            };
        } else if (columns.get(0).equals("id")) {//id不能被修改
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column != 0;
                }
            };

        } else {
            jTable = new JTable() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return true;
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
        scrollPane.setPreferredSize(new Dimension(1000, 350));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jMainPanel.add(jPanelSearch, BorderLayout.CENTER);
        jMainPanel.add(scrollPane, BorderLayout.SOUTH);
        jMainPanel.updateUI();
    }

    public boolean checkDataLegal(String name, String value) {
        //check is legal or not
        //TODO: 外键参考部分、捆绑更新(车牌可以自动更新)
        if (value.contains("'") || value.contains(" ")) {
            noticeMsg("新数据中含有非法字段(\"'\", \" \")");
            return false;
        }
        if (name.equals("事件")) {
            if (DataNameUtils.eventName.indexOf(value) == -1) {
                noticeMsg("事件只有四种类型：借车、还车、损坏维修、罚款");
                return false;//不合法事件代码
            }
        }
        if (name.equals("时间")) {
            if (value.length() != 8 || Pattern.compile("[^\\d]+").matcher(value).find()) {
                noticeMsg("时间格式错误");
                return false;
            }
        }
        if (name.equals("车牌号")) {
            if (value.length() != 7) {
                noticeMsg("车牌号格式错误");
                return false;
            }
        }
        if (name.equals("车况")) {
            if (Pattern.compile("[^\\d]+").matcher(value).find()) {//保证全数字，防止后面强转出错
                noticeMsg("只能输入数字");
                return false;
            }
            if (Integer.valueOf(value) < 1 || Integer.valueOf(value) > 5) {
                noticeMsg("只能输入1-5的数字");
                return false;
            }
        }
        if (name.equals("是否会员")) {
            if (!value.equals("Y") && !value.equals("N")) {
                noticeMsg("只能输入Y或N");
                return false;
            }
        }
        if (name.equals("权限等级")) {
            if (Pattern.compile("[^\\d]+").matcher(value).find()) {//保证全数字，防止后面强转出错
                noticeMsg("只能输入数字");
                return false;
            }
            int valueInt = Integer.valueOf(value);
            if (valueInt < 1 || valueInt > 3) {
                noticeMsg("只能输入1-3的数字");
                return false;
            }
            if (valueInt != 3 && authority != 1) {
                noticeMsg("非法提升权限");
                return false;
            }
        }


        return true;
    }

    /**
     * 弹出右键窗口逻辑
     *
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


    /**
     * 修改表格界面的监听器
     */
    ActionListener changeTableListener = e -> {
        String strClick = e.getActionCommand();
        System.out.println(strClick);
        if (!PANEL_MODE.equals(strClick)) {//界面如已加载过则不继续加载
            PANEL_MODE = strClick;
            frame.getContentPane().removeAll();//移除原有的东西
            if (strClick.equals("顾客")) {
                try {
                    Vector<String> columns = new Vector<>(Arrays.asList(DataNameUtils.customerColumns));
                    Vector<Vector<String>> vectors = DataBase.getInstance().getCustomerLists();
                    setTablePanel(vectors, columns);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    noticeMsg("数据库发生错误");
                }
            } else if (strClick.equals("车辆")) {
                try {
                    Vector<String> columns = new Vector<>(Arrays.asList(DataNameUtils.carColumns));
                    Vector<Vector<String>> vectors = DataBase.getInstance().getCarLists();
                    setTablePanel(vectors, columns);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    noticeMsg("数据库发生错误");
                }
            } else if (strClick.equals("员工")) {
                try {
                    Vector<String> columns = new Vector<>(Arrays.asList(DataNameUtils.stuffColumns));
                    Vector<Vector<String>> vectors = DataBase.getInstance().getStuffLists();
                    setTablePanel(vectors, columns);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    noticeMsg("数据库发生错误");
                }
            } else if (strClick.equals("用户")) {
                try {
                    Vector<String> columns = new Vector<>(Arrays.asList(DataNameUtils.usersColumns));
                    Vector<Vector<String>> vectors = DataBase.getInstance().getUserLists(authority, userName);
                    setTablePanel(vectors, columns);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    noticeMsg("查询用户数据时，数据库发生错误");
                }
            } else if (strClick.equals("事件")) {
                try {
                    Vector<String> columns = new Vector<>(Arrays.asList(DataNameUtils.infoColumns));
                    Vector<Vector<String>> vectors = DataBase.getInstance().getInfoLists();
                    setTablePanel(vectors, columns);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    noticeMsg("查询事件数据时，数据库发生错误");
                }
            }

        }
    };

    JPopupMenu jPopupMenu;

    private void createPopupMenu() {
        if (authority == 3) return;
        jPopupMenu = new JPopupMenu();

        JMenuItem delMenuItem = new JMenuItem();
        delMenuItem.setText("删除本行");
        delMenuItem.addActionListener(evt -> {
            String key = (String) jTable.getValueAt(delete_row_id, 0);
            try {
                DataBase.getInstance().deleteRow(PANEL_MODE, key);
                tableModel.removeRow(delete_row_id);
            } catch (SQLException e) {
                e.printStackTrace();
                noticeMsg("删除失败");
            }
        });

        JMenuItem addMenuItem = new JMenuItem();
        addMenuItem.setText("添加新行");
        addMenuItem.addActionListener(evt -> {
            setAddRowDialog();
        });

        jPopupMenu.add(delMenuItem);
        jPopupMenu.add(addMenuItem);
    }


    JDialog dialogAddRow;

    private void setAddRowDialog() {
        //create a dialog
        dialogAddRow = new JDialog(frame);
        dialogAddRow.setTitle("添加");
        dialogAddRow.setLayout(new FlowLayout());
        JPanel contentPanel = new JPanel();
        dialogAddRow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        String[] columnNames = DataNameUtils.getColumnNamesByMode(PANEL_MODE);
        for (String s : columnNames) {
            if (!s.equals("id")) {
                JPanel jPanel = new JPanel();
                JLabel jLabel = new JLabel(s);
                JTextField jTextField = new JTextField(10);
                jPanel.add(jLabel, BorderLayout.WEST);
                jPanel.add(jTextField, BorderLayout.EAST);
                contentPanel.add(jPanel);
            }
        }
        JButton jButton = new JButton("确认");


        jButton.addActionListener(e -> {
            //set this hashmap
            HashMap<String, String> map = getDataMap();
            if (checkNewData(map)) {
                try {
                    DataBase.getInstance().addRow(PANEL_MODE, map);
                    tableModel.addRow(map2vector(map));
                    dialogAddRow.dispose();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    noticeMsg("新数据不合法或数据库发生错误");
                }
            }
        });
        contentPanel.add(jButton);
        dialogAddRow.setContentPane(contentPanel);
        dialogAddRow.pack();
        setCenter(dialogAddRow);
    }

    private boolean checkNewData(HashMap<String, String> map) {
        Set<String> set = map.keySet();
        Iterator<String> i = set.iterator();
        while (i.hasNext()){
            String key = i.next();
            String value = map.get(key);
            if (value == null) continue;
            if (!checkDataLegal(key,value)){//一个个检查
                return false;
            }
        }
        return true;
    }

    /**
     * 更新表格数据
     */
    private boolean updateData(Object aValue, int row, int column) {

        String[] columnNames = DataNameUtils.getColumnNamesByMode(PANEL_MODE);
        try {
//            if (checkDataLegal(columnNames[column], aValue.toString(), (String) jTable.getValueAt(row, 0))) {
            if (checkDataLegal(columnNames[column], aValue.toString())) {
                DataBase.getInstance().updateData(PANEL_MODE, columnNames[column], aValue.toString(), (String) jTable.getValueAt(row, 0));
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            noticeMsg("新数据格式不合法");
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 获得用户填入的新行的数据
     *
     * @return
     */
    private HashMap<String, String> getDataMap() {
        HashMap<String, String> map = new HashMap<>();
        Component[] components = dialogAddRow.getContentPane().getComponents();
        for (int i = 0; i < components.length - 1; i++) {//不算button
            JPanel jPanel = (JPanel) components[i];
            String key = ((JLabel) jPanel.getComponents()[0]).getText();
            String value = ((JTextField) jPanel.getComponents()[1]).getText();
            map.put(key, value);
            System.out.println("key: " + key + "; value: " + value);
        }
        return map;
    }

    /**
     * hashmap转vector，方便addRow
     *
     * @param map
     * @return
     */
    private Vector<String> map2vector(HashMap<String, String> map) {
        Vector<String> vector = new Vector<>();

        String[] columnNames = DataNameUtils.getColumnNamesByMode(PANEL_MODE);
        for (String s : columnNames) {
            String v = map.get(s);
            if (v != null) {
                vector.add(v);
            } else {
                vector.add("");
            }
        }

        return vector;
    }

}
