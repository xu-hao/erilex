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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 *
 * @author ertri
 */
public class Class {

    public String pack = "erilex.generated";
    public boolean abs;
    public String name;
    public Type[] superType;
    public List<String> params = new ArrayList<String>();
    public List<Method> methods = new ArrayList<Method>();
    public List<Variable> fields = new ArrayList<Variable>();
    public List<Class> classes = new ArrayList<Class>();
    public static final String PRE = "/** Code generated by EriLex */\n";
    public boolean inner = false;
    public boolean isStatic = false;
    public String[] imports;
    public String[] staticImports;
    public boolean inter = false;

    public Type getType() {
        Type[] tparams = new Type[params.size()];

        int i = 0;
        for(String p : params) {
            tparams[i++] = Generator.cons(p);
        }
        return Generator.cons(name, tparams);
    }

    public Class(String pack, boolean abs, String name, Type... superType) {
        this.pack = pack;
        this.abs = abs;
        this.name = name;
        this.superType = superType;
    }

    public Class(String pack, boolean abs, String name) {
        this.pack = pack;
        this.abs = abs;
        this.name = name;
    }

    public Class(String pack, String name) {
        this.pack = pack;
        this.name = name;
    }
    public Class(String name) {
        this.inner = true;
        this.name = name;
    }
    public Class(boolean abs, String name, Type... superType) {
        this.inner = true;
        this.abs = abs;
        this.name = name;
        this.superType = superType;
    }
    public Class(String pack, String name, Type... superType) {
        this.pack = pack;
        this.name = name;
        this.superType = superType;
    }
    public Class(String name, Type... superType) {
        this.inner = true;
        this.name = name;
        this.superType = superType;
    }

    public void writeToFile(String path) throws IOException {
        String[] s = pack.split(";")[0].split("\\.");
        for(String d : s) {
            path += File.separator+d;
        }
        File dir = new File(path);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, name+".java");
        Writer w = new BufferedWriter(new FileWriter(file));
        codeGen(w);
        w.flush();
        w.close();
    }
    public void codeGen(Writer w, String indent) throws IOException {
        // package
        if(!inner) {
            w.write(PRE);
            w.write("package " + pack + ";\n");
        }
        // imports
        if(imports!=null) {
            for(String imp : imports) {
                w.write("import "+imp+";\n");
            }
        }
        if(staticImports!=null) {
            for(String imp : staticImports) {
                w.write("import static "+pack+"."+imp+";\n");
            }
        }
        // class signature
        w.write(indent + "public " + (isStatic ? "static " : "") + (abs ? "abstract " : "") + (inter?"interface ":"class ") + name);
        // tvars
        if (params.size() > 0) {
            w.write("<");
            w.write(params.get(0));
            for (int i = 1; i < params.size(); i++) {
                w.write("," + params.get(i));
            }
            w.write(">");
        }
        final String newIndent = indent + "\t";
        if (superType != null && superType.length > 0) {
            w.write(" extends ");
            superType[0].codeGen(w,newIndent);
            if(superType.length > 1) {
            w.write(" implements ");
                for(int i =1;i<superType.length;i++) {

                    superType[i].codeGen(w,newIndent);
                    if(i<superType.length-1){
                        w.write(", ");
                    }
                }
            }
        }
        w.write(" {\n");
        // members
        for (Variable f : fields) {
            w.write(newIndent);
            f.codeGen(w,newIndent);
            w.write(";\n");
        }
        for (Method m : methods) {
            m.codeGen(w,newIndent);
        }
        for (Class c : classes) {
            c.codeGen(w,newIndent);
        }
        w.write(indent);
        w.write("}\n");//PRE);
    }

    public void renameVar(String v1, String v2) {
        for(int i =0;i<this.params.size();i++) {
            if(this.params.get(i).equals(v1)) {
                this.params.set(i, v2);
            }
        }
        for(Variable v : this.fields) {
            v.type = v.type.renameVar(v1, v2);
        }
        for(Method m : this.methods) {
            m.renameVar(v1, v2);
        }
    }

    public void codeGen(Writer w) throws IOException{
        codeGen(w, "");
    }
}