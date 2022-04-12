package entity;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TZProjectTest {
    @Test
    public void testSeparateFunction() throws IOException {
        FormerCode formerCode=new FormerCode();
        formerCode.init("D:\\Languages\\Maven\\AutoTrust\\src\\main\\resources\\test_code1.txt");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);
        List<Block> blocks = tzProject.getBlocks();
        for(Block block : blocks){
            System.out.println(block);
        }
    }

    @Test
    public void testIdentifySecure() throws IOException {
        FormerCode formerCode=new FormerCode();
        formerCode.init("D:\\Languages\\Maven\\AutoTrust\\src\\main\\resources\\test_code1.txt");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);
        tzProject.identifyFunction();
        tzProject.separateSecureFunction();
        tzProject.showProject();
    }

    @Test
    public void testSeparateVariables() throws IOException {
        FormerCode formerCode=new FormerCode();
        formerCode.init("D:\\Languages\\Maven\\AutoTrust\\src\\main\\resources\\test_code1.txt");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);
        tzProject.identifyFunction();
        tzProject.separateSecureFunction();
        tzProject.identifyVariables();
        tzProject.separateVariables();
        tzProject.showProject();
    }

    @Test
    public void testFunctionArea() throws IOException {
        FormerCode formerCode=new FormerCode();
        formerCode.init("D:\\Languages\\Maven\\AutoTrust\\src\\main\\resources\\test_code2.txt");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);
        tzProject.identifyFunction();
        tzProject.separateSecureFunction();
        tzProject.identifyVariables();
        tzProject.separateVariables();
        tzProject.showProject();
    }
}