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
import erilex.builder.LanguageBuilder;
import erilex.codegen.Generator;
import erilex.codegen.LanguageGenerator;
import erilex.codegen.TypingRule;
import erilex.codegen.Variable;
import erilex.data.CharStream;
import erilex.data.FileStream;
import erilex.data.generic.Pair;
import erilex.data.generic.Tree;
import erilex.tree.ASTValueData;
import erilex.tree.TreeBuildingHandler;
import erilex.tree.TreeTransformerAdapter;
import erilex.tree.TreeUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import static erilex.tree.TreeUtils.*;

/**
 *
 * @author ertri
 */
public class ESL extends LanguageBuilder<SupportCodeGenerator> {
    public static final String Antecedent = "antecedent";
    public static final String Assignment = "assignment";
    public static final String AssignmentLine = "assignmentLine";
    public static final String BLOCK = "block";
    public static final String BLOCK2 = "block2";
    public static final String CHAR = "char";
    public static final String COMMENT = "comment";
    public static final String Dashes = "dashes";
    public static final String DefinitionLine = "definitionLine";
    public static final String DIGIT = "DIGIT";
    public static final String EOL = "eol";
    public static final String EvaluationRule = "evaluatorRule";
    public static final String JAVACODEBLOCK = "javaCodeBlock";
    public static final String Judgment = "judgment";
    public static final String LETTER = "LETTER";
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String STYPE = "STYPE";
    public static final String TYPE = "TYPE";
    public static final String NONEOL = "noneol";
    public static final String Postcedent = "postcedent";
    public static final String ProductionLine = "productionLine";
    public static final String Production = "production";
    public static final String STRING = "string";
    public static final String STRING2 = "string2";
    public static final String Spec = "spec";
    public static final String TypingRule = "typingRule";
    public static final String Term = "term";

    public enum HostLanguage {
        Java {
            public final String[] keywords = {"if", "then", "else", "true", "false", "for", "while", "int", "boolean", "null", "public", "private"};
            public String[] getKeywords() {
                return keywords;
            }
        },
        Scala {
            public final String[] keywords = {"if", "then", "else", "true", "false", "for", "while", "int", "boolean", "null", "public", "private", "val", "def"};
            public String[] getKeywords() {
                return keywords;
            }
        };
        public abstract String[] getKeywords();

    }
    public HostLanguage target = HostLanguage.Java;
    public String[] getKeywords() {
        return target.getKeywords();
    }

    public boolean isKeyword(String k) {
        for(String kw : getKeywords()) {
            if(kw.equals(k)) {
                return true;
            }
        }
        return false;
    }

    public String mapKeyword(String k) {
        return isKeyword(k)?Character.toUpperCase(k.charAt(0))+k.substring(1):k;
    }
    private String trans(String text) {
        if(ntTransTable.containsKey(text)) {
            return ntTransTable.get(text);
        } else {
            return text;
        }
    }

    public Map<Pair<String, String>, Object[]> prodTable = new HashMap();
    public Map<Pair<String, String>, Object[]> trTable = new HashMap();
    public Map<String, String> varTable = new HashMap();
    public List<String> tvarList = new ArrayList();
    public List<String> evarList = new ArrayList();
    public Map<String, String> ntTransTable = new HashMap();
    public Set<String> ntSet = new HashSet();

    public ESL() {
        super(true);
        
        // ignore
        ignore().start().oneOf('\t',' ').or().nt(COMMENT).nt(EOL).optional().end();

        // lexer
        auxlex(LETTER).start()
                .range('a', 'z').or()
                .range('A', 'Z').end();
        auxlex(DIGIT).start()
                .range('0', '9').end();
        auxlex(ID).start()
                .nt(LETTER)
                .start()
                    .nt(LETTER).or()
                    .nt(DIGIT)
                .end().star().end();
        lex(NAME).start()
                .nt(ID).end();
        auxlex(STYPE).start()
                .nt(ID)
                .start()
                    .at('.').nt(ID)
                .end().star().start()
                    .at('<').nt(STYPE).start()
                        .at(',').nt(STYPE)
                    .end().star().at('>')
                .end().optional().end();
        lex(TYPE).start()
                .nt(STYPE).end();
        lex(NONEOL).start()
                .noneOf('\n','\r').star().end();
        auxlex(EOL).start()
                .oneOf('\n','\r').star().end();
        lex(JAVACODEBLOCK).start()
                .nt(BLOCK).end();
        auxlex(BLOCK).start()
                .oneOf('{').nt(BLOCK2).end();
        auxlex(STRING).start()
                .oneOf('\"').nt(STRING2).end();
        auxlex(STRING2).start()
                .oneOf('\"').or()
                .start()
                    .oneOf('\\').noneOf().or()
                    .noneOf('\\','\"')
                .end().nt(STRING2).end();
        auxlex(CHAR).start()
                .oneOf('\'')
                .start()
                    .oneOf('\\').noneOf().or()
                    .noneOf('\\')
                .end().oneOf('\'').end();
        auxlex(COMMENT).start()
                .oneOf('/').oneOf('/').noneOf('\n','\r').star().or()
                .oneOf('/').oneOf('*').start()
                    .noneOf('*').or()
                    .oneOf('*').noneOf('/')
                .end().plus().oneOf('*').oneOf('/').end();
        auxlex(BLOCK2).start()
                .oneOf('}').or()
                .start()
                    .nt(BLOCK).or()
                    .nt(STRING).or()
                    .nt(CHAR).or()
                    .nt(COMMENT).or()
                    .noneOf('{','}','\"','\'').plus()
                .end().nt(BLOCK2).end();
        
        def(Term).start()
                .nt(NAME).star().end();
        def(DefinitionLine).start()
                .nt(NAME)
                .t("=")
                .nt(NONEOL).nt(EOL).end();
        def(Assignment).start()
                .nt(NAME)
                .t(":")
                .nt(TYPE).end();
        def(AssignmentLine).start()
                .nt(Assignment).nt(EOL).end();
        def(Production).start()
                .nt(NAME)
                .t("->")
                .nt(NAME)
                .start()
                    .t("(")
                    .nt(Term)
                    .t(")")
                .end().optional()
                .nt(Term).end();
        def(ProductionLine).start()
                .nt(Production)
                .start().t(":").nt(NAME).end().optional()
                .nt(EOL).end();
        def(TypingRule).start()
                .nt(Antecedent)
                .nt(Dashes)
                .nt(Postcedent).end();
        def(Antecedent).start()
                .start()
                .nt(Judgment).nt(EOL).end().star().end();
        def(Judgment).start()
                .start()
                    .nt(Term)
                    .t("|-")
                .end().optional()
                .nt(NAME)
                .t(":")
                .nt(Term).end();
        def(Postcedent).start()
                .start()
                    .nt(Term)
                    .t("|-")
                .end().optional()
                .nt(Production)
                .t(":")
                .nt(Term).nt(EOL).end();
        def(Dashes).start()
                .oneOf('-').plus().nt(EOL).end();
        def(EvaluationRule).start()
                .nt(Production)
                .nt(JAVACODEBLOCK)
                .nt(EOL).end();
        def(Spec).start()
                .nt("embeddingSec").optional()
                .nt("syntaxSec")
                .nt("staticSec").optional()
                .nt("dynamicSec").end();
        def("embeddingSec").start()
                .t("embedding").nt(EOL)
                .nt(DefinitionLine).plus().end();
        def("syntaxSec").start()
                .t("syntax").nt(NAME).nt(EOL)
                .start()
                    .nt(ProductionLine).or()
                    .nt(AssignmentLine)
                .end().plus().end();
        def("staticSec").start()
                .t("static").nt(EOL)
                .nt("definitionSec").optional()
                .nt("typeSec").optional()
                .nt("environmentSec").optional()
                .nt("typingSec").optional().end();
        def("definitionSec").start()
                .t("definition").nt(EOL)
                .start()
                    .nt(AssignmentLine)
                .end().plus().end();
        def("typeSec").start()
                .t("type").nt(EOL)
                .nt(ProductionLine).plus().end();
        def("environmentSec").start()
                .t("environment").nt(EOL)
                .nt(ProductionLine).plus().end();
        def("typingSec").start()
                .t("typing").nt(Judgment).nt(EOL)
                .nt(TypingRule).star().end();
        def("dynamicSec").start()
                .t("dynamic").nt(NAME).optional().nt(EOL)
                .nt("evaluator").plus().end();
        def("evaluator").start()
                .nt(Assignment).plus().nt(EOL)
                .nt(EvaluationRule).plus().end();
        startSymbol = Spec;

//        dumpRuleMap();

        for (String s : ruleMap.keySet()) {
            if (!isLL1(s)) {
                Utils.warning(ruleMap.get(s), "This is not an LL(1) grammar: "+ruleMap.get(s));
            }
        }

        trans("gen", "syntaxSec").by(new TreeTransformerAdapter<SupportCodeGenerator>(){

            @Override
            public SupportCodeGenerator transform(Tree<ASTValueData> node, SupportCodeGenerator scg) {
                scg.setStartSymbol(text(first(getGroup(node,NAME))));
                for(Tree<ASTValueData> s : TreeUtils.getGroup(node, AssignmentLine)) {
                    Tree<ASTValueData> ss = TreeUtils.getGroup(s, Assignment).get(0);
                    String vn = text(first(getGroup(ss, NAME)));
                    scg.defTermVar(vn);
                    varTable.put(vn,vn);
                    ntSet.add(vn);
                }
                for(Tree<ASTValueData> s : TreeUtils.getGroup(node, ProductionLine)) {
                    Object[] prod = parseProduciton(first(getGroup(s,Production)));
                    prodTable.put(new Pair((String)prod[0], (String)prod[1]),prod);
                    ntSet.add((String)prod[0]);
                }
//                String pn, pnNew;
                // add productions for undefined nts
                Set<Object[]> prods = new HashSet<Object[]>(prodTable.values());
                for(Object[] prod : prods) {
                    for(String prodp : (String[])prod[2]) {
                        if(!ntSet.contains(prodp)) {
                            addNewProduction(prodp);
                        }
                    }
                    for(String prodnp : (String[])prod[3]) {
                        if(!ntSet.contains(prodnp)) {
                            addNewProduction(prodnp);
                        }
                    }
                }

                return scg;
            }

            private void addNewProduction(String pn) {
                String pnNew = "__" + mapKeyword(pn);
                //pn = mapKeyword(pn);
                prodTable.put(new Pair(pnNew, mapKeyword(pn)), new Object[]{pnNew, mapKeyword(pn), new String[0], new String[0]});
                ntSet.add(pnNew);
                ntTransTable.put(pn, pnNew);
                // replace pn with pnNew in all productions
                for (Object[] prod2 : prodTable.values()) {
                    // parameters
                    for (int i = 0; i < ((String[]) prod2[2]).length; i++) {
                        if (((String[]) prod2[2])[i].equals(pn)) {
                            ((String[]) prod2[2])[i] = pnNew;
                        }
                    }
                    for (int i = 0; i < ((String[]) prod2[3]).length; i++) {
                        if (((String[]) prod2[3])[i].equals(pn)) {
                            ((String[]) prod2[3])[i] = pnNew;
                        }
                    }
                }
            }

        });

        trans("gen", "definitionSec").by(new TreeTransformerAdapter<SupportCodeGenerator>(){

            @Override
            public SupportCodeGenerator transform(Tree<ASTValueData> node, SupportCodeGenerator scg) {
                for(Tree<ASTValueData> s : getGroup(node, AssignmentLine)) {
                    Tree<ASTValueData> sAssignment = first(getGroup(s, Assignment));
                    String[] ret = mapText(sAssignment);
                    String vn = (String)ret[0];
                    String vt = (String)ret[2];
                    scg.natTypeMap.put(vn, vt);
                }
                return scg;
            }

        });
        trans("gen", "typeSec").by(new TreeTransformerAdapter<SupportCodeGenerator>(){

            @Override
            public SupportCodeGenerator transform(Tree<ASTValueData> node, SupportCodeGenerator scg) {
                for(Tree<ASTValueData> s : getGroup(node, ProductionLine)) {
                    Tree<ASTValueData> sProduction = first(getGroup(s, Production));
                    Object[] ret = parseProduciton(sProduction);
                    String nt = (String)ret[0];
                    String t = (String)ret[1];
                    // For types, there is no parameter.
                    String names[] = (String[])ret[3];
                    List<Tree<ASTValueData>> ss2 = getGroup(s, NAME);
                    if(!ss2.isEmpty()) {
                        String anno = text(first(ss2));
                        if(anno.equals("var")) {
                            scg.defTypeVar(nt, t);
                            tvarList.add(t);
                        } else if (anno.equals("nat")) {
                            scg.defTypeNat(nt, t);
                        } else if (anno.equals("fun")) {
                            scg.defTypeFun(nt, t, names);
                            
                        } else {
                            Utils.error(this, "Unsupported annotation for "+t+ " : "+anno);
                        }
                    } else {
                        scg.defType(nt, t, names);
                    }
                }
                defAllNatTypes(scg);
                return scg;
            }



        });
        trans("gen", "environmentSec").by(new TreeTransformerAdapter<SupportCodeGenerator>(){

            @Override
            public SupportCodeGenerator transform(Tree<ASTValueData> node, SupportCodeGenerator scg) {
                for(Tree<ASTValueData> s : getGroup(node, ProductionLine)) {
                    Tree<ASTValueData> sProduction = first(getGroup(s, Production));
                    Object[] ret = parseProduciton(sProduction);
                    String nt = (String)ret[0];
                    String t = (String)ret[1];
                    String names[] = (String[])ret[3];
                    
                    List<Tree<ASTValueData>> ss2 = getGroup(s, NAME);
                    if(!ss2.isEmpty()) {
                        String anno = text(first(ss2));
                        if(anno.equals("var")) {
                            scg.defEnvVar(nt, t);
                            evarList.add(t);
                        } else {
                            Utils.error(this, "Unsupported annotation for "+t+ " : "+anno);
                        }
                    } else {
                        scg.defEnv(nt, t, names);
                    }
                }
                return scg;
            }


        });

        trans("gen", "typingSec").by(new TreeTransformerAdapter<SupportCodeGenerator>(){

            @Override
            public SupportCodeGenerator transform(Tree<ASTValueData> node, SupportCodeGenerator scg) {
                addDefaultTVarAndEVarIfNotExist(scg);
                Tree<ASTValueData> j = first(getGroup(node, Judgment));
                String[] ret = parseJudgment(j);
                scg.setStartTypeAndEnv(ret[2], ret[0]);

                for(Tree<ASTValueData> s : getGroup(node, TypingRule)) {
                    Tree<ASTValueData> sPostcedent = first(getGroup(s, Postcedent));
                    ret = parsePostcedent(sPostcedent);
                    Tree<ASTValueData> sProduction = first(getGroup(sPostcedent, Production));
                    String envStr = ret[0];
                    String typeStr = ret[2];
                    Object[] ret2 = parseProduciton(sProduction);
                    String nt = (String) ret2[0];
                    String t = (String) ret2[1];
                    Object[] prod = prodTable.get(new Pair(nt, t));
                    // check that ret2 == prod
                    String[] p = (String[]) ret2[2];
                    String[] np = (String[]) ret2[3];
                    String[] antes = new String[(p.length+np.length)*3];
                    int i = 0;
                    final List<Tree<ASTValueData>> anteNodes = getGroup(first(getGroup(s, Antecedent)), Judgment);
                    for(Tree<ASTValueData> ss : anteNodes) {
                        ret = parseJudgment(ss);
                        
                        while(true){
                            String ntname = p.length>i/3 ? p[i/3] :np[i/3-p.length];
                            if(ntname.equals(ret[1])) {
                                break;
                            } else {
                                //default typing for implicit nonterminals
                                antes[i++] = first(evarList); // use the first evar as the default evar
                                antes[i++] = ntname;
                                antes[i++] = first(tvarList); // use the first tvar as the default tvar
                            }
                        }
                        
                        antes[i++] = ret[0];
                        antes[i++] = ret[1];
                        antes[i++] = ret[2];
                    }
                    while(i<antes.length){
                            String ntname = p.length>i/3 ? p[i/3] :np[i/3-p.length];
                                //default typing for implicit nonterminals
                                antes[i++] = first(evarList); // use the first evar as the default evar
                                antes[i++] = ntname;
                                antes[i++] = first(tvarList); // use the first tvar as the default tvar
                        }
                    Object[] tr = new Object[]{nt, p.length, envStr, typeStr, t, antes};
                    trTable.put(new Pair(nt, t), tr);
                }
                defaultTyping(scg);
                addTypingRulesToSCG(scg);
                scg.typing = true;
                return scg;
            }

        });

        trans("gen", "dynamicSec").by(new TreeTransformerAdapter<SupportCodeGenerator>(){

            @Override
            public SupportCodeGenerator transform(Tree<ASTValueData> node2, SupportCodeGenerator scg) {
                if(scg.typing == false) {
                    // if no typing add default typing
                    addDefaultTVarAndEVarIfNotExist(scg);
                    scg.setStartTypeAndEnv(first(tvarList), first(evarList));
                    defaultTyping(scg);
                    defAllNatTypes(scg);
                    addTypingRulesToSCG(scg);
                }
                final List<Tree<ASTValueData>> group = getGroup(node2, NAME);
                if(group.size() == 1) {
                    String eval = text(first(group));
                    scg.setDefaultEvaluator(eval);
                }
                List<Tree<ASTValueData>> nodeEvals = getGroup(node2, "evaluator");
                for(Tree<ASTValueData> node : nodeEvals) {
                Tree<ASTValueData> eval = first(getGroup(node, Assignment));
                String[] ret = mapText(eval);
                String[] ret3;
                Variable[] state = new Variable[getGroup(node, Assignment).size()-1];
                for(int i =0;i<state.length;i++){
                    Tree<ASTValueData> stateAST = tail(getGroup(node, Assignment)).get(i);
                    ret3 = mapText(stateAST);
                    state[i] = new Variable(Generator.cons(ret3[2]), ret3[0]);
                }
                scg.defEvaluator(ret[0], ret[2], state);

                for(Tree<ASTValueData> s : getGroup(node, EvaluationRule)) {
                    Tree<ASTValueData> sProduction = first(getGroup(s, Production));
                    Object[] ret2 = parseProduciton(sProduction);
                    String nt = (String)ret2[0];
                    String t = (String) ret2[1];
                    String[] p = (String[]) ret2[2];
                    String[] np = (String[]) ret2[3];
                    String[] vars = new String[p.length+np.length +1];
                    vars[0] = "__this__";
                    System.arraycopy(p, 0, vars, 1, p.length);
                    System.arraycopy(np, 0, vars, 1+p.length, np.length);
                    String blob = text(first(getGroup(s, JAVACODEBLOCK)));
                    scg.defEvaluatorComponent(ret[0], nt, t, vars, blob);
                }
                }
                return scg;
            }

        });



    }

    public Object[] parseProduciton(Tree<ASTValueData> sProduction) {
        final List<Tree<ASTValueData>> group = getGroup(sProduction, Term);
        List<Tree<ASTValueData>> ssp = group.size() == 1 ? null : getGroup(first(group), NAME); // For types, there is no parameter.
        List<Tree<ASTValueData>> ssnp = getGroup(last(getGroup(sProduction, Term)), NAME); // For types, there is no parameter.
        // For types, there is no parameter.
        Object[] ret = new Object[4];
        // Object[0] -> Object[1] ( Object[2] ) Object[3]
        ret[0] = text(first(getGroup(sProduction, NAME)));
        ret[1] = mapKeyword(text(first(tail(getGroup(sProduction, NAME)))));
        String[] p;
        String[] np;
        int i = 0;
        if (ssp != null) {
            ret[2] = p = new String[ssp.size()];

            for (Tree<ASTValueData> sss : ssp) {
                p[i++] = trans(text(sss));
            }
        } else {
            ret[2] = new String[]{};
        }
        ret[3] = np = new String[ssnp.size()];
        i = 0;
        for (Tree<ASTValueData> sss : ssnp) {
            np[i++] = trans(text(sss));
        }
        return ret;
    }

    public String[] parseJudgment(Tree<ASTValueData> sProduction) {
        String[] ret = new String[3];
        final List<Tree<ASTValueData>> group = getGroup(sProduction, Term);
        if(group.size() == 2) {
            ret[0] = concat(first(group), " ");
        } else {
            ret[0] = first(evarList);
        }
        ret[1] = trans(text(first(getGroup(sProduction, NAME))));
        ret[2] = concat(last(getGroup(sProduction, Term)), " ");
        return ret;
    }

    public  String[] parsePostcedent(Tree<ASTValueData> sProduction) {
        String[] ret = new String[3];
            final List<Tree<ASTValueData>> group = getGroup(sProduction, Term);
        if(group.size() == 2) {
            ret[0] = concat(first(group), " ");
        } else {
            ret[0] = first(evarList);
        }
        ret[2] = concat(last(group), " ");
        return ret;
    }
    public static String concat(Tree<ASTValueData> node , String sep) {
        String con = "";
        for(Tree<ASTValueData> s : node.subtrees ) {
            con += sep+text(s);
        }
        return con.substring(sep.length());
    }
    public static String[] mapText(Tree<ASTValueData> node) {
        String[] array = new String[node.subtrees.length];
        int i =0;
        for(Tree<ASTValueData> s : node.subtrees ) {
            array[i++] = text(s);
        }
        return array;
    }
    private void addDefaultTVarAndEVarIfNotExist(SupportCodeGenerator scg) {
        // add default type and environemtn variable
        if (tvarList.isEmpty()) {
            tvarList.add("__t");
            if(scg.T.startSymbol==null) {
                scg.defTypeVar("__tS", "__t");
            } else {
                scg.defTypeVar(scg.T.startSymbol, "__t");
            }
        }
        if (evarList.isEmpty()) {
            evarList.add("__E");
            if(scg.E.startSymbol == null) {
                scg.defEnvVar("env", "__E");
            } else {
                scg.defEnvVar("__ES", "__E");
            }
        }
    }
private void defAllNatTypes(SupportCodeGenerator scg) {
                // define all assigned nat type
                String start = scg.T.startSymbol;
                for (String t : scg.natTypeMap.values()) {
                    scg.defTypeNat(start, t);
                }
            }
    private void defaultTyping(SupportCodeGenerator scg) {
        for (Pair<String, String> prodKey : prodTable.keySet()) {
            if (!trTable.containsKey(prodKey)) {
                //default typing for implicit nonterminals and undefined rules
                Object[] prod = prodTable.get(prodKey);
                String[] p = (String[]) prod[2];
                String[] np = (String[]) prod[3];
                String[] antes = new String[(p.length + np.length) * 3];
                int i = 0;
                for (String pnt : p) {
                    antes[i++] = first(evarList);
                    antes[i++] = pnt;
                    if (scg.natTypeMap.containsKey(pnt)) {
                        // if nt type has been assigned
                        antes[i++] = scg.natTypeMap.get(pnt);
                    } else {
                        antes[i++] = first(tvarList);
                    }
                }
                for (String npnt : np) {
                    antes[i++] = first(evarList);
                    antes[i++] = npnt;
                    antes[i++] = first(tvarList);
                }
                Object[] tr = new Object[]{prodKey.fst, p.length, first(evarList), first(tvarList), prodKey.snd, antes};
                trTable.put(prodKey, tr);
            }
        }
    }
            private void addTypingRulesToSCG(SupportCodeGenerator scg) {
                // add typing rules
                for (Object[] tr : trTable.values()) {
                    scg.defTypingRule((String) tr[0], (Integer) tr[1], (String) tr[2], (String) tr[3], (String) tr[4], (String[]) tr[5]);
                }
            }


    public static void main(String[] args) throws FileNotFoundException, IOException {
        Utils.suppressWarning = true;
        ESL cg = new ESL();
        CharStream cs = null;
        String path = null, pack = null;
        if (args.length >= 3) {
            path = args[0];
            pack = args[1];
            for(int i =2;i<args.length-1;i++) {
                pack += ";\nimport "+args[i];
            }
            cs = new FileStream(new File(args[args.length-1]));
        } else {
            System.out.println("Usage: java -jar EriLex.jar <path> <package> <imports> <specification>");
            System.exit(-1);
        }
        System.out.println("Parsing...");
        CharStream.Label start = cs.mark();
        if(cg.parse(Spec, cs)) {
            if(cs.next() != -1) Utils.error(cg,"Not fully parsed!");
        } else {
            Utils.printError(cs);
            //printTree(((TreeBuildingHandler) cg.handler).getAST(),"","|");
            System.exit(-1);
        }
        //printTree(((TreeBuildingHandler) cg.handler).getAST(),"","|");
        final Tree<ASTValueData> ast = ((TreeBuildingHandler) cg.handler).getAST();
        System.out.println("Procesing...");
        final SupportCodeGenerator scg = new SupportCodeGenerator();
        scg.setPack(pack);
        scg.setPath(path);
        cg.semantics.trans("gen", ast, scg);
        boolean verbose = false;
         if(verbose) {   scg.G.dumpRuleMap();
            System.out.println("Preprocesing complete.");
            System.out.println("Grammar:");
            scg.G.dumpRuleMap();
            System.out.println("Types:");
            scg.T.dumpRuleMap();
            System.out.println("Environment:");
            scg.E.dumpRuleMap();

            System.out.println("Typing Rules:");
            List<TypingRule> rs = new LanguageGenerator(scg.t, scg.d, scg.e, scg.E, null, scg.T, null, scg.G, null, null).toTypingRules(scg.G);
            for (TypingRule r : rs) {
                System.out.println(r);
                System.out.println();
            }
         }
    
        System.out.println("Generating code...");
        scg.generate();
        System.out.println("Code generation complete.");
    }

}
