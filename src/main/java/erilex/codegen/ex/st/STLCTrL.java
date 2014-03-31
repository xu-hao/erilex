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
//
//package erilex.codegen.ex.st;
//
//import erilex.Grammar;
//import erilex.codegen.*;
//import erilex.generated.typing.Bot;
//import erilex.generated.typing.DdefaultVisitor;
//import erilex.generated.typing.Utils;
//import java.io.IOException;
//import static erilex.codegen.Generator.*;
//
///**
// *
// * @author ertri
// */
//public class STLCTrL extends STLCTyL {
//        public static void typing() {
//        // typing
//        final DdefaultVisitor ddefaultVisitor = new DdefaultVisitor(new Grammar[]{E, T, G});
//        final Utils utils = new Utils();
//        utils.<Bot>typing().i().s().Eta().Chi().imply().pair().Chi2().Eta().Chi().accept(ddefaultVisitor);
//        utils.<Bot>typing().i().z().imply().pair().Chi().Eta().Chi().accept(ddefaultVisitor);
//        utils.<Bot>typing().e().con().Eta().nChi().imply().Eta().nChi().accept(ddefaultVisitor);
//        utils.<Bot>typing().e().app().Eta().fun().Chi1().Chi().Eta().Chi1().imply().Eta().Chi().accept(ddefaultVisitor);
//        //utils.<Bot>typing().e().appcon().Eta().fun().Chi1().Chi().Eta().Chi1().imply().Eta().Chi().accept(ddefaultVisitor);
//        utils.<Bot>typing().e().abs().pair().Chi1().Eta().Chi2().imply().Eta().fun().Chi1().Chi2().accept(ddefaultVisitor);
//        utils.<Bot>typing().e().fix().pair().Chi().Eta().Chi().imply().Eta().Chi().accept(ddefaultVisitor);
//        utils.<Bot>typing().e().casex().Eta().Tr().Chi1().pair().Chi1().Eta().Chi().pair().Tr().Chi1().pair().Tr().Chi1().pair().Chi1().Eta().Chi().imply().Eta().Chi().accept(ddefaultVisitor);
//    }
//
//    public static void main(String[] args) throws IOException {
//
//        String path = "..\\EriLexString\\src";
//        String pack = "erilex.generated.transform";
//        NameEmbedding t = new NameEmbedding(NameEmbedding.T_PREFIX);
//        NameEmbedding d = new NameEmbedding(NameEmbedding.D_PREFIX);
//        NameEmbedding e = new NameEmbedding(NameEmbedding.E_PREFIX);
//        t.add("Object", "java.lang.Object", false, true);
//        grammar(t, d, e);
//        T.def(T.startSymbol).start().t("Object").end();
//        typing();
//        TransformerLanguageGenerator tlg = new TransformerLanguageGenerator(t, d, T, G, tvar("_TrL_Chi"), new Type[]{Chi, tvar("nChi")}, consnat(erilex.codegen.ex.Lst.class,consnatfuture("erilex.generated.transform.TV", consnat(java.lang.Object.class))),path, pack);
//        tlg.generate();
//        CGUtils.printTypingRules(tlg.tgg, tlg.G);
//
//    }
//}
