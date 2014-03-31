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

package erilex;

import erilex.codegen.IExpression;
import erilex.codegen.*;
import erilex.data.generic.Tree;
import erilex.tree.DeepTreeTransformer;
import erilex.tree.ASTTreeTransformer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author ertri
 */
public class SemanticsCollection {

    public SortedMap<String, DeepTreeTransformer> treeTransformerMap = new TreeMap<String, DeepTreeTransformer>();

    public void setTrans(String transName, Type rType) {
        if(!treeTransformerMap.containsKey(transName)) {
            treeTransformerMap.put(transName, new DeepTreeTransformer(rType, transName));
        } else {
            treeTransformerMap.get(transName).rType = rType;
        }
    }
    public <S> void setTrans(String transName, String refName,String t, ASTTreeTransformer<S> trans)  {
        if(!treeTransformerMap.containsKey(transName)) {
            treeTransformerMap.put(transName, new DeepTreeTransformer(new Type("Object"), transName));
        }
        String enc = encode(refName, t);
        DeepTreeTransformer dtt = treeTransformerMap.get(transName);
        if(dtt.transMap.containsKey(enc)) { // add new transformer to the end of the chain
            Utils.warning(this, "Overriding tree transformer for "+enc);
        }
        dtt.addTransformer(enc, trans);
        trans.setSubtreeTransformer(dtt); // set dtt as the subtree transformer
    }
    public void setTrans(String transName, String refName, String t, String[] vars, IExpression exp)  {
        if(!treeTransformerMap.containsKey(transName)) {
            treeTransformerMap.put(transName, new DeepTreeTransformer(new Type("Object"), transName));
        }
        String enc = encode(refName, t);
        DeepTreeTransformer dtt = treeTransformerMap.get(transName);
        if(dtt.transMap.containsKey(enc)) { // add new transformer to the end of the chain
            Utils.warning(this, "Overriding tree transformer for "+enc);
        }
        dtt.addTransformer(enc, exp, vars);
    }

    public <S> S trans(String transName, Tree node, S state) {
        return ((DeepTreeTransformer<Tree, S>)treeTransformerMap.get(transName)).transform(node, state);
    }

    public List<Method> evaluatorsCode(NameEmbedding em, Map<String,String> natMap) {
        ArrayList<Method> mets = new ArrayList<Method>();
        for(DeepTreeTransformer t : treeTransformerMap.values()) {
            mets.add(t.evaluatorCode(em, natMap));
        }
        return mets;
    }

    public static String encode(String refName, String t) {
        return t == null? refName : refName+"=>"+t;
    }
        public static String[] decode(String enc) {
        return enc.split("=>");
    }

}
