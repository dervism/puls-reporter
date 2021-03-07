package no.dervis.puls.model.survey;

public sealed interface Question
        permits RatedQuestion, PulseTextQuestion {

    /**
     * The question asked in the Pulse.
     * @return the question
     */
    String question();
}
