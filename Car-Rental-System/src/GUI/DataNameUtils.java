package GUI;

import org.jetbrains.annotations.Nullable;

/**
 * 本类用来管理获得数据库中的列名、属性以及在GUI中的名字
 * 在修改数据库后记得查看本类进行检查。
 */
public class DataNameUtils {

    public static String[] customerColumns = new String[]{"id", "姓名", "年龄", "是否会员"};
    public static String[] carColumns = new String[]{"品牌", "车牌号", "租金", "车况", "押金"};
    public static String[] stuffColumns = new String[]{"id", "姓名", "年龄"};
    public static String[] usersColumns = new String[]{"姓名", "密码", "权限等级"};
    public static String[] infoColumns = new String[]{"id", "流水", "车牌", "事件", "备注", "时间", "经手员工"};

    public static String[] getColumnNamesByMode(String mode){
        String[] columnNames = new String[0];
        if (mode.equals("管理员")) {
            columnNames = DataNameUtils.usersColumns;
        } else if (mode.equals("客户")) {
            columnNames = DataNameUtils.customerColumns;
        } else if (mode.equals("员工")) {
            columnNames = DataNameUtils.stuffColumns;
        } else if (mode.equals("车辆")) {
            columnNames = DataNameUtils.carColumns;
        } else if (mode.equals("事件")) {
            columnNames = DataNameUtils.infoColumns;//这里应该用更好的办法
        }
        return columnNames;
    }

    @Nullable
    public static String name2name(String strIn) {
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

    public static String tableMode2Name(String tableMode) {
        if (tableMode.equals("车辆")) {
            return "car";
        } else if (tableMode.equals("客户")) {
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

    public static boolean isIntColumn(String strIn) {
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
            return false;
        } else if (strIn.equals("权限等级")) {
            return true;
        } else if (strIn.equals("押金")) {
            return false;
        }
        return false;
    }
}
