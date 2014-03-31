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
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ertri
 */
public class MultiSeq extends AbstractProduction implements BinaryOp {

    public List<Production> disjuncts = new ArrayList<Production>();

    public MultiSeq(List<Production> a, String name, String prodName, Grammar grammar) {
        super(name, grammar);
        this.disjuncts = a;
        this.prodName = prodName;
    }

    public boolean doMatch(CharStream cs, boolean ignore, boolean tent) {
        Label label = cs.mark();
        Label label2 = null;
        for(Production a : disjuncts) {
//            String ls = "";
//            for(int i=0;i<10;i++) {
//                ls += (char)cs.lookahead(i);
//            }
//            long debug1 = cs.position();
//            System.out.println("match " + a + " with \'" + ls+ "...\' at "+debug1);
            long label3 = cs.position();
            if (!a.match(cs, !lexer && ignore, tent)) {
                cs.reset(label);
                cs.unmark(label);
                if(label2!=null)
                    cs.unmark(label2);
//                System.out.println("not matched " + a + " with \'" + ls+"...\'");
                return false;
            }
//            long debug2 = cs.position();
//            String str = (debug2 - debug1) <= 10?
//                ls.substring(0, (int)(debug2 - debug1)):
//                ls + "...";
//            System.out.println("matched " + a + " with \'" + str +"\'");

            // if a matches epsilon, then reset label2
            if (label3 == cs.position() && label2!=null) {
                cs.reset(label2);
//                System.out.println("reset to label2");
            }
            cs.unmark(label2);
            label2 = cs.mark();
            if (!lexer && ignore && grammar.ignoreStar != null) {
                grammar.ignoreStar.match(cs, false, true);
            }
        }
        cs.unmark(label);
        return true;
    }

    public void setB(Production p) {
        disjuncts.add(p);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for(Production p: disjuncts) {
            buf.append(p);
            buf.append(' ');
        }
        buf.deleteCharAt(buf.length()-1);
        return buf.toString();
    }

    public Set<Integer> computeFirst(java.util.Set<Object> visited) {
        ReverseSet<Integer> set = new ReverseSet<Integer>((ReverseSet<Integer>) disjuncts.get(0).getFirst(visited));
        for(int i=1;i<disjuncts.size();i++) {
            if (set.contains(-1)) {
                set.remove(-1);
                set.addAll(disjuncts.get(i).getFirst(visited));
            }
        }
        return set;
    }

    public MaybeBoolean isLL1() {
        int sizes = 0;
        for(Production a : disjuncts) {
            sizes += a.getFirst().size();
            switch (a.isLL1()) {
                case f:
                    Utils.warning(this, this+" is not LL(1).");
                    return MaybeBoolean.f;
                case t:
                    return getFirst().size() == sizes ? MaybeBoolean.t : MaybeBoolean.f;
                case unsure:
                    sizes --;
            }
        }
        return MaybeBoolean.unsure;
    }

    public MultiSeq doDuplicate(Grammar g) {
        List<Production> newL = new ArrayList<Production>();
        for(Production a: disjuncts) {
            newL.add(a.duplicate(g));
        }
        return new MultiSeq(newL,name,prodName, g);
    }
}
