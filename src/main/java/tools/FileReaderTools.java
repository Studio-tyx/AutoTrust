package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TYX
 * @name FileReaderTools
 * @description
 * @createTime 2022/3/7 13:14
 **/
public class FileReaderTools {
    private final List<String> contents;    // 文件内容（以String行为单位）

    /**
     * 构造函数 初始化行数为0
     */
    public FileReaderTools() {
        contents = new ArrayList<String>();
    }

    /**
     * 读取内容<br>
     * 将path文件中的内容读入
     *
     * @param path 文件路径 String
     * @throws IOException 文件读写异常
     */
    public void init(String path) throws IOException {
        java.io.FileReader fileReader = new java.io.FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            contents.add(line);
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        fileReader.close();
    }


    /**
     * 返回文件内容
     *
     * @return 文件内容 List of ProcessLine
     */
    public List<String> getContents() {
        return contents;
    }

}
