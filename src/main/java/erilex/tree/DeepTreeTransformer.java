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
package erilex.tree;

import erilex.SemanticsCollection;
import erilex.Utils;
import erilex.codegen.And;
import erilex.codegen.Assignment;
import erilex.codegen.Block;
import erilex.codegen.Conversion;
import erilex.codegen.GenericTreeTransformer;
import erilex.codegen.IExpression;
import erilex.codegen.IfStatement;
import erilex.codegen.Invocation;
import erilex.codegen.Method;
import erilex.codegen.NameEmbedding;
import erilex.codegen.Return;
import erilex.codegen.Statement;
import erilex.codegen.Type;
import erilex.codegen.TypeFunction;
import erilex.codegen.Variable;
import erilex.codegen.VariableExpression;
import erilex.data.generic.Tree;
import java.util.HashMap;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ertri
 */
public class DeepTreeTransformer<D extends Tree, S> extends GenericTreeTransformer<D, S> {

    public String name;
    //public String stateParameterName;
    public Type rType;
    public HashMap<String, GenericTreeTransformer<D, S>> transMap = new HashMap<String, GenericTreeTransformer<D, S>>();
    public HashMap<String, Class<? extends GenericTreeTransformer<D, S>>> transClassMap = new HashMap<String, Class<? extends GenericTreeTransformer<D, S>>>();
    public HashMap<String, IExpression> transExprMap = new HashMap<String, IExpression>();
    public HashMap<String, String[]> transVarMap = new HashMap<String, String[]>();
    public Variable[] state;
    public TypeFunction valueTypeFuncRaw;

    public DeepTreeTransformer(Type state, erilex.codegen.TypeFunction vrawParam) {
        this.state = new Variable[]{new Variable(state, null)};
        valueTypeFuncRaw = vrawParam;
    }

    public DeepTreeTransformer(Type rType, String evaluatorName) {
        this(rType, evaluatorName, null, null);
    }
    public DeepTreeTransformer(Type rType, String evaluatorName, Variable... state) {
        this.rType = rType;
        this.name = evaluatorName;
        this.state = state;
    }

    public S transform(D object, S state) {
        D node = object;
        String refName = ((ASTValueData) node.val).name;
        if (refName != null) {
            String t = node.subtrees.length == 0 ? null : ((ASTValueData) node.subtrees[0].val).name;
            String enc = SemanticsCollection.encode(refName, t);
            if (transMap.containsKey(enc)) {
                final GenericTreeTransformer<D, S> trans = transMap.get(enc);
                S state1 = eval(trans, node, state);
                return state1;
            } else if (transMap.containsKey(refName)) {
                final GenericTreeTransformer<D, S> trans = transMap.get(refName);
                S state1 = eval(trans, node, state);
                return state1;
            } else if (transClassMap.containsKey(enc)) {
                final Class<? extends GenericTreeTransformer<D, S>> trans = transClassMap.get(enc);
                S state1;
                state1 = eval(trans, node, state);
                return state1;
            } else if (transClassMap.containsKey(refName)) {
                final Class<? extends GenericTreeTransformer<D, S>> trans = transClassMap.get(refName);
                S state1;
                state1 = eval(trans, node, state);
                return state1;
            } else if (transExprMap.containsKey(enc)) {
                // TODO implement evaluator
                throw new UnsupportedOperationException("An evaluator for expr is currently not implemented.");
            } else if (transExprMap.containsKey(refName)) {
                // TODO implement evaluator
                throw new UnsupportedOperationException("An evaluator for expr is currently not implemented.");
            }
        }
        S state1;
        state1 = eval(node, state);
        return state1;
    }

    private S eval(D node, S state1) {
        for (Tree s : node.subtrees) {
            state1 = transform((D) s, state1);
        }
        return state1;
    }

    private S eval(final Class<? extends GenericTreeTransformer<D, S>> trans, D node, S state) {
        S state1 = null;
        final GenericTreeTransformer<D, S> newInstance;
        try {
            newInstance = trans.newInstance();
            newInstance.setSubtreeTransformer(this);
            state1 = newInstance.transform(node, state);
        } catch (InstantiationException ex) {
            Logger.getLogger(DeepTreeTransformer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DeepTreeTransformer.class.getName()).log(Level.SEVERE, null, ex);
        }
        Utils.error(this, "Can not create instance of TreeTransformer " + trans.getName());
        return state1;
    }

    private S eval(GenericTreeTransformer<D, S> trans, D node, S state) {
        S state1 = trans.transform(node, state);
        return state1;
    }

    public <C extends GenericTreeTransformer<D, S>>  void addTransformer(String name, java.lang.Class<C> trans) {
        this.transClassMap.put(name, trans);
    }

    public void addTransformer(String name, GenericTreeTransformer<D, S> trans) {
        this.transMap.put(name, trans);
    }

    public void addTransformer(String s, IExpression expr, String... vars) {
        this.transExprMap.put(s, expr);
        this.transVarMap.put(s, vars);
    }

    public Method evaluatorCode(NameEmbedding em, Map<String, String> natTypeMap) {
        String param = name + "_param";
        Statement e = new Return(new Conversion(rType, new VariableExpression(param))); // by default return the AST node
        for (Entry<String, IExpression> entry : transExprMap.entrySet()) {
            String[] vars = transVarMap.get(entry.getKey());
            Statement[] stmts = new Statement[vars.length + 1];
            for (int j = 0; j < vars.length; j++) {
                if(em.getnat(vars[j])) { // this needs to change to map vars[j] to the correponding nt
                    String type = natTypeMap.get(vars[j]);
                    if(type == null) type = "Object"; // if type not defined then use Object as the default type
                    stmts[j] = new Assignment("final " + type + " " + vars[j], new VariableExpression("("+type+")NAT(((erilex.data.generic.Tree)" + param + ").subtrees[" + (j) + "])"));

                } else {
                    stmts[j] = new Assignment("final Object " + vars[j], new VariableExpression("((erilex.data.generic.Tree)" + param + ").subtrees[" + (j) + "]"));
                }
            }
            stmts[stmts.length - 1] = (Statement) entry.getValue();
            final Block block = new Block(stmts);
            final String enc = entry.getKey();
            String[] dec = SemanticsCollection.decode(enc);
            String refName = dec[0];
            String t = dec.length==1? null:dec[1];
            final IExpression cond = t == null?
                new Invocation(new VariableExpression("((erilex.tree.ASTValueData)((erilex.data.generic.Tree)" + param + ").val).name"), "equals", new VariableExpression("\"" + refName + "\"")):
                new And(new Invocation(new VariableExpression("((erilex.tree.ASTValueData)((erilex.data.generic.Tree)" + param + ").subtrees[0].val).name"), "equals", new VariableExpression("\"" + t + "\"")), new Invocation(new VariableExpression("((erilex.tree.ASTValueData)((erilex.data.generic.Tree)" + param + ").val).name"), "equals", new VariableExpression("\"" + refName + "\"")));

            e = new IfStatement(cond, block, e);
        }
        Method m = new Method(rType, name);
        m.params.add(new Variable(new Type("Object"), param));
        if(state!=null && state.length>0 && state[0].name!=null)
        m.params.addAll(Arrays.asList(state));
        m.mBody.add(e);
        return m;
    }
}
