/*
 Copyright 2006, 2007, 2008, 2009 Hao Xu
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
package erilex.data.generic;

import java.util.Arrays;

public class Tree<T> {
	
	public Tree<T>[] subtrees;
	
	public T val;
	
	public Tree(T val, Tree<T>... subtrees) {
		super();
		this.val = val;
		this.subtrees = subtrees;
	}
	
	public int degree() {
		return this.subtrees.length;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + Arrays.hashCode(subtrees);
		result = PRIME * result + ((val == null) ? 0 : val.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Tree other = (Tree) obj;
		if (!Arrays.equals(subtrees, other.subtrees))
			return false;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(this.val);
		if(subtrees.length>0) {
		s.append("(");
		for(Tree<T> sub : subtrees) {
			s.append(sub.toString());
			s.append(",");
		}
		s.deleteCharAt(s.length()-1);
		s.append(")");
		}
		return s.toString();
		
	}
	
}
