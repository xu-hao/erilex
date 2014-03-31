/*
Copyright 2010 Hao Xu
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

package erilex.esl;

import erilex.Utils;
import erilex.builder.ExtendedExpressionBuilder;
import erilex.builder.LanguageBuilder;
import erilex.builder.RuleBuilder;
import erilex.codegen.Generator;
import erilex.codegen.LanguageGenerator;
import erilex.codegen.NameEmbedding;
import erilex.codegen.Type;
import erilex.codegen.TypeFunction;
import erilex.codegen.Variable;
import erilex.data.generic.Tree;
import erilex.tree.DeepTreeTransformer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ertri
 */
public class SupportCodeGenerator {
    public static final String __NAT = "__Nat";
    NameEmbedding t = new NameEmbedding("");
    NameEmbedding d = new NameEmbedding("D");
    NameEmbedding e = new NameEmbedding("");
    LanguageBuilder E = new LanguageBuilder();
    LanguageBuilder T = new LanguageBuilder();
    LanguageBuilder G = new LanguageBuilder();
    public Map<String, String> natTypeMap = new HashMap();
    public Set<String> funSet = new HashSet();

    public static SupportCodeGenerator defaultSCG = new SupportCodeGenerator();
    public boolean typing;

//    private String einit = "emp";
    public void setStartSymbol(String ss) {
        G.startSymbol = ss;
    }
    public void defTermVar(String n) {
        // n : var
        d.addNatVar(n,n);
        e.addNatVar(n,n);
    }
    public void defType(String nt, String t, String... symbols) {
        ExtendedExpressionBuilder<RuleBuilder, Object> b = T.def(nt).start().t(t);
        for(String s : symbols)
            b = b.nt(s);
        b.end();
        defEnv(nt, t, symbols);
        E.startSymbol = null; // reset start symbol of E
    }
    Type dChi = new Type(true, "t");
    public void defTypeVar(String nt, String t) {
        T.def(nt).start().t(t).end();
        T.def(nt).start().t(__NAT+t).end();
        this.t.addVar(t, t);
        this.t.addNatVar(__NAT+t, "t"); // add a native counterpart for every variable
        String ESS = E.startSymbol; // add a copy to E
        defEnvVar(nt, t);
        E.startSymbol = ESS;
    }
    public void defTypeNat(String nt, String t) {
        T.def(nt).start().t(t).end();
        this.t.addNat(t, t);
    }
    public void defEnv(String nt, String t, String... symbols) {
        ExtendedExpressionBuilder<RuleBuilder, Object> b = E.def(nt).start().t(t);
        for(String s : symbols)
            b = b.nt(s);
        b.end();
    }
    public void defEnvVar(String nt, String t) {
        E.def(nt).start().t(t).end();
        E.def(nt).start().t(__NAT+t).end();
        this.t.addVar(t, t);
        this.t.addNatVar(__NAT+t, "t"); // add a native counterpart for every variable
    }
    String startType;
    String startEnv;
    public void setStartTypeAndEnv(String st, String se) {
        this.startEnv = se;
        this.startType = st;
    }
    public void defTypingRule(String nt, int p, String env_nt, String type_nt, String t, String... antes) {
        ExtendedExpressionBuilder<RuleBuilder, Object> b = G.def(nt).start(p, env_nt, type_nt).t(t);
        if(antes.length%3!=0) {
            Utils.warning(this, "Number of arguments for antes in method defTypingRule is not divisible by 3, ignoring trailing arguments");
        }
        for(int i=0;i<antes.length/3;i++) {
            String env = antes[i * 3];
            String nt2 = antes[i * 3 + 1];
            String type = antes[i * 3 + 2];
            if(e.nat(nt2)) {
                if(this.t.var(type))
                    type = __NAT+type;
            }
            b = b.nt(nt2, env, type);
        }
        b.end();
    }

    List<String> evalatorName = new ArrayList<String>();
    String stateName = "state";
    String defaultEval;
        DeepTreeTransformer<Tree, Object> dtt; // = G.semantics.treeTransformerMap.get(evalatorName);
        public void setDefaultEvaluator(String evalName) {
            this.defaultEval = evalName;
        }
        public void defEvaluator(String eval, String type, Variable... state) {
        final Type ty = new Type(type);
        G.semantics.setTrans(eval, ty);
        evalatorName.add(eval);
        if(defaultEval==null) {
            defaultEval = eval;
        }
        dtt = G.semantics.treeTransformerMap.get(eval);
        dtt.state = state;
        dtt.valueTypeFuncRaw = new TypeFunction();
        dtt.valueTypeFuncRaw.add(LanguageGenerator.parseType(t, T, startType, null), ty);
    }
    public void defEvaluatorComponent(String evalName, String nt, String t, String[] vars, String blob) {
        G.trans(evalName, nt, t, vars).by(blob);
    }

    String path;
    String pack;

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public void generate() throws IOException {
        

        LanguageGenerator g = new LanguageGenerator(t, d, e, E, startEnv, T, dChi, G, G.semantics.treeTransformerMap.get(defaultEval), G.semantics, natTypeMap, funSet, path, pack);
        g.generate2();
    }

    public void defTypeFun(String nt, String t, String[] names) {
        SupportCodeGenerator scg = this;
        scg.defType(nt, t, names);
        funSet.add(t);
    }

}
