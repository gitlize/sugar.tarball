package jp.kobe_u.sugar.csp;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import jp.kobe_u.sugar.SugarConstants;
import jp.kobe_u.sugar.SugarException;

/**
 * This class implements a comparison literal of CSP.
 * The comparison represents the condition "linearSum cmp 0".
 * @see CSP
 * @see LinearSum
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public abstract class LinearLiteral extends Literal {
    protected LinearSum linearSum;
    protected String cmp;

	/**
	 * Constructs a new comparison literal of given linear expression.
	 * @param linearSum the linear expression
	 */
	public LinearLiteral(LinearSum linearSum) {
		int factor = linearSum.factor();
		if (factor > 1) {
			linearSum.divide(factor);
		}
		this.linearSum = linearSum;
	}
	
	@Override
	public Set<IntegerVariable> getVariables() {
		return linearSum.getVariables();
	}

	protected int floorDiv(int b, int a) {
        if (a < 0) {
            a = - a;
            b = - b;
        }
        if (b >= 0)
            return b / a;
        else
            return (b - a + 1) / a;
    }
    
    protected int ceilDiv(int b, int a) {
        if (a < 0) {
            a = - a;
            b = - b;
        }
        if (b >= 0)
            return (b + a - 1) / a;
        else
            return b / a;
    }
    
	/**
	 * Returns the linear expression of the comparison literal.
	 * @return the linear expression
	 */
	public LinearSum getLinearExpression() {
		return linearSum;
	}
	
	/**
	 * Returns the string representation of the comparison literal.
	 * @return the string representation
	 */
	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("(" + SugarConstants.WSUM + " (");
	    String delim = "";
	    for (IntegerVariable v : linearSum.getVariables()) {
	        sb.append(delim);
	        sb.append("(" + linearSum.getA(v) + " " + v.getName() + ")");
	        delim = " ";
	    }
        sb.append(") " + cmp + " ");
	    sb.append(- linearSum.getB());
	    sb.append(")");
	    return sb.toString();
	}

}
