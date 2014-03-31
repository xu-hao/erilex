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

import erilex.codegen.GenericTreeTransformer;
import erilex.data.generic.Tree;

/**
 *
 * @author ertri
 */
public abstract class ASTTreeTransformer<S> extends GenericTreeTransformer<Tree<ASTValueData>, S>{

    public S subtreeTransformer(Tree<ASTValueData> node, S state) {
        return ((GenericTreeTransformer<Tree<ASTValueData>, S>)subtreeTrans).transform(node, state);
    }
    
}