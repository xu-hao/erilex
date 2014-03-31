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

package erilex.codegen.ex;

import erilex.codegen.embedded.TypingLanguageGenerator;
import erilex.codegen.*;
import erilex.builder.LanguageBuilder;
import java.io.IOException;

/**
 *
 * @author ertri
 */
public class TypingLanguageMain {
    public static void main(String[] args) throws IOException {
        LanguageBuilder<Object> G = new LanguageBuilder<Object>();
        LanguageBuilder<Object> T = new LanguageBuilder<Object>();
        LanguageBuilder<Object> E = new LanguageBuilder<Object>();
        G.def("e").start().t("con").nt("n").end();
        G.def("e").start().t("add").nt("e").nt("e").end();
        G.ignore().start().oneOf(' ').end();
        T.def("t").start().t("N").end();
        E.def("env").start().t("nil").end();

        NameEmbedding t = new NameEmbedding(NameEmbedding.T_PREFIX);
        t.add("Eta", Generator.Eta.cons, true, false);
        t.add("nChi", Generator.Chi.cons, true, true);
        t.add("Chi", Generator.Chi.cons, true, false);
        t.add("Chi1", Generator.Chi.cons + "1", true, false);
        t.add("Chi2", Generator.Chi.cons + "2", true, false);

        NameEmbedding d = new NameEmbedding(NameEmbedding.D_PREFIX);
//        d.add("c", "c", false, true);
        NameEmbedding e = new NameEmbedding(NameEmbedding.E_PREFIX);
//        e.add("c", "c", false, true);
        String path = "generated";
        String pack = "erilex.generated.typing";
        TypingLanguageGenerator tlg = new TypingLanguageGenerator(t, d, e, E, T, G, "typing", Generator.Chi, path, pack);
        tlg.generate();
        tlg.TL.dumpRuleMap();
        System.out.println(tlg.TLS.transClassMap);

    }
}
