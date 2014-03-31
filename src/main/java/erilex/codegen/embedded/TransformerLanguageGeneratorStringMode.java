/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package erilex.codegen.embedded;

import erilex.AbstractProduction;
import erilex.Grammar;
import erilex.Production;
import erilex.SemanticsCollection;
import erilex.Terminal;
import erilex.Utils;
import erilex.builder.ExtendedExpressionBuilder;
import erilex.builder.LanguageBuilder;
import erilex.builder.RuleBuilder;
import erilex.codegen.GenericTreeTransformer;
import erilex.codegen.LanguageGenerator;
import erilex.codegen.NameEmbedding;
import erilex.codegen.SemanticsBuilder;
import erilex.codegen.Type;
import erilex.data.generic.Tree;
import erilex.tree.DeepTreeTransformer;
import erilex.tree.TreeBuildingHandler;
import java.io.IOException;
import static erilex.codegen.Generator.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author ertri
 */
public class TransformerLanguageGeneratorStringMode<State> {

    public static final String BY = "_BY_";
    public static final String by = "by";
    public static final String JUDGEMENT = "judgement";
    public static final String Trans = "Trans";
    public LanguageBuilder<DeepTreeTransformer> TrL = new LanguageBuilder<DeepTreeTransformer>();
    public SemanticsBuilder<Tree<String>, DeepTreeTransformer> TrLS;
    LanguageBuilder<Object> TrLT = new LanguageBuilder<Object>();
    LanguageBuilder<Object> TrLE = new LanguageBuilder<Object>();
    Grammar E;
    Grammar T;
    Grammar G;
    String ss;
    NameEmbedding d;
    java.lang.Class<State> state;
    private final String path;
    private final String pack;
    private final String langPack;
    private LanguageGenerator generator;
    private final NameEmbedding t;
    private final NameEmbedding e;
        public final Type dchi;

    public TransformerLanguageGeneratorStringMode(NameEmbedding t, NameEmbedding d, NameEmbedding e, Grammar E, Grammar T, Grammar G, String ss, Type dchi, java.lang.Class<State> state, String path, String pack, String lgpk) {
        this.E = E;
        this.T = T;
        this.G = G;
        this.ss = ss;
        this.t = t;
        this.d = d;
        this.e = e;
        this.state = state;
        this.path = path;
        this.pack = pack;
        this.langPack = lgpk;
        TrLT.def("type").start().t("Bot").end();
        TrLE.def("env").start().t("Bot").end();
        this.dchi = dchi;
        TrLS = new SemanticsBuilder<Tree<String>, DeepTreeTransformer>(consnat(DeepTreeTransformer.class));
    }

    public void genTransformerL() {

        TreeBuildingHandler treeBuildingHandler = new TreeBuildingHandler();
        TrL.handler = treeBuildingHandler;
        treeBuildingHandler.longText = true; // save text for all named nodes
        ExtendedExpressionBuilder<RuleBuilder, Object> r;
        if (state.getTypeParameters().length != 0) {
            Utils.warning(this, "Generic state.");
        }
        TrL.startSymbol = ss;
        // add rules in E and T, we require that the rule maps are compatible
        TrL.ruleMap.putAll(T.ruleMap);
        TrL.ruleMap.putAll(E.ruleMap);
        // define a rule
        // ss -> e eClause
        // for each nonterminal e in G
        for (Production eProd : G.ruleMap.values()) {
            final String name = eProd.getProdName();
            String eTrans = name + Trans;
            TrL.def(ss).start().t(name).nt(eTrans).end();
            ArrayList<Production> list = new ArrayList<Production>();
            final ArrayList<Production> symlist = new ArrayList<Production>();
            LanguageGenerator.breakOrList(eProd, list);
            for (final Production clause : list) {
                symlist.clear();
                LanguageGenerator.breakSeqList(clause, symlist);
                final String a = ((Terminal) symlist.get(0)).text;
                // for each clause e -> a z1 ... zn in G
                // eTrans -> a by (transformer)
                String eaTrans = name + a + Trans;
                TrL.def(eTrans).start().t(a).nt(eaTrans).end();
                TrL.def(eaTrans).start(1).t(BY + name + a).nt("dt").end();
                final AbstractProduction node = (AbstractProduction) ((erilex.Seq) TrL.ruleMap.get(eaTrans)).b;
                node.typeType = consnat(java.lang.Class.class, conswild("?", consnat(GenericTreeTransformer.class, consnatfuture(langPack + "."+ d.get(a)), consnat(state))));
            }
            TrLS.trans(name, TT.class);
        }

    }

    public void generate() throws IOException {
        genTransformerL();
        NameEmbedding t2 = new NameEmbedding(t);
        t2.add("Bot", "Bot", false, true);
        generator = new LanguageGenerator(t2, d, e, TrLE, "Bot", TrLT, dchi, TrL, TrLS, new SemanticsCollection(), new HashMap<String, String>(), new HashSet<String>(), path, pack);
        generator.generate();
    }

    public static class TT extends GenericTreeTransformer<Tree<String>, DeepTreeTransformer> {

        @Override
        public DeepTreeTransformer transform(Tree<String> node, DeepTreeTransformer state) {
            String a = node.subtrees[1].subtrees[0].val;
            // remove prefix 'class '
            String substring = node.subtrees[1].subtrees[1].subtrees[1].val.substring(6);
            try {
                java.lang.Class<?> c;
                c = java.lang.Class.forName(substring);
//                System.out.println(node);
                state.addTransformer(a, c);

            } catch (ClassNotFoundException ex) {
                Utils.error(this, "Class " + substring + " not found.");
            }
            return state;
        }
    }
}
