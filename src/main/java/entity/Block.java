package entity;

import java.util.List;

/**
 * @author TYX
 * @name Block
 * @description
 * @createTime 2022/3/7 13:47
 **/
public class Block {
    public String name;
    private boolean isSecure;
    public List<String> codes;

    public Block(String name, List<String> codes) {
        this.name = name;
        this.codes = codes;
        this.isSecure=false;
    }

    public Block(String name, boolean isSecure, List<String> codes) {
        this.name = name;
        this.codes = codes;
        this.isSecure=isSecure;
    }

    @Override
    public String toString() {
        String res="Block name='" + name + '\'' + ", isSecure=" + isSecure + ", codes:\n";
        for(String code:codes){
            res+=code+"\n";
        }
        return res;
    }

    public void setSecure() {
        isSecure = true;
    }

    public boolean isSecure() {
        return isSecure;
    }
}
