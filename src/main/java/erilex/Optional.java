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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ertri
 */
public class Optional extends AbstractProduction {

    @Override
    public Production optimize() {
        final Optional optional = new Optional(a.optimize(), name, grammar);
        optional.prodName = prodName;
        return optional;
    }

    Production a;

    public Optional(Production a, String name, Grammar grammar) {
        super(name, grammar);
        this.a = a;
    }

    public boolean doMatch(CharStream cs, boolean ignore, boolean tent) {
        a.match(cs, !lexer && ignore, true);
        return true;

    }

    @Override
    public String toString() {
        String str = a.toString();
        if (str.startsWith("(")) {
            return str + "?";
        } else {
            return "(" + str + ")" + "?";
        }
    }

    public Set<Integer> computeFirst(java.util.Set<Object> visited) {
        ReverseSet<Integer> set = new ReverseSet<Integer>((ReverseSet<Integer>) a.getFirst(visited));
        set.add(-1);
        return set;
    }

    public MaybeBoolean isLL1() {
        return MaybeBoolean.unsure;
    }

    public Production doDuplicate(Grammar g) {
        return new Optional(a, name, g);
    }
}
