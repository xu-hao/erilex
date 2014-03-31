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

import erilex.codegen.NameEmbedding;
import erilex.codegen.Type;
import erilex.data.generic.Tree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ertri
 */
public class TreeUtils {

    public static List<Tree<ASTValueData>> getGroup(Tree<ASTValueData> node, String group) {
        ArrayList<Tree<ASTValueData>> list = new ArrayList<Tree<ASTValueData>>();
        for (Tree<ASTValueData> s : node.subtrees) {
            if (group.equals(s.val.group)) {
                list.add(s);
            }
        }
        return list;
    }
    public static Tree<ASTValueData> firstPath(Tree<ASTValueData> node, String... groups) {
        for(String group : groups) {
            node = first(getGroup(node, group));
        }
        return node;
    }
    public static List<Tree<ASTValueData>> groupPath(Tree<ASTValueData> node, String... groups) {
        List<Tree<ASTValueData>> list = new ArrayList<Tree<ASTValueData>>();
        list.add(node);
        for(String group : groups) {
            list = getGroup(first(list), group);
            if(list.isEmpty()) {
                return list;
            }
        }
        return list;
    }

        public static List<Tree<ASTValueData>> getGroupByVal(Tree<ASTValueData> node, String group) {
        ArrayList<Tree<ASTValueData>> list = new ArrayList<Tree<ASTValueData>>();
        for (Tree<ASTValueData> s : node.subtrees) {
            if (group.equals(s.val.name)) {
                list.add(s);
            }
        }
        return list;
    }

    public static Tree<ASTValueData> newNode(String name, Tree<ASTValueData>... subtrees) {
        return new Tree<ASTValueData>(new ASTValueData(null, -1, -1, name, null), subtrees);
    }

    public static Tree<ASTValueData> replaceSubtrees(Tree<ASTValueData> node, Tree<ASTValueData>... subtrees) {
        node.subtrees = subtrees;
        return node;
    }

    /**
     * replace node[from, to) by subtrees
     * @param node
     * @param from
     * @param to
     * @param subtrees
     */
    public static Tree<ASTValueData> replaceSubtrees(Tree<ASTValueData> node, int from, int to, Tree<ASTValueData>... subtrees) {
//        System.out.println(node);
        Tree[] newtrees = new Tree[node.subtrees.length - to + from + subtrees.length];
        System.arraycopy(node.subtrees, 0, newtrees, 0, from);
        System.arraycopy(subtrees, 0, newtrees, from, subtrees.length);
        System.arraycopy(node.subtrees, to, newtrees, from + subtrees.length, node.subtrees.length - to);
        node.subtrees = newtrees;
//        System.out.println(node);
        return node;
    }
    public static Tree<ASTValueData> createNode(String name, String text, Tree<ASTValueData>... subtrees) {
        final ASTValueData aSTValueData = new ASTValueData(name, text);
        aSTValueData.group = name;
        return new Tree<ASTValueData>(aSTValueData, subtrees);

    }
    public static String text(Tree<ASTValueData> p) {
        return p.val.text;
    }

    public static String name(Tree<ASTValueData> p) {
        return p.val.name;
    }

    public static long start(Tree<ASTValueData> p) {
        return p.val.start;
    }

    public static long finish(Tree<ASTValueData> p) {
        return p.val.finish;
    }

    public static <S> S first(List<S> list) {
        return list.get(0);
    }

    public static <S> S last(List<S> list) {
        return list.get(list.size() - 1);
    }

    public static <S> List<S> tail(List<S> list) {
        return list.subList(1, list.size());
    }

    public static Type toType(Tree<ASTValueData> tree, NameEmbedding e) {
        String name = tree.subtrees[0].val.text;
        // TODO remove ad hoc code for classes
//        if(name.contains("Class")) {
//            final Type type = new Type(name, new Type("?", true, toType(tree.subtrees[1], e)));
//            type.nat = e.nat(name);
//            return type;
//        }
        Type[] params = toType(Arrays.copyOfRange(tree.subtrees, 1, tree.subtrees.length), e);
        Type t = new Type(e.var(name), e.nat(name), name, params);
        return t;
    }

    private static Type[] toType(Tree<ASTValueData>[] subtrees, NameEmbedding e) {
        Type[] types = new Type[subtrees.length];
        for(int i =0;i<subtrees.length;i++) {
            types[i] = toType(subtrees[i], e);
        }
        return types;
    }

}
