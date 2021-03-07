package no.dervis.puls.model.survey;

public sealed interface RatedQuestion extends Question
        permits PulseRatedQuestion {

    /**
     * Default minimum value
     */
    int MIN = 1;

    /**
     * Default maximum value
     */
    int MAX = 7;

    /**
     * Representing the lowest score that can be given.
     * @return a positive int
     */
    int minRating();

    /**
     * Representing the highest score that can be given.
     * @return a positive int
     */
    int maxRating();
}
