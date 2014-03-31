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
package erilex.calculator;

import erilex.data.CharStream;
import erilex.data.CharArray;
import java.math.BigInteger;
import java.util.Stack;

/**
 *
 * @author ertri
 */
public class Main {

    public static void main(String[] args) {
        CalculatorGrammar cg = new CalculatorGrammar();
        if (args.length == 1) {
            CharStream cs = new CharArray(args[0].toCharArray());
            System.out.println(cg.parse("prog", cs) ? (cs.next() == -1 ? "" : "ERROR2!") : "ERROR1!");
            cg.semantics.trans("tree", cg.treeBuildingHandler.getAST(), null);
            cg.semantics.trans("default", cg.treeBuildingHandler.getAST(), new Stack<BigInteger>());
//            System.out.println(cg.treeBuildingHandler.getAST());
        }
    }
}
