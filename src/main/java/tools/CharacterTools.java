package tools;

/**
 * @author TYX
 * @name CharacterTools
 * @description
 * @createTime 2022/3/11 16:01
 **/
public final class CharacterTools {
    private static boolean isIdentified(char ch){
        if(ch>='a'&&ch<='z') return true;
        else if(ch>='0'&&ch<='9') return true;
        else if(ch=='_') return true;
        else return false;
    }

    public static boolean containsVariable(String statement,String variable){
        boolean res=false;
        if(!statement.contains(variable)) return false;
        if(statement.indexOf(variable)!=statement.lastIndexOf(variable)){
            // many variables  int i=0;
        }else {
            // variable前后都得不是字母or数字or_
            int frontIndex=statement.indexOf(variable);
            int backIndex=frontIndex+variable.length();
            if(!isIdentified(statement.charAt(frontIndex-1))&&!isIdentified(statement.charAt(backIndex))){
                res=true;
            }
        }
        return res;
    }
}
