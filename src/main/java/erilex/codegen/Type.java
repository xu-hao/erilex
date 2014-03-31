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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import erilex.*;

/**
 *
 * @author ertri
 */
public class Type extends TypeFunction{

    @Override
    public Type map(Type key) {
        return toRawType().setParams(key);
    }

    public boolean var;
    public boolean nat;
    public boolean box;

    public Type(boolean var, boolean nat, boolean box, String cons, Type... params) {
        this.var = var;
        this.nat = nat;
        this.box = box;
        this.cons = cons;
        this.params = params;
    }
    public String cons;
    public Type[] params;

    private Type(boolean var, boolean nat, boolean box, String cons, boolean wildcard, Type superType, Type[] params) {
        this.var = var;
        this.nat = nat;
        this.box = box;
        this.cons = cons;
        this.params = params;
        this.wildcard = wildcard;
        this.superType = superType;
    }
    public boolean wildcard;
    public Type superType;

    public Type(String cons, Type... params) {
        this.cons = cons;
        this.params = params;
    }

    public Type(boolean var, boolean nat, String cons, Type... params) {
        this.var = var;
        this.nat = nat;
        this.cons = cons;
        this.params = params;
    }
    public Type(String cons, boolean wildcard, Type superType) {
        this.var = !wildcard;
        this.nat = true;
        this.cons = cons;
        this.superType = superType;
        this.wildcard = wildcard;
        this.params = new Type[0];
    }
    public Type(boolean var, String cons, Type... params) {
        this.var = var;
        this.cons = cons;
        this.params = params;
    }

    private Type(boolean var, boolean nat, String cons, boolean wildcard, Type superType, Type... params) {
        this.var = var;
        this.nat = nat;
        this.cons = cons;
        this.wildcard = wildcard;
        this.superType = superType;
        this.params = params;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Type other = (Type) obj;
        if (this.var != other.var) {
            return false;
        }
        if (this.nat != other.nat) {
            return false;
        }
        if ((this.cons == null) ? (other.cons != null) : !this.cons.equals(other.cons)) {
            return false;
        }
        if (!Arrays.deepEquals(this.params, other.params)) {
            return false;
        }
        if (this.wildcard != other.wildcard) {
            return false;
        }
        if (this.superType != other.superType && (this.superType == null || !this.superType.equals(other.superType))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.var ? 1 : 0);
        hash = 89 * hash + (this.nat ? 1 : 0);
        hash = 89 * hash + (this.cons != null ? this.cons.hashCode() : 0);
        hash = 89 * hash + Arrays.deepHashCode(this.params);
        hash = 89 * hash + (this.wildcard ? 1 : 0);
        hash = 89 * hash + (this.superType != null ? this.superType.hashCode() : 0);
        return hash;
    }

    public void codeGen(Writer w, String indent) throws IOException {
        if(wildcard) {
            w.write(cons);
        } else { 
            w.write(cons);
        }
        if(superType!=null) {
            w.write(" extends ");
            superType.codeGen(w, indent);
        }
        if (params.length > 0) {
            w.write("<");

            params[0].codeGen(w, indent);
            for (int i = 1; i < params.length; i++) {
                w.write(",");
                params[i].codeGen(w, indent);
            }
            w.write(">");
        }
    }

    public String unparse() {
        String str =  cons;
        for (int i = 0; i < params.length; i++) {
        str += " " +
            params[i].unparse();
        }
        return str;
    }

    public List<Type> fv() {
        ArrayList<Type> s = new ArrayList<Type>();
        if (var) {
            s.add(this);
        } else {
            for (Type p : params) {
                List<Type> pfv = p.fv();
                pfv.removeAll(s);
                s.addAll(pfv);
            }
        }
        return s;

    }

    public Type renameVar(String v1, String v2) {
        if (var) {
            if (this.cons.equals(v1)) {
                return new Type(var, nat,box, v2, wildcard, superType, params);
            } else {
                return this;
            }
        } else {
            Type[] nparams = new Type[this.params.length];
            int i=0;
            for (Type p : params) {
                nparams[i++]=p.renameVar(v1, v2);
            }
            return new Type(var, nat, box, cons, wildcard, superType, nparams);
        }

    }

    public Type addPrefix(NameEmbedding p) {
        if(!nat) {
        return new Type(var, nat, box, p.get(cons), wildcard, superType!=null?superType.addPrefix(p):null, addPrefix(params, p));
        } else {
            return new Type(var, nat, box, p.getNat(cons), wildcard, superType!=null?superType.addPrefix(p):null, addPrefix(params, p));
        }
    }

    public static Type[] addPrefix(Type[] ts, NameEmbedding p) {
        Type[] tsn = new Type[ts.length];
        for (int i = 0; i < ts.length; i++) {
            tsn[i] = ts[i].addPrefix(p);
        }
        return tsn;
    }
    public Type toRawType() {
        return new Type(var, nat, box, cons, wildcard, superType, new Type[0]);

    }
    public String toStringCodeGen() {
        StringWriter sr = new StringWriter();
        try {
            codeGen(sr, "");
        } catch (IOException ex) {
            Utils.error(this, "can not convert type to string: "+ this);
        }
        return sr.toString();
    }

    @Override
    public String toString() {
        if(cons.equals("@Func")) {
            String paramtype = Arrays.deepToString(Arrays.copyOfRange(params, 0, params.length-1));
            paramtype = "(" + paramtype.substring(1, paramtype.length() - 1) + ")";
            return paramtype + " -> " + params[params.length-1];
        } else if(cons.equals("@Eq")) {
            return params[0] + " == " + params[1];
        } else if(cons.equals("@Cons")) {
            return params[1] + "##" + params[0];
        } else if(cons.equals("@Nil")) {
            return "\u03f5";
        } else if(cons.startsWith("@#")) {
            return cons.substring(1);
        }
        return cons + (!var? Arrays.deepToString(params).replace("[", "<").replace("]", ">"):"");
    }

    public Type setParams(Type... nodetype) {
        this.params = nodetype;
        return this;
    }
}
