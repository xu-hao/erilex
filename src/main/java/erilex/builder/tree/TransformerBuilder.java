/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package erilex.builder.tree;

import erilex.SemanticsCollection;
import erilex.codegen.Blob;
import erilex.tree.ASTTreeTransformer;

/**
 *
 * @author ertri
 */
public class TransformerBuilder<S>  {
    String refName, t, trN;
    String[] vars;
    SemanticsCollection sem;

    public TransformerBuilder(String nt, String t, String[] vars, SemanticsCollection s, String trNParam) {
        refName = nt;
        this.t = t;
        this.vars = vars;
        sem = s;
        trN = trNParam;
    }

    public void by(ASTTreeTransformer<S> mt) {
        sem.setTrans(trN, refName, t, mt);
    }
    public void by(String blob) {
        sem.setTrans(trN, refName, t, vars,new Blob(blob));
    }

}
