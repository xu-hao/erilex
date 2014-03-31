/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package erilex.codegen;

import erilex.data.generic.Tree;
import erilex.tree.DeepTreeTransformer;

/**
 *
 * @author ertri
 */
public class SemanticsBuilder<D extends Tree, S> extends DeepTreeTransformer<D, S> {

    public SemanticsBuilder(Type stateType) {
        super(stateType, (Type)null);
    }

    public <C extends GenericTreeTransformer<D, S>> void trans(java.lang.Class<D> c, java.lang.Class<? extends GenericTreeTransformer<D, S>> trans) {
        trans(c.getSimpleName(), trans);
    }

    public <C extends GenericTreeTransformer<D, S>> void trans(String classname, java.lang.Class<C> trans) {
        this.addTransformer(classname, trans);
    }
}
