import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import tools.VariableTools;
import utils.CsvUtil;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class VariableToolsTest_CsvData {
    public static String filePath = "src/main/resources/variable.csv";

    @DataProvider(name = "testData")
    public static Object[][] words() throws IOException {
        return CsvUtil.getTestData(filePath);
    }

    @Test(dataProvider = "testData")
    public void test(String statement,String variable,String expect) {
        System.out.println("正在测试: "+statement);
        VariableTools variableTools=new VariableTools();
        assertEquals(expect,variableTools.replaceOperator(statement,variable));
    }
}