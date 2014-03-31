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

import erilex.Production;

public class ASTValueData {

    public Production p;
    public long start;
    public long finish;
    public String group;
    public String text;
    public String name;
    public Object obj; // this field is used to store native values

    public ASTValueData(String name, String text) {
        this.name = name;
        this.text = text;
    }
    public ASTValueData(String name, String text, Object obj) {
        this.name = name;
        this.text = null;
        this.obj = obj;
    }

    public ASTValueData(Production p, long start, long finish, String name, String text) {
        super();
        this.p = p;
        this.name = name;
        this.group = name;
        this.start = start;
        this.finish = finish;
        this.text = text;
    }

    public ASTValueData(Production p, long start, long finish, String name) {
        super();
        this.p = p;
        this.name = name;
        this.group = name;
        this.start = start;
        this.finish = finish;
    }


    @Override
    public String toString() {
        return (name == null ? "\u03b5" : name) + (group != null ? "{" + group + "}" : "") + ":" + (text != null ? text : "");
    }
}
