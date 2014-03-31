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

import erilex.data.CharArray;
import erilex.data.CharStream;
import erilex.data.generic.MaybeBoolean;
import erilex.tree.TreeBuildingHandler;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author ertri
 */
public class Grammar {

    public SortedMap<String, Production> ruleMap = new TreeMap();
    public Handler handler = new TreeBuildingHandler();
    public Production ignore = null;
    public Production ignoreStar = null;
    public String startSymbol;
    

    public boolean parse(String startSymbol, CharStream string) {
        Production p = ruleMap.get(startSymbol);
        if (p == null) {
            System.err.println("Rule " + startSymbol + " not found!");
        }
        if(ignoreStar!=null)
        ignoreStar.match(string, false, true);
        boolean ret = p.match(string, true, false);
        if (ret) {
        if(ignoreStar!=null)
            ignoreStar.match(string, false, true);
        }
        return ret && string.next() == -1;

    }

    public boolean parse(String startSymbol, String str) {
        return this.parse(startSymbol, new CharArray(str.toCharArray()));
    }
    public boolean parse(String str) {
        return this.parse(startSymbol, new CharArray(str.toCharArray()));
    }
    public void dumpRuleMap() {
        for (String key : ruleMap.keySet()) {
            final Production rule = ruleMap.get(key);
            System.out.println((rule.getProdName() == null? "*":"") +key + "->" + rule);
        }
    }

    public boolean isLL1(String s) {
        return ruleMap.get(s).isLL1() != MaybeBoolean.f;
    }
}
