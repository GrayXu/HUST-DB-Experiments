package Support;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 本类用来管理获得数据库中的列名、属性以及在GUI中的名字
 * 在修改数据库后记得查看本类进行检查。
 * 注意不能持有外部引用，防止内存泄露。
 */
public class DataNameUtils {

    public static String[] customerColumns = new String[]{"id", "姓名", "年龄", "是否会员"};
    public static String[] carColumns = new String[]{"车牌号", "品牌", "租金", "车况", "押金"};
    public static String[] stuffColumns = new String[]{"id", "姓名", "年龄"};
    public static String[] usersColumns = new String[]{"姓名", "密码", "权限等级", "绑定顾客"};
    public static String[] infoColumns = new String[]{"id", "流水", "车牌号", "顾客id", "顾客", "事件", "备注", "时间", "经手员工id", "经手员工"};

    public static HashMap<String, String> primaryKeyMap = new HashMap<String, String>() {{
        put("users", "name");
        put("car", "license");
        put("info", "infoid");
    }};

    public static String[] getColumnNamesByMode(String mode) {
        String[] columnNames = new String[0];
        switch (mode) {
            case "用户":
                columnNames = DataNameUtils.usersColumns;
                break;
            case "顾客":
                columnNames = DataNameUtils.customerColumns;
                break;
            case "员工":
                columnNames = DataNameUtils.stuffColumns;
                break;
            case "车辆":
                columnNames = DataNameUtils.carColumns;
                break;
            case "事件":
                columnNames = DataNameUtils.infoColumns;//这里应该用更好的办法

                break;
        }
        return columnNames;
    }

    private static HashMap<String, String> name2nameMap = new HashMap<String, String>() {{
        put("姓名", "name");
        put("品牌", "brand");
        put("车牌号", "license");
        put("租金", "cost");
        put("车况", "status");
        put("年龄", "age");
        put("是否会员", "member");
        put("密码", "password");
        put("权限等级", "author");
        put("押金", "pledge");
        put("绑定顾客", "customerid");
        put("顾客id", "customerid");
        put("经手员工id", "stuffid");
        put("事件", "event");
        put("流水", "moychange");
        put("备注", "detailevent");
        put("时间", "time");
        put("id", "id");
    }};

    @Nullable
    public static String name2name(String strIn) {
        return name2nameMap.get(strIn);
    }

    public static ArrayList<String> eventName = new ArrayList<>(Arrays.asList("损坏维修", "罚款", "借车", "还车"));

    public static String swtichEventId(String strIn) {
        return String.valueOf(eventName.indexOf(strIn)+1);
    }

    private static HashMap<String, String> tableMode2NameMap = new HashMap<String, String>() {{
        put("车辆", "car");
        put("顾客", "customer");
        put("事件", "info");
        put("员工", "stuff");
        put("用户", "users");
    }};

    public static String tableMode2Name(String tableMode) {
        return tableMode2NameMap.get(tableMode);
    }
}
