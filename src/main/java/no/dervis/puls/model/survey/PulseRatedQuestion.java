package no.dervis.puls.model.survey;

import java.util.List;
import java.util.stream.Collectors;

public record PulseRatedQuestion(String question, List<Integer> ratings)
        implements RatedQuestion {

    public PulseRatedQuestion(String question) {
        this(question, List.of());
    }

    @Override
    public int minRating() {
        return ratings.stream().min(Integer::compareTo).orElse(MIN);
    }

    @Override
    public int maxRating() {
        return ratings.stream().max(Integer::compareTo).orElse(MAX);
    }

    @Override
    public List<Integer> ratings() {
        return ratings
                .stream()
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());
    }
}
