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

import java.util.HashMap;

/**
 *
 * @author ertri
 */
public class TypeFunction {

    HashMap<Type, Type> typeMap = new HashMap<Type, Type>(0);

    public TypeFunction() {
    }

    public void add(Type key, Type embedding) {
        typeMap.put(key, embedding);
    }

    public Type map(Type key) {
        if (typeMap.containsKey(key)) {
            return typeMap.get(key);
        } else {
            return new Type("Object");
            //return key;
        }
    }
}
