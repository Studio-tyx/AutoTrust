package entity;

import java.util.List;

/**
 * @author TYX
 * @name Function
 * @description
 * @createTime 2022/3/7 13:47
 **/
public class Function {
    public String name;
    public boolean isSecure;    // 到底要不要设成private呢
    public List<String> functionCode;

    public Function(String name, List<String> functionCode) {
        this.name = name;
        this.functionCode = functionCode;
        isSecure=false;
    }

}
