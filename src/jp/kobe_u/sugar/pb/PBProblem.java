package jp.kobe_u.sugar.pb;

import java.util.List;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.SugarMain;

public abstract class PBProblem {
    protected int variablesCount = 0;
    protected int constraintsCount = 0;
    protected long fileSize = 0;
    private int variablesCountSave = 0;
    private int constraintsCountSave = 0;
    private long fileSizeSave = 0;
    
    public void clear() throws SugarException {
        variablesCount = 0;
        constraintsCount = 0;
        fileSize = 0;
        commit();
    }
    
    public void commit() throws SugarException {
        variablesCountSave = variablesCount;
        constraintsCountSave = constraintsCount;
        fileSizeSave = fileSize;
    }
    
    public void cancel() throws SugarException {
        variablesCount = variablesCountSave;
        constraintsCount = constraintsCountSave;
        fileSize = fileSizeSave;
    }
    
    public abstract void done() throws SugarException;

    public int addVariables(int number) throws SugarException {
        int code = variablesCount + 1;
        variablesCount += number;
        return code;
    }

    public void addComment(String comment) throws SugarException {
    }

    public abstract void addPBConstraint(PBExpr expr) throws SugarException;

    public void addPB(PBExpr expr) throws SugarException {
        if (expr.isValid())
            return;
        addPBConstraint(expr);
    }

    public abstract void addMinExpr(PBExpr minExpr) throws SugarException;

    public String summary() {
        return
        variablesCount + " PB variables, " +
        constraintsCount + " PB clauses, " +
        fileSize + " bytes";
    }

}
