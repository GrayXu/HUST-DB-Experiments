package IO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public interface DataBaseIF {

    int checkUser(String name, String psw) throws SQLException;

    Vector<Vector<String>> getCarLists() throws SQLException;

    Vector<Vector<String>> getCarLists(HashMap<String, String> map) throws SQLException;

    Vector<Vector<String>> getCarLists(String sql) throws SQLException;

    Vector<Vector<String>> getCustomerLists() throws SQLException;

    Vector<Vector<String>> getCustomerLists(HashMap<String, String> map) throws SQLException;

    Vector<Vector<String>> getCustomerLists(String sql) throws SQLException;

    Vector<Vector<String>> getStuffLists() throws SQLException;

    Vector<Vector<String>> getStuffLists(HashMap<String, String> map) throws SQLException;

    Vector<Vector<String>> getStuffLists(String sql) throws SQLException;

    //权限3用户只能查看自己的用户信息，和修改用户名和密码
    Vector<Vector<String>> getUserLists(int authority, String userName) throws SQLException;

    Vector<Vector<String>> getUserLists(HashMap<String, String> map, int authority, String userName) throws SQLException;

    Vector<Vector<String>> getUserLists(String sql) throws SQLException;

    Vector<Vector<String>> getInfoLists(String sql) throws SQLException;

    Vector<Vector<String>> getInfoLists(HashMap<String, String> map, int authority, String userName) throws SQLException;

    Vector<Vector<String>> getInfoLists(int authority, String userName) throws SQLException;

    String getIDbyUserName(String name) throws SQLException;

    void deleteRow(String tableMode, String primaryKey) throws SQLException;


    void updateData(String tableMode, String name, String value, String primaryKey) throws SQLException;

    void addRow(String tableMode, HashMap<String, String> data) throws SQLException;

    ArrayList<ArrayList<String>> getAllChartData(String mode, String func) throws SQLException;
}
