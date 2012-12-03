package jp.kobe_u.sugar.csp;

import java.io.IOException;
import java.util.Set;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.encoder.OldEncoder;
import jp.kobe_u.sugar.expression.Expression;

/**
 * This class implements a literal for constraints when they are not linearlized nor decomposed.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class HoldLiteral extends Literal {
    private Expression expr;
    private boolean negative;

	public HoldLiteral(Expression expr, boolean negative) {
        this.expr = expr;
        this.negative = negative;
    }
	
	public Expression getExpression() {
        return expr;
	}

    public boolean getNegative() {
        return negative;
    }

    @Override
	public Set<IntegerVariable> getVariables() {
		// TODO
		return null;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public boolean isValid() throws SugarException {
		return false;
	}

	@Override
	public boolean isUnsatisfiable() throws SugarException {
		return false;
	}
	
	@Override
	public int propagate() {
		// TODO propagate
		return 0;
	}

	@Override
	public boolean isSatisfied() {
		// TODO isSatisfied
		return false;
	}

	@Override
	public void encode(OldEncoder encoder, int[] clause) throws SugarException, IOException {
		// TODO encode
	    throw new SugarException("Encoding HoldLiteral is not implemented");
	}

	@Override
	public String toString() {
	    if (negative)
	        return "(not " + expr.toString() + ")";
	    else
	        return expr.toString();
	}

}
