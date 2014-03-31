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
package erilex.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ertri
 */
public class FileStream implements CharStream {
    File file;
    private RandomAccessFile reader;
    private long max = 0;
    public long position = 0;

    public FileStream(File fileParam) throws FileNotFoundException {
        this.file = fileParam;
        this.reader = new RandomAccessFile(file, "r");
    }

    public int next() {
        try {
            int ch = reader.read();
            if(ch!=-1)
                position ++;
            return ch;
        } catch (IOException ex) {
            return -1;
        } finally {
        }
    }

    FileStreamLabel[] labelMap = new FileStreamLabel[1024*1024];
    public FileStreamLabel mark() {
        final int pos = (int) position;
        if(max < pos)
            max = pos;
        if(labelMap[pos] != null) {
            return labelMap[pos];
        }
        final FileStreamLabel fileStreamLabel = new FileStreamLabel(pos);
        labelMap[pos] = fileStreamLabel;
        return fileStreamLabel;
    }

    public void reset(Label label) {
        try {
            reader.seek(position = label.getPosition());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getString(Label from, Label to) {
        try {
            long cpos = position;
            reader.seek(from.getPosition());
            int n = 0;
            int len = (int) (to.getPosition() - from.getPosition());
            byte[] cbuf = new byte[len];
            while (n < len) {
                n += reader.read(cbuf, n, len - n);
            }
            reader.seek(cpos);
            return new String(cbuf);
        } catch (IOException ex) {
            Logger.getLogger(FileStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void unmark(Label label) {
    }

    public long position() {
            return position;
    }

    public int lookahead(int off) {
        Label l = mark();
        int ch;
        do {
            ch = next();
        }
        while(--off >= 0);
        reset(l);
        unmark(l);
        return ch;

    }

    public long maxMarkedPosition() {
        return max;
    }

    public void seek(long pos) {
        try {
            reader.seek(position = pos);
        } catch (IOException ex) {
            Logger.getLogger(FileStream.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class FileStreamLabel implements Label {

        private long pos;

        public FileStreamLabel(long pos) {
            this.pos = pos;
        }

        public long getPosition() {
            return pos;
        }
    }
}
