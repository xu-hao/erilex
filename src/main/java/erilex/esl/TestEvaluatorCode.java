/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package erilex.esl;

import erilex.SemanticsCollection;
import erilex.builder.LanguageBuilder;
import erilex.codegen.LanguageGenerator;
import erilex.codegen.NameEmbedding;
import erilex.codegen.Type;
import erilex.codegen.TypeFunction;
import erilex.codegen.Variable;
import erilex.data.generic.Tree;
import erilex.tree.DeepTreeTransformer;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author eri
 */
public class TestEvaluatorCode {
    public static void main(String[] args) throws IOException {
//        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(System.out);
//        dtt.evaluatorCode().codeGen(outputStreamWriter, "");
//        outputStreamWriter.flush();
//        outputStreamWriter.close();
        NameEmbedding t = new NameEmbedding("");
        NameEmbedding d = new NameEmbedding("D");
        NameEmbedding e = new NameEmbedding("");
        LanguageBuilder E = new LanguageBuilder();
        LanguageBuilder T = new LanguageBuilder();
        LanguageBuilder G = new LanguageBuilder();

        // syntax e
        G.startSymbol = "e";
        // e -> zero
        // e -> succ e
        // e -> cons(n)
        // n : var
        d.addNatVar("n","n");
        e.addNatVar("n","n");
        // static
        // type
        // ty -> nat
        T.def("ty").start().t("nat").end();
        // ty -> t : var
        T.def("ty").start().t("tNat").end();
        T.def("ty").start().t("t").end();
        t.addVar("t", "t");
        t.addNatVar("tNat", "t"); // add a native counterpart for every variable
        // add a native counterpart for every variable
        Type dChi = new Type(true, "t");
        // ty -> Integer : nat
        T.def("ty").start().t("Integer").end();
        t.addNat("Integer", "Integer");

        // environments
        // env -> E : var
        E.def("env").start().t("E").end();
        t.addVar("E", "E");
        // env -> emp
        E.def("env").start().t("emp").end();
        
        // typing e : nat
        final String startType = "nat";
        // ----------
        // E |- e -> zero : nat
        G.def("e").start(0, "E", "nat").t("zero").end();
        // E |- e : nat
        // ----------
        // E |- e -> succ e : nat
        G.def("e").start(0, "E", "nat").t("succ").nt("e", "E", "nat").end();
        // E |- n : t
        // ----------
        // E |- e -> cons(n) : t
        G.def("e").start(1, "E", "t").t("cons").nt("n","E",e.nat("n")?"tNat":"t").end();
        // dynamic
        // eval : Integer
        final String evalatorName = "eval";
        G.semantics.setTrans(evalatorName, new Type("Integer"));
        DeepTreeTransformer<Tree, Object> dtt = G.semantics.treeTransformerMap.get(evalatorName);
        dtt.state = new Variable[]{new Variable(new Type("Object"), null)};
        dtt.valueTypeFuncRaw = new TypeFunction();
        dtt.valueTypeFuncRaw.add(LanguageGenerator.parseType(t, T, startType, null), new Type("Integer"));
        // e -> zero {
        //     return 0;
        // }
        G.trans("eval", "e", "zero", "__t__").by("return 0;\n");
        // e -> succ e {
        //     return 1+eval(e);
        // }
        G.trans("eval", "e", "succ", "__t__","e").by("return 1+eval(e);\n");

        LanguageGenerator g = new LanguageGenerator(t, d, e, E, "emp", T, dChi, G, dtt, new SemanticsCollection(), new HashMap<String, String>(), new HashSet<String>(), "../esl/src/", "esl.generated");
        g.generate2();
    }

}
