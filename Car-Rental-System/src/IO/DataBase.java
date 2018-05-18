package IO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DataBase {

    public static final String URL = "jdbc:mysql://localhost:3306/lab3?serverTimezone=UTC&useSSL=false";
    public static final String USER = "root";
    public static final String PASSWORD = "XIANG1569348";

//    public static String[] carCols = new String[]{"brand","license","cost","status","pledge"};
    private Connection connection;
    /**
    单例
     */
    private static class InnerHelper {
        private final static DataBase dataBase = new DataBase();
    }

    private DataBase(){}

    public static DataBase getInstance(){
        return InnerHelper.dataBase;
    }

    /**
     *
     * @return status of connection
     */
    public boolean initConnect(){
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
     * @param name
     * @param psw
     * @return authority, -1->no found, 1->root user, 2->administrator, 3->normal user
     * @throws SQLException
     */
    public int checkUser(String name, String psw) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
        while(resultSet.next()){
            if (resultSet.getString("name").equals(name)){
                if (resultSet.getString("password").equals(psw)){
                    return resultSet.getInt("author");
                }
            }
        }
        return -1;
    }

    public String[][] getCarLists() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM car");
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()){
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 2; i <= 6; i++) {//从1开始，很奇怪的设定。。。
                tempList.add(resultSet.getString(i));
            }
            lists.add(tempList);
        }
        return lists2arrays(lists);
    }

    public String[][] getCustomerLists() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM customer");
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()){
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 2; i <= 5; i++) {//从1开始，很奇怪的设定。。。
                tempList.add(resultSet.getString(i));
            }
            lists.add(tempList);
        }
        return lists2arrays(lists);
    }

    public String[][] getStuffLists() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM stuff");
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()){
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 2; i <= 4; i++) {//从1开始，很奇怪的设定。。。
                tempList.add(resultSet.getString(i));
            }
            lists.add(tempList);
        }
        return lists2arrays(lists);
    }

    public String[][] getUserLists() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM user");
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        while (resultSet.next()){
            ArrayList<String> tempList = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {//从1开始，很奇怪的设定。。。
                tempList.add(resultSet.getString(i));
            }
            lists.add(tempList);
        }
        return lists2arrays(lists);
    }

    /**
     * 二维ArrayList转换为二维数组
     * @param lists
     * @return
     */
    private String[][] lists2arrays(ArrayList<ArrayList<String>> lists){
        String[][] stringss = new String[lists.size()][];
        for (int i = 0; i < lists.size(); i++) {
            stringss[i] = lists.get(i).toArray(new String[lists.get(i).size()]);
        }
        return stringss;
    }

}
