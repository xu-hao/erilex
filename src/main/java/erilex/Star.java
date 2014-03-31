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

import erilex.ReverseSet;
import erilex.data.CharStream;
import erilex.data.generic.MaybeBoolean;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ertri
 */
public class Star extends AbstractProduction {

    public Production a;

    @Override
    public Production optimize() {
        final Star star = new Star(a.optimize(), name, grammar);
        star.prodName = prodName;
        return star;
    }

    public Star(Production a, String name, Grammar grammar) {
        super(name, grammar);
        this.a = a;
    }

    public boolean doMatch(CharStream cs, boolean ignore, boolean tent) {
        CharStream.Label label2;
        label2 = cs.mark();
        while (true) {
            if (!a.match(cs, !lexer && ignore, true) || label2.getPosition() == cs.position()) {
                break;
            }
            cs.unmark(label2);
            label2 = cs.mark();
            if (!lexer && ignore && grammar.ignoreStar != null) {
                grammar.ignoreStar.match(cs, false, true);
            }
        }
        cs.reset(label2);
        cs.unmark(label2);
        return true;

    }

    @Override
    public String toString() {
        String str = a.toString();
        if (str.startsWith("(") &&str.endsWith(")")) {
            return str + "*";
        } else {
            return "(" + str + ")" + "*";
        }
    }

    public Set<Integer> computeFirst(java.util.Set<Object> visited) {
        ReverseSet<Integer> set = new ReverseSet<Integer>((ReverseSet<Integer>) a.getFirst(visited));
        set.add(-1);
        return set;
    }

    public MaybeBoolean isLL1() {
        MaybeBoolean ret = a.isLL1();
        return ret == MaybeBoolean.f ? ret : MaybeBoolean.unsure;
    }

    public Production doDuplicate(Grammar g) {
        return new Star(a, name, g);//
    }
}
