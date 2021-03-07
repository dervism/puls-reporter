package no.dervis.puls.model.filters;

import no.dervis.puls.model.survey.Response;
import no.dervis.puls.model.survey.Responses.PulseRatedResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Predicate;

public record Column(List<Response<?>> responses) {
    public Column() {
        this(new LinkedList<>());
    }

    public OptionalDouble average() {
        return responses.stream()
                .filter(response -> Integer.TYPE.isInstance(response.response()))
                .mapToInt(response -> (int) response.response())
                .average();
    }

    public OptionalInt min() {
        return responses.stream()
                .filter(response -> Integer.TYPE.isInstance(response.response()))
                .mapToInt(response -> (int) response.response())
                .min();
    }

    public OptionalInt max() {
        return responses.stream()
                .filter(response -> Integer.TYPE.isInstance(response.response()))
                .mapToInt(response -> (int) response.response())
                .max();
    }

    public int countInt(Predicate<PulseRatedResponse> predicate) {
        return responses.stream()
                .map(response -> (PulseRatedResponse)response)
                .filter(predicate)
                .mapToInt(e -> 1)
                .sum();
    }

    public int countAllInts(List<Predicate<PulseRatedResponse>> predicates) {
        return predicates
                .stream()
                .map(this::countInt)
                .mapToInt(i -> i)
                .sum();
    }
}
