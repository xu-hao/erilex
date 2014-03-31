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

import erilex.builder.LanguageBuilder;

/**
 *
 * @author ertri
 */
public class TreeTransformerBuilder {
    LanguageBuilder<Object> G = new LanguageBuilder<Object>(),
            T = new LanguageBuilder<Object>(),
            E = new LanguageBuilder<Object>();
    public TreeTransformerBuilder() {
        E.def("env").start().t("nil").end();
        T.def("t").start().t("*").end();
        G.def("e").start()
                .t("var")
                .or().t("state")
                .or().t("con").nt("Object")
                .or().t("size").nt("list")
                .or().t("first").nt("list")
                .or().t("tail").nt("list")
                .or().t("last").nt("list")
                .or().t("node").nt("list")
                .or().t("replace").nt("Integer").nt("Integer").nt("list")
                .or().t("rec").nt("list").nt("e")
                .or().t("ifx").nt("e").nt("then").nt("e").nt("else").nt("e")
                .or().t("app").nt("e").nt("e")
                .or().t("fun").nt("String").nt("e")
                .or().t("list").nt("list")
                .end();
        G.def("list").start()
                .t("lstart").nt("e").nt("listtail")
                .or().t("group").nt("String")
                .or().t("nil")
                .or().t("cons").nt("e").nt("e")
                .end();
        G.def("listtail").start()
                .t("lend")
                .or().t("var").t("listtail")
                .or().t("state").t("listtail")
                .or().t("nil").t("listtail")
                .or().t("con").nt("Object").t("listtail")
                .or().t("group").nt("String").t("listtail")
                .or().t("size").nt("e").t("listtail")
                .or().t("first").nt("e").t("listtail")
                .or().t("tail").nt("e").t("listtail")
                .or().t("last").nt("e").t("listtail")
                .or().t("node").nt("list").t("listtail")
                .or().t("replace").nt("Integer").nt("Integer").nt("list").t("listtail")
                .or().t("cons").nt("e").nt("e").t("listtail")
                .or().t("rec").nt("e").nt("e").t("listtail")
                .or().t("ifx").nt("e").nt("then").nt("e").nt("else").nt("e").t("listtail")
                .or().t("app").nt("e").nt("e").t("listtail")
                .or().t("fun").nt("String").nt("e").t("listtail")
                .end();
        G.def("then").start().t("thenx").end();
        G.def("else").start().t("elsex").end();


    }
}
