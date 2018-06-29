package GUI;

import IO.DataBase;
import Support.DataNameUtils;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class MainGui {

    JPanel jMainPanel;
    private JFrame frame;
    private JTable jTable;//当前界面的Table
    private DefaultTableModel tableModel;

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
    private Vector<String> columns;
    private JScrollPane scrollPane;


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

        new MainGui().RunBabyRun();

    }

    /**
     * 修复字体发虚
     */
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

        for (String aDEFAULT_FONT : DEFAULT_FONT) {
            UIManager.put(aDEFAULT_FONT, new Font("微软雅黑", Font.PLAIN, 12));
        }
    }

    private void RunBabyRun() {
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

            dialogLogin.setDefaultCloseOperation(DISPOSE_ON_CLOSE);


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

        } else {
            JOptionPane.showMessageDialog(frame, "数据库连接失败");
        }

    }

    /**
     * 菜单栏中“其他”的监听器
     */
    private ActionListener otherListener = (ActionEvent e) -> {
        String strClick = e.getActionCommand();
        System.out.println(strClick);
        switch (strClick) {
            case "登录":
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
                break;
            case "切换账户":
                frame.getContentPane().removeAll();
                frame.setVisible(false);
                dialogLogin.setVisible(true);
                PANEL_MODE = "";
                break;
            case "说明":
                noticeMsg("本信息管理系统基于Java Swing进行界面开发，MySQL后台支持\n\t——Power by Gray");
                break;
        }
    };

    /**
     * 初始化主框架
     */
    private void initMainFrame() {
        try {
            jMainPanel = new BackgrouPanel("res/mainBack2.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setContentPane(jMainPanel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("汽车租借信息管理系统——欢迎" + userName);
        initMenu();
        createPopupMenu();
        frame.setSize(1025, 635);
        setCenter(frame);
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
//        itemsOp.add(new JMenuItem("导入"));
        for (JMenuItem i :
                itemsOp) {
            menuOp.add(i);
            i.addActionListener(fileListener);
        }

        JMenu menuReport = new JMenu("统计");
        JMenuItem itemChart = new JMenuItem("统计报表");
        JMenuItem itemCredit = new JMenuItem("用户信誉度");
        menuReport.add(itemChart);
        menuReport.add(itemCredit);
        itemChart.addActionListener(e -> showChart());
        itemCredit.addActionListener(e -> showCredit());


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
        if (authority != 3) {
            mb.add(menuOp);
            mb.add(menuReport);
        }
        mb.add(menuManage);
        mb.add(menuOther);
        frame.setJMenuBar(mb);

    }

    /**
     * 文件选择框
     */
    private ActionListener fileListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            JFileChooser jFileChooser = new JFileChooser();
            File file;
            String path = null;
            if (command.equals("导入")) {
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFileChooser.showOpenDialog(frame);
                file = jFileChooser.getSelectedFile();
                if (file.isFile()) {
                    path = file.getAbsolutePath();
                }
            } else {
                jFileChooser.showSaveDialog(frame);
                file = jFileChooser.getSelectedFile();
                String typeInName = jFileChooser.getName(file);
                if (typeInName == null || typeInName.equals("")) return;
                path = jFileChooser.getCurrentDirectory() + "\\" + typeInName;
            }

            if (path != null) {
                try {
                    String bat;
                    if (command.equals("导出")) {
                        bat = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe lab3 -uroot " + " -pXIANG1569348 -r" + path + " --skip-lock-tables";
                    } else {
                        bat = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe lab3 -uroot -pXIANG1569348 -e source " + path;
                    }
                    System.out.println(bat);
                    Process process = Runtime.getRuntime().exec(bat);//save
                    int com = process.waitFor();
                    if (com == 0) {
                        noticeMsg("操作成功");
                    } else {
                        noticeMsg("操作失败");
                    }

                } catch (IOException | InterruptedException e1) {
                    e1.printStackTrace();
                }
            }


        }
    };


    /**
     * 展示图表
     */
    private void showChart() {
        JDialog chartDialog = new JDialog(frame);
        JPanel contChartPane = new JPanel(new BorderLayout());
        chartDialog.setContentPane(contChartPane);
        chartDialog.setTitle("统计图表");

        JPanel argPanel = new JPanel(new FlowLayout());

        JLabelOpen jLabelF = new JLabelOpen("纵轴：");
        JLabelOpen jLabelM = new JLabelOpen("横轴：");

        JComboBox<String> jComboxFunc = new JComboBox<>(new String[]{"借车", "还车", "损坏维修", "罚款", "流水"});
        JComboBox<String> jComboxMode = new JComboBox<>(new String[]{"年", "月", "日"});


        JPanelOpen jPOF = new JPanelOpen();
        JPanelOpen jPOM = new JPanelOpen();

        jPOF.add(jLabelF);
        jPOF.add(jComboxFunc);
        jPOM.add(jLabelM);
        jPOM.add(jComboxMode);

        JButton jConfirmBut = new JButton("确定");
        jConfirmBut.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));

        argPanel.add(jPOF);
        argPanel.add(jPOM);
        argPanel.add(jConfirmBut);


        JPanelOpen chartPanel = new JPanelOpen();

        jConfirmBut.addActionListener(e -> {
            drawChart(((String) jComboxMode.getSelectedItem()),
                    ((String) jComboxFunc.getSelectedItem()),
                    chartPanel);
            chartDialog.pack();
        });


        contChartPane.add(argPanel, BorderLayout.CENTER);
        contChartPane.add(chartPanel, BorderLayout.SOUTH);

        chartDialog.setSize(new Dimension(600, 500));
        chartDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setCenter(chartDialog);
    }

    //绘制图表界面
    private void drawChart(String mode, String func, JPanel panel) {
        panel.removeAll();
        String horizon;
        if (func.equals("流水")) {
            horizon = "元";
        } else {
            horizon = "次数";
        }
        JFreeChart chart = ChartFactory.createBarChart3D(func, mode, horizon, getChartData(func, mode), PlotOrientation.VERTICAL, false, false, false);

        //设置字体
        CategoryPlot plot = chart.getCategoryPlot();//获取图表区域对象
        CategoryAxis domainAxis = plot.getDomainAxis();         //水平底部列表
        domainAxis.setLabelFont(new Font("黑体", Font.BOLD, 14));         //水平底部标题
        domainAxis.setTickLabelFont(new Font("宋体", Font.BOLD, 12));  //垂直标题
        ValueAxis rangeAxis = plot.getRangeAxis();//获取柱状
        rangeAxis.setLabelFont(new Font("黑体", Font.BOLD, 15));
        chart.getTitle().setFont(new Font("宋体", Font.BOLD, 20));//设置标题字体
        ChartPanel chartP = new ChartPanel(chart);
        chartP.setOpaque(false);

        panel.add(chartP);

    }

    //为图表赋值
    private CategoryDataset getChartData(String func, String mode) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try {
            ArrayList<ArrayList<String>> dataLists = DataBase.getInstance().getAllChartData(mode, func);
            for (ArrayList<String> list : dataLists) {
                int length = list.size();
                double value = Double.parseDouble(list.get(length - 1));
                String colunmKey;
                if (length == 3) {
                    colunmKey = list.get(0) + list.get(1);
                } else {
                    colunmKey = list.get(0);
                }
                dataset.addValue(value, "gray", colunmKey);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            noticeMsg("数据库获取数据出错");
        }


        return dataset;
    }

    /**
     * 窗口放置桌面中央
     *
     * @param c component waited to be reset
     */
    private void setCenter(Component c) {
//        ((Window) c).pack();
        c.setLocation((SCREEN_WIDTH - c.getWidth()) / 2, (SCREEN_HEIGHT - c.getHeight()) / 2);
        c.setVisible(true);
    }

    private void noticeMsg(String in) {//make code elegant
        JOptionPane.showMessageDialog(frame, in);
    }


    /**
     * 根据不同的表，绘制不同的成对Label与TextField或Combobox，内部通过获取搜索栏的新约束条件获得新的结果并重绘表格Panel
     * @param jPanelSearch Search components' parent components
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
        jButton.addActionListener(e -> {
            HashMap<String, String> dataMap = getDataMap(jPanelSearch);
            if (checkSearchData(dataMap)) {
                try {
                    //在这里利用搜索栏中新的约束条件获得新的获取结果
                    Vector<Vector<String>> vectors = null;
                    switch (PANEL_MODE) {
                        case "车辆":
                            vectors = DataBase.getInstance().getCarLists(dataMap);
                            break;
                        case "顾客":
                            vectors = DataBase.getInstance().getCustomerLists(dataMap);
                            break;
                        case "用户":
                            vectors = DataBase.getInstance().getUserLists(dataMap, authority, userName);
                            break;
                        case "员工":
                            vectors = DataBase.getInstance().getStuffLists(dataMap);
                            break;
                        case "事件":
                            vectors = DataBase.getInstance().getInfoLists(dataMap, authority, userName);
                            break;
                    }

                    setTablePanel(vectors, columns);
                    jMainPanel.add(scrollPane, BorderLayout.SOUTH);
                    jMainPanel.updateUI();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    noticeMsg("搜索数据不合法或数据库发生错误");
                }
            }
        });
        jButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        jPanelSearch.add(jButton);
    }

    private void setBlankInSearchPanel(JPanelOpen jPanelSearch, String names[]) {
        Set<String> comboxSet = comboxMap.keySet();

        for (String name : names) {
            JPanelOpen jPanelO = new JPanelOpen();
            JLabelOpen jLO = new JLabelOpen();
            if (name.equals("顾客") || name.equals("经手员工")) continue;//dirty code

            jLO.setText(name);

            if (comboxSet.contains(name)) {
                JComboBox<String> stringJComboBox = getComboBox(name);
                jPanelO.add(jLO);
                jPanelO.add(stringJComboBox);
            } else {
                JTextField jTextField = new JTextField();
                jTextField.setColumns(11);
                jPanelO.add(jLO);
                jPanelO.add(jTextField);
            }

            jPanelSearch.add(jPanelO);
        }
    }

    private HashMap<String, String[]> comboxMap = new HashMap<String, String[]>() {{
        put("车况", new String[]{"", "1", "2", "3", "4", "5"});
        put("是否会员", new String[]{"", "Y", "N"});
        put("权限等级", new String[]{"", "1", "2", "3"});
        put("事件", new String[]{"", "损坏维修", "罚款", "借车", "还车"});
    }};

    private JComboBox<String> getComboBox(String name) {
        JComboBox<String> jComboBox = null;
        String[] contentStrings = comboxMap.get(name);
        if (contentStrings != null) {
            jComboBox = new JComboBox<>(contentStrings);
        }
        return jComboBox;
    }


    /**
     * 展示信誉度统计
     */
    private void showCredit(){
        HashMap<String, Float> dataMap = null;
        HashMap<String, String> nameMap = null;

        try {
            dataMap = DataBase.getInstance().getCredit();
            nameMap = DataBase.getInstance().getMapId2Name();
        } catch (SQLException e) {
            noticeMsg("数据库查询信誉度时发生错误");
            e.printStackTrace();
        }
        if (dataMap == null || nameMap == null) return;

        JDialog dialog = new JDialog(frame);
        dialog.setContentPane(new JPanel());
        dialog.getContentPane().setLayout(new BorderLayout());

        Set<String> keys = dataMap.keySet();
        Vector<String> columns = new Vector<>(Arrays.asList("id", "姓名", "信誉值"));
        Vector<Vector<String>> datas = new Vector<>();
        for (String key: keys) {
            datas.add(new Vector<>(Arrays.asList(key,String.valueOf(nameMap.get(key)),String.valueOf(dataMap.get(key)))));
        }

        DefaultTableModel model = new DefaultTableModel(datas,columns);
        JTable creditTable = new JTable(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        creditTable.getTableHeader().setReorderingAllowed(false);
        creditTable.setModel(model);

        JScrollPane jScrollPane = new JScrollPane(creditTable);
        dialog.getContentPane().add(jScrollPane, BorderLayout.CENTER);
        dialog.pack();
        setCenter(dialog);
        dialog.setTitle("信誉度统计");
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * 加载全新的表格
     * after called, scroll panel would be reset, you should add scroll panel to content panel again.
     *
     * @param vectors data
     * @param columns columns' names
     */
    private void setTablePanel(Vector<Vector<String>> vectors, Vector<String> columns) {
        if (scrollPane != null) {
            jMainPanel.remove(scrollPane);
            scrollPane = null;
        }

        tableModel = new DefaultTableModel(vectors, columns) {
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (updateData(aValue, row, column)) {
                    super.setValueAt(aValue, row, column);//在这里做修改值的限定
                }
            }
        };

        /*
          修改权限的体现
          */
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

        scrollPane = new JScrollPane(jTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    /**
     * 检查单项数据是否格式合法
     *
     * @param name attribute's name
     * @param value just value
     * @return yes or no
     */
    private boolean checkDataLegal(String name, String value) {
        //check is legal or not
        if (value.contains("'") || value.contains(" ") || value.contains("=")) {
            noticeMsg("新数据中含有非法字段(\"'\", \" \"), \"=\"");
            return false;
        }
        if (name.equals("事件")) {
            if (DataNameUtils.eventName.indexOf(value) == -1) {
                noticeMsg("事件只有四种类型：借车、还车、损坏维修、罚款");
                return false;//不合法事件代码
            }
        }
        if (name.equals("时间")) {
            if (value.length() != 10 || value.charAt(4) != '-' || value.charAt(4) != '-' || Pattern.compile("[^\\d-]").matcher(value).find()) {
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
                noticeMsg("无权限操作");
                return false;
            }
        }

        return true;
    }

    //判断是否为鼠标的BUTTON3按钮，BUTTON3为鼠标右键
    private void mouseRightButtonClick(MouseEvent evt, JTable jTable) {

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
            if (jPopupMenu != null) {
                jPopupMenu.show(jTable, evt.getX(), evt.getY());
            }
        }
    }


    /**
     * 修改表格界面的监听器
     */
    private ActionListener changeTableListener = e -> {
        String strClick = e.getActionCommand();
        System.out.println(strClick);
        if (!PANEL_MODE.equals(strClick)) {//界面如已加载过则不继续加载
            PANEL_MODE = strClick;
            frame.getContentPane().removeAll();//移除原有的东西

            Vector<Vector<String>> vectors = null;//数据vertor

            try {
                switch (strClick) {
                    case "顾客":
                        columns = new Vector<>(Arrays.asList(DataNameUtils.customerColumns));
                        vectors = DataBase.getInstance().getCustomerLists();
                        break;
                    case "车辆":
                        columns = new Vector<>(Arrays.asList(DataNameUtils.carColumns));
                        vectors = DataBase.getInstance().getCarLists();
                        break;
                    case "员工":
                        columns = new Vector<>(Arrays.asList(DataNameUtils.stuffColumns));
                        vectors = DataBase.getInstance().getStuffLists();
                        break;
                    case "用户":
                        columns = new Vector<>(Arrays.asList(DataNameUtils.usersColumns));
                        vectors = DataBase.getInstance().getUserLists(authority, userName);
                        break;
                    case "事件":
                        columns = new Vector<>(Arrays.asList(DataNameUtils.infoColumns));
                        vectors = DataBase.getInstance().getInfoLists(authority, userName);
                        break;
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
                noticeMsg("数据库发生错误");
            }

            if (vectors != null && columns != null) {
                setTablePanel(vectors, columns);
                //重新渲染整个界面
                JPanelOpen jPanelSearch = new JPanelOpen();
                setSearchPanel(jPanelSearch);
                jMainPanel.add(jPanelSearch, BorderLayout.CENTER);
                jMainPanel.add(scrollPane, BorderLayout.SOUTH);
                jMainPanel.updateUI();
            }

        }
    };

    private JPopupMenu jPopupMenu;

    //右键菜单
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
        addMenuItem.addActionListener(evt -> setAddRowDialog());

        jPopupMenu.add(delMenuItem);
        jPopupMenu.add(addMenuItem);
    }

    private JDialog dialogAddRow;

    private void setAddRowDialog() {
        //create a dialog
        dialogAddRow = new JDialog(frame);
        dialogAddRow.setTitle("添加");
        dialogAddRow.setLayout(new FlowLayout());
        JPanel contentPanel = new JPanel();
        dialogAddRow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        String[] columnNames = DataNameUtils.getColumnNamesByMode(PANEL_MODE);//从对应的静态工具类中读取列表行名

        Set<String> comboxSet = comboxMap.keySet();

        for (String s : columnNames) {
            if (!s.equals("id") && !s.equals("顾客") && !s.equals("经手员工")) {
                JPanel jPanel = new JPanel();
                JLabel jLabel = new JLabel(s);
                jPanel.add(jLabel, BorderLayout.WEST);
                if (comboxSet.contains(s)) {
                    jPanel.add(getComboBox(s), BorderLayout.EAST);
                } else {
                    JTextField jTextField = new JTextField(10);
                    jPanel.add(jTextField, BorderLayout.EAST);
                }
                contentPanel.add(jPanel);
            }
        }
        JButton jButton = new JButton("确认");


        jButton.addActionListener(e -> {
            //set this hashmap
            HashMap<String, String> map = getDataMap((JPanel) dialogAddRow.getContentPane());
            if (checkNewData(map)) {
                try {
                    DataBase.getInstance().addRow(PANEL_MODE, map);//单例模式获取数据库管理对象，调用对应的添加行函数
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
        for (String key : set) {
            String value = map.get(key);
            if (!checkDataLegal(key, value)) {//一个个检查
                return false;
            }
        }
        return true;
    }

    private boolean checkSearchData(HashMap<String, String> map) {
        Set<String> set = map.keySet();
        for (String key : set) {
            String value = map.get(key);
            if (value.equals("")) continue;
            if (!checkDataLegal(key, value)) {//一个个检查
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
     * 传入一个JPanel父容器
     * 传出的字符串可能有带空
     *
     * @return map filled with data
     */
    private HashMap<String, String> getDataMap(JPanel jPanelIn) {
        HashMap<String, String> map = new HashMap<>();
        Component[] components = jPanelIn.getComponents();
        for (int i = 0; i < components.length - 1; i++) {//不算button
            JPanel jPanelGet = (JPanel) components[i];
            String key = ((JLabel) jPanelGet.getComponents()[0]).getText();
            if (key.equals("顾客") || key.equals("经手员工")) {
                continue;
            }

            String value;
            try {
                value = ((JTextField) jPanelGet.getComponents()[1]).getText();
            } catch (Exception e) {
                value = (String) ((JComboBox) jPanelGet.getComponents()[1]).getSelectedItem();
            }
            map.put(key, value);
            System.out.println("key: " + key + "; value: " + value);
        }
        return map;
    }

    /**
     * hashmap转vector，方便addRow
     *
     * @param map old hashmap
     * @return new vector
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
