import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TYX
 * @name test
 * @description
 * @createTime 2022/3/3 10:49
 **/
public class test {
    public static void main(String[] args) {
        String v="a";
        String statement="a --;";
//        Pattern p = Pattern.compile("a\\s*\\+\\+");
//        // get a matcher object
//        Matcher m = p.matcher(statement);
//        statement = m.replaceAll("inc_a_");
        statement=Pattern.compile(v+"\\s*--").matcher(statement).replaceAll("inc_a_");
        System.out.println(statement);
    }
}
