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
package erilex.tree;

import erilex.data.generic.Tree;
import erilex.Handler;
import erilex.NonTerminal;
import erilex.Production;
import erilex.Utils;
import erilex.data.CharStream;
import erilex.data.CharStream.Label;
import java.util.Stack;

/**
 *
 * @author ertri
 */
public class TreeBuildingHandler implements Handler {

    Stack<Stack<Tree<ASTValueData>>> stack = new Stack();
    Stack<Tree<ASTValueData>> top = new Stack<Tree<ASTValueData>>();
    Tree<ASTValueData> ast;
    public boolean longText = false;

    public void handle(Production production, CharStream cs, Label start, Label finish) {
        ASTValueData astValue;
        if (production.getName() != null) {
            if (top.isEmpty() || longText) {
                String text = cs.getString(start, finish);
                astValue = new ASTValueData(production, start.getPosition(), finish.getPosition(), production.getName(), text);
            } else {
                astValue = new ASTValueData(production, start.getPosition(), finish.getPosition(), production.getName());
            }
            ast = new Tree<ASTValueData>(astValue, (Tree<ASTValueData>[]) top.toArray(new Tree[0]));

            if (!stack.isEmpty()) {
                top = stack.pop();
                top.push(ast);
            }
        }

        if (production instanceof NonTerminal) {
            NonTerminal nt = (NonTerminal) production;
            if (nt.grammar.ruleMap.get(nt.refName).getProdName() != null) {
                String group;
                if (nt.group != null) {
                    group = nt.group;
                    top.peek().val.group = group;
                }

            } else if (nt.group != null) {
                Utils.error(production, "Grouping auxiliary nodes: " + production);
            }

        }
        if (production.getProdName() != null) {
            if (top.isEmpty() || longText) {
                String text = cs.getString(start, finish);
                astValue = new ASTValueData(production, start.getPosition(), finish.getPosition(), production.getProdName(), text);
            } else {
                astValue = new ASTValueData(production, start.getPosition(), finish.getPosition(), production.getProdName());
            }
            ast = new Tree<ASTValueData>(astValue, (Tree<ASTValueData>[]) top.toArray(new Tree[0]));

            if (!stack.isEmpty()) {
                top = stack.pop();
                top.push(ast);
            }
        }
        if (production.getName() == null && production.getProdName() == null) {
            if (!stack.isEmpty()) {
                stack.peek().addAll(top);
                top = stack.pop();
            }

        }
//        System.out.println(stack+","+top+" at s "+production);
    }

    public void start(Production production, CharStream cs) {
        stack.push(top);
        top = new Stack<Tree<ASTValueData>>();
//        System.out.println(stack+","+top+" at e "+production);
    }

    public void failed(Production production, CharStream cs) {
        top = stack.pop();
//        System.out.println(stack+","+top+" at f "+production);
    }

    public Tree<ASTValueData> getAST() {
        return ast;
    }

    public Tree<ASTValueData> getASTandReset() {
        Tree<ASTValueData> ast1 = ast;
        reset();
        return ast1;
    }

    public void reset() {
        this.ast = null;
        this.stack.clear();
        this.top.clear();

    }
}
