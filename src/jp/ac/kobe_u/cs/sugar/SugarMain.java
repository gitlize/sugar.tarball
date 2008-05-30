package jp.ac.kobe_u.cs.sugar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import jp.ac.kobe_u.cs.sugar.converter.Converter;
import jp.ac.kobe_u.cs.sugar.csp.CSP;
import jp.ac.kobe_u.cs.sugar.csp.IntegerDomain;
import jp.ac.kobe_u.cs.sugar.csp.IntegerVariable;
import jp.ac.kobe_u.cs.sugar.encoder.Encoder;
import jp.ac.kobe_u.cs.sugar.expression.Expression;
import jp.ac.kobe_u.cs.sugar.expression.Parser;

/**
 * SugarMain main class.
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class SugarMain {
	boolean maxCSP = false;
	boolean competition = false;
	boolean incremental = false;

	private List<Expression> toMaxCSP(List<Expression> expressions0) throws SugarException {
		List<Expression> expressions = new ArrayList<Expression>();
		List<Expression> sum = new ArrayList<Expression>();
		sum.add(Expression.ADD);
		int n = 0;
		for (Expression x: expressions0) {
			if (x.isSequence(Expression.DOMAIN_DEFINITION)
				|| x.isSequence(Expression.INT_DEFINITION)
				|| x.isSequence(Expression.BOOL_DEFINITION)
				|| x.isSequence(Expression.PREDICATE_DEFINITION)
				|| x.isSequence(Expression.RELATION_DEFINITION)) {
				expressions.add(x);
			} else if (x.isSequence(Expression.OBJECTIVE_DEFINITION)){
				throw new SugarException("Illegal " + x);
			} else {
				// (int _Cn 0 1)
				// (or (ge _Cn 1) constraint)
				Expression c = Expression.create("_C" + n);
				expressions.add(Expression.create(
						Expression.INT_DEFINITION,
						c,
						Expression.ZERO,
						Expression.ONE));
				x = (c.ge(Expression.ONE)).or(x);
				expressions.add(x);
				sum.add(c);
				n++;
			}
		}
		// (int _COST 0 n)
		// (ge _COST (add _C1 ... _Cn))
		// (objective minimize _COST)
		Expression cost = Expression.create("_COST");
		expressions.add(Expression.create(
				Expression.INT_DEFINITION,
				cost,
				Expression.ZERO,
				Expression.create(n)));
		expressions.add(cost.ge(Expression.create(sum)));
		expressions.add(Expression.create(
				Expression.OBJECTIVE_DEFINITION,
				Expression.MINIMIZE,
				cost));
		Logger.log("MAX CSP: " + n + " constraints");
		return expressions;
	}
	
	public void encode(String cspFileName, String satFileName, String mapFileName)
	throws SugarException, IOException {
		Logger.log("Parsing " + cspFileName);
		BufferedReader reader;
		if (cspFileName.endsWith(".gz")) {
			InputStream in = new GZIPInputStream(new FileInputStream(cspFileName));
			reader = new BufferedReader(new InputStreamReader(in));
		} else {
			reader = new BufferedReader(new FileReader(cspFileName));
		}
		Parser parser = new Parser(reader);
		List<Expression> expressions = parser.parse();
		Logger.log("parsed " + expressions.size() + " expressions");
		Logger.status();
		if (maxCSP) {
			expressions = toMaxCSP(expressions);
		}
		Logger.log("Converting to clausal form CSP");
		CSP csp = new CSP();
		Converter converter = new Converter(csp);
		converter.convert(expressions);
		Logger.log("CSP : " + csp.summary());
		Logger.status();
		// csp.output(System.out);
		Logger.log("Propagation in CSP");
		csp.propagate();
		Logger.log("CSP : " + csp.summary());
		Logger.status();
		// csp.output(System.out);
		if (csp.isUnsatisfiable()) {
			Logger.log("CSP is unsatisfiable");
			Logger.println("s UNSATISFIABLE");
		} else {
			Logger.log("Simplifing CSP by introducing new Boolean variables");
			csp.simplify();
			Logger.log("CSP : " + csp.summary());
			Logger.status();
			// csp.output(System.out);

			parser = null;
			converter = null;
			expressions = null;
			Expression.clear();
			Runtime.getRuntime().gc();

			if (csp.isUnsatisfiable()) {
				Logger.log("CSP is unsatisfiable after propagation");
				Logger.println("s UNSATISFIABLE");
			} else {
				Logger.log("Encoding CSP to SAT : " + satFileName);
				Encoder encoder = new Encoder(csp);
				encoder.encode(satFileName, incremental);
				Logger.log("Writing map file : " + mapFileName);
				encoder.outputMap(mapFileName);
				Logger.status();
				Logger.log("SAT : " + encoder.summary());
			}
		}
	}
	
	public void decode(String outFileName, String mapFileName)
	throws SugarException, IOException {
		Logger.log("Decoding " + outFileName);
		CSP csp = new CSP();
		String objectiveVariableName = null;
		BufferedReader rd = new BufferedReader(new FileReader(mapFileName));
		while (true) {
			String line = rd.readLine();
			if (line == null)
				break;
			String[] s = line.split("\\s+");
			if (s[0].equals("objective")) {
				if (s[1].equals(SugarConstants.MINIMIZE)) {
					csp.setObjective(CSP.Objective.MINIMIZE);
				} else if (s[1].equals(SugarConstants.MAXIMIZE)) {
					csp.setObjective(CSP.Objective.MAXIMIZE);
				}
				objectiveVariableName = s[2];
			} else if (s[0].equals("int")) {
				String name = s[1];
				int code = Integer.parseInt(s[2]);
				IntegerDomain domain = null;
				if (s.length == 4) {
					int lb;
					int ub;
					int pos = s[3].indexOf("..");
					if (pos < 0) {
						lb = ub = Integer.parseInt(s[3]);
					} else {
						lb = Integer.parseInt(s[3].substring(0, pos));
						ub = Integer.parseInt(s[3].substring(pos+2));
					}
					domain = new IntegerDomain(lb, ub);
				} else {
					SortedSet<Integer> d = new TreeSet<Integer>();
					for (int i = 3; i < s.length; i++) {
						int lb;
						int ub;
						int pos = s[i].indexOf("..");
						if (pos < 0) {
							lb = ub = Integer.parseInt(s[i]);
						} else {
							lb = Integer.parseInt(s[i].substring(0, pos));
							ub = Integer.parseInt(s[i].substring(pos+2));
						}
						for (int value = lb; value <= ub; value++) {
							d.add(value);
						}
					}
					domain = new IntegerDomain(d);
				}
				IntegerVariable v = new IntegerVariable(name, domain);
				v.setCode(code);
				csp.add(v);
				if (name.equals(objectiveVariableName)) {
					csp.setObjectiveVariable(v);
				}
			} else if (s[0].equals("bool")) {
				// TODO
			}
		}
		rd.close();
		Encoder encoder = new Encoder(csp);
		if (encoder.decode(outFileName)) {
			if (csp.getObjectiveVariable() == null) {
				Logger.println("s SATISFIABLE");
			} else {
				String name = csp.getObjectiveVariable().getName();
				int value = csp.getObjectiveVariable().getValue();
				Logger.println("c OBJECTIVE " + name + " " + value);
				Logger.println("o " + value);
			}
			if (competition) {
				Logger.print("v");
				for (IntegerVariable v : csp.getIntegerVariables()) {
					if (! v.isAux() && ! v.getName().startsWith("_")) {
						Logger.print(" " + v.getValue());
					}
				}
				Logger.println("");
			} else {
				for (IntegerVariable v : csp.getIntegerVariables()) {
					if (! v.isAux() && ! v.getName().startsWith("_")) {
						Logger.println("a " + v.getName() + "\t" + v.getValue());
					}
				}
				Logger.println("a");
			}
		} else {
			Logger.println("s UNSATISFIABLE");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SugarMain sugarMain = new SugarMain();
			String option = null;
			int i = 0;
			while (i < args.length) {
				if (args[i].equals("-max")) {
					sugarMain.maxCSP = true;
				} else if (args[i].equals("-competition")) {
					sugarMain.competition = true;
				} else if (args[i].equals("-incremental")) {
					sugarMain.incremental = true;
				} else if (args[i].startsWith("-")) {
					option = args[i];
					break;
				}
				i++;
			}
			int n = args.length - i;
			if (option.equals("-encode") && n == 4) {
				String cspFileName = args[i+1];
				String satFileName = args[i+2];
				String mapFileName = args[i+3];
				sugarMain.encode(cspFileName, satFileName, mapFileName);
			} else if (option.equals("-decode") && n == 3) {
				String outFileName = args[i+1];
				String mapFileName = args[i+2];
				sugarMain.decode(outFileName, mapFileName);
			} else {
				String s = "";
				for (String a : args) {
					s += " " + a;
				}
				throw new SugarException("Invalid arguments" + s);
			}
			Logger.status();
		} catch (Exception e) {
			Logger.log("ERROR Exception " + e.getMessage());			
			for (StackTraceElement t : e.getStackTrace()) {
				Logger.log(t.toString());
			}
			Logger.println("s UNKNOWN");
			System.exit(1);
		} catch (Error e) {
			Logger.log("ERROR Exception " + e.getMessage());			
			for (StackTraceElement t : e.getStackTrace()) {
				Logger.log(t.toString());
			}
			Logger.println("s UNKNOWN");
			System.exit(1);
		}
	}

}
