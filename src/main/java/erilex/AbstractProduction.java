/*
Copyright 2009, 2010 Hao Xu
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
package erilex;

import erilex.codegen.Type;
import erilex.data.CharStream;
import erilex.data.CharStream.Label;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ertri
 */
public abstract class AbstractProduction implements Production {

    public Production optimize() {
        return this;
    }
    public Type envType;
    public Type typeType;

    public String name;
    public String prodName;
    public Handler handler;
    public boolean lexer = false;
    public Set<Integer> first = null;
    public Grammar grammar;
    public String env, type;
    public int arity;

    public Set<String> keywords = new HashSet<String>();
    public static boolean DEBUG = false;

    public AbstractProduction(String name, Grammar grammar) {
        this.name = name;
        this.handler = grammar.handler;
        this.grammar = grammar;
    }
    public boolean isLexer() {
        return lexer;
    }

    public void setLexer(boolean l) {
        lexer = l;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractProduction other = (AbstractProduction) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public void setProdName(String name) {
        this.prodName = name;
    }

    public String getProdName() {
        return prodName;
    }

    public final boolean match(CharStream cs, boolean ignore, boolean tentative) {
        handler.start(this, cs);
        Label labelStart = cs.mark();
        Label labelFinish;
        boolean ret;
        ret = doMatch(cs, ignore, tentative);
        if (ret) {
            labelFinish = cs.mark();
            // todo fix this, this does not work with AST builder.
            //final String string = cs.getString(labelStart, labelFinish);
            //if(!keywords.isEmpty())System.out.println(string);
//            if(keywords.contains(string))
//                handler.failed(this, cs);
//                if (!tentative) {
//                    String msg = "";
//                    final int[] la = new int[16];
//                    for(int i = 0;i<la.length;i++) {
//                        la[i] = cs.lookahead(i);
//                        msg += "\'" + (char) la[i] + "\'(" + la[i] + ")";
//                    }
//                    Utils.warning(this, "Matching failed at " + cs.position() + ". Expected " + this + ", encountered "+msg);
//                }
//            else {
                handler.handle(this, cs, labelStart, labelFinish);
//            }
            cs.unmark(labelFinish);
        } else {
            handler.failed(this, cs);
            if (!tentative && DEBUG) {
                String msg = "";
                final int[] la = new int[16];
                for(int i = 0;i<la.length;i++) {
                    la[i] = cs.lookahead(i);
                    msg += "\'" + (char) la[i] + "\'(" + la[i] + ")";
                }
                Utils.warning(this, "Matching failed at " + cs.position() + ". Expected " + this + ", encountered "+msg);
            }
        }
        cs.unmark(labelStart);
        return ret;

    }

    public void setFirst(java.util.Set<Object> visited) {
        first = computeFirst(visited);
    }

    public Set<Integer> getFirst() {
        if (first == null) {
            final HashSet<Object> hashSet = new HashSet<Object>();
            setFirst(hashSet);
        }
        return first;
    }
    public Set<Integer> getFirst(java.util.Set<Object> visited) {
        if (first == null) {
            //if(visited.contains(this))
            //    return new ReverseSet<Integer>(new HashSet<Integer>(), false);
            //visited.add(this);
            setFirst(visited);
        }
        return first;
    }

    public Production duplicate(Grammar g) {
        AbstractProduction dd = (AbstractProduction) doDuplicate(g);
        dd.prodName = prodName;
        dd.name = name;
        dd.grammar = g;
        dd.handler = g.handler;
        dd.first = null;
        dd.lexer = lexer;
        dd.env = env;
        dd.arity = arity;
        dd.envType = envType;
        dd.type = type;
        dd.typeType = typeType;

        return dd;
    }
    public abstract Production doDuplicate(Grammar g);
    public abstract boolean doMatch(CharStream cs, boolean ignore, boolean tentative);

    public abstract Set<Integer> computeFirst(java.util.Set<Object> visited);
}
