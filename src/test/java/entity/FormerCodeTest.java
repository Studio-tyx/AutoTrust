package entity;

import org.junit.Test;
import tools.FileReaderTools;

import java.io.IOException;
import java.util.List;

public class FormerCodeTest {
    @Test
    public void testReadFile() throws IOException {
        FileReaderTools fileReader=new FileReaderTools();
        fileReader.init("D:\\Languages\\Maven\\AutoTrust\\src\\main\\resources\\test_code1.txt");
        List<String> contents = fileReader.getContents();
        for(String content:contents){
            System.out.println(content);
        }
    }

    @Test
    public void testTransferCode() throws IOException {
        FormerCode formerCode=new FormerCode();
        formerCode.init("D:\\Languages\\Maven\\AutoTrust\\src\\main\\resources\\test_code1.txt");
        List<String> peripherals = formerCode.getPeripherals();
        System.out.println("peripheral:");
        for(String p:peripherals){
            System.out.println(p);
        }
        System.out.println();
        List<String> codes = formerCode.getCodes();
        for(String code:codes){
            System.out.println(code);
        }
    }
}