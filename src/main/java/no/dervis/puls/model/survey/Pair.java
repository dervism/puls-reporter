package no.dervis.puls.model.survey;

public record Pair<LEFT, RIGHT>(LEFT left, RIGHT right) {
    public static Pair<String, String> of(String left, String right) {
        return new Pair<>(left, right);
    }
    public static Pair<String, Integer> of(String left, Integer right) {
        return new Pair<>(left, right);
    }
    public static Pair<Integer, Integer> of(Integer left, Integer right) {
        return new Pair<>(left, right);
    }
}
