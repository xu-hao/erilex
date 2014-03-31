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

package erilex;

import erilex.data.CharStream;

public class DefaultHandler implements Handler {

    public void handle(Production production, CharStream cs, CharStream.Label start, CharStream.Label finish) {
        if (production.getName() != null) {
            System.out.println(production.getName() + "->" + cs.getString(start, finish));
        }
    }

    public void start(Production production,CharStream cs) {
    }

    public void failed(Production production,CharStream cs) {
    }
}
