package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author TYX
 * @name CsvUtil
 * @description
 * @createTime 2022/3/2 13:55
 **/
public class CsvUtil {
    public static Object[][] getTestData(String filepath)throws IOException {
        Object[][] res=new Object[11][3];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            String line = null;
            int i=0;
            while((line=reader.readLine())!=null){
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                res[i]=item;
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        CsvUtil csvUtil=new CsvUtil();
        Object[][] data=csvUtil.getTestData("src/main/resources/data.csv");
        for(int i=0;i<11;i++){
            for(int j=0;j<3;j++){
                System.out.print(data[i][j]+" ");
            }
            System.out.println();
        }
    }
}
