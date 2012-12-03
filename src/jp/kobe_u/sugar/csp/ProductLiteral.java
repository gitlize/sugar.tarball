package jp.kobe_u.sugar.csp;

import java.io.IOException;
import java.util.Set;

import jp.kobe_u.sugar.SugarException;
import jp.kobe_u.sugar.encoder.OldEncoder;

/**
 * NOT IMPLEMENTED YET.
 * This class implements a literal for arithmetic power.
 * @see CSP
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class ProductLiteral extends Literal {
	private IntegerVariable v;
	private IntegerVariable v1;
	private IntegerVariable v2;

	public ProductLiteral(IntegerVariable v, IntegerVariable v1, IntegerVariable v2) {
		this.v = v;
		this.v1 = v1;
		this.v2 = v2;
	}

	@Override
	public Set<IntegerVariable> getVariables() {
		// TODO
		return null;
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
		return v.getValue() == v1.getValue() * v2.getValue();
	}

	@Override
	public void encode(OldEncoder encoder, int[] clause) throws SugarException, IOException {
		// TODO encode
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public String toString() {
		String s = "(eq " + v.getName() + " (mul " + v1.getName() + " " + v2.getName() + "))";
		return s;
	}
}
