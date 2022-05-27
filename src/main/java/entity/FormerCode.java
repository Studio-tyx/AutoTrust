package entity;

import tools.FileReaderTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author TYX
 * @name FormerCode
 * @description
 * @createTime 2022/3/7 13:19
 **/
public class FormerCode {
    private List<String> codes;

    public FormerCode() {
        codes=null;
    }

    public void init(String path) throws IOException {
        FileReaderTools fileReader=new FileReaderTools();
        fileReader.init(path);
        codes=new LinkedList<String>(fileReader.getContents());
    }

    public List<String> getCodes() {
        return codes;
    }
}
