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
public class Return extends Statement {

    public IExpression exp;

    public Return(IExpression exp) {
        this.exp = exp;
    }

    public void codeGen(Writer w, String indent) throws IOException {
        w.write(indent+"return ");
        exp.codeGen(w, indent);
        w.write(";\n");
    }

    @Override
    public void renameVar(String v1, String v2) {
        exp.renameVar(v1, v2);
    }
}
