/*
Copyright 2009, 2010 Hao Xu
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
public class Invocation implements IExpression {

    IExpression source;
    String methodname;
    IExpression[] args;
    Type[] typeArgs;
    IExpression[] impArgs;

    public Invocation(IExpression object, String methodname, IExpression... params) {
        this.args = params;
        this.source = object;
        this.methodname = methodname;
    }

    public Invocation(IExpression source, String methodname, IExpression[] args, Type[] typeArgs, IExpression[] impArgs) {
        this.source = source;
        this.methodname = methodname;
        this.args = args;
        this.typeArgs = typeArgs;
        this.impArgs = impArgs;
    }

//    public ObjectCreation(Type type, Expression... args) {
//        this.type = type;
//        this.args = args;
//    }
    @Override
    public void codeGen(Writer w, String indent) throws IOException {

        if (source != null) {
            source.codeGen(w, indent);
            w.write(".");
            if (typeArgs!=null && typeArgs.length > 0) {
                w.write("<");
                w.write(typeArgs[0].toStringCodeGen());
                for (int i = 1; i < typeArgs.length; i++) {
                    w.write(", ");
                    w.write("\n" + indent + "\t");
                    w.write(typeArgs[i].toStringCodeGen());
                }
                w.write(">");
            }

        }
        w.write(methodname + "(");
        if (args.length > 0) {
            args[0].codeGen(w, indent + "\t");
            if (args[0] instanceof VariableExpression); else {
                w.write("\n" + indent + "\t");
            }
            for (int i = 1; i < args.length; i++) {
                w.write(", ");
                w.write("\n" + indent + "\t");
                args[i].codeGen(w, indent + "\t");
            }
        }
        if (impArgs!=null && impArgs.length > 0) {
            if(args.length > 0) {
                w.write(", \n");
            }
            impArgs[0].codeGen(w, indent + "\t");
            if (impArgs[0] instanceof VariableExpression); else {
                w.write("\n" + indent + "\t");
            }
            for (int i = 1; i < impArgs.length; i++) {
                w.write(", ");
                w.write("\n" + indent + "\t");
                impArgs[i].codeGen(w, indent + "\t");
            }
        }
        w.write(")");
    }

    @Override
    public void renameVar(String v1, String v2) {
        source.renameVar(v1, v2);
        for (IExpression e : args) {
            e.renameVar(v1, v2);
        }
    }
}
