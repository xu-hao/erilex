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
package erilex.codegen;

/**
 *
 * @author ertri
 */
public class Judgement {

    Type env;
    Type t;
    String e;

    public Judgement(String e) {
        this.e = e;
        this.t = Generator.Chi;
        this.env = Generator.Eta;
    }

    public Judgement(String e, Type t) {
        this.e = e;
        this.t = t;
    }

    public Judgement(Type env, String e, Type t) {
        this.env = env;
        this.t = t;
        this.e = e;
    }

    @Override
    public String toString() {
        return toString(e);
    }

    public String toString(String e) {
        return env + " ├ " + e + " : " + t;
    }
}
