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
//package erilex.codegen.ex.st;
//
//import erilex.Grammar;
//import erilex.tree.DeepTreeTransformer;
//import erilex.codegen.Generator;
//import erilex.codegen.LanguageGenerator;
//import erilex.codegen.NameEmbedding;
//import erilex.data.generic.Pair;
//import erilex.generated.transform.*;
//import static erilex.Utils.*;
//import static erilex.codegen.Generator.*;
//import java.io.IOException;
//import erilex.data.generic.Tree;
//
///**
// *
// * @author eri
// */
//public class STLC extends STLCTrL {
//    static DeepTreeTransformer<erilex.data.generic.Tree<String>, Object> S = new DeepTreeTransformer<erilex.data.generic.Tree<String>, Object>(consnat(java.lang.Object.class), consnat(erilex.generated.transform.TV.class));
//    public static void main(String[] args) throws IOException {
//
//        String path = "..\\EriLexLang\\src";
//        String pack = "erilex.generated.lang";
//        NameEmbedding t = new NameEmbedding(NameEmbedding.T_PREFIX);
//        NameEmbedding d = new NameEmbedding(NameEmbedding.D_PREFIX);
//        NameEmbedding e = new NameEmbedding(NameEmbedding.E_PREFIX);
//        grammar(t, d, e);
//        typing();
//        trans();
//        LanguageGenerator tlg = new LanguageGenerator(t, d, e, E, "nil", T, Generator.Chi,G, S,  path, pack);
//        tlg.generate();
//    }
//
//    private static void trans() {
//Utils utils = new Utils();
//final DdefaultVisitor visitor = new DdefaultVisitor(new Pair<Grammar, DeepTreeTransformer>(G, S));
//utils.<Object>trans().e_conBy()
//  .appcon(getType(new Val<Object>()))
//    .var(new Dz<Object, Tnil>()).accept(visitor);
//utils.<Object>trans().e_varBy()
//  .<Tol_i, Object>visit(new ID<De<TV<Object>, Tpair<T_TrL_Node<Tol_i, Object>, Tnil>>>())
//    .state(new ID<De<Lst<TV<Object>>, Tpair<T_TrL_Node<Tol_i, Object>, Tnil>>>())
//    .var(new Dz<T_TrL_Node<Tol_i, Object>, Tnil>()).accept(visitor);
//utils.<Object>trans().i_zBy()
//  .appcon(getType(new Hd<TV<Object>>()))
//    .state(new ID<De<Lst<TV<Object>>, Tnil>>()).accept(visitor);
//utils.<Object>trans().i_sBy()
//  .<Tol_i, Object>visit(new ID<De<TV<Object>, Tpair<T_TrL_Node<Tol_i, Object>, Tnil>>>())
//    .appcon(getType(new Tl<TV<Object>>()))
//      .state(new ID<De<Lst<TV<Object>>, Tpair<T_TrL_Node<Tol_i, Object>, Tnil>>>())
//    .var(new Dz<T_TrL_Node<Tol_i, Object>, Tnil>()).accept(visitor);
//utils.<Tfun<Object,Object>>trans().e_absBy(new ID<Dtrans<Tfun<Object, Object>, Tnil>>())
//  .appcon(getType(new Abs<Object, Object>()))
//    .abs(new ID<De<F<TV<Object>, TV<Object>>, Tpair<T_TrL_Node<Tol_e, Object>, Tnil>>>())
//      .<Tol_e, Object>visit(new ID<De<TV<Object>, Tpair<TV<Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tnil>>>>())
//        .appappcon(getType(new Cons<TV<Object>>()))
//          .var(new Dz<TV<Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tnil>>())
//          .state(new ID<De<Lst<TV<Object>>, Tpair<TV<Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tnil>>>>())
//        .var(new Ds<T_TrL_Node<Tol_e, Object>, TV<Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tnil>>(new Dz<T_TrL_Node<Tol_e, Object>, Tnil>())).accept(visitor);
//utils.<Object>trans().e_appBy()
//  .appappcon(getType(new App<Object, Object>()))
//    .<Tol_e, Tfun<Object, Object>>visit(new ID<De<TV<Tfun<Object, Object>>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Tfun<Object, Object>>, Tnil>>>>())
//      .state(new ID<De<Lst<TV<Object>>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Tfun<Object, Object>>, Tnil>>>>())
//      .var(new Ds<T_TrL_Node<Tol_e, Tfun<Object, Object>>, T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Tfun<Object, Object>>, Tnil>>(new Dz<T_TrL_Node<Tol_e, Tfun<Object, Object>>, Tnil>()))
//    .<Tol_e, Object>visit(new ID<De<TV<Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Tfun<Object, Object>>, Tnil>>>>())
//      .state(new ID<De<Lst<TV<Object>>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Tfun<Object, Object>>, Tnil>>>>())
//      .var(new Dz<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Tfun<Object, Object>>, Tnil>>()).accept(visitor);
//utils.<Object>trans().e_casexBy()
//  .ifx()
//    .<TV<TTr<Object>>>app()
//      .con(getType(new IsLeaf<Object>()))
//      .<Tol_e,TTr<Object>>visit(new ID<De<TV<TTr<Object>>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>>>>>())
//        .state(new ID<De<Lst<TV<Object>>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>>>>>())
//        .var(new Ds<T_TrL_Node<Tol_e, TTr<Object>>, T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>>>(new Ds<T_TrL_Node<Tol_e, TTr<Object>>, T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>>(new Dz<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>())))
//    .<Tol_e, Object>visit(new ID<De<TV<Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>>>>>())
//      .state(new ID<De<Lst<TV<Object>>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>>>>>())
//      .var(new Ds<T_TrL_Node<Tol_e, Object>, T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>>>(new Dz<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>>()))
//    .<Tol_e, Object>visit(new ID<De<TV<Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>>>>>())
//      .state(new ID<De<Lst<TV<Object>>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>>>>>())
//      .var(new Dz<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tpair<T_TrL_Node<Tol_e, TTr<Object>>, Tnil>>>()).accept(visitor);
//utils.<Object>trans().e_fixBy()
//  .fix()
//    .<Tol_e, Object>visit(new ID<De<TV<Object>, Tpair<TV<Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tnil>>>>())
//      .<Lst<TV<Object>>>app()
//        .<TV<Object>>app()
//          .con(getType(new Cons<TV<Object>>()))
//          .var(new Dz<TV<Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tnil>>())
//        .state(new ID<De<Lst<TV<Object>>, Tpair<TV<Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tnil>>>>())
//      .var(new Ds<T_TrL_Node<Tol_e, Object>, TV<Object>, Tpair<T_TrL_Node<Tol_e, Object>, Tnil>>(new Dz<T_TrL_Node<Tol_e, Object>, Tnil>())).accept(visitor);
//    }
//    public static class Hd<V> extends F<Lst<V>, V> {
//
//        @Override
//        public V APP(Lst<V> X) {
//            assert(!X.nil);
//            return X.hd;
//        }
//
//
//    }
//    public static class Tl<V> extends F<Lst<V>, Lst<V>> {
//
//        @Override
//        public Lst<V> APP(Lst<V> X) {
//            assert(!X.nil);
//            return X.tl;
//        }
//
//
//    }
//
//    public static class Cons<V> extends F<V, F<Lst<V>, Lst<V>>> {
//
//        @Override
//        public F<Lst<V>, Lst<V>> APP(final V X) {
//            return new F<Lst<V>, Lst<V>>() {
//                @Override
//                public Lst<V> APP(Lst<V> Y) {
//                    return new Lst<V>(X, Y);
//                }
//            };
//
//        }
//
//
//    }
//    public static class Wrapper<T> extends TV<T> {
//        T v;
//        public Wrapper(T v) {
//            this.v = v;
//        }
//        public T unwrap() {
//            return v;
//        }
//
//    }
//    public static class FunWrapper<S, T> extends TV<Tfun<S,T>> {
//        F<TV<S>, TV<T>> v;
//        public FunWrapper(F<TV<S>,TV<T>> v) {
//            this.v = v;
//        }
//        public F<TV<S>,TV<T>> unwrap() {
//            return v;
//        }
//
//    }
//
//    public static class Tree<T> extends TV<TTr<T>> {
//        Tree<T> l;
//        Tree<T> r;
//        T val;
//
//        public Tree( T val,Tree<T> l, Tree<T> r) {
//            this.l = l;
//            this.r = r;
//            this.val = val;
//        }
//
//
//    }
//    public static class Abs<S,T> extends F<F<TV<S>,TV<T>>, TV<Tfun<S, T>>> {
//
//        @Override
//        public TV<Tfun<S, T>> APP(F<TV<S>, TV<T>> X) {
//            return new FunWrapper<S, T>(X);
//
//        }
//
//
//    }
//    public static class App<S,T> extends F<TV<Tfun<S, T>>, F<TV<S>, TV<T>>> {
//
//        @Override
//        public F<TV<S>, TV<T>> APP(TV<Tfun<S, T>> X) {
//            return ((FunWrapper<S,T>)X).unwrap();
//        }
//
//
//    }
//    public static class Val<T> extends F<T, TV<T>> {
//
//        @Override
//        public TV<T> APP(T X) {
//            return new Wrapper<T>(X);
//        }
//
//
//    }
//    public static class IsLeaf<T> extends F<TV<TTr<T>>, Boolean> {
//
//        @Override
//        public Boolean APP(TV<TTr<T>> X) {
//            return ((Tree<T>)X).l == null && ((Tree<T>)X).r == null;
//        }
//
//
//    }
//
//}
