/**
 * @author TYX
 * @name test
 * @description
 * @createTime 2022/3/3 10:49
 **/
public class test {
    public static void main(String[] args) {
        String statement="abc";
        String variable="b";
        int frontIndex=statement.indexOf(variable);
        int backIndex=frontIndex+variable.length();
        System.out.println(statement.charAt(frontIndex));
        System.out.println(statement.charAt(backIndex));
    }
}
