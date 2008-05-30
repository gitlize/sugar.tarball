package jp.ac.kobe_u.cs.sugar.expression;

import java.util.HashMap;
import java.util.List;

import jp.ac.kobe_u.cs.sugar.SugarConstants;

/**
 * This is an abstract class for expressions.
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public abstract class Expression implements Comparable<Expression> {
	public static boolean intern = false;
	public static final int MAX_MAP_SIZE = 100000;
	private static HashMap<Expression,Expression> map =
		new HashMap<Expression,Expression>();
	public static final Expression DOMAIN_DEFINITION =
		create(SugarConstants.DOMAIN_DEFINITION);
	public static final Expression INT_DEFINITION =
		create(SugarConstants.INT_DEFINITION);
	public static final Expression BOOL_DEFINITION =
		create(SugarConstants.BOOL_DEFINITION);
	public static final Expression PREDICATE_DEFINITION =
		create(SugarConstants.PREDICATE_DEFINITION);
	public static final Expression RELATION_DEFINITION =
		create(SugarConstants.RELATION_DEFINITION);
	public static final Expression OBJECTIVE_DEFINITION =
		create(SugarConstants.OBJECTIVE_DEFINITION);
	public static final Expression MINIMIZE =
		create(SugarConstants.MINIMIZE);
	public static final Expression MAXIMIZE =
		create(SugarConstants.MAXIMIZE);
	public static final Expression SUPPORTS =
		create(SugarConstants.SUPPORTS);
	public static final Expression CONFLICTS =
		create(SugarConstants.CONFLICTS);
	public static final Expression FALSE =
		create(SugarConstants.FALSE);
	public static final Expression TRUE =
		create(SugarConstants.TRUE);
	public static final Expression NOT =
		create(SugarConstants.NOT);
	public static final Expression AND =
		create(SugarConstants.AND);
	public static final Expression OR =
		create(SugarConstants.OR);
	public static final Expression IMP =
		create(SugarConstants.IMP);
	public static final Expression XOR =
		create(SugarConstants.XOR);
	public static final Expression IFF =
		create(SugarConstants.IFF);
	public static final Expression EQ =
		create(SugarConstants.EQ);
	public static final Expression NE =
		create(SugarConstants.NE);
	public static final Expression LE =
		create(SugarConstants.LE);
	public static final Expression LT =
		create(SugarConstants.LT);
	public static final Expression GE =
		create(SugarConstants.GE);
	public static final Expression GT =
		create(SugarConstants.GT);
	public static final Expression NEG =
		create(SugarConstants.NEG);
	public static final Expression ABS =
		create(SugarConstants.ABS);
	public static final Expression ADD =
		create(SugarConstants.ADD);
	public static final Expression SUB =
		create(SugarConstants.SUB);
	public static final Expression MUL =
		create(SugarConstants.MUL);
	public static final Expression DIV =
		create(SugarConstants.DIV);
	public static final Expression MOD =
		create(SugarConstants.MOD);
	public static final Expression POW =
		create(SugarConstants.POW);
	public static final Expression MIN =
		create(SugarConstants.MIN);
	public static final Expression MAX =
		create(SugarConstants.MAX);
	public static final Expression IF =
		create(SugarConstants.IF);
	public static final Expression ALLDIFFERENT =
		create(SugarConstants.ALLDIFFERENT);
	public static final Expression WEIGHTEDSUM =
		create(SugarConstants.WEIGHTEDSUM);
	public static final Expression CUMULATIVE =
		create(SugarConstants.CUMULATIVE);
	public static final Expression ELEMENT =
		create(SugarConstants.ELEMENT);
	public static final Expression NIL =
		create(SugarConstants.NIL);
	public static final Expression ZERO =
		create(0);
	public static final Expression ONE =
		create(1);
	private String comment = null; 
	
	private static Expression intern(Expression x) {
		if (intern) {
			if (! map.containsKey(x)) {
				if (map.size() < MAX_MAP_SIZE) {
					map.put(x, x);
				}
			} else {
				// System.out.println("Found " + x);
			}
		}
		return x;
	}
	
	public static Expression create(Integer i) {
		return intern(new Atom(i));
	}

	public static Expression create(String token) {
		return intern(new Atom(token));
	}

	public static Expression create(Expression[] expressions) {
		return intern(new Sequence(expressions));
	}

	public static Expression create(List<Expression> expressions) {
		return intern(new Sequence(expressions));
	}

	public static Expression create(Expression x0) {
		return create(new Expression[] { x0 });
	}

	public static Expression create(Expression x0, Expression x1) {
		return create(new Expression[] { x0, x1 });
	}

	public static Expression create(Expression x0, Expression x1, Expression x2) {
		return create(new Expression[] { x0, x1, x2 });
	}

	public static Expression create(Expression x0, Expression x1, Expression x2, Expression x3) {
		return create(new Expression[] { x0, x1, x2, x3 });
	}

	public static void clear() {
		map.clear();
	}
	
	public boolean isAtom() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	public boolean isString(String s) {
		return false;
	}

	public boolean isInteger() {
		return false;
	}
	
	public boolean isSequence() {
		return false;
	}

	public boolean isSequence(Expression x) {
		return false;
	}

	public boolean isSequence(int arity) {
		return false;
	}

	public String stringValue() {
		return null;
	}

	public Integer integerValue() {
		return null;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Expression not() {
		return create(NOT, this);
	}

	public Expression and(Expression x) {
		return create(AND, this, x);
	}

	public Expression or(Expression x) {
		return create(OR, this, x);
	}

	public Expression imp(Expression x) {
		return create(IMP, this, x);
	}

	public Expression eq(Expression x) {
		return create(EQ, this, x);
	}

	public Expression ne(Expression x) {
		return create(NE, this, x);
	}

	public Expression le(Expression x) {
		return create(LE, this, x);
	}

	public Expression lt(Expression x) {
		return create(LT, this, x);
	}

	public Expression ge(Expression x) {
		return create(GE, this, x);
	}

	public Expression gt(Expression x) {
		return create(GT, this, x);
	}

	public Expression neg() {
		return create(NEG, this);
	}

	public Expression abs() {
		return create(ABS, this);
	}

	public Expression add(Expression x) {
		return create(ADD, this, x);
	}

	public Expression sub(Expression x) {
		return create(SUB, this, x);
	}

	public Expression mul(Expression x) {
		return create(MUL, this, x);
	}

	public Expression div(Expression x) {
		return create(DIV, this, x);
	}

	public Expression mod(Expression x) {
		return create(MOD, this, x);
	}

	public Expression min(Expression x) {
		return create(MIN, this, x);
	}

	public Expression max(Expression x) {
		return create(MAX, this, x);
	}

	public static void appendString(StringBuilder sb, int[] xs) {
		String delim = "";
		for (int x : xs) {
			sb.append(delim + x);
			delim = " ";
		}
	}

	public static void appendString(StringBuilder sb, Object[] xs) {
		String delim = "";
		for (Object x : xs) {
			sb.append(delim);
			sb.append(x.toString());
			delim = " ";
		}
	}

}
