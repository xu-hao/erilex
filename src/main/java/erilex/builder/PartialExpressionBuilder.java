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
import erilex.codegen.Type;

/**
 *
 * @author ertri
 */
public class PartialExpressionBuilder<T extends Builder<N>, N> implements Builder<ExtendedExpressionBuilder<T, N>> {

    private T pb;
    private Production production;
    private final PartialApp partial;
    private final String name;
    private final Grammar grammar;

    public PartialExpressionBuilder(T pb, Production production, PartialApp p, String name, Grammar grammar) {
        this.pb = pb;
        this.partial = p;
        this.production = production;
        this.name = name;
        this.grammar = grammar;
    }

    public Production build() {
        return production;
    }

    public ExpressionBuilder<PartialExpressionBuilder<T, N>, ExtendedExpressionBuilder<T, N>> start() {
        return new ExpressionBuilder<PartialExpressionBuilder<T, N>, ExtendedExpressionBuilder<T, N>>(this, null, grammar);
    }

    public ExpressionBuilder<PartialExpressionBuilder<T, N>, ExtendedExpressionBuilder<T, N>> start(String name) {
        return new ExpressionBuilder<PartialExpressionBuilder<T, N>, ExtendedExpressionBuilder<T, N>>(this, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> exp(Production p) {
        partial.app(p);
        return new ExtendedExpressionBuilder<T, N>(pb, production, true, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> nt(String refName) {
        partial.app(new NonTerminal(refName, null, grammar));
        return new ExtendedExpressionBuilder<T, N>(pb, production, true, name, grammar);
    }
    
    public ExtendedExpressionBuilder<T, N> nt(String refName, String group) {
        partial.app(new NonTerminal(refName, group, null, grammar));
        return new ExtendedExpressionBuilder<T, N>(pb, production, true, name, grammar);
    }
    public ExtendedExpressionBuilder<T, N> nt(String refName, String env, String type) {
        NonTerminal nonTerminal = new NonTerminal(refName, null, grammar);
        nonTerminal.env = env;
        nonTerminal.type = type;
        partial.app(nonTerminal);

        return new ExtendedExpressionBuilder<T, N>(pb, production, true, name, grammar);
    }
    public ExtendedExpressionBuilder<T, N> nt(String refName, Type env, Type type) {
        NonTerminal nonTerminal = new NonTerminal(refName, null, grammar);
        nonTerminal.envType = env;
        nonTerminal.typeType = type;
        partial.app(nonTerminal);

        return new ExtendedExpressionBuilder<T, N>(pb, production, true, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> nt(String refName, String group, String env, String type) {
        NonTerminal nonTerminal = new NonTerminal(refName, group, null, grammar);
        nonTerminal.env = env;
        nonTerminal.type = type;
        //.nat = env;
        partial.app(nonTerminal);
        return new ExtendedExpressionBuilder<T, N>(pb, production, true, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> t(String text) {
        partial.app(new Terminal(text, grammar));
        return new ExtendedExpressionBuilder<T, N>(pb, production, true, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> range(char a, char b) {
        partial.app(new Range(a, b, null, grammar));
        return new ExtendedExpressionBuilder<T, N>(pb, production, true, name, grammar);
    }
    public ExtendedExpressionBuilder<T, N> oneOf(char... a) {
        partial.app(new FSet(a, true, null, grammar));
        return new ExtendedExpressionBuilder<T, N>(pb, production, true, name, grammar);
    }
    public ExtendedExpressionBuilder<T, N> noneOf(char... a) {
        partial.app(new FSet(a, false, null, grammar));
        return new ExtendedExpressionBuilder<T, N>(pb, production, true, name, grammar);
    }
    public ExtendedExpressionBuilder<T, N> at(char a) {
        return oneOf(a);
    }
}
