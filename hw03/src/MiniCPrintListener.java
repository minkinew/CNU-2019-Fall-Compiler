import org.antlr.v4.runtime.tree.ParseTreeProperty;

public class MiniCPrintListener extends MiniCBaseListener {
    ParseTreeProperty<String> newTexts = new ParseTreeProperty<String>();
    public int indentDepth = 0;

    @Override
    public void exitProgram(MiniCParser.ProgramContext ctx) { // program End
        for (int i = 0; i < ctx.getChildCount(); i++) {
            System.out.println(newTexts.get(ctx.decl(i)));
        }
    }

    @Override
    public void exitDecl(MiniCParser.DeclContext ctx) { // decl End
        if (ctx.getChild(0) == ctx.var_decl())
            newTexts.put(ctx, newTexts.get(ctx.var_decl()));
        else
            newTexts.put(ctx, newTexts.get(ctx.fun_decl()));

    }

    @Override
    public void exitVar_decl(MiniCParser.Var_declContext ctx) { // var_decl End
        switch (ctx.getChild(2).getText()) {
            case ";": // type_spec IDENT ';'
                newTexts.put(ctx, newTexts.get(ctx.type_spec()) + " " + ctx.IDENT() + ";");
                break;
            case "=": // type_spec IDENT '=' LITERAL ';'
                newTexts.put(ctx, newTexts.get(ctx.type_spec()) + " " + ctx.IDENT() + " " + "= " + ctx.LITERAL() + ";");
                break;
            case "[": // type_spec IDENT '[' LITERAL ']' ';'
                newTexts.put(ctx, newTexts.get(ctx.type_spec()) + " " + ctx.IDENT() + "[" + ctx.LITERAL() + "];");
                break;
            default: // defalut
                break;
        }
    }

    @Override
    public void exitType_spec(MiniCParser.Type_specContext ctx) { // type_spec End
        if (ctx.getChild(0).equals(ctx.VOID())) // VOID
            newTexts.put(ctx, "" + ctx.VOID());
        else // INT
            newTexts.put(ctx, "" + ctx.INT());
    }

    @Override
    public void exitFun_decl(MiniCParser.Fun_declContext ctx) { // fun_decl End
        newTexts.put(ctx, newTexts.get(ctx.type_spec()) + " " + ctx.IDENT() + "(" + newTexts.get(ctx.params()) + ")" + newTexts.get(ctx.compound_stmt()));
    }

    @Override
    public void exitParams(MiniCParser.ParamsContext ctx) { // Params End
        StringBuffer stringbuffer2 = new StringBuffer();
        if (ctx.param().size() == 0 && ctx.getChildCount() == 1) // VOID
            newTexts.put(ctx, "" + ctx.VOID());
        else if (ctx.param().size() == 0 && ctx.getChildCount() != 1) // blank
            newTexts.put(ctx, "");
        else if (ctx.param().size() == 1) // param
            newTexts.put(ctx, newTexts.get(ctx.param(0)) + "");
        else { // param (',' param)*
            stringbuffer2.append(newTexts.get(ctx.param(0)));
            for (int i = 1; i < ctx.param().size(); i++)
                stringbuffer2.append(", " + newTexts.get(ctx.param(i)) + "");
            newTexts.put(ctx, stringbuffer2 + "");
        }
    }

    @Override
    public void exitParam(MiniCParser.ParamContext ctx) { // Param End
        switch (ctx.getChildCount()) {
            case 2: // type_spec IDENT
                newTexts.put(ctx, newTexts.get(ctx.type_spec()) + " " + ctx.IDENT());
                break;
            case 4: // type_spec IDENT '[' ']'
                newTexts.put(ctx, newTexts.get(ctx.type_spec()) + " " + ctx.IDENT() + "[ ]");
                break;
            default: // default
                break;
        }
    }

    @Override
    public void exitStmt(MiniCParser.StmtContext ctx) { // Stmt End
        if (ctx.getChild(0) == ctx.expr_stmt())  // expr_stmt
            newTexts.put(ctx, newTexts.get(ctx.expr_stmt()));
        else if (ctx.getChild(0) == ctx.compound_stmt())  // compound_stmt
            newTexts.put(ctx, newTexts.get(ctx.compound_stmt()));
        else if (ctx.getChild(0) == ctx.if_stmt())  // if_stmt
            newTexts.put(ctx, newTexts.get(ctx.if_stmt()));
        else if (ctx.getChild(0) == ctx.while_stmt())  // while_stmt
            newTexts.put(ctx, newTexts.get(ctx.while_stmt()));
        else if (ctx.getChild(0) == ctx.return_stmt())  // return_stmt
            newTexts.put(ctx, newTexts.get(ctx.return_stmt()));
    }

    @Override
    public void exitExpr_stmt(MiniCParser.Expr_stmtContext ctx) { // expr_stmt End
        newTexts.put(ctx, newTexts.get(ctx.expr()) + ";");
    }

    @Override
    public void exitWhile_stmt(MiniCParser.While_stmtContext ctx) { // while_stmt End
        String indentStringEdge = "";
        String indentStringInner = "";

        for (int indentCount = 0; indentCount < indentDepth; indentCount++)
            indentStringEdge += "....";

        for (int indentCount = 0; indentCount < indentDepth + 1; indentCount++)
            indentStringInner += "....";

        newTexts.put(ctx, ctx.WHILE() + " (" + newTexts.get(ctx.expr()) + ")");
        String check = ctx.stmt().getChild(0).getChild(0).getText();
        if (check.equals("{"))
            newTexts.put(ctx, newTexts.get(ctx) + newTexts.get(ctx.stmt()));
        else // "{ }" 없이 while문이 온 경우
            newTexts.put(ctx, newTexts.get(ctx) + "\n" + indentStringEdge + "{\n" + indentStringInner +
                    newTexts.get(ctx.stmt()) + "\n" + indentStringEdge + "}");
    }

    @Override
    public void enterCompound_stmt(MiniCParser.Compound_stmtContext ctx) { // Compound_stmt Start
        // "{ }" 부분이므로 들여쓰기 필요하여 들여쓰기를 진행하기 위해 indentDepth를 1 증가
        indentDepth += 1;
    }

    @Override
    public void exitCompound_stmt(MiniCParser.Compound_stmtContext ctx) { // Compound End
        // indent(들여쓰기) 부분
        String indentStringEdge = "";
        String indentStringInner = "";

        for (int indentCount = 0; indentCount < indentDepth - 1; indentCount++)
            indentStringEdge += "....";

        for (int indentCount = 0; indentCount < indentDepth; indentCount++)
            indentStringInner += "....";

        newTexts.put(ctx, "\n" + indentStringEdge + "{\n"); // '{'과 들여쓰기
        for (int count = 0; count < ctx.local_decl().size(); count++) // local_decl*
            newTexts.put(ctx, newTexts.get(ctx) + indentStringInner + newTexts.get(ctx.local_decl(count)) + "\n");

        for (int count = 0; count < ctx.stmt().size(); count++) { // stmt*
            newTexts.put(ctx, newTexts.get(ctx) + indentStringInner + newTexts.get(ctx.stmt(count)) + "\n");
        }
        newTexts.put(ctx, newTexts.get(ctx) + indentStringEdge + "}"); // '}'과 들여쓰기
        indentDepth -= 1;
    }

    @Override
    public void exitLocal_decl(MiniCParser.Local_declContext ctx) { // local_decl End
        switch (ctx.getChild(2).getText()) {
            case ";": // type_spec IDENT ';'
                newTexts.put(ctx, newTexts.get(ctx.type_spec()) + " " + ctx.IDENT() + ";");
                break;
            case "=": // type_spec IDENT '=' LITERAL ';'
                newTexts.put(ctx, newTexts.get(ctx.type_spec()) + " " + ctx.IDENT() + " " + "= " + ctx.LITERAL() + ";");
                break;
            case "[": // type_spec IDENT '[' LITERAL ']' ';'
                newTexts.put(ctx, newTexts.get(ctx.type_spec()) + " " + ctx.IDENT() + "[" + ctx.LITERAL() + "];");
                break;
            default: // defalut
                break;
        }
    }

    @Override
    public void exitIf_stmt(MiniCParser.If_stmtContext ctx) { // if_stmt End
        String indentStringEdge = "";
        String indentStringInner = "";
        // indent(들여쓰기)
        for (int indentCount = 0; indentCount < indentDepth; ++indentCount)
            indentStringEdge += "....";

        for (int indentCount = 0; indentCount < indentDepth + 1; ++indentCount)
            indentStringInner += "....";

        if (ctx.stmt().size() == 1) { // if
            newTexts.put(ctx, ctx.IF() + " (" + newTexts.get(ctx.expr()) + ")");
            String check = ctx.stmt(0).getChild(0).getChild(0).getText();
            if (check.equals("{"))
                newTexts.put(ctx, newTexts.get(ctx) + newTexts.get(ctx.stmt(0)));
            else // "{ }" 없이 if문이 온 경우
                newTexts.put(ctx, newTexts.get(ctx) + "\n" + indentStringEdge + "{\n" + indentStringInner +
                        newTexts.get(ctx.stmt(0)) + "\n" + indentStringEdge + "}");
        } else { // if ~ else ~
            // if ~ 부분
            newTexts.put(ctx, ctx.IF() + " (" + newTexts.get(ctx.expr()) + ")");
            String check = ctx.stmt(0).getChild(0).getChild(0).getText();
            if (check.equals("{"))
                newTexts.put(ctx, newTexts.get(ctx) + newTexts.get(ctx.stmt(0)));
            else // "{ }" 없이 if문이 온 경우
                newTexts.put(ctx, newTexts.get(ctx) + "\n" + indentStringEdge + "{\n" + indentStringInner +
                        newTexts.get(ctx.stmt(0)) + "\n" + indentStringEdge + "}");


            // else ~ 부분
            newTexts.put(ctx, ctx.IF() + " (" + newTexts.get(ctx.expr()) + ")");
            check = ctx.stmt(1).getChild(0).getChild(0).getText();
            if (check.equals("{"))
                newTexts.put(ctx, newTexts.get(ctx) + newTexts.get(ctx.stmt(1)));
            else // "{ }" 없이 else 문이 온 경우
                newTexts.put(ctx, newTexts.get(ctx) + "\n" + indentStringEdge + "{\n" + indentStringInner +
                        newTexts.get(ctx.stmt(1)) + "\n" + indentStringEdge + "}");
        }
    }

    @Override
    public void exitReturn_stmt(MiniCParser.Return_stmtContext ctx) { // return_stmt End
        switch (ctx.getChildCount()) {
            case 1: // RETURN
                newTexts.put(ctx, ctx.RETURN() + ";");
                break;
            case 2: // RETURN expr
                newTexts.put(ctx, ctx.RETURN() + newTexts.get(ctx.expr()) + ";");
                break;
            default: // default
                break;
        }
    }

    @Override
    public void exitExpr(MiniCParser.ExprContext ctx) { // expr End
        String s1, s2, operation;

        if (ctx.getChild(0) == ctx.LITERAL())  // LITERAL
            newTexts.put(ctx, ctx.LITERAL() + "");
        else if (ctx.getChild(0).getText() == "(")  // '(' expr ')'
            newTexts.put(ctx, "(" + newTexts.get(ctx.expr(0)) + ")");
        else if (ctx.getChild(0) == ctx.IDENT()) { // IDENT
            if (ctx.getChildCount() == 1) // IDENT
                newTexts.put(ctx, ctx.IDENT() + "");
            else if (ctx.expr().size() == 1 && ctx.getChildCount() != 3)  // '[' expr ']'
                newTexts.put(ctx, ctx.IDENT() + "[" + ctx.expr(0) + "]");
            else if (ctx.expr().size() == 1 && ctx.getChildCount() == 3) // IDENT '=' expr
                newTexts.put(ctx, ctx.IDENT() + " = " + newTexts.get(ctx.expr(0)));
            else if (ctx.expr().size() == 2) // IDENT '[' expr ']' '=' expr
                newTexts.put(ctx, ctx.IDENT() + "[" + newTexts.get(ctx.expr(0)) + "] = " + newTexts.get(ctx.expr(1)));
            else // IDENT '(' args ')'
                newTexts.put(ctx, ctx.IDENT() + "(" + newTexts.get(ctx.args()) + ")");
        } else if (ctx.getChildCount() == 3 && ctx.getChild(1) != ctx.expr()) {  // expr ' ' expr
            s1 = newTexts.get(ctx.expr(0));
            s2 = newTexts.get(ctx.expr(1));
            operation = ctx.getChild(1).getText();
            newTexts.put(ctx, s1 + " " + operation + " " + s2);
        } else if (ctx.getChild(0) != ctx.expr()) { // ' ' expr
            operation = ctx.getChild(0).getText();
            newTexts.put(ctx, operation + "" + newTexts.get(ctx.expr(0)));
        }
    }

    @Override
    public void exitArgs(MiniCParser.ArgsContext ctx) { // args End
        StringBuffer stringbuffer3 = new StringBuffer();

        if (ctx.getChildCount() == 0)  // blank
            newTexts.put(ctx, "");
        else if (ctx.expr().size() == 1) // expr
            newTexts.put(ctx, newTexts.get(ctx.expr(0)) + "");
        else { // expr (',' expr)*
            stringbuffer3.append(newTexts.get(ctx.expr(0)));
            for (int i = 1; i < ctx.expr().size(); i++)
                stringbuffer3.append("(, " + newTexts.get(ctx.expr(i)) + ")");
            newTexts.put(ctx, stringbuffer3 + "");
        }
    }

}