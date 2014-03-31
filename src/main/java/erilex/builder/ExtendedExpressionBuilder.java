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
public class ExtendedExpressionBuilder<T extends Builder<N>, N> implements Builder<ExtendedExpressionBuilder<T, N>> {

    private T pb;
    private Production production;
    public boolean openOr;
    private final String name;
    private final Grammar grammar;

    public ExtendedExpressionBuilder(T pb, Production production, String name, Grammar grammar) {
        this.pb = pb;
        this.production = production;
        this.openOr = false;
        this.name = name;
        this.grammar = grammar;
    }

    public ExtendedExpressionBuilder(T pb, Production production, boolean open, String name, Grammar grammar) {
        this.pb = pb;
        this.production = production;
        this.openOr = open;
        this.name = name;
        this.grammar = grammar;
    }

    public Production build() {
        return production;
    }

    public ExpressionBuilder<PartialExpressionBuilder<T, N>, ExtendedExpressionBuilder<T, N>> start() {
        return seq().start();
    }

    public ExpressionBuilder<PartialExpressionBuilder<T, N>, ExtendedExpressionBuilder<T, N>> start(String name) {
        return seq().start(name);
    }

    public N end() {
        production.setProdName(name);
        return pb.exp(production);
    }

    public ExtendedExpressionBuilder<T, N> exp(Production p) {
        production = p;
        return this;
    }

    public PartialExpressionBuilder<T, N> or() {
        PartialApp partial;
        Or or;
        production = or = new Or(production, null, null, grammar);
        partial = new PartialApp(or);
        return new PartialExpressionBuilder<T, N>(pb, production, partial, name, grammar);
    }

    public PartialExpressionBuilder<T, N> seq() {
        PartialApp partial;
        Seq seq;
        if (openOr && production instanceof Or) {
            seq = new Seq(((Or) production).b, null, null, grammar);
            production = new Or(((Or) production).a, seq, null, grammar);
        } else {
            production = seq = new Seq(production, null, null, grammar);
        }
        partial = new PartialApp(seq);
        return new PartialExpressionBuilder<T, N>(pb, production, partial, name, grammar);
    }

    public ExtendedExpressionBuilder<T, N> star() {
        if (openOr) {
            if (production instanceof Seq) {
                Seq seq = (Seq) production;
                production = new Seq(seq.a, new Star(seq.b, null, grammar), null, grammar);
            } else if (production instanceof Or) {
                Or or = (Or) production;
                production = new Or(or.a, new Star(or.b, null, grammar), null, grammar);
            } else {
                production = new Star(production, null, grammar);
            }
        } else {
            production = new Star(production, null, grammar);
        }
        return this;
    }

    public ExtendedExpressionBuilder<T, N> plus() {
        if (openOr) {
            if (production instanceof Seq) {
                Seq seq = (Seq) production;
                production = new Seq(new Seq(seq.a, seq.b, null, grammar), new Star(seq.b, null, grammar), null, grammar);
            } else if (production instanceof Or) {
                Or or = (Or) production;
                production = new Or(or.a, new Seq(or.b, new Star(or.b, null, grammar), null, grammar), null, grammar);
            } else {
                production = new Seq(production, new Star(production, null, grammar), null, grammar);
            }
        } else {
            production = new Seq(production, new Star(production, null, grammar), null, grammar);
        }
        return this;
    }

    public ExtendedExpressionBuilder<T, N> optional() {
        if (openOr) {
            if (production instanceof Seq) {
                Seq seq = (Seq) production;
                production = new Seq(seq.a, new Optional(seq.b, null, grammar), null, grammar);
            } else if (production instanceof Or) {
                Or or = (Or) production;
                production = new Or(or.a, new Optional(or.b, null, grammar), null, grammar);
                //System.out.println(production);
            } else {
                production = new Optional(production, null, grammar);
            }
        } else {
            production = new Optional(production, null, grammar);
        }
        return this;
    }

    public ExtendedExpressionBuilder<T, N> t(String t) {
        return this.seq().t(t);
    }

    public ExtendedExpressionBuilder<T, N> range(char a, char b) {
        return this.seq().range(a, b);
    }

    public ExtendedExpressionBuilder<T, N> nt(String t) {
        return this.seq().nt(t);
    }

    public ExtendedExpressionBuilder<T, N> nt(String t, String group) {
        return this.seq().nt(t, group);
    }

    public ExtendedExpressionBuilder<T, N> nt(String t, String env, String type) {
        return this.seq().nt(t, env, type);
    }

    public ExtendedExpressionBuilder<T, N> nt(String t, Type env, Type type) {
        return this.seq().nt(t, env, type);
    }
    
    public ExtendedExpressionBuilder<T, N> nt(String t, String group, String env, String type) {
        return this.seq().nt(t, group, env, type);
    }

    public ExtendedExpressionBuilder<T, N> at(char a) {
        return this.seq().at(a);
    }
    public ExtendedExpressionBuilder<T, N> oneOf(char... a) {
        return this.seq().oneOf(a);
    }

    public ExtendedExpressionBuilder<T, N> noneOf(char... a) {
        return this.seq().noneOf(a);
    }
}
