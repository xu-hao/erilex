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

import java.math.BigInteger;
import java.util.Stack;
import erilex.Handler;
import erilex.Production;
import erilex.data.CharStream;

/**
 *
 * @author ertri
 */
public class CalculatorHandler implements Handler {

    private Stack<BigInteger> stack = new Stack();
    private Stack<String> opStack = new Stack();
    public BigInteger res = null;
    private String op = null;

    public void handle(Production production, CharStream cs, CharStream.Label start, CharStream.Label finish) {
        String name = production.getProdName();
        if ("op".equals(name)) {
            op = cs.getString(start, finish);
        } else if ("NUM".equals(name)) {
            res = new BigInteger(cs.getString(start, finish));
        } else if ("term".equals(name)) {
            BigInteger local = res;
            res = stack.pop();
            op = opStack.pop();
            if (op == null) {
                res = local;
            } else {
                if (op.equals("+")) {
                    res = res.add(local);
                } else if (op.equals("-")) {
                    res = res.subtract(local);
                } else if (op.equals("*")) {
                    res = res.multiply(local);
                } else if (op.equals("/")) {
                    res = res.divide(local);
                } else if (op.equals("^")) {
                    res = res.pow(local.intValue());
                } else {
                    // error
                }
                op = null;
            }
        } else if ("factorial".equals(name)) {
            res = factorial(res);
        }
    }

    public void start(Production production, CharStream cs) {
        if ("term".equals(production.getProdName())) {
            stack.push(res);
            res = null;
            opStack.push(op);
            op = null;
        }
    }

    public void failed(Production production, CharStream cs) {
        if ("term".equals(production.getProdName())) {
            res = stack.pop();
            op = opStack.pop();
        }
    }

    public static BigInteger factorial(BigInteger res) {
        BigInteger fac = BigInteger.ONE;
        for (BigInteger i = res; i.compareTo(BigInteger.ONE) >= 0; i = i.subtract(BigInteger.ONE)) {
            fac = fac.multiply(i);
        }
        return fac;
    }
}
