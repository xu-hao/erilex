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
package erilex.codegen;

import java.util.HashMap;

/**
 *
 * @author ertri
 */
public class NameEmbedding {

    public static final String T_PREFIX = "T", D_PREFIX = "D", E_PREFIX = "";
    public static final String TLT_PREFIX = "T", TLD_PREFIX = "D", TLE_PREFIX = "";
    public static final String TrLT_PREFIX = "T", TrLD_PREFIX = "D", TrLE_PREFIX = "";

    public boolean getnat(String s) {
        return natMap.containsKey(s) && natMap.get(s);
    }

    public NameEmbedding(String prefix) {
        this.prefix = prefix;
    }
    HashMap<String, String> specialChars = new HashMap<String, String>();
    HashMap<String, Boolean> natMap = new HashMap<String, Boolean>();
    HashMap<String, Boolean> varMap = new HashMap<String, Boolean>();
    public String prefix;

    public NameEmbedding(NameEmbedding t) {
        prefix = t.prefix;
        specialChars.putAll(t.specialChars);
        natMap.putAll(t.natMap);
        varMap.putAll(t.varMap);
    }

    public void add(String key, String embedding) {
        specialChars.put(key, embedding);
    }

    public void add(String key, String embedding, boolean var, boolean nat) {
        specialChars.put(key, embedding);
        natMap.put(key, nat);
        varMap.put(key, var);
    }

    public void addVar(String key, String embedding) {
        specialChars.put(key, embedding);
        natMap.put(key, false);
        varMap.put(key, true);
    }

    public void addNatVar(String key, String embedding) {
        specialChars.put(key, embedding);
        natMap.put(key, true);
        varMap.put(key, true);
    }

    public void addNat(String key, String embedding) {
        specialChars.put(key, embedding);
        natMap.put(key, true);
        varMap.put(key, false);
    }

    public String get(String key) {
        if (specialChars.containsKey(key)) {
            return specialChars.get(key);
        } else {
            return prefix + key;
        }
    }
    /**
     * Return the translation without adding the prefix.
     * @param key
     * @return
     */
    public String getNat(String key) {
        if (specialChars.containsKey(key)) {
            return specialChars.get(key);
        } else {
            return key;
        }
    }

    public boolean nat(String key) {
        return natMap.containsKey(key) && natMap.get(key);
    }

    public boolean var(String key) {
        return varMap.containsKey(key) && varMap.get(key);
    }
}
