package no.dervis.puls.model.survey;

public class Responses {

    public static record PulseRatedResponse(int score) implements Response<Integer>{
        @Override
        public Integer response() {
            return score;
        }
    }

    public static record PulseTextResponse(String text) implements Response<String> {
        @Override
        public String response() {
            return text;
        }
    }

    public static record PulseGenericResponse<T>(T value) implements Response<T> {
        @Override
        public T response() {
            return value;
        }
    }

}