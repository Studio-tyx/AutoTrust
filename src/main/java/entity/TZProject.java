package entity;

import java.util.*;

/**
 * @author TYX
 * @name TZProject
 * @description
 * @createTime 2022/3/7 13:53
 **/
public class TZProject {
    private List<Function> functions;
    private List<String> secureCodes;
    private List<String> nonSecureCodes;

    public void initFunctions(FormerCode formerCode){
        List<String> codes=formerCode.getCodes();
        Map<Integer,String> functionCodeNo=new LinkedHashMap<Integer, String>();
        for(int i=0;i<codes.size();i++){
            String code=codes.get(i);
            String filterBlank=code.replace(" ","");
            boolean hasFunction=false;
            if(filterBlank.contains("){")) hasFunction=true;
            if(i<codes.size()-1){
                if(filterBlank.endsWith(")")&&codes.get(i+1).startsWith("{")) hasFunction=true;
            }
            if(hasFunction){
                int left=code.indexOf("(");
                int nameStart = code.substring(0, left - 1).lastIndexOf(" ");
                String functionName=code.substring(nameStart,left);
                functionName=functionName.replace(" ","");
                functionCodeNo.put(i,functionName);
            }
        }
        Set<Integer> integers = functionCodeNo.keySet();
        for(Integer integer:integers){
            System.out.println(integer+","+functionCodeNo.get(integer));
        }

    }
}
