package IO;

import GUI.MainGui;
import Support.DataNameUtils;

import java.sql.*;
import java.util.*;

public class DataBase {

    public static final String URL = "jdbc:mysql://localhost:3306/lab3?serverTimezone=UTC&useSSL=false";
    public static final String USER = "root";
    public static final String PASSWORD = "XIANG1569348";

    private Connection connection;

    /**
     * singleton
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
     * @param name 输入的用户名
     * @param psw  输入的密码
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
     * @return 获得的数据项
     * @throws SQLException 语句错误或者受到约束
     */
    public Vector<Vector<String>> getCarLists() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM car");
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()) {
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 1; i <= DataNameUtils.carColumns.length; i++) {//从1开始，很奇怪的设定。。。
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
            for (int i = 1; i <= DataNameUtils.customerColumns.length; i++) {//从1开始，很奇怪的设定。。。
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
            for (int i = 1; i <= DataNameUtils.stuffColumns.length; i++) {//从1开始，很奇怪的设定。。。
                tempList.add(resultSet.getString(i));
            }
            lists.add(tempList);
        }
        statement.close();
        return lists2vectors(lists);
    }

    public Vector<Vector<String>> getUserLists(int authority, String userName) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = null;
        if (authority == 1){
            sql = "SELECT * FROM users";
        }else if (authority == 2){
            sql = "SELECT * FROM users WHERE author <> 1";
        }else if (authority == 3){
            sql = "SELECT * FROM users WHERE name = '"+userName+"'";
        }
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()) {
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 1; i <= DataNameUtils.usersColumns.length; i++) {//从1开始，很奇怪的设定。。。
                tempList.add(resultSet.getString(i));
            }
            lists.add(tempList);
        }
        statement.close();
        return lists2vectors(lists);
    }


    public Vector<Vector<String>> getInfoLists() throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println("SELECT info.infoid , info.moychange, car.license, customer.name, info.event, info.detailevent, info.time, stuff.name\n" +
                "FROM info,car,stuff,customer\n"+
                "WHERE info.license = car.license AND info.stuffid = stuff.id AND customer.id = info.customerid");
        ResultSet resultSet = statement.executeQuery("SELECT info.infoid , info.moychange, car.license, customer.id,customer.name, info.event, info.detailevent, info.time, stuff.id,stuff.name\n" +
                "FROM info,car,stuff,customer\n"+
                "WHERE info.license = car.license AND info.stuffid = stuff.id AND customer.id = info.customerid");
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()) {
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 1; i <= DataNameUtils.infoColumns.length; i++) {//从1开始，很奇怪的设定。。。
                if (i == 6) {
                    switch (resultSet.getInt(i)) {//eventid to event
                        case 1:
                            tempList.add("损坏维修");
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
     * @param tableMode  表格模式的标志字符串
     * @param primaryKey 主键的值
     * @throws SQLException 语句错误或者受到约束
     */
    public void deleteRow(String tableMode, String primaryKey) throws SQLException {
        String tableName = DataNameUtils.tableMode2Name(tableMode);

        String primaryKeyName = DataNameUtils.primaryKeyMap.get(tableName);
        if (primaryKeyName == null) primaryKeyName = "id";
        String sql = "DELETE FROM " + tableName + " WHERE " + primaryKeyName + " = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, primaryKey);
        preparedStatement.execute();

    }

    /**
     * 更新数据
     *
     * @param tableMode  表格模式的标志字符串
     * @param name       更新属性的列名
     * @param value      更新后的值
     * @param primaryKey 主键的值
     * @throws SQLException 语句错误或者受到约束
     */

    public void updateData(String tableMode, String name, String value, String primaryKey) throws SQLException {
        String tableName = DataNameUtils.tableMode2Name(tableMode);

        if (name.equals("事件")) value = DataNameUtils.swtichEventId(value);

        //拿到主键的名字
        String primaryKeyName = DataNameUtils.primaryKeyMap.get(tableName);
        if (primaryKeyName == null) primaryKeyName = "id";

        String sql = "UPDATE " + tableName + " SET " + DataNameUtils.name2name(name) + " = ? where " + primaryKeyName + " = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, value);
        preparedStatement.setString(2, primaryKey);
        System.out.println(preparedStatement.toString());
        preparedStatement.execute();
    }

    /**
     * 添加行
     *
     * @param tableMode 表格模式的标志字符串
     * @param data      一张哈希表存新数据
     * @throws SQLException 语句错误或者受到约束
     */
    public void addRow(String tableMode, HashMap<String, String> data) throws SQLException {
        Statement statement = connection.createStatement();
        String tableName = DataNameUtils.tableMode2Name(tableMode);
        if (tableName != null) {
            StringBuilder columns = new StringBuilder("");
            StringBuilder values = new StringBuilder("");
            Set<String> keySet = data.keySet();
            Iterator<String> iterator = keySet.iterator();

            boolean isFirst = true;
            while (iterator.hasNext()) {
                String s = iterator.next();
                System.out.println(s);
                String value = data.get(s);
                if (value != null) {
                    if (isFirst) {//第一个不加逗号
                        isFirst = false;
                        columns.append(DataNameUtils.name2name(s));
                        values.append("'" + data.get(s) + "'");
                    } else {
                        columns.append("," + DataNameUtils.name2name(s));
                        values.append(",'" + data.get(s) + "'");
                    }
                }
            }
            String sql = "INSERT INTO " + tableName + " (" + columns.toString() + ") VALUES (" + values.toString() + ")";
            System.out.println(sql);
            statement.execute(sql);
            statement.close();
        }
    }

    /**
     * 二维ArrayList转换为二维Vector
     *
     * @param lists 待转换的二维list
     * @return 二维Vector
     */
    private Vector<Vector<String>> lists2vectors(ArrayList<ArrayList<String>> lists) {
        Vector<Vector<String>> vectors = new Vector<>();

        for (ArrayList<String> list : lists) {
            vectors.add(new Vector<>(list));
        }
        return vectors;
    }

}
