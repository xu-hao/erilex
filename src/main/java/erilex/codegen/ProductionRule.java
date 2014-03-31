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
import static erilex.codegen.TypingRule.*;
/**
 *
 * @author ertri
 */
public class ProductionRule {



    public String conseq;
    public String a;
    public int p;

    public ProductionRule(String conseq, String a, String[] antece) {
        this.conseq = conseq;
        this.a = a;
        this.antece = antece;
    }

    public ProductionRule(String conseq, String a, int p, String[] antece) {
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
            String expi = antece[i]+(char)(circOne+i);
            str += expi+" ∊ "+ antece[i] + "\n";
            exp += " " + expi;
        }
        str += "------------------------------" + "(" + a + ")\n";
        str += exp + " ∊ "+ conseq;
        return str;
    }
    public String[] antece;
}
