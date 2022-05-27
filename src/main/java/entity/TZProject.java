package entity;

import tools.CharacterTools;
import tools.VariableTools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author TYX
 * @name TZProject
 * @description
 * @createTime 2022/3/7 13:53
 **/
public class TZProject {
    private List<Block> blocks;
    private List<VariableInfo> variables;   // key=>identification value=>type
    private List<Block> secureCodes;
    private List<Block> nonSecureCodes;
    private final String proAnnotation = "@TrustZone";
    private final String NSCAnnotation = "__NONSECURE_ENTRY";
    private List<FunctionInfo> s2nsFunctions;

    public TZProject() {

    }

    private Map<Integer, String> initNameMap(List<String> codes) {
        Map<Integer, String> nameMap = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < codes.size(); i++) {
            String code = codes.get(i);
            String filterBlank = code.replace(" ", ""); // delete blank
            boolean hasFunction = false;
            if (filterBlank.matches(".*\\(.*\\)\\{")) hasFunction = true;   // (..){
            if (i < codes.size() - 1) {
                if(codes.get(i+1).startsWith("{")){
                    if(filterBlank.matches(".*\\(.*\\)\\s*//.*")||  // (..) //
                            filterBlank.matches(".*\\(.*\\)\\s*/\\*.*\\*/")||   // (..) /*
                            filterBlank.matches(".*\\(.*\\).*"))    // (..)
                        hasFunction=true;
                }
            }
            if (hasFunction) {
                int left = code.indexOf("(");
                int nameStart = code.substring(0, left - 1).lastIndexOf(" ");
                if(nameStart==-1)nameStart=0;
                String functionName = code.substring(nameStart, left);
                if(functionName.equals("while")||functionName.equals("for")||functionName.equals("if")){
                    // has (){ but not block
                }else{
                    functionName = functionName.replace(" ", "");
                    nameMap.put(i, functionName);
                }
            }
        }
        return nameMap;
    }

    private List<String> moveFormerCode(List<String> codes, int start, int end) {
        List<String> functionCodes = new LinkedList<String>();
        for (int j = start; j < end; j++) {
            functionCodes.add(codes.get(j));
        }
        return functionCodes;
    }

    public void initFunctions(FormerCode formerCode) {
        blocks = new LinkedList<Block>();
        secureCodes = new LinkedList<Block>();
        nonSecureCodes = new LinkedList<Block>();
        List<String> codes = formerCode.getCodes();
        Map<Integer, String> nameMap = initNameMap(codes);
        Set<Integer> codeNos = nameMap.keySet();
        int i = 0, formerCodeIndex = 0;
        String formerName = null;
        for (Integer codeNo : codeNos) {
            if (i == 0) {
                if (codeNo != 0) {  // #include<stdio.h>
                    Block block = new Block("void", moveFormerCode(codes, formerCodeIndex, codeNo));
                    blocks.add(block);
                    formerCodeIndex = codeNo;
                }
            } else {
                Block block = new Block(formerName, moveFormerCode(codes, formerCodeIndex, codeNo));
                blocks.add(block);
                formerCodeIndex = codeNo;
            }
            formerName = nameMap.get(codeNo);
            i++;
        }
        Block block = new Block(formerName, moveFormerCode(codes, formerCodeIndex, codes.size()));
        blocks.add(block);
    }

    public void identifyFunction() {
        for (Block block : blocks) {
            block.setSecure();  // if block has @TrustZone, set block secure
        }
        for (Block block : blocks) {
            if (block.level == 2) setBlockAll(block);   // @TrustZoneAll
        }
    }

    private void setBlockAll(Block block) { // recursion
        block.level = 2;
        for (String code : block.codes) {
            for (Block other : blocks) {    // other self-defined functions
                if (!other.name.equals(block.name)) {   // if call other ... function
                    if (code.contains(other.name + "(") && other.level != 2)    // other function is not @TZAll
                        setBlockAll(other);
                }
            }
        }
    }

    private void addSecureFunction(Block block) {
        List<String> codes1 = new ArrayList<String>();
        codes1.add(NSCAnnotation);  // __NONSECURE_ENTRY
        codes1.addAll(setOtherFunction(block).codes);   // secure code
        secureCodes.add(new Block(block.name, block.level, codes1));    // add secure block to secure code
        String externStr = "extern " + block.codes.get(0).replace(proAnnotation, "")
                .replace("{", "") + ";";    // extern secure functions
        List<String> externList = new ArrayList<String>();
        externList.add(externStr);
        nonSecureCodes.add(new Block("extern", externList));    // non-secure code add extern statement
    }

    public void separateSecureFunction() {
        s2nsFunctions = new ArrayList<FunctionInfo>();
        for (int i=0;i<blocks.size();i++) {
            Block block=blocks.get(i);
            if (block.name.equals("void")) {
                secureCodes.add(block); // secure code add headers
            } else {
                if (block.getLevel() != 0) {
                    addSecureFunction(block);   // secure code add secure block
//                    if (block.getLevel() == 1) blocks.set(i,setOtherFunction(block));
                } else {
                    s2nsFunctions.add(new FunctionInfo(block.name, 0)); // ns function name
                    nonSecureCodes.add(block);  // non-secure code add non-secure block
                }
            }
        }
    }

    public Block setOtherFunction(Block block) { // some ns=>s transform
        int i = 0;
        for (; i < block.codes.size(); i++) {
            String code = block.codes.get(i);
//        for(String code:block.codes){
            for (FunctionInfo functionInfo : s2nsFunctions) {   // ns function names
                if (code.contains(functionInfo.functionName)) {
                    if (functionInfo.pfName != null) {  // this ns function has been called once
                        code = code.replace(functionInfo.functionName, functionInfo.pfName);
                    } else {    // this ns function has not been called
                        setPf(functionInfo.functionName);
                        functionInfo.pfName = functionInfo.functionName + "_pf";
                        code = code.replace(functionInfo.functionName + "(", functionInfo.functionName + "_pf(1u");
                        //test1(); => test1_pf(1u);
//                        System.out.println(code);
                    }
                    block.codes.set(i, code);
                }
            }
        }
        return block;
    }

    public void setPf(String functionName) {
        int blockNo = 0;
        for (; blockNo < secureCodes.size(); blockNo++) {
            Block sBlock = secureCodes.get(blockNo);
            if (!sBlock.name.equals("void")) break;
        }
        List<String> tempCodes = new ArrayList<String>();
        tempCodes.add("static NonSecure_funcptr " + functionName + "_pf = (NonSecure_funcptr)NULL;");
        tempCodes.add(NSCAnnotation);   // __NONSECURE_ENTRY
        tempCodes.add("int32_t " + functionName + "_callback(NonSecure_funcptr *callback){");
        tempCodes.add("\t" + functionName + "_pf = (NonSecure_funcptr)cmse_nsfptr_create(callback);");
        tempCodes.add("\treturn 0;");
        tempCodes.add("}");
        secureCodes.add(blockNo, new Block("void", 2, tempCodes));

        blockNo = 0;
        for (; blockNo < nonSecureCodes.size(); blockNo++) {
            Block nsBlock = nonSecureCodes.get(blockNo);
            if (!nsBlock.name.equals("void")) break;
        }
        tempCodes = new ArrayList<String>();
        tempCodes.add("extern int32_t " + functionName + "_callback(int32_t (*)(uint32_t));");
        nonSecureCodes.add(blockNo, new Block("void", 0, tempCodes));
        for (; blockNo < blocks.size(); blockNo++) {
            Block nsBlock = blocks.get(blockNo);
            if (nsBlock.name.equals("main")) {
                String callback = "\t" + functionName + "_callback(&" + functionName + ");";
                if (nsBlock.codes.get(0).contains("{")) nsBlock.codes.add(1, callback);
                else nsBlock.codes.add(2, callback);
                blocks.set(blockNo, nsBlock);
                break;
            }
        }
    }

    public void identifyVariables() {
        variables = new ArrayList<VariableInfo>();
        for (Block block : blocks) {
            for (int j = 0; j < block.codes.size(); j++) {
                String code = block.codes.get(j);
                if (code.contains(proAnnotation)) { // protect annotation @TrustZone
                    VariableInfo variableInfo = new VariableInfo(code);
                    block.codes.set(j, code.replace(proAnnotation, ""));
                    variables.add(variableInfo);    // variables need to be  protected
//                    System.out.println(variableInfo);
                }
            }
        }
    }


    private void addRW(VariableInfo variableInfo) { // read & write
        int voidIndex = 0;
        for (; voidIndex < secureCodes.size(); voidIndex++) {
            if (!secureCodes.get(voidIndex).name.equals("void")) break;
        }
        List<String> RWCodes = new ArrayList<String>();
        RWCodes.add(variableInfo.declaration);
        RWCodes.add(NSCAnnotation);
        RWCodes.add(variableInfo.type + " read_" + variableInfo.name + "(){return " + variableInfo.name + ";}");
        RWCodes.add(NSCAnnotation);
        RWCodes.add("void write_" + variableInfo.name + "(" + variableInfo.type + " value){" + variableInfo.name + "=value;}");
        secureCodes.add(voidIndex, new Block("void", 2, RWCodes));
    }

    private void addIncDec(String code, VariableInfo vi) {  // increment or decrement
        String[] names = {"inc_left_", "inc_right", "dec_left_", "dec_right"};
        String[] bodies = {vi.type + " inc_left_" + vi.name + "(){++" + vi.name + ";return " + vi.name + ";}",
                vi.type + " inc_right_" + vi.name + "(){++" + vi.name + ";return " + vi.name + "-1;}",
                vi.type + " dec_left_" + vi.name + "(){--" + vi.name + ";return " + vi.name + ";}",
                vi.type + " dec_right_" + vi.name + "(){--" + vi.name + ";return " + vi.name + "-1;}"};
        for (int i = 0; i < names.length; i++) {
            if (code.contains(names[i])) {
                int voidIndex = 0;
                for (; voidIndex < secureCodes.size(); voidIndex++) {
                    if (!secureCodes.get(voidIndex).name.equals("void")) break;
                }
                List<String> RWCodes = new ArrayList<String>();
                RWCodes.add(bodies[i]);
                secureCodes.add(voidIndex, new Block("void", 2, RWCodes));
            }
        }
    }

    public void separateVariables() {
        for (Block block : nonSecureCodes) {
            for (int i = 0; i < block.codes.size(); i++) {
                String code = block.codes.get(i);
                for (VariableInfo vi : variables) {
                    String variable = vi.name;
                    if (CharacterTools.containsVariable(code, variable)) {
                        VariableTools variableTools = new VariableTools();
                        code = variableTools.replaceOperator(code, variable);   // transform variable statement
                        block.codes.set(i, code);
                        if (code.contains("inc_") || code.contains("dec_")) {
                            addIncDec(code, vi);
                        } else {
                            addRW(vi);
                        }
                    }
                }
            }
        }
    }

    public void change() {
        changePeripheralFun();
        changeTZHeader();
        changePeripheralHeaders();
        addSecureMain();
    }

    private void changePeripheralFun() {
        // TZ_GPIO_set_mode => TZ_GPIO_secure_set_mode
        for (Block secure : secureCodes) {
            for (int i = 0; i < secure.codes.size(); i++) {
                String code = secure.codes.get(i);
                if (code.contains("TZ_")) { // original API
                    code = code.replace("TZ_", "TZ_s_");
                    secure.codes.set(i, code);
                }
            }
        }
    }

    private void changeTZHeader() {
        boolean find = false;
        for (Block block : secureCodes) {
            if (find) break;
            for (int i = 0; i < block.codes.size(); i++) {
                String code = block.codes.get(i);
                if (code.contains("TZ.h")) {    // TZ.h ==> TZ_s.h + TZ_ns.h
                    block.codes.set(i, code.replace("TZ.h", "TZ_s.h"));
                    find = true;
                    break;
                }
            }
        }
        List<String> code = new ArrayList<String>();
        code.add("#include \"TZ_ns.h\"");
        nonSecureCodes.add(0, new Block("void", 2, code));
    }

    private void changePeripheralHeaders() {    // elastic headers
        boolean GPIO_use = false, UART_use = false;
        for (Block block : blocks) {
            for (String code : block.codes) {
                if (code.contains("TZ_GPIO")) GPIO_use = true;
                if (code.contains("TZ_UART")) UART_use = true;
                if (GPIO_use && UART_use) break;
            }
        }
        List<String> sAdd = new ArrayList<String>();
        List<String> nsAdd = new ArrayList<String>();
        if (GPIO_use) {
            sAdd.add("#include \"TZ_GPIO_s.h\"");
            nsAdd.add("#include \"TZ_GPIO_ns.h\"");
        }
        if (UART_use) {
            sAdd.add("#include \"TZ_UART_s.h\"");
            nsAdd.add("#include \"TZ_UART_ns.h\"");
        }
        if (!sAdd.isEmpty()) {
            secureCodes.add(0, new Block("void", 2, sAdd));
            nonSecureCodes.add(0, new Block("void", 0, nsAdd));
        }
    }

    private Block findFuncName(String name) {
        for (Block block : blocks) {
            if (block.name.equals(name)) {
                return block;
            }
        }
        return null;
    }

    private void addSecureMain() {
        Block nsMain = new Block("main", 0, new ArrayList<String>());
        for (Block ns : nonSecureCodes) {
            if (ns.name.equals("main")) {
                nsMain = ns;
                break;
            }
        }
        boolean out = false;
        List<String> sMainCode = new ArrayList<String>();
        sMainCode.add("int main(void){");
        for (int j = 1; j < nsMain.codes.size(); j++) {
            String code = nsMain.codes.get(j);
            if (code.contains("_callback(")) {
                continue;
            }
            String t = code.trim(); // delete blank of front and back
            if(t.contains(" ")){    // int a; <== temp variable
                out=true;
                break;
            }
            if (!t.contains("(")) continue;
            String name = t.substring(0, t.indexOf("("));
            if(name.equals("while")||name.equals("for")||name.equals("if")) {   // something will affect stop point
                out=true;
                break;
            }
            Block findBlock = findFuncName(name);
            if (findBlock != null) {
                if (findBlock.level != 0 && findBlock.codes.get(0).startsWith("void")) {
                    sMainCode.add(code);
                    nsMain.codes.remove(code);
                    j--;
                } else {
                    out = true;
                    break;
                }
            } else {
                // not find
                sMainCode.add(code);
                nsMain.codes.remove(code);
                j--;
            }
        }
        if (out) {
            sMainCode.add("    TZ_Boot_Init(NEXT_BOOT_BASE);"); // from ns to s
        }
        sMainCode.add("}");
        secureCodes.add(new Block("main", 2, sMainCode));
    }

    public void writeCode(String url){
        try{
            File file =new File(url+"main_s.c");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(url+"main_s.c",false);
            for(Block s:secureCodes){
                for (String code : s.codes) {
                    fileWriter.write(code+"\n");
                }
            }
            fileWriter.close();

            file =new File(url+"main_ns.c");
            if(!file.exists()){
                file.createNewFile();
            }
            fileWriter = new FileWriter(url+"main_ns.c",false);
            for(Block ns:nonSecureCodes){
                for (String code : ns.codes) fileWriter.write(code+"\n");
            }
            fileWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void showBlocks() {
        for (Block block : blocks) {
            System.out.println(block);
        }
    }

    public void showNS(){
        System.out.println("---nonSecure:");
        for (Block block : nonSecureCodes) {
            for (String code : block.codes) System.out.println(code);
        }
    }

    public void showProject() {
        System.out.println("final codes:");
        System.out.println("---secure:");
        for (Block block : secureCodes) {
            for (String code : block.codes) System.out.println(code);
        }
        System.out.println("---nonSecure:");
        for (Block block : nonSecureCodes) {
            for (String code : block.codes) System.out.println(code);
        }
        System.out.println();
        System.out.println("variables:");
        for (VariableInfo v : variables) {
            System.out.print(v.name + ",");
        }
        System.out.println();
    }
}
