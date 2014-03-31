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


package erilex.codegen.ex;

import erilex.codegen.embedded.TypingLanguageGenerator;
import erilex.Grammar;
import erilex.codegen.*;
import erilex.builder.LanguageBuilder;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author ertri
 */
public class STLCTyL {
    public static LanguageBuilder<Object> T;
    public static LanguageBuilder<Object> E;
    public static LanguageBuilder<Object> G;

    public static void grammar(NameEmbedding t, NameEmbedding d, NameEmbedding e) {
    t.addVar("Eta", Generator.Eta.cons);
    t.addNatVar("nChi", Generator.Chi.cons);
    t.addVar("Chi", Generator.Chi.cons);
    t.addVar("Chi1", Generator.Chi.cons + "1");
    t.addVar("Chi2", Generator.Chi.cons + "2");
    t.addNat("Object", "java.lang.Object");
//    d.addNat("c", "c");
//    e.addNat("c", "c");
    // types
    T = new LanguageBuilder<Object>();
    T.def("type").start()
            .t("fun").nt("type").nt("type")
            .or().t("Tr").nt("type")
            .or().t("Chi1")
            .or().t("Chi2")
            .or().t("Chi")
            .or().t("nChi").end();
    T.ignore().start().oneOf(' ').end();
    // environments
    E = new LanguageBuilder<Object>();
    E.def("env").start()
            .t("nil")
            .or().t("pair").nt("type").nt("env")
            .or().t("Eta").end();
    E.copy(T);
    E.ignore().start().oneOf(' ').end();
    // terms
    G = new LanguageBuilder<Object>();
    G.def("e").start(1).t("con").nt("c").end();
    G.def("e").start(1).t("var").nt("i").end();
    G.def("e").start(0).t("abs").nt("e").end();
    G.def("e").start(0).t("app").nt("e").nt("e").end();
//    G.def("e").start(1).t("appcon").nt("c").nt("e").end();
    G.def("e").start(0).t("fix").nt("e").end();
    G.def("e").start(0).t("casex").nt("e").nt("e").nt("e").end();
    G.def("i").start(0).t("z").end();
    G.def("i").start(0).t("s").nt("i").end();
    }
     public static String packty = "erilex.generated.typing";
     public static String packtr = "erilex.generated.trans";
     public static String packLang = "erilex.generated.lang";
     public static String packLangVisitor = "erilex.generated.lang.visitor";
    public static void main(String[] args) throws IOException {
        NameEmbedding t = new NameEmbedding(NameEmbedding.TLT_PREFIX);
        
       
        String path = "..\\EriLexTyping\\src";
        grammar(t, new NameEmbedding((String) null), new NameEmbedding((String)null));
        // typing
        TypingLanguageGenerator tgg = new TypingLanguageGenerator(t, E, T, G, "typing", Generator.Chi, path, packty);
        tgg.generate();

//        TransformerLanguageGenerator<Object> tlg = new TransformerLanguageGenerator<Object>(t,d,e, E, T, G, "trans", Object.class, path, packtr, packLang);
//        tlg.generate();
        // print rules
//        printGrammarRules(tgg.generator, T);
//        printGrammarRules(tgg.generator, E);
//        printTypingRules(tgg.generator, G);
     }

    public static void printGrammarRules(LanguageGenerator gg, Grammar T) {
        List<ProductionRule> rs2 = gg.toGrammarRules(T);
        for (ProductionRule r : rs2) {
            System.out.println(r);
            System.out.println();
        }
    }

    public static void printTypingRules(LanguageGenerator gg, Grammar G) {
        List<TypingRule> rs = gg.toTypingRules(G);
        for (TypingRule r : rs) {
            System.out.println(r);
            System.out.println();
        }
    }

}
