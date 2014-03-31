/*
Copyright 2009 Hao Xu
ertranne@hotmail.com

This file is part of EriLex.

EriLex is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

EriLex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with EriLex; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package erilex.codegen;

import erilex.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 *
 * @author ertri
 */
public class Generator {

    public static final String ACCEPT = "accept";
    public static final String STATE = "state";
    public static final String TRANSFORM = "transform";
    public static final String VISITORPARAM = "d";
    public NameEmbedding t, d, e; // type, ast, env
    public static final String FUNCTION = "F";
    public static final String APPLY = "APP";
    public final static String VISITOR = "Visitor";
    public final static String VISIT = "visit";
    public final static Type Void = cons("void");
    public final static Type Visitor = cons(VISITOR);
    public final static Type Chi = tvar("Chi");
    public final Type DefaultChi;
    public final static Type natChi = new Type(true, true, Chi.cons);
    public final static Type Eta = tvar("Eta");
    public final static Type Sigma = tvar("Sigma");
    public final static Type Kappa = tvar("Kappa");
    public String pack;
    public HashMap<String, Class> classMap = new HashMap<String, Class>();

    public Generator(
            NameEmbedding tParam,
            NameEmbedding dParam,
            NameEmbedding eParam,
            Type defaultChi,
            String packParam) {
        pack = packParam;
        e = eParam;
        d = dParam;
        t = tParam;
        DefaultChi = defaultChi;
    }

    public static Type fun(final Type Chi_1, final Type Chi_2) {
        return new Type("Fun", Chi_1, Chi_2);
    }

    public static Type pair(final Type Chi_1, final Type Eta) {
        return new Type("pair", Chi_1, Eta);
    }

    public void addFV(Type dt, List<Type> fv) {
        List<Type> dtfv = dt.fv();
        List<Type> newFv = new ArrayList<Type>();
        outer:
        for(Type v : dtfv) {
            for(Type ve : fv) {
                if(ve.cons.equals(v.cons)) {
                    continue outer;
                }
            }
            newFv.add(v);
        }
        fv.addAll(newFv);
    }

    public static Type cons(String cons, Type... dt) {
        return new Type(cons, dt);
    }

    public static Type consnat(final java.lang.Class a, Type... params) {
        return new Type(false, true, getLongName(a), params);
    }
    public static Type consnatfuture(final String a, Type... params) {
        return new Type(false, true, a, params);
    }

    public static Type conswild(final String a, Type superType) {
        return new Type( a, true, superType);
    }

    public String dd(String e) {
        return d.get(e);
    }

    public String ee(String e) {
        return this.e.get(e);
    }

    public String dt(String zz) {
        return this.t.get(zz);
    }

    public Class en_e_L(String z, String a, boolean fun, String... zn) {
        return en_t_L(z, a, fun, zn);
    }

//    public Class generateEL(String z) {
//        Class c = new Class(pack, true, dt(z));
//        return c;
//
//    }
    public Class en_t_L(String z, String a, boolean fun, String... zn) {
        Class c = new Class(pack, fun, dt(a));
        for (String zz : zn) {
            c.params.add(dt(zz));
        }
        if(fun) {
            Method m = new Method(true, cons(dt(zn[1])), "app");
            m.params.add(new Variable(cons(dt(zn[0])), "x"));
            c.methods.add(m);
            Class f2 = new Class(true,dt(a)+"2",cons(dt(a),cons("T1"),cons(dt(a),cons("T2"),cons("T3"))));
            f2.isStatic = true;
            f2.params.add("T1");
            f2.params.add("T2");
            f2.params.add("T3");
            Method m2 = new Method(true, cons("T3"), "app");
            m2.params.add(new Variable(cons("T1"), "a"));
            m2.params.add(new Variable(cons("T2"), "b"));
            f2.methods.add(m2);
            Method m1 = new Method(cons(dt(a),cons("T2"),cons("T3")), "app", cons("T1"), "x",
                    new Return(new Abstraction(dt(a), "app", "y", cons("T2"), cons("T3"),
                        new Return(new Invocation(new VariableExpression(dt(a)+"2.this"),
                                                  "app",
                                                  new VariableExpression("x"), new VariableExpression("y"))))));
            f2.methods.add(m1);

            c.classes.add(f2);

            Class f3 = new Class(true,dt(a)+"3",cons(dt(a),cons("T1"),cons(dt(a),cons("T2"),cons(dt(a),cons("T3"),cons("T4")))));
            f3.isStatic = true;
            f3.params.add("T1");
            f3.params.add("T2");
            f3.params.add("T3");
            f3.params.add("T4");
            Method m3 = new Method(true, cons("T4"), "app");
            m3.params.add(new Variable(cons("T1"), "a"));
            m3.params.add(new Variable(cons("T2"), "b"));
            m3.params.add(new Variable(cons("T3"), "c"));
            f3.methods.add(m3);
            Method m11 = new Method(cons(dt(a),cons("T2"),cons(dt(a),cons("T3"),cons("T4"))), "app", cons("T1"), "x",
                    new Return(new Abstraction(dt(a), "app", "y", cons("T2"), cons(dt(a),cons("T3"),cons("T4")),
                        new Return(new Abstraction(dt(a), "app", "z", cons("T3"), cons("T4"),
                            new Return(new Invocation(new VariableExpression(dt(a)+"3.this"),
                                                      "app",
                                                      new VariableExpression("x"),
                                                      new VariableExpression("y"),
                                                      new VariableExpression("z"))))))));
            f3.methods.add(m11);
            c.classes.add(f3);
       }
        return c;
    }

//    public Class generateTL(String z) {
//        Class c = new Class(pack, true, T_PREFIX + z);
//        return c;
//
//    }
    public Class en_d_L(Judgement z, String a, int p, Variable[] state, Type evalType, TypeFunction vcons, Judgement... zn) {
        List<Type> fv = new ArrayList<Type>();

        Type dt0 = en_t_S(z.t);
        Type denv0 = en_e_S(z.env);
        Type phi = cons(dd(z.e), dt0, denv0);
        final String className = dd(z.e) + dd(a);
        Class c = new Class(pack, className, phi);
        addFV(phi, fv);

        Type dt;
        Type denv;
        Method m = new Method(className);
        IExpression[] args = new IExpression[zn.length + 2];
        args[0] = new ObjectCreation(cons("erilex.tree.ASTValueData"), new VariableExpression("\"" + z.e + "\""), new VariableExpression("\"" + z.e + "\""));
        args[1] = new ObjectCreation(cons("erilex.data.generic.Tree"), new ObjectCreation(cons("erilex.tree.ASTValueData"),new VariableExpression("\"" + a + "\""),new VariableExpression("\"" + a + "\"")));
        for (int i = 0; i < zn.length; i++) {
            Judgement zz = zn[i];
            Variable var;
            final String vName = "i" + i;
            dt = en_t_S(zz.t);
            denv = en_e_S(zz.env);
            if(i<p) {
            phi = nat(d.getnat(zz.e), dd(zz.e), dt, denv);
            } else {
                phi = box(dd(zz.e),dt,denv);
            }

            args[i + 2] = i<p&&dt.nat ?
                new ObjectCreation(
                    consnat(erilex.data.generic.Tree.class),
                    new ObjectCreation(
                        cons("erilex.tree.ASTValueData"),
                        new VariableExpression("\"nat\""),  // name
                        new VariableExpression("null"),           // text
                        new VariableExpression(vName))) // obj
                : new VariableExpression(vName);
            var = new Variable(phi, vName);
            c.fields.add(var);
            m.params.add(var);
            m.mBody.add(new Assignment("this." + vName, new VariableExpression(vName)));
            addFV(phi, fv);
        }
        m.mBody.add(0, new ExprStatement(new Invocation(null, "super", args)));
        c.methods.add(m);
        c.params.addAll(toStringList(fv));
        // visitor method
        Method visit = new Method(evalType, ACCEPT);
        visit.params.add(new Variable(Visitor, "v"));
        visit.params.addAll(Arrays.asList(state));
        IExpression[] args1 = new IExpression[state.length+1];
        for(int i = 0;i<state.length;i++) {
            args1[i] = new VariableExpression(state[i].name);
        }
        args1[args1.length-1] = new VariableExpression("this");
        visit.mBody.add((new Return(new Invocation(new VariableExpression("v"), VISIT, args1))));
        c.methods.add(visit);
        if(state.length > 0 && false) {
        Method visit2 = new Method(vcons(vcons, dt0), ACCEPT);
        visit2.params.add(new Variable(Visitor, "v"));
        visit2.mBody.add((new Return(new Conversion(vcons(vcons, dt0), new Invocation(new VariableExpression("v"), VISIT, new Invocation(new VariableExpression("v"), "getstate"), new VariableExpression("this"))))));
        c.methods.add(visit2);
        }

        Method consMethod = new Method(c.getType(), ee(a));
        consMethod.isStatic = true;
        consMethod.params.addAll(m.params);
        consMethod.typeParams.addAll(c.params);
        VariableExpression[] consargs = new VariableExpression[m.params.size()];
        int i = 0;
        for(Variable p2 : m.params) {
            consargs[i++] = new VariableExpression(p2.name);
        }
        consMethod.mBody.add(new Return(new ObjectCreation(c.getType(),consargs)));
        c.methods.add(consMethod);

        return c;
    }

    public Class en_e_L(ProductionRule r, boolean fun) {
        return en_e_L(r.conseq, r.a, fun, r.antece);
    }

    public Class en_t_L(ProductionRule r, boolean fun) {
        return en_t_L(r.conseq, r.a, fun, r.antece);
    }

    public Class en_e_L(TypingRule r) {
        return en_e_L(r.conseq, r.a, r.p, r.antece);
    }

    public Class en_d_L(TypingRule r, Variable[] state, Type evalType, TypeFunction vcons) {
        return en_d_L(r.conseq, r.a, r.p, state, evalType, vcons, r.antece);
    }

    public Class en_e_L(Judgement z, String a, int p, Judgement... zn) {
        ArrayList<Type> fv = new ArrayList<Type>();

        Type tve = en_e_S(z.env);

        if (!tve.var) {
            tve = en_e_S(Eta);
        } else if (!tve.equals(en_e_S(Eta))) {
            Utils.warning(this, "Default metavariable Eta is not used for the typing environment of " + z.e + " in the typing rule " + a);
        }
        Type tvt = en_t_S(z.t);
        if (!tvt.var) {
            tvt = en_t_S(DefaultChi);
        } else if (!tvt.cons.equals(en_t_S(DefaultChi).cons)) {
            Utils.warning(this, "Default metavariable Chi is not used for the type of " + z.e + " in the typing rule " + a);
        }

        Class c;
        c = classMap.get(ee(z.e));
        if (c == null) {
            c = new Class(pack, ee(z.e));
            c.params.add(Kappa.cons);
            c.params.add(Sigma.cons);
            c.params.add(tvt.cons);
            c.params.add(tve.cons);
            Type bType = mfun(cons(dd(z.e), tvt, tve), Sigma);
            Type kType = mfun(Sigma, Kappa);

            Method constructor = new Method(ee(z.e));
            Variable bVar = new Variable(bType, "b");
            Variable kVar = new Variable(kType, "k");
            c.fields.add(bVar);
            c.fields.add(kVar);
            constructor.params.add(bVar);
            constructor.params.add(kVar);
            constructor.mBody.add(new Assignment("this." + bVar.name, new VariableExpression(bVar.name)));
            constructor.mBody.add(new Assignment("this." + kVar.name, new VariableExpression(kVar.name)));
            c.methods.add(constructor);
            classMap.put(ee(z.e), c);
        } else {
            if (!tvt.equals(tvar(c.params.get(2)))) {
                Utils.error(this, "Different metavariables are used for the type of " + z.e + " in typing rule " + a);
            }
            if (!tve.equals(tvar(c.params.get(3)))) {
                Utils.error(this, "Different metavariables are used for the typing environment of " + z.e + " in typing rule " + a);
            }
        }

        Judgement[] z_p = Arrays.copyOfRange(zn, 0, p);
        Judgement[] z_n = Arrays.copyOfRange(zn, p, zn.length);
        int n = zn.length - p;

        Type phi = cons(dd(z.e), en_t_S(z.t), en_e_S(z.env));

        Type[] phi_p = new Type[p + 1];
        for (int i = 1; i <= p; i++) {
            final Judgement z_p_i = z_p[i - 1];
            phi_p[i] = nat(d.getnat(z_p_i.e), dd(z_p_i.e), en_t_S(z_p_i.t), en_e_S(z_p_i.env));
            addFV(phi_p[i], fv);
        }

        Type[] phi_n = new Type[n + 1];
        for (int i = 1; i <= n; i++) {
            final Judgement z_n_i = z_n[i - 1];
            phi_n[i] = box(dd(z_n_i.e), en_t_S(z_n_i.t), en_e_S(z_n_i.env));
            addFV(phi_n[i], fv);
        }

        Type[] sigma_n = new Type[n + 1];
        sigma_n[0] = Sigma;
        for (int i = 1; i <= n; i++) {
            sigma_n[i] = mfun(phi_n[n - i + 1], sigma_n[i - 1]);
        }
        addFV(sigma_n[n], fv);


        Type[] kappa_n = new Type[n + 1];
        kappa_n[0] = Kappa;
        for (int i = 1; i <= n; i++) {
            final Judgement zz = z_n[n - i];
            kappa_n[i] = cons(ee(zz.e), kappa_n[i - 1], sigma_n[i - 1], en_t_S(zz.t), en_e_S(zz.env));
        }
        addFV(kappa_n[n], fv);

        IExpression[] k_n = new IExpression[n + 1];
        k_n[0] = new VariableExpression("k");
        for (int i = 1; i <= n; i++) {
            final Judgement zz = z_n[n - i];
            k_n[i] = new Abstraction("b" + i, sigma_n[i], kappa_n[i], new Return(new ObjectCreation(cons(ee(zz.e)), new VariableExpression("b" + i), k_n[i - 1])));
        }

        IExpression[] B_n = new IExpression[n + 1];
        IExpression[] params = new IExpression[p + n];
        for (int i = 1; i <= p; i++) {
            params[i - 1] = new VariableExpression("i" + i);
        }
        for (int i = 1; i <= n; i++) {
            params[i - 1 + p] = new VariableExpression("t" + i);
        }
        Type expected = cons(dd(z.e), tvt, tve);
        boolean castNeeded = !phi.equals(expected);
        if (castNeeded) {
            Type t_phi = en_t_S(z.t); // need to convert t_phi to tvt
            Type e_phi = en_e_S(z.env); // need to convert e_phi to tve
            Type axiom = cons(d.get(z.e)+"EqAxiom");
            if(t_phi.equals(tvt)) {
                B_n[0] = new App(
                        new VariableExpression("b"),
                        new App(
                            new Invocation(
                                new ObjectCreation(axiom),
                                "<"+tvt.cons+","+e_phi.toStringCodeGen()+","+tve.cons+">appr",
                                new VariableExpression("cast")),
                            new ObjectCreation(cons(dd(z.e)+dd(a)), params)));
            } else if(e_phi.equals(tve)) {
                B_n[0] = new App(
                        new VariableExpression("b"),
                        new App(
                            new Invocation(
                                new ObjectCreation(axiom),
                                "<"+tve.cons+","+t_phi.toStringCodeGen()+","+tvt.cons+">appl",
                                new VariableExpression("cast")),
                            new ObjectCreation(cons(dd(z.e)+dd(a)), params)));

            } else {

                B_n[0] = new App(new VariableExpression("b"), new App(new VariableExpression("cast"), new ObjectCreation(cons(dd(z.e)+dd(a)), params)));
            }
        } else {
            B_n[0] = new App(new VariableExpression("b"), new ObjectCreation(cons(dd(z.e)+dd(a)), params));

        }
        for (int i = 1; i <= n; i++) {
            B_n[i] = new Abstraction("t" + (n - i + 1), phi_n[n - i + 1], sigma_n[i - 1], new Return(B_n[i - 1]));
        }

        Method e_a = new Method(kappa_n[n], ee(a));
        for (int i = 1; i <= p; i++) {
            e_a.params.add(new Variable(phi_p[i], "i" + i));
        }
        if (castNeeded) {
            Type t_phi = en_t_S(z.t); // need to convert t_phi to tvt
            Type e_phi = en_e_S(z.env); // need to convert e_phi to tve
            if(t_phi.equals(tvt)) {
                e_a.params.add(new Variable(mfun(e_phi, tve), "cast"));
                addFV(e_phi, fv);
            } else if(e_phi.equals(tve)) {
                e_a.params.add(new Variable(mfun(t_phi, tvt), "cast"));
                addFV(t_phi, fv);
            } else {
                e_a.params.add(new Variable(mfun(phi, expected), "cast"));
                addFV(phi, fv);
            }
        }
        e_a.mBody.add(new Return(new App(k_n[n], B_n[n])));
        removeFV(fv, Kappa);
        removeFV(fv, Sigma);
        removeFV(fv, tvt);
        removeFV(fv, tve);
        e_a.typeParams.addAll(toStringList(fv));
        c.methods.add(e_a);

        return c;
    }

    public Class en_d_L(String z, Variable[] state, Type evalType, TypeFunction vcons, String evalName) {
        final Type asttreenodevaluetype = consnat(erilex.tree.ASTValueData.class);
        final Type asttreenodetype = consnat(erilex.data.generic.Tree.class, asttreenodevaluetype);
        Class c = new Class(pack, true, d.get(z), asttreenodetype);
        c.params.add(DefaultChi.cons);
        c.params.add(Eta.cons);
        Method cons = new Method(d.get(z));
        cons.params.add(new Variable(asttreenodevaluetype, "name"));
        cons.params.add(new Variable(cons("erilex.data.generic.Tree<erilex.tree.ASTValueData>..."), "sub"));
        cons.mBody.add(new ExprStatement(new Invocation(null, "super", new VariableExpression("name"), new VariableExpression("sub"))));
        c.methods.add(cons);
        Method eval = new Method(evalType, "run");
        IExpression[] args = new IExpression[state.length+1];
        args[0] = new ObjectCreation(cons(d.get(evalName)));
        for(int i = 0;i<state.length;i++) {
            args[i+1] = isAbsOrWithoutDefaultConstructor(state[i].type)? new VariableExpression("null"):new ObjectCreation(state[i].type);
        }
        eval.mBody.add(new Return(
                    new Invocation(
                        new VariableExpression("this"),
                        ACCEPT,
                        args
                    )
                ));
        c.methods.add(eval);
        // visitor method
        Method visit = new Method(true, evalType, ACCEPT);
        visit.params.add(new Variable(Visitor, "v"));
        visit.params.addAll(Arrays.asList(state));
        c.methods.add(visit);
        if(state.length > 0 && false) {
        Method visit2 = new Method(true, consnat(java.lang.Object.class), ACCEPT);
        visit2.params.add(new Variable(Visitor, "v"));
        c.methods.add(visit2);
        }
        return c;
    }

    private boolean isAbsOrWithoutDefaultConstructor(Type state) {
        java.lang.Class<?> clazz = null;
        boolean abs = true;
        try {
            String className = state.cons.contains("<") ? state.cons.substring(0, state.cons.indexOf('<')) : state.cons;
            className = className.contains(".") ? className : "java.lang." + className;
            clazz = java.lang.Class.forName(className);
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                abs = true;
            } else {
            	Constructor<?>[] consts = clazz.getConstructors();
            	abs = true;
            	for(Constructor<?> cons : consts) {
            		if(cons.getParameterTypes().length == 0) {
            			abs = false;
            			break;
            		}
            	}
            }
        } catch (Exception ex) {
            Utils.error(this, "Class " + state.cons + " not found.");
        }
        return abs;
    }
    public Class en_d_eq_axiom(String z, TypeFunction vcons) {
        Class c = new Class(pack, d.get(z)+"EqAxiom");
        final Type applrt = mfun(cons(d.get(z), cons("S"), cons("E")), cons(d.get(z), cons("T"), cons("E")));
        Method appl = new Method(applrt, "appl");
        appl.typeParams.add("E");
        appl.typeParams.add("S");
        appl.typeParams.add("T");
        appl.params.add(new Variable(mfun(cons("S"),cons("T")), "eq"));
        appl.mBody.add(new Return(
            new Conversion(applrt,
                new VariableExpression("eq"))));
        c.methods.add(appl);
        Type apprrt = mfun(cons(d.get(z), cons("t"), cons("T")), cons(d.get(z), cons("t"), cons("T")));
        Method appr = new Method(apprrt,"appr");
        appr.typeParams.add("t");
        appr.typeParams.add("S");
        appr.typeParams.add("T");
        appr.params.add(new Variable(mfun(cons("S"),cons("T")), "eq"));
        appr.mBody.add(new Return(
            new Conversion(apprrt,
                new VariableExpression("eq"))));
        c.methods.add(appr);
        return c;
    }

    public static List<String> toStringList(List<Type> l) {
        ArrayList<String> l2 = new ArrayList<String>();
        for(Type e: l) {
            l2.add(e.cons);
        }
        return l2;
    }
    public static Type tvar(String n) {
        return new Type(true, n);
    }

    public Type en_t_S(Type t) {
        return t.addPrefix(this.t);
    }

    public Type en_e_S(Type env) {
        return env == null ? null : env.addPrefix(t);
    }

    public Class en_e_F() {
        Class c = new Class(pack, true, "F");
        c.params.add("D");
        c.params.add("R");
        Method m = new Method(true, tvar("R"), "APP");
        m.params.add(new Variable(tvar("D"), "X"));
        c.methods.add(m);
        return c;
    }

    public Class en_e_F2() {
        Class c = new Class(pack, true, "F2");
        c.params.add("D1");
        c.params.add("D2");
        c.params.add("R");
        Method m = new Method(true, tvar("R"), "APP");
        m.params.add(new Variable(tvar("D1"), "X1"));
        m.params.add(new Variable(tvar("D2"), "X2"));
        c.methods.add(m);
        return c;
    }

    public Class en_e_F3() {
        Class c = new Class(pack, true, "F3");
        c.params.add("D1");
        c.params.add("D2");
        c.params.add("D3");
        c.params.add("R");
        Method m = new Method(true, tvar("R"), "APP");
        m.params.add(new Variable(tvar("D1"), "X1"));
        m.params.add(new Variable(tvar("D2"), "X2"));
        m.params.add(new Variable(tvar("D2"), "X3"));
        c.methods.add(m);
        return c;
    }
    public Class en_e_ID() {
        Class c = new Class(pack, "ID", mfun(tvar("C"), tvar("C")));
        c.params.add("C");
        Method m = new Method(tvar("C"), "APP");
        m.params.add(new Variable(tvar("C"), "X"));
        m.mBody.add(new Return(new VariableExpression("X")));
        c.methods.add(m);
        return c;
    }

    public Class en_e_Bot() {
        Class c = new Class(pack, "Bot");
        return c;
    }

    public Class enc_d_Visitor(Variable[] state, Type evalType, TypeFunction vcons, Class... dtypes) {
        Class c = new Class(pack, true, VISITOR);
        //Method gets = new Method(true, cons(state), "get"+STATE);
//        gets.mBody.add(new Return(new VariableExpression(stateVar.name)));
        //c.methods.add(gets);
        for (Class d : dtypes) {
//            Type nodetype = d.superType[0];
            Type rtype = evalType; // vcons(vcons, nodetype.params[0]);
            Method m = new Method(true, rtype, VISIT);
            Type type = cons(d.name);
            type.params = new Type[d.params.size()];
            int i = 0;
            for (String t : d.params) {
                type.params[i++] = cons(t);
            }
            m.params.addAll(Arrays.asList(state));
            m.params.add(new Variable(type.toRawType(), "d"));
            c.methods.add(m);
        }
        return c;
    }
    
    public Class enc_d_VisitorImpl(String name, Variable[] state, Type evalType, TypeFunction vcons, Map<String, ?> map, Class[] dtypes) {
        final String cname = d.get(name) + VISITOR;
        Class c = new Class(pack,cname, cons(VISITOR));
        //Variable stateVar = new Variable(cons(state), STATE);
        //Variable stateVarInit = new Variable(cons(state), STATE);
        //stateVarInit.init = new ObjectCreation(cons(state));
        // field state
        //c.fields.add(stateVarInit);
        // constructor
        //Method cons = new Method(cname);
        //cons.params.add(stateVar);
        //cons.mBody.add(new Assignment("this."+stateVar.name, new VariableExpression(stateVar.name)));
        //c.methods.add(cons);
        // method getState
        //Method gets = new Method(stateVar.type, "get"+STATE);
        //gets.mBody.add(new Return(new VariableExpression(stateVar.name)));
        //c.methods.add(gets);
        for (Class ds : dtypes) {
//            Type nodetype = ds.superType[0];
            Type rtype = evalType;
            Method m = new Method(rtype, VISIT);
            Type type = cons(ds.name);
            type.params = new Type[ds.params.size()];
            int i = 0;
            for (String ts : ds.params) {
                type.params[i++] = cons(ts);
            }
            m.params.addAll(Arrays.asList(state));
            m.params.add(new Variable(type.toRawType(), VISITORPARAM));
            IExpression dtt = (IExpression) map.get(ds.name);
            if (dtt != null) {
                m.mBody.add(new Return(dtt));
            }
            c.methods.add(m);
        }
        return c;
    }
    /** Evaluator based visitor implementation
     * 
     * @param name
     * @param state
     * @param vcons
     * @param map
     * @param dtypes
     * @return
     */
    public Class enc_d_VisitorImpl2(String name, Variable[] state, Type evalType, String evaluatorClass, String evaluatorMethod, Class[] dtypes) {
        final String cname = d.get(name);
        Class c = new Class(pack,cname, cons(VISITOR));
//        Variable stateVar = new Variable(stateType, stateName);
        // field state
//        c.fields.add(stateVar);
        // constructor
//        Method cons = new Method(cname);
//        cons.params.add(stateVar);
//        cons.mBody.add(new Assignment("this."+stateVar.name, new VariableExpression(stateVar.name)));
//        c.methods.add(cons);
        // method getState
//        Method gets = new Method(stateVar.type, "get"+STATE);
//        gets.mBody.add(new Return(new VariableExpression(stateVar.name)));
//        c.methods.add(gets);
        for (Class ds : dtypes) {
//            Type nodetype = ds.superType[0];
//            Type rtype = consnat(java.lang.Object.class);
            Method m = new Method(evalType, VISIT);
            Type type = cons(ds.name);
            type.params = new Type[ds.params.size()];
            int i = 0;
            for (String ts : ds.params) {
                type.params[i++] = cons(ts);
            }
            m.params.addAll(Arrays.asList(state));
            m.params.add(new Variable(type.toRawType(), VISITORPARAM));
            IExpression[] params = new IExpression[state.length+1];
            params[0]=new VariableExpression(VISITORPARAM);
            for(int k=1;k<params.length;k++) {
                params[k]=new VariableExpression(state[k-1].name);
            }
            IExpression dtt = new Invocation(new ObjectCreation(new Type(evaluatorClass)), evaluatorMethod, params);
            m.mBody.add(new Return(dtt));
            c.methods.add(m);
        }
        return c;
    }
    /*  encode class Utils
     public static <T> typing<Dtyping<T, Bot>, Dtyping<T, Bot>, T, Bot> typing() {
        return new typing<Dtyping<T, Bot>, Dtyping<T, Bot>, T, Bot>(
            new F<Dtyping<T, Bot>, Dtyping<T, Bot>>() {

                public Dtyping<T, Bot> APP(Dtyping<T, Bot> x) {
                    return x;
                }
            },
            new F<Dtyping<T, Bot>, Dtyping<T, Bot>>() {

                public Dtyping<T, Bot> APP(Dtyping<T, Bot> x) {
                    return x;
                }
            });
    }
     */
public Class enc_e_Utils(String ss, Type oeinit) {
    Class c = new Class(pack, "Utils");

        Type tparam = tvar("T");
        String dss = dd(ss);
        String ess = ee(ss);
        Type einit = en_t_S(oeinit);
        final Type init = cons(dss, tparam, einit);
        Type rtype = cons(ess, init, init, tparam, einit);
        Method m = new Method(rtype, ss);
        m.isStatic = true;
    m.typeParams.add(tparam.cons);
    if(t.var(oeinit.cons)) {
        m.typeParams.add(einit.cons);
    }
        m.mBody.add(new Return(new ObjectCreation(rtype,new ObjectCreation(cons("UtilsST")), new ObjectCreation(cons("UtilsST")))));
        c.methods.add(m);
        // prog()
        Method m2 = new Method(rtype, "prog");
        m2.isStatic = true;
        m2.typeParams.add(tparam.cons);
        if(t.var(oeinit.cons)) {
        m2.typeParams.add(einit.cons);
    }
        m2.mBody.add(new Return(new Invocation(null, ss)));
        c.methods.add(m2);
//        // wrapfunc
//        Method mw1 = new Method(cons("F<S,T>"), "wrapfunc");
//        mw1.typeParams.add("S");
//        mw1.typeParams.add("T");
//        mw1.typeParams.add(tparam.cons);
//        mw1.mBody.add(new Return(new Invocation(new VariableExpression("this"), ss)));
//        c.methods.add(mw1);
//        // wrapfunc2
//        Method mw2 = new Method(cons("F2<S1,S2,T>"), "wrapfunc2");
//        mw2.typeParams.add("S1");
//        mw2.typeParams.add("S2");
//        mw2.typeParams.add("T");
//        mw2.typeParams.add(tparam.cons);
//        mw2.mBody.add(new Return(new Invocation(new VariableExpression("this"), ss)));
//        c.methods.add(mw2);
//        // wrapfunc3
//        Method mw3 = new Method(cons("F3<S1,S2,S3,T>"), "wrapfunc3");
//        mw3.typeParams.add("S1");
//        mw3.typeParams.add("S2");
//        mw3.typeParams.add("S3");
//        mw3.typeParams.add("T");
//        mw3.typeParams.add(tparam.cons);
//        mw3.mBody.add(new Return(new Invocation(new VariableExpression("this"), ss)));
//        c.methods.add(mw3);

    Class ST = new Class("UtilsST", mfun(init,init));
    ST.isStatic = true;
    ST.params.add(tparam.cons);
    if(t.var(oeinit.cons)) {
        ST.params.add(einit.cons);
    }
    Method APP = new Method(init, APPLY);
    APP.params.add(new Variable(init, "x"));
    APP.mBody.add(new Return(new VariableExpression("x")));
    ST.methods.add(APP);
    c.classes.add(ST);
return c;

}
    public static String getLongName(java.lang.Class<?> state) {
        if (state.isArray()) {
            return getLongName(state.getComponentType()) + "[]";
        }
        return state.toString().replace("class ", "").replace("$", "."); //state.getPackage().getName() + "." + state.getSimpleName();
    }

    public void removeFV(ArrayList<Type> fv, Type t) {
        Iterator<Type> itr = fv.iterator();
        while(itr.hasNext()) {
            if(itr.next().cons.equals(t.cons))
                itr.remove();
        }
    }

    public  Type vcons(TypeFunction vcons, Type nodetype) {
        return vcons == null ? nodetype : vcons.map(nodetype);
    }

    private Type mfun(Type dtt, Type rtt) {
        return cons(FUNCTION, dtt, rtt);
    }

    public Type nat(boolean nat, String dd, Type t, Type env) {
        return nat ? t : cons(dd, t, env);
    }

    public static Judgement judgement(String s) {
        return new Judgement(s);
    }

//    public static void main(String[] args) throws IOException {
//        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(System.out));
//        Generator g = new Generator(new NameEmbedding(NameEmbedding.T_PREFIX), new NameEmbedding(NameEmbedding.D_PREFIX), new NameEmbedding(NameEmbedding.E_PREFIX), Chi, "erilex.generated");
//        String path = "generated";
//        final Type Chi_1 = tvar("Chi_1");
//        final Type Chi_2 = tvar("Chi_2");
//
//        final Type natChiPrime = new Type(true, true, "ChiPrime");
//        g.en_t_L("t", "Fun", "t1", "t2").writeToFile(path);
//        g.en_e_L("env", "nil").writeToFile(path);
//        g.en_e_L("env", "pair", "t1", "t2").writeToFile(path);
//        g.en_d_L(new Judgement(Eta, "e", natChi), "cons", 1, new Judgement("c", natChi)).writeToFile(path);
//        g.en_d_L(new Judgement(pair(Chi, Eta), "i", Chi), "z",0).writeToFile(path);
//        g.en_d_L(new Judgement(pair(natChiPrime, Eta), "i", Chi), "s", 0, new Judgement(Eta, "i", Chi)).writeToFile(path);
//        g.en_d_L(new Judgement(Eta, "e", Chi), "varx", 0,new Judgement(Eta, "i", Chi)).writeToFile(path);
//        g.en_d_L(new Judgement(Eta, "e", Chi), "app", 0,new Judgement(Eta, "e", fun(Chi_1, Chi)), new Judgement(Eta, "e", Chi_1)).writeToFile(path);
//        g.en_d_L(new Judgement(Eta, "e", fun(Chi_1, Chi_2)), "abs",0, new Judgement(pair(Chi_1, Eta), "e", Chi_2)).writeToFile(path);
//        g.en_d_L("e").writeToFile(path);
//        g.en_d_L("i").writeToFile(path);
//
//        g.en_e_L(new Judgement(Eta, "e", natChi), "cons", 1, new Judgement("c", natChi)).writeToFile(path);
//        g.en_e_L(new Judgement(Eta, "e", Chi), "varx", 1, new Judgement(Eta, "i", Chi)).writeToFile(path);
//        g.en_e_L(new Judgement(Eta, "e", Chi), "app", 0, new Judgement(Eta, "e", fun(Chi_1, Chi)), new Judgement(Eta, "e", Chi_1)).writeToFile(path);
//        g.en_e_L(new Judgement(Eta, "e", fun(Chi_1, Chi_2)), "abs", 0, new Judgement(pair(Chi_1, Eta), "e", Chi_2)).writeToFile(path);
//
//        w.flush();
//        w.close();
//    }

    private Type box(String dd, Type en_t_S, Type en_e_S) {
        return nat(false, dd, en_t_S, en_e_S);
    }
}
