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
public class MultiOr extends AbstractProduction implements BinaryOp {

    public List<Production> disjuncts = new ArrayList<Production>();

    public MultiOr(List<Production> a, String name, String prodName, Grammar grammar) {
        super(name, grammar);
        this.disjuncts = a;
        this.prodName = prodName;
    }

    public boolean doMatch(CharStream cs, boolean ignore, boolean tent) {
        int la = cs.lookahead(0); // try la1 algorithm
        int ntry = 0;
        for(Production a : disjuncts) {
            final Set<Integer> firstA = a.getFirst();
            boolean tryA = firstA.contains(la) || firstA.contains(-1);
            if(tryA) {
                ntry ++;
            }
    //        if(DEBUG)
    //            if (tryA && tryB) {
    //                    Utils.warning(this, "Ambiguity in rule " + this + ". Use backtracking." + " first(A)=" + firstA + ", first(B)=" + firstB);
    //            }
            if (tryA && a.match(cs, !lexer && ignore, tent)) {
                return true;
            }
        }
        return false;
    }

    public void setB(Production p) {
        disjuncts.add(p);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(disjuncts.get(0).toString());
        for(int i=1;i<disjuncts.size();i++) {
            buf.append("|");
            String as = disjuncts.get(i).toString();
            if(as.startsWith("(") && as.endsWith(")"))
                as = as .substring(1,as.length()-1);
            buf.append(as);
        }
        return "(" + buf + ")";
    }

    public Set<Integer> computeFirst(java.util.Set<Object> visited) {
        //System.out.println(this);
        ReverseSet<Integer> set = new ReverseSet<Integer>((ReverseSet<Integer>) disjuncts.get(0).getFirst(visited));
        for(int i=1;i<disjuncts.size();i++) {
            set.addAll(disjuncts.get(i).getFirst(visited));
        }
        return set;
    }

    public MaybeBoolean isLL1() {
        int sizes = 0;
        boolean unsure = false;
        for(Production a: disjuncts) {
            final Set<Integer> first1 = a.getFirst();
            final int size1 = first1.size();
            if (size1 >= 0) {
                sizes += size1;
                if (first1.contains(-1)) {
                    if(unsure) {
                        sizes--;
                    } else {
                        unsure = true;
                    }
                }
            }
        }
        if (sizes == getFirst().size()) {
            if (unsure) {
                return MaybeBoolean.unsure;
            } else {
                return MaybeBoolean.t;
            }
        } else {
            Utils.warning(this, this + " is not LL(1)."/* + " first[" + a + "]=" + a.getFirst() + ", first[" + b + "]=" + b.getFirst()*/);
            return MaybeBoolean.f;
        }
    }

    public Production doDuplicate(Grammar g) {
        List<Production> newL = new ArrayList<Production>();
        for(Production a: disjuncts) {
            newL.add(a.duplicate(g));
        }
        return new MultiOr(newL, name, prodName, g);
    }
}
