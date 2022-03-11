package entity;

/**
 * @author TYX
 * @name VariableInfo
 * @description
 * @createTime 2022/3/11 13:44
 **/
public class VariableInfo {
    public String name;
    public String type;
    public String declaration;
    private String annotation="@TrustZone";

    public VariableInfo(String declaration) {
        this.declaration = declaration;
        String[] words=declaration.split(" ");
        for(int i=0;i<words.length;i++){
            if(words[i].contains(annotation)){
                int index=words[i].indexOf("@");
                this.name=words[i].substring(0,index);
                this.type=words[i-1];
                this.declaration=declaration.replace(annotation,"");
            }
        }
    }

    @Override
    public String toString() {
        return "VariableInfo{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", declaration='" + declaration + '\'' +
                '}';
    }
}
