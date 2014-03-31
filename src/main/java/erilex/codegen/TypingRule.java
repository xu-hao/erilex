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
package erilex.codegen;

/**
 *
 * @author ertri
 */
public class TypingRule {

    public final static char circOne = '\u2460';
    public Judgement conseq;
    public String a;
    public int p;

    public TypingRule(Judgement conseq, String a, Judgement[] antece) {
        this.conseq = conseq;
        this.a = a;
        this.antece = antece;
    }

    public TypingRule(Judgement conseq, String a, int p, Judgement[] antece) {
        this.conseq = conseq;
        this.a = a;
        this.p = p;
        this.antece = antece;
    }

    @Override
    public String toString() {
        String str = "";
        String exp = a;
        for (int i = 0; i < antece.length; i++) {
            String expi = antece[i].e+(char)(circOne+i);
            str += antece[i].toString(expi) + "\n";
            exp += " " + expi;
        }
        str += "------------------------------" + "(" + a + ")\n";
        str += conseq.toString(exp);
        return str;
    }
    public Judgement[] antece;
}
