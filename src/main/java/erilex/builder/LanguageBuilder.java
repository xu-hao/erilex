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
package erilex.builder;

import erilex.Grammar;
import erilex.Production;
import erilex.SemanticsCollection;
import erilex.builder.tree.TransformerBuilder;
import erilex.tree.DeepTreeTransformer;

/**
 *
 * @author ertri
 */
public class LanguageBuilder<S> extends Grammar {

    public SemanticsCollection semantics = new SemanticsCollection();

    public LanguageBuilder() {
        ignore().start().oneOf(' ','\t','\n','\r').end();
    }
    public LanguageBuilder(boolean compact) {
        if(!compact)ignore().start().oneOf(' ','\t','\n','\r').end();
    }
    public RuleBuilder def(String name) {
        return new RuleBuilder(name, this);
    }

    public RuleBuilder auxdef(String name) {
        return new RuleBuilder(name, true, false, this);
    }

    public RuleBuilder lex(String name) {
        return new RuleBuilder(name, false, true, this);
    }

    public RuleBuilder auxlex(String name) {
        return new RuleBuilder(name, true, true, this);
    }

    public IgnoreBuilder ignore() {
        return new IgnoreBuilder(this);
    }

    public void copy(Grammar g) {
        
        for(Production p : g.ruleMap.values()) {
            final Production duplicate = p.duplicate(this);
            
            this.ruleMap.put(p.getProdName(),duplicate);
        }
    }

    public TransformerBuilder<S> trans(String transName, String refName, String t, String... vars) {
        TransformerBuilder<S> tr = new TransformerBuilder<S>(refName, t, vars, semantics, transName);
        return tr;
    }
        public TransformerBuilder<S> trans(String transName, String refName) {
        TransformerBuilder<S> tr = new TransformerBuilder<S>(refName, null, null, semantics, transName);
        return tr;
    }
}
