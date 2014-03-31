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
import erilex.data.CharStream.Label;

/**
 *
 * @author ertri
 */
public class ChainHandler implements Handler {

    public ChainHandler(Handler... handlers) {
        this.handlers = handlers;
    }
    Handler[] handlers;

    public void handle(Production production, CharStream cs, Label start, Label finish) {
        for (Handler h : handlers) {
            h.handle(production, cs, start, finish);
        }
    }

    public void start(Production production, CharStream cs) {
        for (Handler h : handlers) {
            h.start(production, cs);
        }
    }

    public void failed(Production production, CharStream cs) {
        for (Handler h : handlers) {
            h.failed(production, cs);
        }
    }
}
