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
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author ertri
 */
public class FSet extends AbstractProduction {

    char[] chars;
    boolean in;

    public FSet(char[] chars, boolean inclusive, String name, Grammar grammar) {
        super(name, grammar);
        this.chars = chars;
        Arrays.sort(this.chars);
        in = inclusive;

    }

    @Override
    public boolean doMatch(CharStream cs, boolean ignore, boolean tent) {
        Label label = cs.mark();
        int ch = cs.next();
        if (ch == -1) {
            cs.unmark(label);
            return false;
        } else {
            if (Arrays.binarySearch(chars, (char) ch) > -1 ^ !in) {
                cs.unmark(label);
                return true;
            } else {
                cs.reset(label);
                cs.unmark(label);
                return false;
            }
        }
    }

    public java.util.Set<Integer> computeFirst(java.util.Set<Object> visited) {
        final HashSet<Integer> hashSet = new HashSet<Integer>();
        for (char ch : chars) {
            hashSet.add(new Integer(ch));
        }
        if (in) {
            return new ReverseSet<Integer>(hashSet, false);
        } else {
            hashSet.add(-1);
            return new ReverseSet<Integer>(hashSet, true);
        }
    }

    public MaybeBoolean isLL1() {
        return MaybeBoolean.t;
    }

    @Override
    public String toString() {
        String chs =  (this.in ? "" : "^") +"[";
        for (char ch : chars) {
            chs += ch;
        }
        chs += "]";
        return Utils.encode(chs);
    }

    public Production doDuplicate(Grammar g) {
        return new FSet(chars, in, name, g);
    }
}
