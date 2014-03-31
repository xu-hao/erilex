/*
Copyright 2006, 2007, 2008, 2009 Hao Xu
ertranne@hotmail.com

This file is part of OSHL-S.

OSHL-S is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

OSHL-S is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Foobar; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package erilex.data.generic;

public class Triple<U, V, W> {

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Triple<U, V, W> other = (Triple<U, V, W>) obj;
        if (this.first != other.first && (this.first == null || !this.first.equals(other.first))) {
            return false;
        }
        if (this.second != other.second && (this.second == null || !this.second.equals(other.second))) {
            return false;
        }
        if (this.third != other.third && (this.third == null || !this.third.equals(other.third))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.first != null ? this.first.hashCode() : 0);
        hash = 97 * hash + (this.second != null ? this.second.hashCode() : 0);
        hash = 97 * hash + (this.third != null ? this.third.hashCode() : 0);
        return hash;
    }
    public U first;
    public V second;
    public W third;

    public Object[] toArray() {
        return new Object[]{first, second, third};
    }

    public final U first() {
        return first;
    }

    public final V second() {
        return second;
    }

    public final W third() {
        return third;
    }

    public Triple(U a, V b, W c) {
        this.first = a;
        this.second = b;
        this.third = c;
    }
}
