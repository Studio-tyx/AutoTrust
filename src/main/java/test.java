import entity.Block;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TYX
 * @name test
 * @description
 * @createTime 2022/3/3 10:49
 **/
public class test {
    public static void main(String[] args) {
        try{
            File file =new File("hey.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter("hey.txt",true);
            fileWriter.write("test from AutoTrust");
            fileWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
