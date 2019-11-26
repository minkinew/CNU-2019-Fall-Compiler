package listener.main;

import java.util.HashMap;
import java.util.Map;
import generated.MiniCParser;
import generated.MiniCParser.Fun_declContext;
import generated.MiniCParser.Local_declContext;
import generated.MiniCParser.Var_declContext;

import static listener.main.BytecodeGenListenerHelper.*;


public class SymbolTable {
    enum Type {
        INT, INTARRAY, VOID, ERROR
    }

    static public class VarInfo {
        Type type;
        int id;
        int initVal;

        public VarInfo(Type type, int id, int initVal) {
            this.type = type;
            this.id = id;
            this.initVal = initVal;
        }

        public VarInfo(Type type, int id) {
            this.type = type;
            this.id = id;
            this.initVal = 0;
        }
    }

    static public class FInfo {
        public String sigStr;
    }

    private Map<String, VarInfo> _lsymtable = new HashMap<>();    // local v.
    private Map<String, VarInfo> _gsymtable = new HashMap<>();    // global v.
    private Map<String, FInfo> _fsymtable = new HashMap<>();    // function


    private int _globalVarID = 0;
    private int _localVarID = 0;
    private int _labelID = 0;
    private int _tempVarID = 0;

    SymbolTable() {
        initFunDecl();
        initFunTable();
    }

    void initFunDecl() {        // at each func decl
        _localVarID = 0;
        _labelID = 0;
        _tempVarID = 32;
        _lsymtable = new HashMap<>();
    }

    void putLocalVar(String varname, Type type) { // putLocalVar
        //<Fill here>
        VarInfo localVar = new VarInfo(type, _localVarID); // VarInfo로  localVar객체 생성
        _lsymtable.put(varname, localVar); // local v 해쉬맵에 put
        _localVarID++; // 1증가
    }

    void putGlobalVar(String varname, Type type) { // putGlobalVar
        //<Fill here>
        VarInfo globalVar = new VarInfo(type, _globalVarID); // VarInfo로 global 객체 생성
        _gsymtable.put(varname, globalVar); // global v 해쉬맵에 put
        _globalVarID++; // 1증가
    }

    void putLocalVarWithInitVal(String varname, Type type, int initVar) { // putLocalVarWithInitVal
        //<Fill here>
        VarInfo localVarWithInitVal = new VarInfo(type, _localVarID, initVar); // VarInfo로 local initVal 객체 생성
        _lsymtable.put(varname, localVarWithInitVal); // local v 해쉬맵에 put
        _localVarID++; // 1증가
    }

    void putGlobalVarWithInitVal(String varname, Type type, int initVar) { // putGlobalVarWithInitVal
        //<Fill here>
        VarInfo globalVarWithInitVal = new VarInfo(type, _globalVarID, initVar); // VarInfo로 global initVal 객체 생성
        _gsymtable.put(varname, globalVarWithInitVal); // global v 해쉬맵에 put
        _globalVarID++; // 1증가
    }

    void putParams(MiniCParser.ParamsContext params) { // putParams
        for(int i = 0; i < params.param().size(); i++) {
            //<Fill here>
            MiniCParser.ParamContext paramContext = params.param(i); // ParamContext 객체 생성
            MiniCParser.Type_specContext type_specContext = paramContext.type_spec(); // Type_specContext 객체 생성
            if (type_specContext.VOID() != null) { // VOID일 때
                putLocalVar(paramContext.IDENT().getText(), Type.VOID);
            } else if (type_specContext.INT() != null) { // INT일 때
                putLocalVar(paramContext.IDENT().getText(), Type.INT);
            }
        }
    }


    private void initFunTable() {
        FInfo printlninfo = new FInfo();
        printlninfo.sigStr = "java/io/PrintStream/println(I)V";

        FInfo maininfo = new FInfo();
        maininfo.sigStr = "main([Ljava/lang/String;)V";
        _fsymtable.put("_print", printlninfo);
        _fsymtable.put("main", maininfo);
    }

    public String getFunSpecStr(String fname) { // getFunSpecStr
        // <Fill here>
        FInfo fInfo = _fsymtable.get(fname);
        return fInfo.sigStr;
    }

    public String getFunSpecStr(Fun_declContext ctx) { // getFunSpecStr
        // <Fill here>
        return ctx.getText();
    }

    public String putFunSpecStr(Fun_declContext ctx) { // putFunSpecStr
        String fname = getFunName(ctx);
        String argtype = "";
        String rtype = "";
        String res = "";

        // <Fill here>
        argtype += getParamTypesText(ctx.params()); // params
        rtype += getTypeText(ctx.type_spec()); // type_spec

        res = fname + "(" + argtype + ")" + rtype;

        FInfo finfo = new FInfo();
        finfo.sigStr = res;
        _fsymtable.put(fname, finfo);

        return res;
    }

    String getVarId(String name) { // getVarId
        // <Fill here>
        VarInfo lvar = (VarInfo) _lsymtable.get(name);
        if (lvar != null)
            return Integer.toString(lvar.id);

        VarInfo gvar = (VarInfo) _lsymtable.get(name);
        if (gvar != null)
            return Integer.toString(gvar.id);

        return null;
    }

    Type getVarType(String name) {
        VarInfo lvar = (VarInfo) _lsymtable.get(name);
        if (lvar != null) {
            return lvar.type;
        }

        VarInfo gvar = (VarInfo) _gsymtable.get(name);
        if (gvar != null) {
            return gvar.type;
        }

        return Type.ERROR;
    }

    String newLabel() {
        return "label" + _labelID++;
    }

    String newTempVar() {
        String id = "";
        return id + _tempVarID--;
    }

    // global
    public String getVarId(Var_declContext ctx) { // getVarID
        // <Fill here>
        return ctx.getChild(0).getText();
    }

    // local
    public String getVarId(Local_declContext ctx) { // getVarID
        String sname = "";
        sname += getVarId(ctx.IDENT().getText());
        return sname;
    }

}
