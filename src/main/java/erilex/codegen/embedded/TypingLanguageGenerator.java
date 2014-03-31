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
package erilex.codegen.embedded;

import erilex.AbstractProduction;
import erilex.Grammar;
import erilex.Production;
import erilex.SemanticsCollection;
import erilex.Terminal;
import erilex.builder.ExtendedExpressionBuilder;
import erilex.builder.LanguageBuilder;
import erilex.builder.RuleBuilder;
import erilex.codegen.Generator;
import erilex.codegen.GenericTreeTransformer;
import erilex.codegen.LanguageGenerator;
import erilex.codegen.NameEmbedding;
import erilex.codegen.SemanticsBuilder;
import erilex.codegen.Type;
import erilex.data.generic.Tree;
import erilex.tree.ASTValueData;
import erilex.tree.TreeBuildingHandler;
import erilex.tree.TreeUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

/**
 *
 * @author ertri
 */
public class TypingLanguageGenerator {

    public static final String IMPLY = "IMP";
    public static final String imply = "imply";
    public static final String JUDGEMENT = "judgement";
    public static final String Clause = "Clause";
    public LanguageBuilder<Grammar[]> TL = new LanguageBuilder<Grammar[]>();
    public SemanticsBuilder<Tree<ASTValueData>, Grammar[]> TLS;
    Grammar E;


    Grammar T; 
    Grammar G;
    String ss;
    String path;
    String pack;
    NameEmbedding t,d,e;
    public final Type dchi;
    public LanguageGenerator generator;
    LanguageBuilder<Object> TLT = new LanguageBuilder<Object>();
    LanguageBuilder<Object> TLE = new LanguageBuilder<Object>();

    public void genTypingL() {

        TreeBuildingHandler treeBuildingHandler = new TreeBuildingHandler();
        TL.handler = treeBuildingHandler;
        treeBuildingHandler.longText = true; // save text for all named nodes
        ExtendedExpressionBuilder<RuleBuilder, Object> r;
        // auxiliary rule imply -> "imply"
        TL.startSymbol = ss;
        TL.def(IMPLY).start().t(imply).end();
        // add rules in E and T, we require that the rule maps are compatible
        TL.ruleMap.putAll(T.ruleMap);
        TL.ruleMap.putAll(E.ruleMap);
        final String zE = E.startSymbol;
        final String zt = T.startSymbol;
        // define a rule
        // ss -> e eClause
        // for each nonterminal e in G
        for (Production e : G.ruleMap.values()) {
            final String name = e.getProdName();
            String eClause = name + Clause;
            TL.def(ss).start().t(name).nt(eClause).end();
            ArrayList<Production> list = new ArrayList<Production>();
            final ArrayList<Production> symlist = new ArrayList<Production>();
            LanguageGenerator.breakOrList(e, list);
            for (final Production clause : list) {
                symlist.clear();
                LanguageGenerator.breakSeqList(clause, symlist);
                final String a = ((Terminal)symlist.get(0)).text;
                // for each clause e -> a z1 ... zn in G
                // eClause -> a E1 t1 ... En tn imply E t
                r = TL.def(eClause).start().t(a);
                for (int i = 1; i < symlist.size(); i++) {
                    r = r.nt(zE).nt(zt);
                }
                r = r.nt(IMPLY);
                r.nt(zE).nt(zt).end();

            //TLS.trans(a, TT.class);
            }
            TLS.trans(name, TT.class);
        }

    }
    public void generate() throws IOException {
        genTypingL();
        NameEmbedding t2 = new NameEmbedding(t);
        t2.add("Bot", "Bot", false, true);
        generator = new LanguageGenerator(t2, d, e, TLE, "Bot", TLT, dchi, TL, TLS, new SemanticsCollection(), new HashMap<String, String>(), new HashSet<String>(), path, pack);
        generator.parameterized = false;
        generator.generate();
    }

    public TypingLanguageGenerator(NameEmbedding tp, Grammar E, Grammar T, Grammar G, String ss, Type dchi, String path, String pack) {

        this.t = tp;
        this.e = new NameEmbedding(NameEmbedding.TLE_PREFIX);
        this.d = new NameEmbedding(NameEmbedding.TLD_PREFIX);
        this.E = E;
        this.T = T;
        this.G = G;
        this.ss = ss;
        this.path = path;
        this.pack = pack;
        TLT.def("type").start().t("Bot").end();
        TLE.def("env").start().t("Bot").end();
        this.dchi = dchi;
        TLS  = new SemanticsBuilder<Tree<ASTValueData>, Grammar[]>(Generator.consnat(erilex.Grammar[].class));

    }
    public TypingLanguageGenerator(NameEmbedding tp, NameEmbedding dp, NameEmbedding ep, Grammar E, Grammar T, Grammar G, String ss, Type dchi, String path, String pack) {
        this.t = tp;
        this.e = ep;
        this.d = dp;
        this.E = E;
        this.T = T;
        this.G = G;
        this.ss = ss;
        this.path = path;
        this.pack = pack;
        TLT.def("type").start().t("Bot").end();
        TLE.def("env").start().t("Bot").end();
        this.dchi = dchi;
    }
    public static class TT extends GenericTreeTransformer<Tree<ASTValueData>, Grammar[]> {

        @Override
        public Grammar[] transform(Tree<ASTValueData> node, Grammar[] state) {
            Grammar E = state[0], T = state[1], G = state[2];
            Tree<ASTValueData> enode = node.subtrees[0];
            Tree<ASTValueData> clsnode = node.subtrees[1];
            Tree<ASTValueData> anode = clsnode.subtrees[0];
            AbstractProduction ap;
            Production p = G.ruleMap.get(enode.val.name);
            ArrayList<Production> seqlist = new ArrayList<Production>();
            ArrayList<Production> symlist = new ArrayList<Production>();
            LanguageGenerator.breakOrList(p, seqlist);
            for (Production p2 : seqlist) {
                symlist.clear();
                LanguageGenerator.breakSeqList(p2, symlist);
                if (anode.val.name.equals(((Terminal)symlist.get(0)).text)) {
                    List<Tree<ASTValueData>> Es = TreeUtils.getGroupByVal(clsnode, E.startSymbol);
                    List<Tree<ASTValueData>> ts = TreeUtils.getGroupByVal(clsnode, T.startSymbol);
                    for (int i = 0; i < Es.size() - 1; i++) {
                        ap = ((AbstractProduction) symlist.get(i+1));
                        ap.env = toTypeString(Es.get(i));
                        ap.type = toTypeString(ts.get(i));
                    }
                    int n = Es.size() - 1;
                    ((AbstractProduction) p2).env = toTypeString(Es.get(n));
                    ((AbstractProduction) p2).type = toTypeString(ts.get(n));
                }
            }
            return state;
        }

        public static Type toType(Tree<ASTValueData> anode, NameEmbedding t) {
            Type[] subs = new Type[anode.subtrees.length - 1];
            
            for (int i = 1;i<anode.degree();i++) {
                subs[i-1] = toType(anode.subtrees[i], t);
            }
            return Generator.cons(anode.subtrees[0].val.name, subs);

        }
        
    }public static String toTypeString(Tree<ASTValueData> anode) {
            String type = anode.subtrees[0].val.name;

            for (int i = 1;i<anode.degree();i++) {
                type += " " + toTypeString(anode.subtrees[i]);
            }
            return type;

        }
}
