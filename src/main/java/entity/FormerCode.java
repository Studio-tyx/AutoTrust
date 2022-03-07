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
    private List<String> peripherals;

    public FormerCode() {
        codes=null;
        peripherals=new ArrayList<String>();
    }

    public void init(String path) throws IOException {
        FileReaderTools fileReader=new FileReaderTools();
        fileReader.init(path);
        codes=new LinkedList<String>(fileReader.getContents());
        for(int i=0;i<codes.size();i++){
            String code=codes.get(i);
            if(code.contains("peripheral@TrustZone:")){
                int index=code.indexOf(":");
                String[] peripheralString=code.substring(index+1,code.length()).split(",");
                for(String p:peripheralString){
                    peripherals.add(p);
                }
                codes.remove(code);
                i--;
            }
        }
    }

    public List<String> getCodes() {
        return codes;
    }

    public List<String> getPeripherals() {
        return peripherals;
    }
}
