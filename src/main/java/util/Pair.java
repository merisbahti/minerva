package util;

public class Pair<A, B> {
    public final A fst;
    public final B snd;
    public Pair (A fst, B snd) {
        this.fst = fst;
        this.snd = snd;
    }
    public String toString() {
        return "(" + fst.toString() + ", " + snd.toString() + ")";
    }
}
