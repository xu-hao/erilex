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
public class RuleBuilder implements Builder<Object> {

    private final String name;
    private  String env;
    private  String type;
    private final boolean aux;
    private final boolean lex;
    private final Grammar grammar;
    private ExpressionBuilder<RuleBuilder, Object> exp;
    private int arity;
    private Type envType;
    private Type typeType;

    public RuleBuilder(String nameParam, Grammar g) {
        name = nameParam;
        aux = false;
        lex = false;
        grammar = g;
    }

    RuleBuilder(String nameParam, boolean auxParam, boolean lexParam, Grammar g) {
        name = nameParam;
        aux = auxParam;
        lex = lexParam;
        grammar = g;
    }

    public ExpressionBuilder<RuleBuilder, Object> start() {
        return exp = new ExpressionBuilder(this, grammar);
    }
    public ExpressionBuilder<RuleBuilder, Object> start(int p) {
        this.arity = p;
        return exp = new ExpressionBuilder(this, grammar);
    }
    public ExpressionBuilder<RuleBuilder, Object> start(int p, String env, String type) {
        this.arity = p;
        this.env = env;
        this.type = type;
        return exp = new ExpressionBuilder(this, grammar);

    }
    public ExpressionBuilder<RuleBuilder, Object> start(int p, Type env, Type type) {
        this.arity = p;
        this.envType = env;
        this.typeType = type;
        return exp = new ExpressionBuilder(this, grammar);

    }
    public Production build() {
        return null;
    }

    @Override
    public Object exp(Production p) {
        //System.out.println("optimizing: "+p);
        p = p.optimize();
        //System.out.println("result:     "+p);
        if(grammar.startSymbol == null && !lex && !aux) {
            grammar.startSymbol = name;
            Utils.warning(p, "No start symbol specified. Start symbol is automatically set to " + name+".");
        }

        ((AbstractProduction)p).arity = this.arity;
        ((AbstractProduction)p).env = this.env;
        ((AbstractProduction)p).type = this.type;
        ((AbstractProduction)p).envType = this.envType;
        ((AbstractProduction)p).typeType = this.typeType;
        if(grammar.ruleMap.containsKey(name)) {
            Production p0 = grammar.ruleMap.get(name);
            if(p0.getProdName()!=null) {
                if(aux) {
                    Utils.warning(p, name+" is changed to an auxiliary rule.");
                    p0.setProdName(null);
                    p = new Or(p0, p, null, grammar);
                } else {
                    p0.setProdName(null);
                    p = new Or(p0, p, null, grammar);
                    p.setProdName(name);
                }
                if(p0.isLexer() != lex) {
                    p0.setLexer(false);
                    Utils.warning(p, name+" is changed to a "+(lex?"lexer":"parser")+" rule.");
                }
                p.setLexer(lex);
            } else {
                // was aux rule
                if(!aux) {
                    Utils.warning(p, name+" is changed to a nonauxiliary rule.");
                    p = new Or(p0, p, null, grammar);
                    p.setProdName(name);
                } else {
                    p = new Or(p0, p, null, grammar);
                }
                if(p0.isLexer() != lex) {
                    p0.setLexer(false);
                    Utils.warning(p, name+" is changed to a "+(lex?"lexer":"parser")+" rule.");
                }
                p.setLexer(lex);
            }
            grammar.ruleMap.put(name, p);
        } else {
            if (!aux) {
                p.setProdName(name);
            }
            p.setLexer(lex);
            grammar.ruleMap.put(name, p);
        }
        return null;
    }
}
