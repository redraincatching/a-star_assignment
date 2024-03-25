public class Node {
    int f, g, h, x, y;   // can store g and h in the nodes themselves?
    Node parent = null; // useful
    // also like hell am i using setters and getters for this

    public Node(int x, int y) {
        f = 0;
        g = 0;
        h = 0;
        this.x = x;
        this.y = y;
    }

    // setters
    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setF() {
        f = g + h;
    }

    public void setG(int g) {
        this.g = g;
    }

    public void setH(int h) {
        this.h = h;
    }
}
