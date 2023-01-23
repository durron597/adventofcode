package org.jared.mix;

import io.vavr.control.Option;
import lombok.Getter;

import java.util.*;

/* Class ScapeGoatTree */
public class ScapeGoatTree<T extends Identifiable> implements Iterable<T>
{
    @Override
    public Iterator<T> iterator() {
        return new SGTIterator(getFirstEntry());
    }

    @Override
    public Spliterator<T> spliterator() {
        return Spliterators.spliterator(iterator(), n, Spliterator.ORDERED | Spliterator.SIZED);
    }

    class SGTIterator implements Iterator<T> {
        SGTNode next;

        SGTIterator(SGTNode next) {
            this.next = next;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            if (next == null) throw new NoSuchElementException();
            SGTNode e = next;
            next = successor(e);

            return e.getValue();
        }
    }

    public class SGTNode implements Identifiable
    {
        SGTNode right, left, parent;

        @Getter
        T value;

        int lCount;

        /* Constructor */
        public SGTNode(T value)
        {
            this.value = value;
        }

        public int getId() {
            return value.getId();
        }
    }

    private SGTNode root;
    private int n, q;

    /* Constructor */
    public ScapeGoatTree()
    {
        root = null;
        // size = 0
        n = 0;
    }
    /* Function to check if tree is empty */
    public boolean isEmpty()
    {
        return root == null;
    }
    /* Function to clear  tree */
    public void makeEmpty()
    {
        root = null;
        n = 0;
    }
    /* Function to count number of nodes recursively */
    private int size(SGTNode r)
    {
        if (r == null)
            return 0;
        else
        {
            int l = 1;
//            if (size(r.left) != r.lCount) throw new RuntimeException();
            l += r.lCount;
            l += size(r.right);
            return l;
        }
    }
    /* Functions to search for an element */
    public boolean search(int id)
    {
        return search(root, id) != null;
    }
    /* Function to search for an element recursively */
    private SGTNode search(SGTNode r, int id)
    {
        while (r != null)
        {
            int rId = r.getId();
            if (id < rId)
                r = r.left;
            else if (id > rId)
                r = r.right;
            else
            {
                return r;
            }
        }
        return null;
    }
    /* Function to return current size of tree */
    public int size()
    {
        return n;
    }

    private static int log32(int q)
    {
        final double log23 = 2.4663034623764317;
        return (int)Math.ceil(log23*Math.log(q));
    }
    /* Function to insert an element */
    public boolean insert(T x)
    {
        /* first do basic insertion keeping track of depth */
        SGTNode u = new SGTNode(x);
        int d = addWithDepth(u);

        if (d > log32(q)) {
            /* depth exceeded, find scapegoat */
            SGTNode w = u.parent;
            while (3*size(w) <= 2*size(w.parent))
                w = w.parent;
            rebuild(w.parent);
        }

        return d >= 0;
    }

    private void splice(SGTNode u) {
        SGTNode s, p;
        if (u.left != null) {
            s = u.left;
        } else {
            s = u.right;
        }
        if (u == root) {
            root = s;
            p = null;
        } else {
            p = u.parent;
            if (p.left == u) {
                p.left = s;
                p.lCount = size(s);
            } else {
                p.right = s;
            }
            SGTNode pp = p;
            while (pp.parent != null) {
                if (pp.parent.left == pp)
                   pp.parent.lCount--;
                pp = pp.parent;
            }
        }
        if (s != null) {
            s.parent = p;
        }
        n--;
    }

    private void remove(SGTNode u) {
        if (u.left == null || u.right == null) {
            splice(u);
        } else {
            SGTNode w = u.right;
            while (w.left != null)
                w = w.left;
            u.value = w.value;
            splice(w);

        }
    }

    private T unbalancedRemove(int id) {
        SGTNode node = search(root, id);
        if (node == null) return null;
        T value = node.getValue();
        remove(node);
        return value;
    }

    public T remove(int id) {
        T removed = unbalancedRemove(id);
        if (removed != null) {
            if (2*n < q) {
                rebuild(root);
                q = n;
            }
            return removed;
        }
        return null;
    }

    /* Function to rebuild tree from node u */
    private void rebuild(SGTNode u)
    {
        int ns = size(u);
        SGTNode p = u.parent;
        Identifiable[] a = new Identifiable[ns];
        packIntoArray(u, a, 0);
        if (p == null)
        {
            root = buildBalanced(a, 0, ns);
            root.parent = null;
        }
        else if (p.right == u)
        {
            p.right = buildBalanced(a, 0, ns);
            p.right.parent = p;
        }
        else
        {
            p.left = buildBalanced(a, 0, ns);
            p.left.parent = p;
        }
    }
    /* Function to packIntoArray */
    private int packIntoArray(SGTNode u, Identifiable[] a, int i)
    {
        if (u == null)
        {
            return i;
        }
        i = packIntoArray(u.left, a, i);
        a[i++] = u;
        return packIntoArray(u.right, a, i);
    }
    /* Function to build balanced nodes */
    @SuppressWarnings("unchecked")
    private SGTNode buildBalanced(Identifiable[] a, int i, int ns)
    {
        if (ns == 0)
            return null;
        int m = ns / 2;

        SGTNode theNode = (SGTNode) a[i + m];

        theNode.left = buildBalanced(a, i, m);
        theNode.lCount = m;
        if (theNode.left != null)
            theNode.left.parent = theNode;
        theNode.right = buildBalanced(a, i + m + 1, ns - m - 1);
        if (theNode.right != null)
            theNode.right.parent = theNode;
        return theNode;
    }
    /* Function add with depth */
    private int addWithDepth(SGTNode u)
    {
        SGTNode w = root;
        int sizeU = size(u);
        if (w == null)
        {
            root = u;
            n++;
            q++;
            return 0;
        }
        boolean done = false;
        int d = 0;
        do {

            if (u.getId() < w.getId())
            {
                if (w.left == null)
                {
                    w.left = u;
                    u.parent = w;
                    w.lCount += sizeU;
                    done = true;
                }
                else
                {
                    w.lCount += sizeU;
                    w = w.left;
                }
            }
            else if (u.getId() > w.getId())
            {
                if (w.right == null)
                {
                    w.right = u;
                    u.parent = w;
                    done = true;
                }
                w = w.right;
            }
            else
            {
                return -1;
            }
            d++;
        } while (!done);
        n++;
        q++;

        return d;
    }

    SGTNode successor(SGTNode t) {
        if (t == null)
            return null;
        else if (t.right != null) {
            SGTNode p = t.right;
            while (p.left != null)
                p = p.left;
            return p;
        } else {
            SGTNode p = t.parent;
            SGTNode ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    // Function to find k'th largest element in BST
    // Here count denotes the number of nodes processed so far
    public T kthSmallest(int k) {
        return kthSmallest(root, k + 1).getValue();
    }

    private SGTNode kthSmallest(SGTNode r, int k) {
        // base case
        if (r == null)
            return null;

        int count = r.lCount + 1;
        if (count == k)
            return r;

        if (count > k)
            return kthSmallest(r.left, k);

        // else search in right subtree
        return kthSmallest(r.right, k - count);
    }

    public int rank(T value) {
        return rank(root, value.getId(), 0);
    }

    private int rank(SGTNode r, int x, int current) {
        if (r == null) {
            return current;
        }
        if (r.getId() < x) {
            if (r.left == null) {
                current++;
            } else {
                current = current + 1 + r.lCount;
            }
            return rank(r.right, x, current);
        } else {
            return rank(r.left, x, current);
        }
    }

    @Override
    public String toString() {
        return "SGTNode[" + inorder(root) + "]";
    }

    public void verifyInorderSize() {
        verifyInorderSize(root);
    }

    private void verifyInorderSize(SGTNode r) {
        if (r == null) return;
        verifyInorderSize(r.left);
        if (size(r.left) != r.lCount) {
            throw new RuntimeException();
        }
        verifyInorderSize(r.right);
    }

    private String inorder(SGTNode r)
    {
        StringBuilder sb = new StringBuilder();
        if (r != null) {
            sb.append(inorder(r.left));
            sb.append(r.value);
            sb.append(",");
            sb.append(inorder(r.right));
        }
        return sb.toString();
    }

    final SGTNode getFirstEntry() {
        SGTNode p = root;
        if (p != null)
            while (p.left != null)
                p = p.left;
        return p;
    }
}