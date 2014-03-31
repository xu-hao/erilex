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
package erilex.codegen;

import erilex.AbstractProduction;
import erilex.Grammar;
import erilex.NonTerminal;
import erilex.Or;
import erilex.Production;
import erilex.SemanticsCollection;
import erilex.Seq;
import erilex.MultiSeq;
import erilex.Terminal;
import erilex.Utils;
import erilex.data.generic.Tree;
import erilex.tree.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ertri
 */
public class LanguageGenerator {

    public NameEmbedding t, d, e;
    public Grammar E, T, G;
    public SemanticsCollection SC;
    public DeepTreeTransformer S;
    public String path, pack;
    public boolean parameterized = true;
    public String enil;
    public final Type dchi;
    public Map<String, String> natTypeMap;
    public Map<String, DeepTreeTransformer> dttMap;
    public Set<String> funSet;

    public LanguageGenerator(NameEmbedding t, NameEmbedding d, NameEmbedding e, Grammar E, String enil, Grammar T, Type dChi, Grammar G, DeepTreeTransformer S, SemanticsCollection sc, Map<String, String> natTypeMap, Set<String> fs, String path, String pack) {
        this.t = t;
        this.d = d;
        this.e = e;
        this.E = E;
        this.T = T;
        this.G = G;
        this.SC = sc;
        this.S = S;
        this.SC.treeTransformerMap.put(S.name, S);
        this.natTypeMap = natTypeMap;
        this.funSet = fs;
        this.path = path;
        this.pack = pack;
        this.enil = enil;
        this.dchi = dChi;
    }

    public LanguageGenerator(NameEmbedding t, NameEmbedding d, NameEmbedding e, Grammar E, String enil, Grammar T, Type dChi, Grammar G, String path, String pack) {
        this.t = t;
        this.d = d;
        this.e = e;
        this.E = E;
        this.T = T;
        this.G = G;
        this.path = path;
        this.pack = pack;
        this.enil = enil;
        this.dchi = dChi;
    }
    public Judgement[] array0 = new Judgement[0];
    public static String[] array1 = new String[0];

    public static void breakOrList(Production p, List<Production> seqList) {
        if (p instanceof Or) {
            breakOrList(((Or) p).a, seqList);
            breakOrList(((Or) p).b, seqList);
        } else {
            seqList.add(p);
        }
    }

    public static void breakSeqList(Production p, List<Production> symList) {
        if (p instanceof Seq) {
            breakSeqList(((Seq) p).a, symList);
            breakSeqList(((Seq) p).b, symList);
        } else {
            symList.add(p);
        }

    }

    public void generate() throws IOException {
        generate_t(path, pack);
        generate_e(path, pack);
        generate_db(path, pack);
        generate_s(path, pack);
    }
    public void generate2() throws IOException {
        generate_t(path, pack);
        generate_e(path, pack);
        generate_db(path, pack);
        generate_s2(path, pack);
    }

    @Deprecated
    private String getStateStr() {
        return S.state[0].type.toStringCodeGen();
    }

    public Type parseEnv(Production p) {
        Type env;
        final AbstractProduction ap = (AbstractProduction) p;
        if (ap.envType != null) {
            env = ap.envType;
        } else {
            String envStr = (ap).env;
            env = parseType(t, E, envStr, Generator.Eta);
        }
        return env;
    }

    public static Type parseType(NameEmbedding t, Grammar T, String type1, Type def) {
        Type type;
        if (type1 != null) {
            if (!T.parse(type1)) {
                Utils.error(T, "failed to parse type " + type1);
            }
            type = TreeUtils.toType(((TreeBuildingHandler) T.handler).getASTandReset(), t);
        } else {
            type = def;
        }
        return type;
    }

    public Type parseType(Production p) {
        Type type;
        final AbstractProduction ap = (AbstractProduction) p;
        if (ap.typeType != null) {
            type = ap.typeType;
        } else {
            type = parseType(t, T, ap.type, Generator.Chi);
        }
        return type;
    }

    public List<TypingRule> toTypingRules(Grammar g) {
        List<TypingRule> list = new ArrayList<TypingRule>();
        String a;
        String zn;
        Type env, type;
        for (Production p : g.ruleMap.values()) {
            String name = p.getProdName();
            if (name == null) {
                Utils.error(this, "Unsupported: grammars with auxiliary rules");
                continue;
            } else {

                Judgement z;
                List<Production> seqList = new ArrayList<Production>();
                breakOrList(p, seqList);

                for (Production pp : seqList) {
//                    in 
                    env = parseEnv(pp);
                    type = parseType(pp);
                    z = new Judgement(env, name, type);
                    int ari = parameterized ? ((AbstractProduction) pp).arity : 0; // set arity to 0 if not parameterize
                    if (pp instanceof Terminal) {
                        a = ((Terminal) pp).text;
                        list.add(new TypingRule(z, a, array0));
                    } else {
                        List<Judgement> l = new ArrayList<Judgement>();

			List<Production> seq = ((MultiSeq) pp).disjuncts;
                        a = ((Terminal) seq.get(0)).text;
                        for (Production pnt : seq.subList(1, seq.size())) {
                            NonTerminal nt = (NonTerminal) pnt;
                            zn = (nt).refName;
                            env = parseEnv(nt);
                            type = parseType(nt);

                            l.add(new Judgement(env, zn, type));
                        }
                        list.add(new TypingRule(z, a, ari, l.toArray(array0)));
                    }
                }
            }
        }
        return list;
    }

    public static List<ProductionRule> toGrammarRules(Grammar g) {
        List<ProductionRule> list = new ArrayList<ProductionRule>();
        String a;
        String zn;
        for (Production p : g.ruleMap.values()) {
            String name = p.getProdName();
            if (name == null) {
                Utils.error(g, "Unsupported: grammars with auxiliary rules");
                continue;
            } else {
                String z = name;
                List<Production> seqList = new ArrayList<Production>();
                breakOrList(p, seqList);

                for (Production pp : seqList) {
                    int i = 0;
                    if (pp instanceof Terminal) {
                        a = ((Terminal) pp).text;
                        list.add(new ProductionRule(z, a, array1));
                    } else {
                    	List<String> l = new ArrayList<String>();
                        List<Production> seq = ((MultiSeq) pp).disjuncts;
                        a = ((Terminal) seq.get(0)).text;
                        for (Production pnt : seq.subList(1, seq.size())) {
                            NonTerminal nt = (NonTerminal) pnt;
                            zn = nt.refName + (i++);
                        
                            l.add(zn);
                        }
                        list.add(new ProductionRule(z, a, l.toArray(array1)));
                    }
                }
            }
        }
        return list;
    }
    Class[] ds;

    public void generate_db(String path, String pack) throws IOException {
        List<TypingRule> rs = toTypingRules(G);
        Generator g = new Generator(t, d, e, dchi, pack);
        // generate interface for nts
        for (TypingRule r : rs) {
            g.en_d_L(r.conseq.e, S.state, S.rType, S.valueTypeFuncRaw, S.name).writeToFile(path);
            g.en_d_eq_axiom(r.conseq.e, /*S.state[0].type, */S.valueTypeFuncRaw).writeToFile(path);
        }
        ds = new Class[rs.size()];
        int i = 0;
        // generate ast node for productions
        for (TypingRule r : rs) {
            (ds[i++] = g.en_d_L(r, S.state, S.rType, S.valueTypeFuncRaw)).writeToFile(path);
        }
        for (TypingRule r : rs) {
            g.en_e_L(r).writeToFile(path);
        }
        g.en_e_F().writeToFile(path);
        g.en_e_F2().writeToFile(path);
        g.en_e_F3().writeToFile(path);
        g.en_e_ID().writeToFile(path);
        g.en_e_Bot().writeToFile(path);
        g.enc_d_Visitor(S.state, S.rType, S.valueTypeFuncRaw, ds).writeToFile(path);
    }

    public void generate_e(String path, String pack) throws IOException {
        List<ProductionRule> rs = toGrammarRules(E);
        Generator g = new Generator(t, d, e, dchi, pack);
        for (ProductionRule r : rs) {
            if (!t.nat(r.a) && !t.var(r.a)) {
                g.en_e_L(r, funSet.contains(r.a)).writeToFile(path);
            }
        }
        g.enc_e_Utils(G.startSymbol, parseType(t, E, enil, null)).writeToFile(path);
    }

    public void generate_t(String path, String pack) throws IOException {
        List<ProductionRule> rs = toGrammarRules(T);
        Generator g = new Generator(t, d, e, dchi, pack);
        for (ProductionRule r : rs) {
            if (!t.nat(r.a) && !t.var(r.a)) {
                g.en_t_L(r, this.funSet.contains(r.a)).writeToFile(path);
            }
        }
    }

    /**
     * This method require that ds be initialized by the <code>generate_db</code> method.
     * @param path
     * @param pack
     */
    public void generate_s(String path, String pack) throws IOException {
        for (DeepTreeTransformer S : SC.treeTransformerMap.values()) {
            Generator g = new Generator(t, d, e, dchi, pack);
            // convert map to use d(z)
            HashMap<String, IExpression> map;
            map=S.transExprMap;
            HashMap map2 = new HashMap();
            for (String key : map.keySet()) {
                map2.put(d.get(key), map.get(key));
            }
            if (ds == null) {
                List<TypingRule> rs = toTypingRules(G);
                ds = new Class[rs.size()];
                int i = 0;
                for (TypingRule r : rs) {
                    ds[i++] = g.en_d_L(r, S.state,S.rType, S.valueTypeFuncRaw);
                }
            }
            g.enc_d_VisitorImpl(S.name, S.state,S.rType,S.valueTypeFuncRaw, map2, ds).writeToFile(path);
        }
    }
    /**
     * This method generates a class that contains all evaluators.
     * @param path
     * @param pack
     * @throws IOException
     */
    public void generate_s2(String path, String pack) throws IOException {
        Class c = new Class(pack, "Evaluators");
        c.methods.addAll(SC.evaluatorsCode(e, natTypeMap));
        Method nativeMethod = new Method(Generator.consnat(Object.class), "NAT", Generator.consnat(Object.class),"tree",
            new Return(
                new FieldAccess(
                    new Conversion(
                        Generator.consnat(ASTValueData.class),
                        new FieldAccess(
                            new Conversion(
                                Generator.consnat(Tree.class),
                                new VariableExpression("tree")
                            ),
                            "val"
                        )
                    ),
                    "obj"
                )
            )
        );

        c.methods.add(nativeMethod);
        c.writeToFile(path);
        // constants
        Class consts = new Class(pack, "Constants");
        for(String natType : new HashSet<String>(natTypeMap.values())) {

            Variable var = new Variable(Generator.cons("public static ID",Generator.cons(natType)), natType.replace('.', '_').toUpperCase()+"=new ID<"+natType+">()");
            consts.fields.add(var);
        }
        consts.writeToFile(path);
        
        Generator g = new Generator(t, d, e, dchi, pack);
        //for(DeepTreeTransformer S : SC.treeTransformerMap.values()) {
            //String stateArgName = S.state[0].name;
            g.enc_d_VisitorImpl2(S.name, S.state,S.rType, "Evaluators",S.name, ds).writeToFile(path);
        //}
    }

}
