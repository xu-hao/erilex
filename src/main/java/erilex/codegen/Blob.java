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

import erilex.Utils;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author ertri
 */
public class Blob extends Statement implements   IExpression  {
    private String code;

    public Blob(String code) {
        this.code = code;
    }

    public void codeGen(Writer w, String indent) throws IOException {
        String nc;
        if(code.endsWith("\n")) {
            nc = code.replaceAll("\n", indent+"\n");
            nc = nc.substring(0, nc.length()-indent.length());
        } else {
            nc = code.replaceAll("\n", indent+"\n");
        }
        w.write(indent+nc);
    }

    public void renameVar(String v1, String v2) {
        Utils.warning(this, "renameVar not supported by Blob");
    }
}
