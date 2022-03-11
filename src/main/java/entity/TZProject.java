package entity;

import com.sun.org.apache.xpath.internal.operations.VariableSafeAbsRef;
import tools.VariableTools;

import java.util.*;
import java.util.List;

/**
 * @author TYX
 * @name TZProject
 * @description
 * @createTime 2022/3/7 13:53
 **/
public class TZProject {
    private List<Block> blocks;
    private List<String> peripherals;
    private List<VariableInfo> variables;   // key=>identification value=>type
    private List<Block> secureCodes;
    private List<Block> nonSecureCodes;
    private List<String> codes;
    private String annotation="@TrustZone";

    public TZProject(FormerCode formerCode) {
        blocks =new LinkedList<Block>();
        peripherals= formerCode.getPeripherals();
        secureCodes=new LinkedList<Block>();
        nonSecureCodes=new LinkedList<Block>();
        codes=formerCode.getCodes();
        variables=new ArrayList<VariableInfo>();
    }

    private Map<Integer,String> initNameMap(){
        Map<Integer,String> nameMap=new LinkedHashMap<Integer, String>();
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
                nameMap.put(i,functionName);
            }
        }
        return nameMap;
    }

    private List<String> moveFormerCode(int start,int end){
        List<String> functionCodes=new LinkedList<String>();
        for(int j=start;j<end;j++){
            functionCodes.add(codes.get(j));
        }
        return functionCodes;
    }

    public void initFunctions(){
        Map<Integer,String> nameMap=initNameMap();
        Set<Integer> codeNos = nameMap.keySet();
        int i=0,formerCodeIndex=0;
        String formerName=null;
        for(Integer codeNo:codeNos){
            if(i==0){
                if(codeNo!=0){  // #include<stdio.h>
                    Block block =new Block("void",moveFormerCode(formerCodeIndex,codeNo));
                    blocks.add(block);
                    formerCodeIndex=codeNo;
                }
            }
            else{
                Block block =new Block(formerName,moveFormerCode(formerCodeIndex,codeNo));
                blocks.add(block);
                formerCodeIndex=codeNo;
            }
            formerName=nameMap.get(codeNo);
            i++;
        }
        Block block =new Block(formerName,moveFormerCode(formerCodeIndex,codes.size()));
        blocks.add(block);
    }

    public void identifySecure(){
        for(Block block:blocks) {
            if (block.name.contains(annotation)) {
                block.name=block.name.replace(annotation, "");
                block.codes.set(0,block.codes.get(0).replace(annotation,""));
                block.setSecure();
            }
        }
    }

    private void addSecure(Block block){
        List<String> codes1=new ArrayList<String>();
        codes1.add("__NONSECURE_ENTRY");
        codes1.addAll(block.codes);
        secureCodes.add(new Block(block.name, true, codes1));
        String externStr="extern "+block.codes.get(0).replace(annotation,"")
                .replace("{","")+";";
        List<String> externList=new ArrayList<String>();
        externList.add(externStr);
        nonSecureCodes.add(new Block("extern",externList));
    }

    public void separateSecure(){
        for(Block block:blocks){
            if(block.name.equals("void")){
                secureCodes.add(block);
            }
            else{
                if (block.isSecure()) {
                    addSecure(block);
                }
                else {
                    boolean hasPeripheral=false;
                    for(String code:block.codes){
                        for(String peripheral:peripherals){
                            if(code.contains(peripheral)) hasPeripheral=true;
                        }
                    }
                    if(hasPeripheral){
                        addSecure(block);
                    }
                    else {
                        nonSecureCodes.add(block);
                    }
                }
            }
        }
    }

    public void identifyVariables(){
        for(Block block:blocks){
            for(int j=0;j<block.codes.size();j++){
                String code=block.codes.get(j);
                if(code.contains(annotation)){
                    VariableInfo variableInfo=new VariableInfo(code);
                    block.codes.set(j,code.replace(annotation,""));
                    variables.add(variableInfo);
//                    System.out.println(variableInfo);
                }
            }
        }
    }

    private boolean isIdentified(char ch){
        if(ch>='a'&&ch<='z') return true;
        else if(ch>='0'&&ch<='9') return true;
        else if(ch=='_') return true;
        else return false;
    }

    private boolean containsVariable(String statement,String variable){
        boolean res=false;
        if(!statement.contains(variable)) return false;
        if(statement.indexOf(variable)!=statement.lastIndexOf(variable)){
            // many variables  int i=0;
        }else {
            // variable前后都得不是字母or数字or_
            int frontIndex=statement.indexOf(variable);
            int backIndex=frontIndex+variable.length();
            if(!isIdentified(statement.charAt(frontIndex-1))&&!isIdentified(statement.charAt(backIndex))){
                res=true;
            }
        }
        return res;
    }

    private void addRW(VariableInfo variableInfo){
        int voidIndex=0;
        for(;voidIndex<secureCodes.size();voidIndex++){
            if(!secureCodes.get(voidIndex).name.equals("void")) break;
        }
        List<String> RWCodes=new ArrayList<String>();
        RWCodes.add(variableInfo.declaration);
        RWCodes.add(variableInfo.type+" read_"+variableInfo.name+"(){return "+variableInfo.name +";}");
        RWCodes.add("void write_"+variableInfo.name+"("+variableInfo.type+" value){" +variableInfo.name+"=value;}");
        secureCodes.add(voidIndex,new Block("void",true,RWCodes));
    }

    public void separateVariables(){
        for(Block block:nonSecureCodes){
            for(int i=0;i<block.codes.size();i++){
                String code=block.codes.get(i);
                for(VariableInfo vi:variables){
                    String variable=vi.name;
                    if(containsVariable(code,variable)) {
                        VariableTools variableTools = new VariableTools();
                        code = variableTools.replaceOperator(code, variable);
                        block.codes.set(i,code);
                        addRW(vi);
                    }
                }
            }
        }
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void showProject(){
        System.out.println("final codes:");
        System.out.println("---secure:");
        for(Block block:secureCodes){
            for(String code:block.codes)    System.out.println(code);
        }
        System.out.println("---nonSecure:");
        for(Block block:nonSecureCodes){
            for(String code:block.codes)    System.out.println(code);
        }
        System.out.println("peripherals:");
        for(String p:peripherals){
            System.out.print(p+",");
        }
        System.out.println();
        System.out.println("variables:");
        for(VariableInfo v:variables){
            System.out.print(v.name+",");
        }
        System.out.println();
    }
}
