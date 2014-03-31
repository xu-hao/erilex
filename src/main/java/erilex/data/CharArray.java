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
public class CharArray implements CharStream {

    private char[] chars;
    private int i = 0;
    private int max = 0;

    public CharArray(char[] chars) {
        this.chars = chars;
    }

    public int next() {
        if (i < chars.length) {
            return chars[i++];
        } else {
            return -1;
        }
    }

    public CharArrayLabel mark() {
        max = Math.max(max, i);
        return new CharArrayLabel(i);
    }

    public void reset(Label label) {
        i = (int) label.getPosition();
    }

    public String getString(Label from, Label to) {
        return new String(chars, (int)from.getPosition(), (int)(to.getPosition() - from.getPosition()));
    }

    public void unmark(Label label) {
    }

    public long position() {
        return i;
    }

    public int lookahead(int off) {
        if(this.i + off < chars.length) {
            return chars[this.i + off];
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return new String(chars)+"["+i+"]";
    }

    public long maxMarkedPosition() {
        return max;
    }

    public void seek(long start) {
        i = (int) start;
    }

    public class CharArrayLabel implements Label {
        private long pos;

        public CharArrayLabel(long pos) {
            this.pos = pos;
        }

        public long getPosition() {
            return pos;
        }

    }
}
