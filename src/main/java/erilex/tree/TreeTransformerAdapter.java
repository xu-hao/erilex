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

import erilex.data.generic.Tree;

/**
 *
 * @author ertri
 */
public class TreeTransformerAdapter<S> extends ASTTreeTransformer<S> {

    public S transformBefore(Tree<ASTValueData> node, S state) {
        return state;
    }

    public S transformAfter(Tree<ASTValueData> node, S state) {
        return state;
    }

    public S transform(Tree<ASTValueData> node, S state) {
        state = transformBefore(node, state);
        for (Tree<ASTValueData> s : node.subtrees) {
            state = subtreeTransformer(s, state);
        }
        state = transformAfter(node, state);
        return state;
    }
}
