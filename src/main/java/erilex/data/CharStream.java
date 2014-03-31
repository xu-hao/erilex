/*
Copyright 2009, 2010 Hao Xu
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
package erilex.data;

/**
 *
 * @author ertri
 */
public interface CharStream {

    int next();
    Label mark();
    void unmark(Label label);
    long position();
    void reset(Label label);
    String getString(Label from, Label to);
    long maxMarkedPosition();

    int lookahead(int off);

    public void seek(long start);

    public interface Label {
        long getPosition();
    }

}
