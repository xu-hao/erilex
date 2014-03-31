/*
Copyright 2008, 2009 Hao Xu
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package erilex.data.generic;

/**
 *
 * @author ertri
 */
public class Quadruple<U,V,W,X> {
    public U first;
    public V second;
    public W third;
    public X fourth;

    public Quadruple(U a, V b, W c, X d) {
        this.first = a;
        this.second = b;
        this.third = c;
        this.fourth = d;
    }

    public U first() {
        return first;
    }

    public V second() {
        return second;
    }

    public W third() {
        return third;
    }

    public X fourth() {
        return fourth;
    }
    

}
