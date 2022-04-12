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
    private String annotation="@TrustZone";

    public Block(String name, List<String> codes) {
        this.name = name;
        this.codes = codes;
        this.isSecure = false;
    }

    public Block(String name, boolean isSecure, List<String> codes) {
        this.name = name;
        this.codes = codes;
        this.isSecure = isSecure;
    }

    @Override
    public String toString() {
        String res = "Block name='" + name + '\'' + ", isSecure=" + isSecure + ", codes:\n";
        for (String code : codes) {
            res += code + "\n";
        }
        return res;
    }


    public boolean isSecure() {
        return isSecure;
    }

    public void setSecure() {
        if(name.contains(annotation)){
            isSecure = true;
            name=name.replace(annotation,"");
            codes.set(0,codes.get(0).replace(annotation,""));
        }
    }

}
