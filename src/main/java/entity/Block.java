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
    public int level;
    public List<String> codes;

    public Block(String name, List<String> codes) {
        this.name = name;
        this.codes = codes;
        this.level = 0;
    }

    public Block(String name, int level, List<String> codes) {
        this.name = name;
        this.codes = codes;
        this.level = level;
    }

    @Override
    public String toString() {
        String res = "Block name='" + name + '\'' + ", level=" + level + ", codes:\n";
        for (String code : codes) {
            res += code + "\n";
        }
        return res;
    }


    public int getLevel() {
        return level;
    }

    public void setSecure() {
        if (name.contains("@TrustZoneAll")) {
            level = 2;
            name = name.replace("@TrustZoneAll", "");
            codes.set(0, codes.get(0).replace("@TrustZoneAll", ""));
        }
        else if(name.contains("@TrustZoneOnly")) {
            level = 1;
            name = name.replace("@TrustZoneOnly", "");
            codes.set(0, codes.get(0).replace("@TrustZoneOnly", ""));
        }
        else level=0;
    }
}
