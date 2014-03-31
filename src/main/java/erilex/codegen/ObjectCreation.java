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

/**
 *
 * @author ertri
 */
public class ObjectCreation implements  IExpression {

    Type type;
    IExpression[] args;
    private Method[] methods;

    public ObjectCreation(Type type, IExpression... args) {
        this.type = type;
        this.args = args;
    }
    public ObjectCreation(Type type, IExpression[] args, Method... methods) {
        this.type = type;
        this.args = args;
        this.methods = methods;
    }

    @Override
    public void codeGen(Writer w, String indent) throws IOException {
        w.write("new ");
        type.codeGen(w, indent);
        w.write("(");
        if (args.length > 0) {
            w.write("\n" + indent + "\t");
            args[0].codeGen(w, indent + "\t");
            for (int i = 1; i < args.length; i++) {
                w.write(", ");
                w.write("\n" + indent + "\t");
                args[i].codeGen(w, indent + "\t");
            }
        }
        w.write(")");
        if(methods!= null) {
            w.write(" {\n");
            for(Method m : methods) {
                m.codeGen(w, indent+"\t");
            }
            w.write(indent+"}");
        }
    }

    @Override
    public void renameVar(String v1, String v2) {
        type = type.renameVar(v1, v2);
        for(IExpression e : args) {
            e.renameVar(v1, v2);
        }
    }
}
