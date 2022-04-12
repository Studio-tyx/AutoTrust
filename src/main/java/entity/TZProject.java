package entity;

import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
import com.sun.org.apache.xpath.internal.operations.VariableSafeAbsRef;
import tools.CharacterTools;
import tools.FunctionTools;
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
    private String proAnnotation="@TrustZone";
    private String NSCAnnotation="__NONSECURE_ENTRY";
    private List<FunctionInfo> functions;

    public TZProject() {

    }

    private Map<Integer,String> initNameMap(List<String> codes){
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

    private List<String> moveFormerCode(List<String> codes,int start,int end){
        List<String> functionCodes=new LinkedList<String>();
        for(int j=start;j<end;j++){
            functionCodes.add(codes.get(j));
        }
        return functionCodes;
    }

    public void initFunctions(FormerCode formerCode){
        blocks =new LinkedList<Block>();
        secureCodes=new LinkedList<Block>();
        nonSecureCodes=new LinkedList<Block>();
        List<String> codes=formerCode.getCodes();
        peripherals= formerCode.getPeripherals();
        Map<Integer,String> nameMap=initNameMap(codes);
        Set<Integer> codeNos = nameMap.keySet();
        int i=0,formerCodeIndex=0;
        String formerName=null;
        for(Integer codeNo:codeNos){
            if(i==0){
                if(codeNo!=0){  // #include<stdio.h>
                    Block block =new Block("void",moveFormerCode(codes,formerCodeIndex,codeNo));
                    blocks.add(block);
                    formerCodeIndex=codeNo;
                }
            }
            else{
                Block block =new Block(formerName,moveFormerCode(codes,formerCodeIndex,codeNo));
                blocks.add(block);
                formerCodeIndex=codeNo;
            }
            formerName=nameMap.get(codeNo);
            i++;
        }
        Block block =new Block(formerName,moveFormerCode(codes,formerCodeIndex,codes.size()));
        blocks.add(block);
    }

    public void identifyFunction(){
        for(Block block:blocks) {
            block.setSecure();
        }
    }

    private void addSecureFunction(Block block){
        List<String> codes1=new ArrayList<String>();
        codes1.add(NSCAnnotation);
        codes1.addAll(block.codes);
        secureCodes.add(new Block(block.name, true, codes1));
        String externStr="extern "+block.codes.get(0).replace(proAnnotation,"")
                .replace("{","")+";";
        List<String> externList=new ArrayList<String>();
        externList.add(externStr);
        nonSecureCodes.add(new Block("extern",externList));
    }

    public void separateSecureFunction(){
        functions=new ArrayList<FunctionInfo>();
        for(Block block:blocks){
            if(block.name.equals("void")){
                secureCodes.add(block);
            }
            else{
                if (block.isSecure()) {
                    setOtherFunction(block);
                    addSecureFunction(block);
                }
                else {
                    functions.add(new FunctionInfo(block.name,false));
                    nonSecureCodes.add(block);
                }
            }
        }
    }

    public void setOtherFunction(Block block){
        int i=0;
        for(;i<block.codes.size();i++){
            String code=block.codes.get(i);
//        for(String code:block.codes){
            for(FunctionInfo functionInfo:functions){
                if(code.contains(functionInfo.functionName)){
                    if(functionInfo.pfName!=null){
                        code=code.replace(functionInfo.functionName, functionInfo.pfName);
                    }
                    else{
                        setPf(functionInfo.functionName);
                        functionInfo.pfName=functionInfo.functionName+"_pf";
                        code=code.replace(functionInfo.functionName, functionInfo.functionName+"_pf");
                    }
                    block.codes.set(i,code);
                }
            }
        }
    }

    public void setPf(String functionName){
        int blockNo=0;
        for(;blockNo< secureCodes.size();blockNo++){
            Block sBlock=secureCodes.get(blockNo);
            if(!sBlock.name.equals("void")) break;
        }
        List<String> tempCodes=new ArrayList<String>();
        tempCodes.add("static NonSecure_funcptr "+functionName+"_pf = (NonSecure_funcptr)NULL;");
        tempCodes.add(NSCAnnotation);
        tempCodes.add("int32_t "+functionName+"_callback(NonSecure_funcptr *callback){");
        tempCodes.add("\t"+functionName+"_pf = (NonSecure_funcptr)cmse_nsfptr_create(callback);");
        tempCodes.add("\treturn 0;");
        tempCodes.add("}");
        secureCodes.add(blockNo,new Block("void",true,tempCodes));

        blockNo=0;
        for(;blockNo< nonSecureCodes.size();blockNo++){
            Block nsBlock=nonSecureCodes.get(blockNo);
            if(!nsBlock.name.equals("void")) break;
        }
        tempCodes=new ArrayList<String>();
        tempCodes.add("extern int32_t "+functionName+"_callback(int32_t (*)(uint32_t));");
        nonSecureCodes.add(blockNo,new Block("void",false,tempCodes));
        for(;blockNo< blocks.size();blockNo++){
            Block nsBlock=blocks.get(blockNo);
            if(nsBlock.name.equals("main")){
                String callback="\t"+functionName+"_callback(&"+ functionName+");";
                if(nsBlock.codes.get(0).contains("{"))nsBlock.codes.add(1,callback);
                else nsBlock.codes.add(2,callback);
                blocks.set(blockNo,nsBlock);
                break;
            }
        }
    }

    public void identifyVariables(){
        variables=new ArrayList<VariableInfo>();
        for(Block block:blocks){
            for(int j=0;j<block.codes.size();j++){
                String code=block.codes.get(j);
                if(code.contains(proAnnotation)){
                    VariableInfo variableInfo=new VariableInfo(code);
                    block.codes.set(j,code.replace(proAnnotation,""));
                    variables.add(variableInfo);
//                    System.out.println(variableInfo);
                }
            }
        }
    }


    private void addRW(VariableInfo variableInfo){
        int voidIndex=0;
        for(;voidIndex<secureCodes.size();voidIndex++){
            if(!secureCodes.get(voidIndex).name.equals("void")) break;
        }
        List<String> RWCodes=new ArrayList<String>();
        RWCodes.add(variableInfo.declaration);
        RWCodes.add(NSCAnnotation);
        RWCodes.add(variableInfo.type+" read_"+variableInfo.name+"(){return "+variableInfo.name +";}");
        RWCodes.add(NSCAnnotation);
        RWCodes.add("void write_"+variableInfo.name+"("+variableInfo.type+" value){" +variableInfo.name+"=value;}");
        secureCodes.add(voidIndex,new Block("void",true,RWCodes));
    }

    private void addIncDec(String code, VariableInfo vi){
        String[] names={"inc_left_","inc_right","dec_left_","dec_right"};
        String[] bodies={vi.type+" inc_left_"+vi.name+"(){++"+vi.name+";return "+vi.name+";}",
                vi.type+" inc_right_"+vi.name+"(){++"+vi.name+";return "+vi.name+"-1;}",
                vi.type+" dec_left_"+vi.name+"(){--"+vi.name+";return "+vi.name+";}",
                vi.type+" dec_right_"+vi.name+"(){--"+vi.name+";return "+vi.name+"-1;}"};
        for(int i=0;i<names.length;i++){
            if(code.contains(names[i])){
                int voidIndex=0;
                for(;voidIndex<secureCodes.size();voidIndex++){
                    if(!secureCodes.get(voidIndex).name.equals("void")) break;
                }
                List<String> RWCodes=new ArrayList<String>();
                RWCodes.add(bodies[i]);
                secureCodes.add(voidIndex,new Block("void",true,RWCodes));
            }
        }
    }

    public void separateVariables(){
        for(Block block:nonSecureCodes){
            for(int i=0;i<block.codes.size();i++){
                String code=block.codes.get(i);
                for(VariableInfo vi:variables){
                    String variable=vi.name;
                    if(CharacterTools.containsVariable(code,variable)) {
                        VariableTools variableTools = new VariableTools();
                        code = variableTools.replaceOperator(code, variable);
                        block.codes.set(i,code);
                        if(code.contains("inc_")||code.contains("dec_")){
                            addIncDec(code,vi);
                        }
                        else{
                            addRW(vi);
                        }
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
