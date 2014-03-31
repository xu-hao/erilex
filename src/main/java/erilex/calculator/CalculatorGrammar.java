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
package erilex.calculator;

import erilex.ChainHandler;
import erilex.Utils;
import erilex.builder.LanguageBuilder;
import erilex.data.generic.Tree;
import erilex.tree.ASTValueData;
import erilex.tree.TreeBuildingHandler;
import erilex.tree.TreeTransformerAdapter;
import java.math.BigInteger;
import java.util.List;
import java.util.Stack;
import static erilex.tree.TreeUtils.*;

public class CalculatorGrammar extends LanguageBuilder<Stack<BigInteger>> {

    CalculatorHandler calculatorHandler = new CalculatorHandler();
    TreeBuildingHandler treeBuildingHandler = new TreeBuildingHandler();

    public CalculatorGrammar() {
        handler = new ChainHandler(calculatorHandler, treeBuildingHandler);

        def("prog").start()
                .nt("expr").end();

        def("expr").start()
                .nt("term", "expr").start().nt("aop", "op").nt("term", "expr").end().star().end();

        def("term").start()
                .nt("factor", "expr").start().nt("mop", "op").nt("factor", "expr").end().star().end();

        def("factor").start()
                .start().nt("NUM").or().t("(").nt("expr").t(")").end().nt("unaryOp", "op").optional().end();

        lex("NUM").start()
                .nt("SIGN").optional().range('0', '9').plus().end();

        def("unaryOp").start().nt("factorial").end();
        def("aop").start().t("+").end();
        def("aop").start().t("-").end();
        def("mop").start().t("*").end();
        def("mop").start().t("/").end();
        def("mop").start().t("^").end();

        auxdef("factorial").start().t("!").end();
        auxdef("SIGN").start().t("+").or().t("-").end();

        ignore().start().nt("whitespace").end();

        def("whitespace").start().t(" ").or().t("\t").or().t("\n").or().t("\r").end();

        trans("default","NUM").by(new TreeTransformerAdapter<Stack<BigInteger>>() {

            public Stack<BigInteger> transformAfter(Tree<ASTValueData> node, Stack<BigInteger> state) {
                state.push(new BigInteger(text(node)));
                return state;
            }
        });
        trans("default","factor").by(new TreeTransformerAdapter<Stack<BigInteger>>() {

            public Stack<BigInteger> transformAfter(Tree<ASTValueData> node, Stack<BigInteger> state) {
                List<Tree<ASTValueData>> ops = getGroup(node, "op");
                if (ops.size() == 1) {
                    if (text(first(ops)).equals("!")) {
                        state.push(CalculatorHandler.factorial(state.pop()));
                    }
                }
                return state;
            }
        });
        trans("default","term").by(new TreeTransformerAdapter<Stack<BigInteger>>() {

            public Stack<BigInteger> transformAfter(Tree<ASTValueData> node, Stack<BigInteger> state) {
                List<Tree<ASTValueData>> ops = getGroup(node, "op");
                if (!ops.isEmpty()) {
                    BigInteger n2 = state.pop();
                    BigInteger n1 = state.pop();
                    Tree<ASTValueData> op = first(ops);
                    if (text(op).equals("*")) {
                        n1 = n1.multiply(n2);
                    } else if (text(op).equals("/")) {
                        n1 = n1.divide(n2);
                    } else if (text(op).equals("^")) {
                        n1 = n1.pow(n2.intValue());
                    }
                    state.push(n1);
                }
                return state;
            }
        });
        trans("default","expr").by(new TreeTransformerAdapter<Stack<BigInteger>>() {

            public Stack<BigInteger> transformAfter(Tree<ASTValueData> node, Stack<BigInteger> state) {
                List<Tree<ASTValueData>> ops = getGroup(node, "op");
                if (!ops.isEmpty()) {
                    BigInteger n2 = state.pop();
                    BigInteger n1 = state.pop();
                    Tree<ASTValueData> op = first(ops);
                    if (text(op).equals("+")) {
                        n1 = n1.add(n2);
                    } else if (text(op).equals("-")) {
                        n1 = n1.subtract(n2);
                    }
                    state.push(n1);
                }
                return state;
            }
        });
        trans("default","prog").by(new TreeTransformerAdapter<Stack<BigInteger>>() {

            public Stack<BigInteger> transformAfter(Tree<ASTValueData> node, Stack<BigInteger> state) {
                System.out.println(state.peek());
                return state;
            }
        });

        trans("tree", "term").by(new TreeTransformerAdapter() {

            public Object transformAfter(Tree node, Object state) {
                List<Tree<ASTValueData>> ops = getGroup(node, "op");
                List<Tree<ASTValueData>> factors = getGroup(node, "expr");
                if (ops.size() > 1) {
                    return transformAfter(replaceSubtrees(node, 0, 3, newNode("expr", first(factors), first(ops), first(tail(factors)))), state);
                } else {
                    return state;
                }
            }
        });
        trans("tree", "expr").by(new TreeTransformerAdapter() {

            public Object transformAfter(Tree node, Object state) {
                List<Tree<ASTValueData>> ops = getGroup(node, "op");
                List<Tree<ASTValueData>> terms = getGroup(node, "expr");
                if (ops.size() > 1) {
                    return transformAfter(replaceSubtrees(node, 0, 3, newNode("expr", first(terms), first(ops), first(tail(terms)))), state);
                } else {
                    return state;
                }
            }
        });

//        trans("treeDecl", "term").by()
//                .let("ops").group("op")
//                .ifx().compare("<=").size().var("ops").con(1).then()
//                    .state()
//                .elsex()
//                    .let("terms").group("factor")
//                    .rec()
//                        .replace(0, 3).startList()
//                            .node("expr").startList()
//                                .first().var("terms")
//                                .first().var("ops")
//                                .first().tail().var("terms")
//                            .endList()
//                        .endList()
//                        .state()
//                .end();
//
//        trans("treeDecl", "expr").by()
//                .let("ops").group("op")
//                .ifx().compare("<=").size().var("ops").con(1).then()
//                    .state()
//                .elsex()
//                    .let("terms").group("term")
//                    .rec()
//                        .replace(0, 3).startList()
//                            .node("expr").startList()
//                                .first().var("terms")
//                                .first().var("ops")
//                                .first().tail().var("terms")
//                            .endList()
//                        .endList()
//                        .state()
//                .end();


        dumpRuleMap();

        for (String s : ruleMap.keySet()) {
            if (!isLL1(s)) {
                Utils.warning(ruleMap.get(s), "This is not an LL(1) grammar.");
            }
        }
    }
}
