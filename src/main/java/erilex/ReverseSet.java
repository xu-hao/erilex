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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ReverseSet<E> implements Set<E> {

    Set<E> set;
    boolean reverse = false;

    public ReverseSet(Set<E> set, boolean reverse) {
        super();
        this.set = set;
        this.reverse = reverse;
    }

    ReverseSet(ReverseSet<E> rset) {
        super();
        this.set = new HashSet<E>(rset.set);
        this.reverse = rset.reverse;
    }

    public int size() {
        return reverse?-1-set.size() : set.size();
    }

    public boolean isEmpty() {
        return reverse ? false : set.isEmpty();
    }

    public boolean contains(Object o) {
        return set.contains(o) ^ reverse;
    }

    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean add(E e) {
        if (reverse) {
            return set.remove(e);
        } else {
            return set.add(e);
        }
    }

    public boolean remove(Object o) {
        if (reverse) {
            return set.add((E) o);
        } else {
            return set.remove(o);
        }
    }

    public boolean containsAll(Collection<?> c) {
        if (reverse) {
            if (c instanceof ReverseSet) {
                if (((ReverseSet<E>) c).reverse) {
                    return ((ReverseSet<E>) c).set.containsAll(set);
                } else {
                    return Collections.disjoint(set, ((ReverseSet<E>) c).set);
                }
            } else {
                return Collections.disjoint(set, c);
            }
        } else {
            if (c instanceof ReverseSet) {
                if (((ReverseSet<E>) c).reverse) {
                    return false;
                } else {
                    return set.containsAll(((ReverseSet<E>) c).set);
                }
            } else {
                return set.containsAll(c);
            }
        }
    }

    public boolean addAll(Collection<? extends E> c) {
        if (reverse) {
            if (c instanceof ReverseSet) {
                if (((ReverseSet<E>) c).reverse) {
                    return set.retainAll(((ReverseSet<E>) c).set);
                } else {
                    return set.removeAll(((ReverseSet<E>) c).set);
                }
            } else {
                return set.removeAll(c);
            }
        } else {
            if (c instanceof ReverseSet) {
                if (((ReverseSet<E>) c).reverse) {
                    reverse = true;
                    Set<E> old = set;
                    set = new HashSet<E>(((ReverseSet<E>) c).set);
                    set.removeAll(old);
                    return true;
                } else {
                    return set.addAll(((ReverseSet<E>) c).set);
                }
            } else {
                return set.addAll(c);
            }
        }
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return (reverse ? "~" : "") + set.toString();
    }

    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
