package entity;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class TZProjectTest {
    @Test
    public void testSeparateFunction() throws IOException {
        FormerCode formerCode=new FormerCode();
        formerCode.init("D:\\Languages\\Maven\\AutoTrust\\src\\main\\resources\\test_code1.txt");
        TZProject tzProject=new TZProject();
        tzProject.initFunctions(formerCode);

    }
}