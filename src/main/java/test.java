import java.util.ArrayList;
import java.util.List;
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
        List<String> list=new ArrayList<String>();
        System.out.println(list.size());
        list.add("a");
        System.out.println(list.indexOf("a"));
        System.out.println("----------");
        System.out.println(list.size());
        list.add("b");
        System.out.println(list.indexOf("b"));
        System.out.println("----------");
        System.out.println(list.size());
        list.add("c");
        System.out.println(list.indexOf("b"));
    }
}
