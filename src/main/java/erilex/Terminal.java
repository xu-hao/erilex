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
import erilex.data.CharStream.Label;
import erilex.data.generic.MaybeBoolean;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author ertri
 */
public class Terminal extends AbstractProduction {

    public String text;

    public Terminal(String text, String name, Grammar grammar) {
        super(name, grammar);
        this.text = text;
        if(text == null || text.isEmpty()) {
           Utils.error(null, "empty terminal symbol.");
        }
    }
    public Terminal(String text, Grammar grammar) {
        super(text, grammar);
        this.text = text;
        if(text == null || text.isEmpty()) {
           Utils.error(null, "empty terminal symbol.");
        }
    }

    public Terminal duplicate() {
        return (Terminal) doDuplicate(grammar);
    }

    public boolean doMatch(CharStream cs, boolean ignore, boolean tent) {
        Label label = cs.mark();
        for (int i = 0; i < text.length(); i++) {
            int ch = cs.next();
            if (ch == -1 || ch != text.charAt(i)) {
                cs.reset(label);
                cs.unmark(label);
                return false;
            }
        }
        cs.unmark(label);
        return true;

    }

    @Override
    public String toString() {
        return Utils.encode("\'"+text+"\'");
    }

    public Set<Integer> computeFirst(java.util.Set<Object> visited) {
        return new ReverseSet<Integer>(Collections.singleton(new Integer(text.charAt(0))), false);
    }

    public MaybeBoolean isLL1() {
        return MaybeBoolean.t;
    }

    public Production doDuplicate(Grammar g) {
        return new Terminal(text, name, g);
    }
}
