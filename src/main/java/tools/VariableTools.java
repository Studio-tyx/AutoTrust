package tools;

/**
 * @author TYX
 * @name tools.VariableTools
 * @description
 * @createTime 2022/3/2 10:11
 **/
public class VariableTools {
    private final String[] simpleOperators = {"+", "-", "*", "/", "%", "&", "|", "^", "<<", ">>"};

    public String replaceRead(String statement, String variable) {
        if (statement.contains(variable)) return statement.replace(variable, "read_" + variable + "()");
        else return statement;
    }

    public String replaceOperator(String statement, String variable) {
        String res = statement;
        if (statement.contains(variable)) {
            if (statement.contains("=")) {
                int assign_no = statement.indexOf('=');
                String left = statement.substring(0, assign_no);   // a+,=b a,=b
                String right = statement.substring(assign_no);
                right = replaceRead(right, variable);
                boolean hasOperator = false;
                if (left.contains(variable)) {
                    for (String simpleOperator : simpleOperators) {
                        if (left.endsWith(simpleOperator)) {   // a*,=b;
                            hasOperator = true;
                        }
                    }
                    if (hasOperator) {
                        res = left.replace(variable, "write_" + variable + "(read_" + variable + "()")
                                + right.substring(1, right.length() - 1) + ");";
                    } else {
                        if (left.endsWith("<") || left.endsWith(">")) {    // a<,=b
                            res = replaceRead(left, variable) + right;
                        } else {  // a,=b+1;
                            if (right.startsWith("==")) {
                                res = replaceRead(left, variable) + right;
                            } else {
                                res = "write_" + variable + "(" + right.substring(1, right.length() - 1) + ");";
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
