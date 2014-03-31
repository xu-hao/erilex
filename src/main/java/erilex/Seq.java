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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ertri
 */
public class Seq extends AbstractProduction implements BinaryOp {

    public Production a, b;

    public Seq(Production a, Production b, String name, Grammar grammar) {
        super(name, grammar);
        this.a = a;
        this.b = b;
    }
    @Override

    public Production optimize() {
        List<Production> prodList = new ArrayList<Production>();
        Production ao = a.optimize();
        if(ao instanceof MultiSeq && ao.getName() == null && ao.getProdName() == null) {
            MultiSeq mor = (MultiSeq) ao;
            prodList.addAll(mor.disjuncts);
        } else {
            prodList.add(ao);
        }
        ao = b.optimize();
        if(ao instanceof MultiSeq && ao.getName() == null && ao.getProdName() == null) {
            MultiSeq mor = (MultiSeq) ao;
            prodList.addAll(mor.disjuncts);
        } else {
            prodList.add(ao);
        }
        return new MultiSeq(prodList, name, prodName, grammar);
    }

    public boolean doMatch(CharStream cs, boolean ignore, boolean tent) {
        Label label = cs.mark();
        if (!a.match(cs, !lexer && ignore, tent)) {
            cs.unmark(label);
            return false;
        }
        Label label2 = cs.mark();
        if (!lexer && ignore && grammar.ignoreStar != null) {
            grammar.ignoreStar.match(cs, false, true);
        }
        long label3 = cs.position();
        // if b matches epsilon, then reset label2
        if (!b.match(cs, !lexer && ignore, false)) {
            cs.reset(label);
            cs.unmark(label);
            cs.unmark(label2);
            return false;
        }
        if (label3 == cs.position()) {
            cs.reset(label2);
        }
        cs.unmark(label);
        cs.unmark(label2);
        return true;
    }

    public void setB(Production p) {
        b = p;
    }

    @Override
    public String toString() {
        return a + " " + b;
    }

    public Set<Integer> computeFirst(java.util.Set<Object> visited) {
        ReverseSet<Integer> set = new ReverseSet<Integer>((ReverseSet<Integer>) a.getFirst(visited));
        if (set.contains(-1)) {
            set.remove(-1);
            set.addAll(b.getFirst(visited));
        }
        return set;
    }

    public MaybeBoolean isLL1() {
        switch (a.isLL1()) {
            case f:
                return MaybeBoolean.f;
            case t:
                return MaybeBoolean.t;
            case unsure:
                switch(b.isLL1()) {
                    case f:
                        Utils.warning(this, this+" is not LL(1).");
                    return MaybeBoolean.f;
                    case t:
                        return MaybeBoolean.t;
                    case unsure:
                        if(getFirst().size()==a.getFirst().size()+b.getFirst().size()-1) {
                            return MaybeBoolean.unsure;
                        } else {
                            return MaybeBoolean.f;
                        }
                }
            default:
                return MaybeBoolean.unsure;
        }
    }

    public Seq doDuplicate(Grammar g) {
        return new Seq(a.duplicate(g),b.duplicate(g),name, g);
    }
}
