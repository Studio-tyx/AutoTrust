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

    @Test
    public void testIdentifyLevel() throws IOException {
        FormerCode formerCode=new FormerCode();
        formerCode.init("D:\\Languages\\Maven\\AutoTrust\\src\\main\\resources\\test_code2.txt");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);
//        tzProject.showBlocks();
        tzProject.identifyFunction();
        tzProject.separateSecureFunction();
        tzProject.identifyVariables();
        tzProject.separateVariables();
        tzProject.change();
        tzProject.showProject();
        //tzProject.showBlocks();
    }

    @Test
    public void testTrueSample() throws IOException {
        FormerCode formerCode=new FormerCode();
        formerCode.init("D:\\Languages\\Maven\\AutoTrust\\src\\main\\resources\\test_code3.txt");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);
        tzProject.identifyFunction();
        tzProject.separateSecureFunction();
        tzProject.identifyVariables();
        tzProject.separateVariables();
        tzProject.change();
        tzProject.showProject();
        //tzProject.showBlocks();
    }

    @Test
    public void testBlinky() throws IOException {
        FormerCode formerCode=new FormerCode();
        String path="D:\\Languages\\Maven\\AutoTrust\\code example\\Blinky\\";
        formerCode.init(path+"main.c");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);
        tzProject.identifyFunction();
        tzProject.separateSecureFunction();
        tzProject.identifyVariables();
        tzProject.separateVariables();
        tzProject.change();
        tzProject.writeCode(path);
        //tzProject.showProject();
        //tzProject.showBlocks();
    }

    @Test
    public void testDebug() throws IOException {
        FormerCode formerCode=new FormerCode();
        String path="D:\\Languages\\Maven\\AutoTrust\\code example\\Debug\\";
        formerCode.init(path+"main.c");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);
        tzProject.identifyFunction();
        tzProject.separateSecureFunction();
        tzProject.identifyVariables();
        tzProject.separateVariables();
        tzProject.change();
        tzProject.writeCode(path);
        //tzProject.showProject();
        //tzProject.showBlocks();
    }

    @Test
    public void testAudio() throws IOException {
        FormerCode formerCode=new FormerCode();
        String path="D:\\Languages\\Maven\\AutoTrust\\code example\\Audio\\";
        formerCode.init(path+"main.c");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);
        tzProject.identifyFunction();
        tzProject.separateSecureFunction();
        tzProject.identifyVariables();
        tzProject.separateVariables();
        tzProject.change();
        tzProject.writeCode(path);
        //tzProject.showProject();
        //tzProject.showBlocks();
    }

    @Test
    public void testAES() throws IOException {
        FormerCode formerCode=new FormerCode();
        String path="D:\\Languages\\Maven\\AutoTrust\\code example\\AES\\";
        formerCode.init(path+"main.c");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);
        tzProject.identifyFunction();
        tzProject.separateSecureFunction();
        tzProject.identifyVariables();
        tzProject.separateVariables();
        tzProject.change();
        tzProject.writeCode(path);
        //tzProject.showProject();
        //tzProject.showBlocks();
    }

    @Test
    public void testWifi() throws IOException {
        FormerCode formerCode=new FormerCode();
        String path="D:\\Languages\\Maven\\AutoTrust\\code example\\WiFi\\";
        formerCode.init(path+"main.c");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);
        tzProject.identifyFunction();
        tzProject.separateSecureFunction();
        tzProject.identifyVariables();
        tzProject.separateVariables();
        tzProject.change();
        tzProject.writeCode(path);
        //tzProject.showProject();
        //tzProject.showBlocks();
    }

}