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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ertri
 */
public class Or extends AbstractProduction implements BinaryOp {

    @Override
    public Production optimize() {
        List<Production> prodList = new ArrayList<Production>();
        Production ao = a.optimize();
        if(ao instanceof MultiOr && ao.getName() == null && ao.getProdName() == null) {
            MultiOr mor = (MultiOr) ao;
            prodList.addAll(mor.disjuncts);
        } else {
            prodList.add(ao);
        }
        ao = b.optimize();
        if(ao instanceof MultiOr && ao.getName() == null && ao.getProdName() == null) {
            MultiOr mor = (MultiOr) ao;
            prodList.addAll(mor.disjuncts);
        } else {
            prodList.add(ao);
        }
        return new MultiOr(prodList, name, prodName, grammar);
    }

    public Production a, b;

    public Or(Production a, Production b, String name, Grammar grammar) {
        super(name, grammar);
        this.a = a;
        this.b = b;
    }

    public boolean doMatch(CharStream cs, boolean ignore, boolean tent) {
        int la = cs.lookahead(0); // try la1 algorithm
        final Set<Integer> firstA = a.getFirst();
        final Set<Integer> firstB = b.getFirst();
        boolean tryA = firstA.contains(la) || firstA.contains(-1);
        boolean tryB = firstB.contains(la) || firstB.contains(-1);
        if(DEBUG)
            if (tryA && tryB) {
                    Utils.warning(this, "Ambiguity in rule " + this + ". Use backtracking." + " first(A)=" + firstA + ", first(B)=" + firstB);
            }
        if (tryA && a.match(cs, !lexer && ignore, tent)) {
            return true;
        }
        if (tryB && b.match(cs, !lexer && ignore, tent)) {
            return true;
        }
        return false;
    }

    public void setB(Production p) {
        b = p;
    }

    @Override
    public String toString() {
        String as = a.toString();
        String bs = b.toString();
        if(as.startsWith("(") && as.endsWith(")")) as = as .substring(1,as.length()-1);
        if(bs.startsWith("(") && as.endsWith(")")) bs = bs .substring(1,bs.length()-1);
        return "(" + as + "|" + bs + ")";
    }

    public Set<Integer> computeFirst(java.util.Set<Object> visited) {
        ReverseSet<Integer> set = new ReverseSet<Integer>((ReverseSet<Integer>) a.getFirst(visited));
        set.addAll(b.getFirst(visited));
        return set;
    }

    public MaybeBoolean isLL1() {
        final Set<Integer> first1 = a.getFirst();
        final Set<Integer> first2 = b.getFirst();
        final int size1 = first1.size();
        final int size2 = first2.size();
        if (size1 >= 0 || size2 >= 0) {
            int n = size1 + size2;
            boolean unsure = false;
            if (first1.contains(-1)) {
                unsure = true;
                if (first2.contains(-1)) {
                    n--;
                }
            } else {
                if (first2.contains(-1)) {
                    unsure = true;
                }

            }
            if (n == getFirst().size()) {
                if (unsure) {
                    return MaybeBoolean.unsure;
                } else {
                    return MaybeBoolean.t;
                }
            }
        }
        Utils.warning(this, this + " is not LL(1)." + " first[" + a + "]=" + a.getFirst() + ", first[" + b + "]=" + b.getFirst());
        return MaybeBoolean.f;
    }

    public Production doDuplicate(Grammar g) {
        return new Or(a.duplicate(g), b.duplicate(g), name, g);
    }
}
