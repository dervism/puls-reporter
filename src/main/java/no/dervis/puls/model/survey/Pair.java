package no.dervis.puls.model.survey;

public record Pair<LEFT, RIGHT>(LEFT left, RIGHT right) {
    public static Pair<String, Integer> of(String left, int right) {
        return new Pair<>(left, right);
    }
    public static Pair<Integer, String> of(Integer left, String right) {
        return new Pair<>(left, right);
    }

    public record IntPair(int left, int right) {
        public static IntPair of(int left, int right) {
            return new IntPair(left, right);
        }
    }

    public record StringPair(String left, String right) {
        public static StringPair of(String left, String right) {
            return new StringPair(left, right);
        }
    }
}
