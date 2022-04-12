package entity;

/**
 * @author TYX
 * @name FunctionInfo
 * @description
 * @createTime 2022/3/23 14:20
 **/
public class FunctionInfo {
    public String functionName;
    public boolean isSecure;
    public String pfName;

    public FunctionInfo(String functionName, boolean isSecure) {
        this.functionName = functionName;
        this.isSecure = isSecure;
        pfName=null;
    }

    public void setPfName(String pfName) {
        this.pfName = pfName;
    }
}
