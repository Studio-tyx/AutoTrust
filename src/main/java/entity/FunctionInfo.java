package entity;

/**
 * @author TYX
 * @name FunctionInfo
 * @description
 * @createTime 2022/3/23 14:20
 **/
public class FunctionInfo {
    public String functionName;
    public int level;
    public String pfName;

    public FunctionInfo(String functionName, int level) {
        this.functionName = functionName;
        this.level = level;
        pfName=null;
    }

    public void setPfName(String pfName) {
        this.pfName = pfName;
    }
}
