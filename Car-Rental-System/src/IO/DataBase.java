package IO;

import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;

public class DataBase {

    public static final String URL = "jdbc:mysql://localhost:3306/lab3?serverTimezone=UTC&useSSL=false";
    public static final String USER = "root";
    public static final String PASSWORD = "XIANG1569348";

    private Connection connection;

    /**
     * 单例
     */
    private static class InnerHelper {
        private final static DataBase dataBase = new DataBase();
    }

    private DataBase() {
    }

    public static DataBase getInstance() {
        return InnerHelper.dataBase;
    }

    /**
     * @return status of connection
     */
    public boolean initConnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * return users' authority
     *
     * @param name
     * @param psw
     * @return authority, -1->no found, 1->root user, 2->administrator, 3->normal user
     * @throws SQLException
     */
    public int checkUser(String name, String psw) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
        while (resultSet.next()) {
            if (resultSet.getString("name").equals(name)) {
                if (resultSet.getString("password").equals(psw)) {
                    return resultSet.getInt("author");
                }
            }
        }
        statement.close();
        return -1;
    }

    /**
     * 以下五个getLists都为从数据库中获得对应的数据
     *
     * @return
     * @throws SQLException
     */
    public Vector<Vector<String>> getCarLists() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM car");
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()) {
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {//从1开始，很奇怪的设定。。。
                tempList.add(resultSet.getString(i));
            }
            lists.add(tempList);
        }
        statement.close();
        return lists2vectors(lists);
    }

    public Vector<Vector<String>> getCustomerLists() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM customer");
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()) {
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {//从1开始，很奇怪的设定。。。
                tempList.add(resultSet.getString(i));
            }
            lists.add(tempList);
        }
        statement.close();
        return lists2vectors(lists);
    }

    public Vector<Vector<String>> getStuffLists() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM stuff");
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()) {
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {//从1开始，很奇怪的设定。。。
                tempList.add(resultSet.getString(i));
            }
            lists.add(tempList);
        }
        statement.close();
        return lists2vectors(lists);
    }

    public Vector<Vector<String>> getUserLists() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()) {
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {//从1开始，很奇怪的设定。。。
                tempList.add(resultSet.getString(i));
            }
            lists.add(tempList);
        }
        statement.close();
        return lists2vectors(lists);
    }

    public Vector<Vector<String>> getInfoLists() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT info.infoid ,info.moychange, car.license, info.event, info.detailevent, info.time, stuff.name\n" +
                "FROM info,car,stuff\n" +
                "WHERE info.license = car.license AND info.stuffid = stuff.id\n");
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()) {
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {//从1开始，很奇怪的设定。。。
                if (i == 4) {
                    switch (resultSet.getInt(i)) {
                        case 1:
                            tempList.add("损坏");
                            break;
                        case 2:
                            tempList.add("罚款");
                            break;
                        case 3:
                            tempList.add("借车");
                            break;
                        case 4:
                            tempList.add("还车");
                            break;
                    }
                } else {
                    tempList.add(resultSet.getString(i));
                }
            }
            lists.add(tempList);
        }
        statement.close();
        return lists2vectors(lists);
    }

    /**
     * 在数据库中级联删除一行
     *
     * @param tableMode
     * @param id
     * @throws SQLException
     */
    public void deleteRow(String tableMode, int id) throws SQLException {
        Statement statement = connection.createStatement();
        String tableName = tableMode2Name(tableMode);
        if (tableName != null) {
            String sql = "DELETE FROM " + tableName + " WHERE id = " + id;
            System.out.println(sql);
            statement.execute(sql);
        }
        statement.close();
    }

    /**
     * 更新数据
     * @param value
     * @throws SQLException
     */
    public void updateData(String value) throws SQLException {
        Statement statement = connection.createStatement();
    }

    @Nullable
    private String name2name(String strIn) {
        if (strIn.equals("姓名")) {
            return "name";
        } else if (strIn.equals("品牌")) {
            return "brand";
        } else if (strIn.equals("车牌号")) {
            return "license";
        } else if (strIn.equals("租金")) {
            return "cost";
        } else if (strIn.equals("车况")) {
            return "status";
        } else if (strIn.equals("年龄")) {
            return "age";
        } else if (strIn.equals("是否会员")) {
            return "member";
        } else if (strIn.equals("密码")) {
            return "password";
        } else if (strIn.equals("权限等级")) {
            return "author";
        } else if (strIn.equals("押金")) {
            return "pledge";
        }
        return null;
    }

    private boolean isIntColumn(String strIn) {
        if (strIn.equals("姓名")) {
            return false;
        } else if (strIn.equals("品牌")) {
            return false;
        } else if (strIn.equals("车牌号")) {
            return false;
        } else if (strIn.equals("租金")) {
            return true;
        } else if (strIn.equals("车况")) {
            return true;
        } else if (strIn.equals("年龄")) {
            return true;
        } else if (strIn.equals("是否会员")) {
            return false;
        } else if (strIn.equals("密码")) {
            return true;
        } else if (strIn.equals("权限等级")) {
            return false;
        } else if (strIn.equals("押金")) {
            return false;
        }
        return false;
    }

    /**
     * 添加行
     *
     * @param tableMode
     * @param data
     * @throws SQLException
     */
    public void addRow(String tableMode, HashMap<String, String> data) throws SQLException {
        Statement statement = connection.createStatement();
        String tableName = tableMode2Name(tableMode);
        if (tableName != null) {
            StringBuilder columns = new StringBuilder("");
            StringBuilder values = new StringBuilder("");
            Set<String> keySet = data.keySet();
            Iterator<String> iterator = keySet.iterator();
            boolean isFirst = true;
            while (iterator.hasNext()) {
                String s = iterator.next();
                if (isFirst) {
                    isFirst = false;
                    columns.append(name2name(s));
                    if (isIntColumn(s)) {
                        values.append(data.get(s));
                    }else{
                        values.append("'"+data.get(s)+"'");
                    }
                } else {
                    columns.append("," + name2name(s));
                    if (isIntColumn(s)) {
                        values.append(","+data.get(s));
                    }else{
                        values.append(",'"+data.get(s)+"'");
                    }
                }
            }
            String sql = "INSERT INTO " + tableName + " (" + columns.toString() + ") VALUES (" + values.toString() + ")";
            System.out.println(sql);
            statement.execute(sql);
            statement.close();
        }
    }

    private String tableMode2Name(String tableMode) {
        if (tableMode.equals("车辆")) {
            return "car";
        } else if (tableMode.equals("顾客")) {
            return "customer";
        }
        if (tableMode.equals("信息")) {
            return "info";
        }
        if (tableMode.equals("员工")) {
            return "stuff";
        }
        if (tableMode.equals("管理员")) {
            return "users";
        } else {
            return null;
        }
    }

    /**
     * 二维ArrayList转换为二维Vector
     *
     * @param lists
     * @return
     */
    private Vector<Vector<String>> lists2vectors(ArrayList<ArrayList<String>> lists) {
        Vector<Vector<String>> vectors = new Vector<>();

        for (int i = 0; i < lists.size(); i++) {
            vectors.add(new Vector<>(lists.get(i)));
        }
        return vectors;
    }

}
