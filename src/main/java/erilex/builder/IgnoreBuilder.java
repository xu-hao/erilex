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

package erilex.builder;

import erilex.Global;
import erilex.Grammar;
import erilex.Or;
import erilex.Production;
import erilex.Star;

/**
 *
 * @author ertri
 */
public class IgnoreBuilder implements Builder<Object> {
    private ExpressionBuilder<RuleBuilder, Object> exp;
    private final Grammar grammar;

    public IgnoreBuilder(Grammar g) {
        this.grammar = g;
    }

    public ExpressionBuilder<RuleBuilder, Object> start() {
        return exp = new ExpressionBuilder(this, grammar);
    }

    public Production build() {
        return null;
    }

    @Override
    public Object exp(Production p) {
        if(grammar.ignore == null) {
            grammar.ignore = p;
            grammar.ignoreStar = new Star(p, null, grammar);
        } else {
            grammar.ignore = new Or(grammar.ignore, p, null, grammar);
            grammar.ignoreStar = new Star(grammar.ignore, null, grammar);
        }
        return null;
    }

}
