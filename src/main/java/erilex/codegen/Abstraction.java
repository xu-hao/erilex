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
import java.util.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ertri
 */
public class Abstraction implements  IExpression {

    Variable var;
    Type rType;
    List<Statement> mBody = new ArrayList<Statement>();
    private final String ftype;
    private final String mname;

    public Abstraction(String string, Type type, Type rtype, Statement... stmt) {
        this.ftype = Generator.FUNCTION;
        this.mname = Generator.APPLY;
        this.var = new Variable(type, string);
        this.rType = rtype;
        this.mBody.addAll(Arrays.asList(stmt));
    }
    public Abstraction(String ftype, String mname, String string, Type type, Type rtype, Statement... stmt) {
        this.ftype = ftype;
        this.mname = mname;
        this.var = new Variable(type, string);
        this.rType = rtype;
        this.mBody.addAll(Arrays.asList(stmt));
    }

    @Override
    public void codeGen(Writer w, String indent) throws IOException {
        w.write("new "+ftype+"<");
        var.type.codeGen(w,indent);
        w.write(",");
        rType.codeGen(w,indent);
        w.write(">() {\n" +
                indent + "\tpublic ");
        rType.codeGen(w,indent);
        w.write(" "+mname/*Generator.APPLY*/+"(final ");
        var.codeGen(w,indent);
        w.write(") {\n");
        for(Statement stmt : mBody) {
            stmt.codeGen(w, indent+ "\t\t");
        }
        w.write(indent + "\t}\n" +
                indent + "}");

    }

    @Override
    public void renameVar(String v1, String v2) {
        var.type = var.type.renameVar(v1, v2);
        rType = rType.renameVar(v1, v2);
        for(Statement s : mBody) {
            s.renameVar(v1, v2);
        }
    }
}
