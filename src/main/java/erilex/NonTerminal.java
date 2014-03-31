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
package erilex;

import erilex.data.CharStream;
import erilex.data.generic.MaybeBoolean;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ertri
 */
public class NonTerminal extends AbstractProduction {

    public String refName;
    private final Map<String, Production> ruleMap;
    public String group;


    public NonTerminal(String refName, Map<String, Production> ruleMap, String name, Grammar grammar) {
        super(name, grammar);
        this.refName = refName;
        this.ruleMap = ruleMap;
    }

    public NonTerminal(String refName, String name, Grammar grammar) {
        super(name, grammar);
        this.refName = refName;
        this.ruleMap = grammar.ruleMap;
    }

    public NonTerminal(String refName, String group, String name, Grammar grammar) {
        super(name, grammar);
        this.group = group;
        this.refName = refName;
        this.ruleMap = grammar.ruleMap;
    }

    public boolean doMatch(CharStream cs, boolean ignore, boolean tent) {
        Production p = ruleMap.get(refName);
        if (p == null) {
            Utils.warning(this, "Undefined symbol " + refName + " in the grammar.");
        }
        return p.match(cs, !lexer && ignore, tent);
    }

    @Override
    public String toString() {
        return refName+(group!=null?"::"+group:"");
    }

    public Set<Integer> computeFirst(java.util.Set<Object> visited) {
        Production p = ruleMap.get(refName);
        if (p == null) {
            Utils.warning(this, "Undefined symbol " + refName + " in the grammar.");
        }
        final Set<Integer> first1 = p.getFirst(visited);
        return first1;
    }

    public MaybeBoolean isLL1() {
        Production p = ruleMap.get(refName);
        if (p == null) {
            Utils.warning(this, "Undefined symbol " + refName + " in the grammar.");
        }
        return p.isLL1();
    }

    public Production doDuplicate(Grammar g) {
        return new NonTerminal(refName, name, g);
    }
}
