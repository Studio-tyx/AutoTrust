package tools;

import java.util.regex.Pattern;

/**
 * @author TYX
 * @name tools.VariableTools
 * @description
 * @createTime 2022/3/2 10:11
 **/
public class VariableTools {
    private final String[] simpleOperators = {"+", "-", "*", "/", "%", "&", "|", "^", "<<", ">>"};

    private String replaceRead(String statement, String variable) {
        if (statement.contains(variable)) return statement.replace(variable, "read_" + variable + "()");
        else return statement;
    }

    private String replaceSelfOperate(String left, String right, String variable){
        return left.replace(variable, "write_" + variable + "(read_" + variable + "()")
                + right.substring(1, right.length() - 1) + ");";
    }

    private String replaceWrite(String right, String variable){
        return "write_" + variable + "(" + right.substring(1, right.length() - 1) + ");";
    }

    public String replaceOperator(String statement, String variable) {
        String res = statement;
        if (CharacterTools.containsVariable(statement,variable)) {
            if(statement.contains("++")||statement.contains("--")){ // a++ ++a a-- --a
                statement= Pattern.compile(variable+"\\s*\\+\\+").matcher(statement)
                        .replaceAll("inc_right_"+variable+"()");
                statement= Pattern.compile(variable+"\\s*--").matcher(statement)
                        .replaceAll("dec_right_"+variable+"()");
                statement= Pattern.compile("\\+\\+\\s*"+variable).matcher(statement)
                        .replaceAll("inc_left_"+variable+"()");
                statement= Pattern.compile("--\\s*"+variable).matcher(statement)
                        .replaceAll("dec_left_"+variable+"()");
            }
            if(!CharacterTools.containsVariable(statement,variable)) return statement;
            if (statement.contains("=")) {
                int assign_no = statement.indexOf('=');
                String left = statement.substring(0, assign_no);   // a+,=b a,=b
                String right = statement.substring(assign_no);
                right = replaceRead(right, variable);
                boolean hasOperator = false;
                if (left.contains(variable)) {
                    for (String simpleOperator : simpleOperators) {
                        if (left.endsWith(simpleOperator)) {   // a*,=b; and don't worry about space
                            hasOperator = true;     // because there is no a * = b
                            break;
                        }
                    }
                    if (hasOperator) {
                        res = replaceSelfOperate(left,right,variable);
                    } else {
                        if (left.endsWith("<") || left.endsWith(">")) {    // a<,=b
                            res = replaceRead(left, variable) + right;
                        } else {  // a,=b+1;
                            if (right.startsWith("==")) {
                                res = replaceRead(left, variable) + right;
                            } else {
                                res = replaceWrite(right,variable);
                            }
                        }
                    }
                } else {
                    res = left + right;
                }
            } else {
                res = replaceRead(statement, variable);
            }
        }
        return res;
    }
}
