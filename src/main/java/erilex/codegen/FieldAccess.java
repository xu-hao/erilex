/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package erilex.codegen;

import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author ertri
 */
public class FieldAccess implements IExpression {

    IExpression source;
    String methodname;

    public FieldAccess(IExpression object, String methodname) {
        this.source = object;
        this.methodname = methodname;
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
            w.write(methodname);
        }
    }

    @Override
    public void renameVar(String v1, String v2) {
        source.renameVar(v1, v2);
    }
}
