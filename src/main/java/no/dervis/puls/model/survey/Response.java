package no.dervis.puls.model.survey;

public sealed interface Response<T> permits
        Responses.PulseGenericResponse,
        Responses.PulseRatedResponse,
        Responses.PulseTextResponse {

    T response();

}
