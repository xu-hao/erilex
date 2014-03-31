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
import erilex.data.FileStream;
import erilex.tree.TreeBuildingHandler;
import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author ertri
 */
public class JavaParser extends erilex.builder.LanguageBuilder {
    public static final String BLOCK = "Block";
    public static final String CLASSDEF = "ClassDef";
    public static final String COMMENTS = "Comments";
    public static final String PROG = "Prog";
    public static final String CATEGORY = "Category";
    public static final String EXTENDSCLAUSE = "ExtendsClause";
    public static final String IDENTIFIER = "Identifier";
    public static final String IMPLEMENTSCLAUSE = "ImplementsClause";
    public static final String LETTERUNDERSCORE = "LetterUnderscore";
    public static final String LETTERUNDERSCOREDIGIT = "LetterUnderscoreDigit";
    public static final String MEMBER = "Member";
    public static final String PARAMETER = "Parameter";
    public static final String PARAMETERLIST = "ParameterList";
    public static final String SCOPEMODIFIER = "ScopeModifier";
    public static final String TYPE = "Type";
    public static final String WHITESPACE = "Whitespace";
    public JavaParser() {
        this.handler = new TreeBuildingHandler();
        def(PROG).start().nt(CLASSDEF).star().end();
        def(CLASSDEF).start()
                .t("public").nt(CATEGORY).nt(TYPE)
                .nt(EXTENDSCLAUSE).optional().nt(IMPLEMENTSCLAUSE).optional()
                .t("{").nt(MEMBER).star().t("}").end();
        def(CATEGORY).start()
                .t("interface").or().t("class").end();
        def(EXTENDSCLAUSE).start()
                .t("extends").nt(TYPE).start().t(",").nt(TYPE).end().star().end();
        def(IMPLEMENTSCLAUSE).start()
                .t("implements").nt(TYPE).start().t(",").nt(TYPE).end().star().end();
        def(TYPE).start()
                .nt(IDENTIFIER)
                .start().t("<").nt(TYPE).start().t(",").nt(TYPE).end().star().t(">").end().optional().end();
        def(MEMBER).start()
                .nt(SCOPEMODIFIER).nt(TYPE).nt(IDENTIFIER)
                .start().t("(").nt(PARAMETERLIST).t(")").nt(BLOCK).or().t(";").end().end();
        def(PARAMETERLIST).start()
                .nt(PARAMETER).start().t(",").nt(PARAMETER).end().star().end();
        def(PARAMETER).start()
                .nt(TYPE).nt(IDENTIFIER).end();
        def(BLOCK).start()
                .t("{")
                .start().noneOf('{','}').or().nt(BLOCK).end().star()
                .t("}").end();
        def(SCOPEMODIFIER).start()
                .t("public").or().t("private").end();
        lex(IDENTIFIER).start()
                .nt(LETTERUNDERSCORE).nt(LETTERUNDERSCOREDIGIT).star().end();
        auxlex(LETTERUNDERSCORE).start()
                .range('a', 'z').or().range('A', 'Z').or().oneOf('_').end();
        auxlex(LETTERUNDERSCOREDIGIT).start()
                .nt(LETTERUNDERSCORE).or().range('0','9').end();
        ignore().start().nt(WHITESPACE).or().nt(COMMENTS).end();

        auxdef(WHITESPACE).start().t(" ").or().t("\t").or().t("\n").or().t("\r").end();
        def(COMMENTS).start().t("//").noneOf('\n').star().end();
        def(COMMENTS).start().t("/*").start().noneOf('*').or().oneOf('*').noneOf('/').end().star().t("*/").end();

        dumpRuleMap();
        for(String s:ruleMap.keySet()) {
            if(!isLL1(s)) {
                Utils.warning(ruleMap.get(s), "This is not an LL(1) grammar.");
            }
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        JavaParser jp = new JavaParser();
        jp.parse(PROG, new FileStream(new File("Source.java")));
//        jp.parse(PROG, new CharArray("public class A {}".toCharArray()));
        System.out.println(((TreeBuildingHandler)jp.handler).getAST());
    }

}
