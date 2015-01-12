package tmg.labs.studentmanagement.util;

public class Triple<A, B, C> {
  A first;
  B second;
  C thirst;

  public Triple(
      A first, B second, C thirst) {
    this.first = first;
    this.second = second;
    this.thirst = thirst;
  }

  public static <A, B, C> Triple<A, B, C> create(A first, B second, C thirst) {
    return new Triple<A, B, C>(first, second, thirst);
  }
}