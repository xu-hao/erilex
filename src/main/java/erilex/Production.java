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
import erilex.data.generic.MaybeBoolean;
import java.util.Set;

/**
 *
 * @author ertri
 */
public interface Production {

    /**
     * Match a string in a CharStream. cs is set to the next char after the matched string, if any.
     * @param cs
     * @return if matches.
     */
    public boolean match(CharStream cs, boolean ignore, boolean tent);

    public String getName();

    public void setName(String name);
public String getProdName();

    public void setProdName(String name);
    public void setLexer(boolean l);

    public boolean isLexer();

    public void setFirst(java.util.Set<Object> visited);

    public java.util.Set<Integer> getFirst(java.util.Set<Object> visited);

    public MaybeBoolean isLL1();

    public Production duplicate(Grammar g);

    Set<Integer> getFirst();

    public Production optimize();
}
