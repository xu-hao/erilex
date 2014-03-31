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
package erilex.data.generic;

public class Pair<S, T> {

    public S fst;
    public T snd;

    public Pair(S fst, T snd) {
        super();
        this.fst = fst;
        this.snd = snd;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<S, T> other = (Pair<S, T>) obj;
        if (this.fst != other.fst && (this.fst == null || !this.fst.equals(other.fst))) {
            return false;
        }
        if (this.snd != other.snd && (this.snd == null || !this.snd.equals(other.snd))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.fst != null ? this.fst.hashCode() : 0);
        hash = 29 * hash + (this.snd != null ? this.snd.hashCode() : 0);
        return hash;
    }
}
