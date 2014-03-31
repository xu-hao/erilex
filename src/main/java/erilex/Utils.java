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
import erilex.data.CharStream.Label;
import erilex.data.generic.Tree;

/**
 *
 * @author ertri
 */
public class Utils {

    public static boolean suppressWarning = false;

    public static void warning(Object src, String msg) {

        if(!suppressWarning)System.err.println("WARNING: " + msg);
    }

    public static void error(Object src, String msg) {

        System.err.println("ERROR: " + msg);
    }

    public static void info(Object src, String msg) {

        System.out.println("INFO: " + msg);
    }

    public static String encode(String str) {
        return str.replaceAll("\t", "\\\\t").replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
    }

    public static <T> Class<T> getType(T obj) {
        return (Class<T>)obj.getClass();
    }

    public static void printTree(Tree t, String indent, String step) {
        System.out.println(indent+t.val);
        for(Tree s : t.subtrees) {
            printTree(s, indent+step, step);
        }

    }

    public static void printError(CharStream cs) {
        Utils.error(null, errorMsg(cs, cs.maxMarkedPosition(), -1, "Not parsable! Syntax "));
    }
    public static void printError(CharStream cs, long start, long finish) {
        Utils.error(null, errorMsg(cs, start, finish, "Typing "));
    }
    public static String errorMsg(CharStream cs, long start, long finish, String msgPrefix) {
        cs.seek(0);
        int l = 1;
        int c = 1;
        String errstr = "";
        String errstr2 = "";
        int ch;
        for (int i = 0; i < start; i++) {
            if ((ch = cs.next()) != '\n' && ch!=-1) {
                c++;
                errstr += (char )ch;
                errstr2 += " ";
            } else {
                c = 1;
                errstr = "";
                errstr2 = "";
                l++;
            }
        }
        while ((ch = cs.next()) != '\n' && ch != -1 ) {
            errstr += (char) ch;
        }
        return msgPrefix+"At (" + l + "," + c + ").\n" + errstr + "\n" + errstr2 + "^";
    }

}
