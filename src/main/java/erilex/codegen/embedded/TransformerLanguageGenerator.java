/*
Copyright 2009, 2010 Hao Xu
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


package erilex.codegen.embedded;

import erilex.data.generic.Pair;
import erilex.AbstractProduction;
import erilex.Grammar;
import erilex.NonTerminal;
import erilex.Production;
import erilex.SemanticsCollection;
import erilex.Terminal;
import erilex.builder.ExpressionBuilder;
import erilex.builder.ExtendedExpressionBuilder;
import erilex.builder.RuleBuilder;
import erilex.builder.LanguageBuilder;
import erilex.codegen.Abstraction;
import erilex.codegen.App;
import erilex.codegen.Cns;
import erilex.codegen.Conversion;
import erilex.codegen.Generator;
import erilex.codegen.GenericTreeTransformer;
import erilex.codegen.Hd;
import erilex.codegen.IExpression;
import erilex.codegen.If;
import erilex.codegen.IfStatement;
import erilex.codegen.Invocation;
import erilex.codegen.LanguageGenerator;
import erilex.codegen.NameEmbedding;
import erilex.codegen.ObjectCreation;
import erilex.codegen.Return;
import erilex.codegen.SemanticsBuilder;
import erilex.codegen.Statement;
import erilex.codegen.Tl;
import erilex.codegen.Type;
import erilex.codegen.Variable;
import erilex.codegen.VariableExpression;
import erilex.data.generic.Tree;
import erilex.tree.ASTValueData;
import erilex.tree.DeepTreeTransformer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import static erilex.codegen.LanguageGenerator.*;
import static erilex.codegen.Generator.*;

/**
 *
 * @author ertri
 */
public class TransformerLanguageGenerator {
    public static final String ANY = "ANY";
    public static final String OL_PREFIX = "ol_";
    public static final String NODE_TYPE = OL_PREFIX + "type";
    public static final String TRL_NODE_CONS = "_TrL_Node";

    public LanguageBuilder<Object> T = new LanguageBuilder<Object>();
    public LanguageBuilder<Object> E = new LanguageBuilder<Object>();
    public LanguageBuilder<Object> G = new LanguageBuilder<Object>();
    public final SemanticsBuilder<Tree<ASTValueData>, Pair<Grammar, DeepTreeTransformer>> S;
    public NameEmbedding t = new NameEmbedding(NameEmbedding.TrLT_PREFIX);
    public NameEmbedding d = new NameEmbedding(NameEmbedding.TrLD_PREFIX);
    public NameEmbedding e = new NameEmbedding(NameEmbedding.TrLE_PREFIX);
    NameEmbedding OLt;
    NameEmbedding OLd;
    Grammar OLT;
    Grammar OLG;
    Type state;
    String path;
    String pack;
    public LanguageGenerator tgg;
        public final Type dchi;
    private final Type[] dchiTL;
    private final Type ntchi;
    /**
     *
     * @param t
     * @param d
     * @param e
     * @param tg a typed grammar of inductive types
     * @param tgd name embedding for tg
     */
    public void grammar(NameEmbedding Gt, NameEmbedding Gd, Grammar GT, Grammar GG) {
        d.add("c", "c", false, true);
        e.add("c", "c", false, true);
        // types
        T = new LanguageBuilder<Object>();
        T.copy(GT);
        T.startSymbol = GT.startSymbol;
        String tprefix = t.prefix;
        t = new NameEmbedding(Gt);
        t.prefix = tprefix;
        types(t, T, GT.startSymbol);

        // environments
        E = new LanguageBuilder<Object>();
        E.def("env").start().t("nil").or().t("pair").nt(GT.startSymbol).nt("env").or().t("Eta").end();
        E.copy(GT);
        String eprefix = e.prefix;
        e = new NameEmbedding(Gt);
        e.prefix = eprefix;
        types(new NameEmbedding(NameEmbedding.T_PREFIX), E, GT.startSymbol);

        // terms
        G = new LanguageBuilder<Object>();
        Type eString2;
        for (Production p : GG.ruleMap.values()) {
            String z = p.getProdName();
            String casez = "case_" + z;
            T.def(NODE_TYPE).start().t(OL_PREFIX + z).end();
            E.def(NODE_TYPE).start().t(OL_PREFIX + z).end();
            ExpressionBuilder<RuleBuilder, Object> r = G.def("e").start(0, Eta, dchi);

            ExtendedExpressionBuilder<RuleBuilder, Object> r2 = r.t(casez).nt("e", Eta, getProductionTypeStr(Gt, Gd, GT, p));
            ArrayList<Production> seqList = new ArrayList<Production>();
            breakOrList(p, seqList);
            for (Production p1 : seqList) {
                String a;
                Type eString = Eta;
                Type tString = getAbstactProductionOriginalTypeStr((AbstractProduction) p1, GT, Gt, null);
                if (p1 instanceof Terminal) { // z -> a
                    a = ((Terminal) p1).text;
                    r2 = r2.nt("e", eString, dchi);
                } else { // z -> a z1 ... zn
                    ArrayList<Production> ntList = new ArrayList<Production>();
                    breakSeqList(p1, ntList);
                    a = ((Terminal) ntList.get(0)).text;
                    for (int i = 1; i < ntList.size(); i++) {
                        NonTerminal nt = (NonTerminal) ntList.get(i);
                        Type type = getNodeTypeStr(Gt, Gd, GT, nt);
                        eString = cons("pair",type,eString);
                    }
                    r2 = r2.nt("e", eString, dchi);
                }
                eString2 = eString;
                for(Type dchiTLvar : dchiTL) {
                    tString = tString.renameVar(dchiTLvar.cons, dchi.cons);
                    eString2 = eString2.renameVar(dchiTLvar.cons, dchi.cons);
                }
                G.def("trans").start(0,Eta, tString).t(z+"_"+a+"By").nt("e",eString2,cons("V", tString)).end();
                S.trans(z+"_"+a+"By", TT.class);
            }
            r2.end();
        }
        S.state = new Variable[]{new Variable(consnat(erilex.data.generic.Pair.class,consnat(erilex.Grammar.class),consnat(erilex.tree.DeepTreeTransformer.class)),null)};

        G.def("e").start(1, Eta,dnchi).t("con").nt("c", Eta, consnatfuture("_Class", new Type("?", true, dnchi))).end();
        G.def("e").start(1, Eta, dchi).t("var").nt("i", Eta, dchi).end();
        G.def("e").start(0, Eta, cons("F", dchi1, dchi2)).t("abs").nt("e", cons("pair", dchi1,  Eta), dchi2).end();
        G.def("e").start(0, Eta, dchi).t("app").nt("e", Eta, cons("F", dchi1,dchi)).nt("e", Eta, dchi1).end();
        G.def("e").start(1, Eta, dchi).t("appcon").nt("c", Eta, consnatfuture("_Class", new Type("?", true, cons("F", dchi1,dchi)))).nt("e", Eta, dchi1).end();
        G.def("e").start(1, Eta, dchi).t("appappcon").nt("c", Eta, consnatfuture("_Class", new Type("?", true, cons("F", dchi2,cons("F", dchi1,dchi))))).nt("e", Eta, dchi2).nt("e", Eta, dchi1).end();
        G.def("e").start(0, Eta, dchi).t("fix").nt("e", cons("pair",dchi, Eta), dchi).end();
        G.def("e").start(0, Eta, cons("V", dchiTL[0])).t("visit").nt("e", Eta, state).nt("e",Eta,cons(TRL_NODE_CONS,ntchi, dchiTL[0])).end();// TODO replace hard coded "Chi" by actually dchi of the typing language
        G.def("e").start(0, Eta, state).t("state").end();
        G.def("i").start(0, cons("pair",dchi,Eta), dchi).t("z").end();
        G.def("i").start(0, cons("pair", dchi1, Eta), dchi).t("s").nt("i", Eta, dchi).end();
        G.def("e").start(0, Eta, dchi).t("error").end();
        G.def("e").start(0, Eta, dchi).t("ifx").nt("e", Eta, consnat(Boolean.class)).nt("e", Eta, dchi).nt("e", Eta, dchi).end();
        G.def("e").start(0, Eta, dchi).t("hd").nt("e", Eta, cons("List", dchi)).end();
        G.def("e").start(0, Eta, cons("List", dchiTL[0])).t("tl").nt("e", Eta, cons("List", dchiTL[0])).end();
        G.def("e").start(0, Eta, cons("List", dchiTL[0])).t("cns").nt("e", Eta, dchiTL[0]).nt("e", Eta, cons("List", dchiTL[0])).end();
        
    }

    public TransformerLanguageGenerator(NameEmbedding tgt, NameEmbedding tgd, Grammar tt, Grammar tg, Type dChi, Type[] dChiTL, Type state, String path, String pack) {
        this.OLt = tgt;
        this.OLd = tgd;
        this.OLT = tt;
        this.OLG = tg;
        this.path = path;
        this.pack = pack;
        this.dchi = dChi;
        this.dchiTL = dChiTL;
        this.state = state;
        dnchi = new Type(true, true,dchi.cons+"nat");
        dchi1 = tvar(dchi.cons+"1");
        dchi2 = tvar(dchi.cons+"2");
        ntchi = tvar("ntChi");
        S = new SemanticsBuilder<Tree<ASTValueData>, Pair<Grammar, DeepTreeTransformer>>(state);
    }
    final Type dnchi;
            final Type dchi1;
            final Type dchi2;

    public void generate() throws IOException {
        grammar(OLt, OLd, OLT, OLG);
        tgg = new LanguageGenerator(t, d, e, E, "nil", T, dchi, G, S, new SemanticsCollection(), new HashMap<String, String>(), new HashSet<String>(), path, pack);
        tgg.generate();
    }

    public Type getAbstactProductionOriginalTypeStr(AbstractProduction nt,  Grammar GT, NameEmbedding Gt, Type def) {
        Type ty;
        if (nt.typeType == null) {
            ty = LanguageGenerator.parseType(Gt, GT, nt.type, def);
        } else {
            ty = nt.typeType;
        }
        if (ty != null) {
            return ty;
        } else {
            Type tvar = Generator.Chi;
            return tvar; //return "Object";
        }
    }

    public Type getNodeTypeStr(NameEmbedding Gt, NameEmbedding Gd, Grammar GT, NonTerminal nt) {
        Type ty = getAbstactProductionOriginalTypeStr(nt, GT, Gt, null);
        if (Gd.nat(nt.refName)) {
            return ty;
        } else {
            return cons(TRL_NODE_CONS, cons(OL_PREFIX + nt.refName), ty);
        }
    }

    public static String getNodeEnvStr(NonTerminal nt) {
        if (nt.envType != null) {
            return nt.envType.unparse();

        } else {
            return nt.env;
        }
    }

    public void types(NameEmbedding t, LanguageBuilder T, String ss) {
        t.add("Eta", Generator.Eta.cons, true, false);
        t.add(dnchi.cons, dchi.cons, true, true);
        t.add(ntchi.cons, ntchi.cons, true, false);
        t.add(dchi.cons, dchi.cons, true, false);
        t.add(dchi1.cons, dchi1.cons + "1", true, false);
        t.add(dchi2.cons, dchi2.cons + "2", true, false);
        t.add("_Class", "java.lang.Class", false, true); // TODO change this to ? extends
        t.add("Boolean", "java.lang.Boolean", false, true);
        // This is used to ensure that type F has an APP method.
        // A class F without any method is first generated
        // and then overriden by the utility class.
        t.add("F", "F", false, false);

        T.def(ss).start()
                .t("F").nt(ss).nt(ss)
                .or().t("V").nt(ss)
                .or().t("List").nt(ss)
                .or().t(TRL_NODE_CONS).nt(NODE_TYPE).nt(ss).end();
        T.def(NODE_TYPE).start()
                .t("ntChi").end();
    }
    private int anyn = 0;
    private Type getProductionTypeStr(NameEmbedding Gt, NameEmbedding Gd, Grammar GT, Production p) {
        String anynstr = ANY+anyn++;
        T.def(T.startSymbol).start().t(anynstr).end();
        t.add(anynstr, "_"+anynstr, true, false);
        Type ty = getAbstactProductionOriginalTypeStr((AbstractProduction) p,GT, Gt,Generator.tvar(anynstr));
        if (Gd.nat(p.getProdName())) {
            return ty;
        } else {
            return cons(TRL_NODE_CONS,cons(OL_PREFIX+p.getProdName()), ty);
        }
    }

    public static class Eval {
        Grammar OLG;
        HashMap<String, Pair<String, Integer>[]> parameterMap = new HashMap();

        public Eval(Grammar OLG) {
            this.OLG = OLG;
            ArrayList<Production> seqList =new ArrayList<Production>();
            ArrayList<Production> elemList =new ArrayList<Production>();
            for(Production p : OLG.ruleMap.values()) {
                String name = p.getProdName();
                seqList.clear();
                LanguageGenerator.breakOrList(p, seqList);
                Pair<String, Integer>[] parameters = new Pair[seqList.size()];
                int i = 0;
                for(Production p2 : seqList) {
                    elemList.clear();
                    LanguageGenerator.breakSeqList(p2, elemList);
                    parameters[i++] = new Pair(((Terminal)elemList.get(0)).text, elemList.size() - 1);
                }
                    parameterMap.put(name, parameters);
            }
        }
        public Tree<ASTValueData> eval(Pair env, Tree<ASTValueData> expr) {
            final Tree<ASTValueData> appnode = new Tree<ASTValueData>(new ASTValueData("app", "app"));
            final Tree<ASTValueData> connode = new Tree<ASTValueData>(new ASTValueData("con", "con"));

            String cmd = expr.subtrees[0].val.name;
            if (cmd.equals("con")) {
                return expr;
            } else if (cmd.equals("var")) {
                return (Tree<ASTValueData>) lookup(env, expr);
            } else if (cmd.equals("abs")) {
                return new Tree<ASTValueData>(expr.val, expr.subtrees[0], eval(new Pair(null, env), expr.subtrees[1]));
            } else if (cmd.equals("app")) {
                Tree<ASTValueData> f = eval(env, expr.subtrees[1]);
                Tree<ASTValueData> e = eval(env, expr.subtrees[2]);
                String fcmd = f.subtrees[0].val.name;
                if (fcmd.equals("abs")) {
                    return subst(0, f.subtrees[1], e);
                } else {
                    return new Tree<ASTValueData>(expr.val, expr.subtrees[0], f, e);
                }
            } else if (cmd.equals("appcon")) {
                Tree<ASTValueData> f = new Tree<ASTValueData>(expr.val, connode, expr.subtrees[1]);
                Tree<ASTValueData> e = eval(env, expr.subtrees[2]);
                return new Tree<ASTValueData>(expr.val, appnode, f, e);
            } else if (cmd.equals("appappcon")) {
                Tree<ASTValueData> f = new Tree<ASTValueData>(expr.val, connode, expr.subtrees[1]);
                Tree<ASTValueData> e = eval(env, expr.subtrees[2]);
                Tree<ASTValueData> e2 = eval(env, expr.subtrees[3]);
                return new Tree<ASTValueData>(expr.val, appnode, new Tree<ASTValueData>(expr.val, appnode, f, e), e2);
            } else if (cmd.equals("fix")) {
                return expr;
            } else if (cmd.equals("visit")) {
                return new Tree<ASTValueData>(expr.val, expr.subtrees[0], eval(env, expr.subtrees[1]), eval(env, expr.subtrees[2]));

            } else if (cmd.equals("state")) {
                return expr;

            } else if (cmd.equals("ifx")) {
                return new Tree<ASTValueData>(expr.val, expr.subtrees[0], eval(env, expr.subtrees[1]), eval(env, expr.subtrees[2]), eval(env, expr.subtrees[3]));
            } else if (cmd.startsWith("case_")) {
                String pname = cmd.substring(5); // product name
                Pair<String, Integer>[] ps = parameterMap.get(pname);
                Tree<ASTValueData> val = eval(env, expr.subtrees[1]);
                if (val.subtrees[0].val.name.startsWith("node_")) {
                    // TODO implement partial evaluation
                }
                Tree<ASTValueData>[] sub = new Tree[expr.subtrees.length];
                sub[0] = expr.subtrees[0];
                sub[1] = val;
                for (int j = 2; j < sub.length; j++) {
                    Pair env2 = env;
                    for (int k = 0; k < ps[j - 2].snd; k++) {
                        env2 = new Pair(null, env2);
                    }
                    sub[j] = eval(env2, expr.subtrees[j]);
                }
                return new Tree<ASTValueData>(expr.val, sub);
            } else if (cmd.equals("hd")) {
                return expr;
            } else if (cmd.equals("tl")) {
                return expr;
            } else if (cmd.equals("cns")) {
                return expr;
            } else { // constructor
                Tree<ASTValueData>[] sub = new Tree[expr.subtrees.length];
                sub[0] = expr.subtrees[0];
                for (int j = 1; j < sub.length; j++) {
                    sub[j] = eval(env, expr.subtrees[j]);
                }
                return new Tree<ASTValueData>(expr.val, sub);

            }

        }

        int vStack = 0;
        int cStack = 0;
        Type tObj = new Type("java.lang.Object");
        public IExpression gen(Pair env, Tree<ASTValueData> expr) {
            String cmd = expr.subtrees[0].val.name;
            if (cmd.equals("con")) {
                // remove prefix 'class '
                final String className = expr.subtrees[1].val.name.substring(6).replace("$", ".");
//                try {
//                    java.lang.Class<?> c = java.lang.Class.forName(className);
//                } catch (ClassNotFoundException ex) {
//                    Logger.getLogger(TransformerLanguageGenerator2.class.getName()).log(Level.SEVERE, null, ex);
//                }
                return new ObjectCreation(cons(className));
            } else if (cmd.equals("var")) {
                return (IExpression) lookup(env, expr);
            } else if (cmd.equals("abs")) {
                vStack++;
                final String v = "_var_" + vStack;
                final Abstraction abstraction = new Abstraction(v, tObj, tObj, new Return(gen(new Pair(new VariableExpression(v), env), expr.subtrees[1])));
                vStack--;
                return abstraction;
            } else if (cmd.equals("app")) {
                IExpression f = gen(env, expr.subtrees[1]);
                IExpression e = gen(env, expr.subtrees[2]);
                return new App(new Conversion(consnatfuture("erilex.generated.transform.F"), f), e);
            } else if (cmd.equals("fix")) {
                return gen(new Pair(new VariableExpression("this"), env), expr.subtrees[1]); // TODO make this work for top level fix
//                Tree<String> f = eval(env, expr.subtrees[1]);
//                return subst(0, f, f);
            } else if (cmd.equals("visit")) {
                return new Invocation(gen(env, expr.subtrees[2]), "accept", new VariableExpression("DdefaultVisitor.this"), gen(env, expr.subtrees[1]));
            } else if (cmd.equals("state")) {
                return new VariableExpression(STATE);
            } else if (cmd.startsWith("case_")) {
                cStack++;
                String v = "_data_" + cStack;
                String pname = cmd.substring(5); // product name
                Pair<String, Integer>[] ps = parameterMap.get(pname);
                IExpression val = gen(env, expr.subtrees[1]);
                Statement[] sub = new Statement[expr.subtrees.length - 1];
                for (int j = 0; j < sub.length - 1; j++) {
                    Pair env2 = env;
                    for (int k = 0; k < ps[j].snd; k++) {
                        env2 = new Pair(new VariableExpression(v + ".i" + j), env2);
                    }
                    sub[j] = new IfStatement(new Invocation(new VariableExpression(v + ".subtrees[0].val"), "equals", new VariableExpression("\"" + ps[j].fst + "\"")), new Return(gen(env2, expr.subtrees[j + 2])), null);
                }
                sub[sub.length - 1] = new Return(new VariableExpression("null"));
                cStack--;
                return new App(new Abstraction(v, tObj, tObj, sub), val);
            } else if (cmd.equals("ifx")) {
                return new If(new Conversion(consnat(java.lang.Boolean.class), gen(env, expr.subtrees[1])), gen(env, expr.subtrees[1]), gen(env, expr.subtrees[1]));
            } else if (cmd.equals("hd")) {
                IExpression f = gen(env, expr.subtrees[1]);
                return new Hd(f);
            } else if (cmd.equals("tl")) {
                IExpression f = gen(env, expr.subtrees[1]);
                return new Tl(f);
            } else if (cmd.equals("cns")) {
                IExpression f = gen(env, expr.subtrees[1]);
                IExpression e = gen(env, expr.subtrees[2]);
                return new Cns(f, e);

            } else { // constructor
                IExpression[] sub = new IExpression[expr.subtrees.length - 1];
                Type cons = cons(expr.subtrees[0].val.name);
                for (int j = 1; j <= sub.length; j++) {
                    sub[j - 1] = gen(env, expr.subtrees[j]);
                }
                return new ObjectCreation(cons, sub);

            }

        }

        public Tree<ASTValueData> subst(int i, Tree<ASTValueData> expr, Tree<ASTValueData> valExpr) {
            String cmd = expr.subtrees[0].val.name;
            if (valExpr == null) {
                return expr;
            }
            if (cmd.equals("con")) {
                return expr;
            } else if (cmd.equals("var")) {
                Tree<ASTValueData> expr2 = expr.subtrees[1];
                int s = 0;
                while (expr2.subtrees.length > 1) {
                    s++;
                    expr2 = expr2.subtrees[1];
                }
                if (i == s) {
                    return valExpr;
                } else {
                    return expr;
                }
            } else if (cmd.equals("abs")) {
                return new Tree<ASTValueData>(expr.val, expr.subtrees[0], subst(i + 1, expr.subtrees[1], valExpr));
            } else if (cmd.equals("app")) {
                return new Tree<ASTValueData>(expr.val, expr.subtrees[0], subst(i, expr.subtrees[1], valExpr), subst(i, expr.subtrees[2], valExpr));
            } else if (cmd.equals("fix")) {
                return new Tree<ASTValueData>(expr.val, expr.subtrees[0], subst(i, expr.subtrees[1], valExpr));
            } else if (cmd.equals("visit")) {
                return new Tree<ASTValueData>(expr.val, expr.subtrees[0], subst(i, expr.subtrees[1], valExpr), subst(i, expr.subtrees[2], valExpr));

            } else if (cmd.startsWith("case_")) {
                String pname = cmd.substring(5); // product name
                Pair<String, Integer>[] ps = parameterMap.get(pname);
                Tree<ASTValueData>[] sub = new Tree[expr.subtrees.length];
                sub[0] = expr.subtrees[0];
                for (int j = 1; j < sub.length; j++) {
                    sub[j] = subst(i + ps[j - 1].snd, expr.subtrees[j], valExpr);
                }
                return new Tree<ASTValueData>(expr.val, sub);
            } else if (cmd.equals("hd")) {
                return new Tree<ASTValueData>(expr.val, expr.subtrees[0], subst(i, expr.subtrees[1], valExpr));
            } else if (cmd.equals("tl")) {
                return new Tree<ASTValueData>(expr.val, expr.subtrees[0], subst(i, expr.subtrees[1], valExpr));
            } else if (cmd.equals("cns")) {
                return new Tree<ASTValueData>(expr.val, expr.subtrees[0], subst(i, expr.subtrees[1], valExpr), subst(i, expr.subtrees[2], valExpr));
            } else { // constructor
                Tree<ASTValueData>[] sub = new Tree[expr.subtrees.length];
                sub[0] = expr.subtrees[0];
                for (int j = 1; j < sub.length; j++) {
                    sub[j] = subst(i, expr.subtrees[j], valExpr);
                }
                return new Tree<ASTValueData>(expr.val, sub);
            }
        }

        public Object lookup(Pair env, Tree<ASTValueData> expr) {
            Tree<ASTValueData> expr2 = expr.subtrees[1];
            Pair env2 = env;
            Object exprVal = env2.fst;
            while (expr2.subtrees.length > 1) {
                expr2 = expr2.subtrees[1];
                env2 = env2 == null ? null : (Pair) env2.snd;
                exprVal = env2 == null ? null : env2.fst;
            }
            return exprVal == null ? expr : exprVal;
        }
    }
    public static class TT extends GenericTreeTransformer<Tree<ASTValueData>, Pair<Grammar, DeepTreeTransformer>> {

        public Pair<Grammar, DeepTreeTransformer> transform(Tree<ASTValueData> node, Pair<Grammar, DeepTreeTransformer> state) {
            String a = node.subtrees[0].val.name;
            a = a.substring(0, a.length()-2);
            String z = a.substring(0, a.indexOf('_'));
            a = a.substring(a.indexOf('_')+1);
            Grammar OLG = state.fst;
            Eval e = new Eval(OLG);
            int n = getProduction(OLG, z, a);
            Pair env = new Pair(null, null);
            for(int i=0;i<n;i++) {
                env = new Pair(null, env);
            }
            Pair env2 = new Pair(new VariableExpression(STATE), null);
            for(int i=0;i<n;i++) {
                env2 = new Pair(new VariableExpression("d."+"i"+i), env2);
            }
            System.out.println("evaluating: "+ node);
            IExpression code = e.gen(env2, e.eval(env, node.subtrees[1]));
            state.snd.addTransformer(a, code);
            return state;
        }

        private int getProduction(Grammar G, String e, String a) {
            List<Production> seqList = new ArrayList<Production>();
            List<Production> symList = new ArrayList<Production>();
            LanguageGenerator.breakOrList(G.ruleMap.get(e),seqList);
            for(Production p : seqList) {
                symList.clear();
                LanguageGenerator.breakSeqList(p, symList);
                if(((Terminal)symList.get(0)).text.equals(a)) {
                    return symList.size()-1;
                }
            }
            return -1;
        }
    }

}
