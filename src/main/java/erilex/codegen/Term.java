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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ertri
 */
public class Term {

    public boolean var;
    public boolean nat;
    public String cons;
    public Term[] params;

    public Term(String cons, Term... params) {
        this.cons = cons;
        this.params = params;
    }

    public Term(boolean var, boolean nat, String cons, Term... params) {
        this.var = var;
        this.nat = nat;
        this.cons = cons;
        this.params = params;
    }

    public Term(boolean var, String cons, Term... params) {
        this.var = var;
        this.cons = cons;
        this.params = params;
    }

    public void codeGen(Writer w) throws IOException {
        w.write(cons);
        if (params.length > 0) {
            w.write("<");

            params[0].codeGen(w);
            for (int i = 1; i < params.length; i++) {
                w.write(",");
                params[i].codeGen(w);
            }
            w.write(">");
        }
    }

    public List<String> fv() {
        ArrayList<String> s = new ArrayList<String>();
        if (var) {
            s.add(cons);
        } else {
            for (Term p : params) {
                List<String> pfv = p.fv();
                pfv.removeAll(s);
                s.addAll(pfv);
            }
        }
        return s;

    }

    public Term addPrefix(String p) {
        if(nat) {
        return new Term(var, cons, addPrefix(params, p));
        } else {
        return new Term(var, p + cons, addPrefix(params, p));
        }
    }

    public static Term[] addPrefix(Term[] ts, String p) {
        Term[] tsn = new Term[ts.length];
        for (int i = 0; i < ts.length; i++) {
            tsn[i] = ts[i].addPrefix(p);
        }
        return tsn;
    }
}
