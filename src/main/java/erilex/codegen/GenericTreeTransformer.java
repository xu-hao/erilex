/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package erilex.codegen;

/**
 *
 * @author ertri
 */
public abstract class GenericTreeTransformer<D, S> {
    public GenericTreeTransformer subtreeTrans = null;
    public abstract S transform(D d, S s);
    public void setSubtreeTransformer(GenericTreeTransformer nttParam) {
        subtreeTrans = nttParam;
    }


}
