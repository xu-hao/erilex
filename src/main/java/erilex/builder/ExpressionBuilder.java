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
package erilex.builder;

import erilex.*;

/**
 *
 * @author ertri
 */
public class ExpressionBuilder<T extends Builder<N>, N> implements Builder<ExtendedExpressionBuilder<T, N>> {

    private T pb;
    private final String name;
    private Production production;
    private final Grammar grammar;

    public ExpressionBuilder(T pb, Grammar grammar) {
        this(pb, null, grammar);
    }

    public ExpressionBuilder(T pb, String name, Grammar grammar) {
        this.pb = pb;
        this.name = name;
        this.grammar = grammar;
    }

    public Production build() {
        return production;
    }

    public ExpressionBuilder<ExpressionBuilder<T, N>, ExtendedExpressionBuilder<T, N>> start() {
        return new ExpressionBuilder<ExpressionBuilder<T, N>, ExtendedExpressionBuilder<T, N>>(this, null, grammar);
    }

    public ExpressionBuilder<ExpressionBuilder<T, N>, ExtendedExpressionBuilder<T, N>> start(String name) {
        return new ExpressionBuilder<ExpressionBuilder<T, N>, ExtendedExpressionBuilder<T, N>>(this, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> exp(Production p) {
        production = p;
        return new ExtendedExpressionBuilder<T, N>(pb, production, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> nt(String refName) {
        production = new NonTerminal(refName, null, grammar);
        return new ExtendedExpressionBuilder<T, N>(pb, production, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> nt(String refName, String group) {
        production = new NonTerminal(refName, group, null, grammar);
        return new ExtendedExpressionBuilder<T, N>(pb, production, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> nt(String refName, String env, String type) {
        production = new NonTerminal(refName, null, grammar);
        ((NonTerminal) production).env = env;
        ((NonTerminal) production).type = type;
        return new ExtendedExpressionBuilder<T, N>(pb, production, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> nt(String refName, String group, String env, String type) {
        production = new NonTerminal(refName, group, null, grammar);
        ((NonTerminal) production).env = env;
        ((NonTerminal) production).type = type;
        return new ExtendedExpressionBuilder<T, N>(pb, production, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> t(String text) {
        production = new Terminal(text, grammar);
        return new ExtendedExpressionBuilder<T, N>(pb, production, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> at(char text) {
        return oneOf(text);
    }
    public ExtendedExpressionBuilder<T, N> range(char a, char b) {
        production = new Range(a, b, null, grammar);
        return new ExtendedExpressionBuilder<T, N>(pb, production, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> oneOf(char... a) {
        production = new FSet(a, true, null, grammar);
        return new ExtendedExpressionBuilder<T, N>(pb, production, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> noneOf(char... a) {
        production = new FSet(a, false, null, grammar);
        return new ExtendedExpressionBuilder<T, N>(pb, production, name, grammar);
    }
}
