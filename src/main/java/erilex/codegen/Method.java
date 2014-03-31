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
import java.util.List;

/**
 *
 * @author ertri
 */
public class Method {

    public String annotation;
    public boolean cons;
    public boolean abs;
    public Type rType;
    public String name;
    public List<String> typeParams = new ArrayList<String>();
    public List<Variable> params = new ArrayList<Variable>();
    public List<Statement> mBody = new ArrayList<Statement>();
    public boolean isStatic;

    public Method(String name) {
        this.cons = true;
        this.name = name;
    }

    public Method(Type rType, String name) {
        cons = false;
        this.rType = rType;
        this.name = name;
    }

    public Method(Type rType, String name, Type pType, String pName, Statement e) {
        this(rType, name);
        this.params.add(new Variable(pType, pName));
        this.mBody.add(e);
    }

    public void codeGen(Writer w, String indent) throws IOException {
        // scope
        w.write(indent);
        if(annotation!=null) {
            w.write("@"+annotation+" ");
        }
        w.write("public ");
        if(isStatic) {
            w.write("static ");
        }
        if(abs) {
            w.write("abstract ");
        }

        // tvars
        if (typeParams.size() > 0) {
            w.write("<");
            w.write(typeParams.get(0));
            for (int i = 1; i < typeParams.size(); i++) {
                w.write("," + typeParams.get(i));
            }
            w.write("> ");
        }
        // rtype
        if (!cons) {
            rType.codeGen(w, indent);
            w.write(" ");
        }
        w.write(name);
        // parameters
        w.write("(");
        if (params.size() > 0) {
            w.write("\n" + indent + "\tfinal ");
            params.get(0).codeGen(w, indent + "\t");
            for (int i = 1; i < params.size(); i++) {
                w.write(", ");
                w.write("\n" + indent + "\tfinal ");
                params.get(i).codeGen(w, indent + "\t");
            }
        }
        w.write(")");
        if(!abs) {
        w.write(" {\n");
        for (Statement stmt : mBody) {
            stmt.codeGen(w, indent + "\t");
        }
        w.write(indent + "}\n");
        } else {
            w.write(";\n");
        }
    }

    public Method(boolean abs, Type rType, String name) {
        this.abs = abs;
        this.rType = rType;
        this.name = name;
    }

    public void renameVar(String v1, String v2) {
        for(int i =0;i<this.typeParams.size();i++) {
            if(this.typeParams.get(i).equals(v1)) {
                this.typeParams.set(i, v2);
            }
        }
        this.rType = rType.renameVar(v1, v2);
        for(Variable v : this.params) {
            v.type = v.type.renameVar(v1, v2);
        }
        for(Statement s : this.mBody) {
            s.renameVar(v1, v2);
        }
    }
}
