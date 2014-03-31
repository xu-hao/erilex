/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package erilex.codegen;

/**
 *
 * @author eri
 */
public class GeneratorUtils {
    public static VariableExpression varExp(String var) {
        return new VariableExpression(var);
    }
    public static FieldAccess fAcc(IExpression exp, String f) {
        return new FieldAccess(exp, f);
    }
//    public static FieldAccess abs(String vName, Type vType, IExpression exp, String f) {
//        return new Abstraction(vName, vType, rType, stmt);
//    }

}
