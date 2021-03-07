package no.dervis.puls.model.filters;

import no.dervis.puls.model.survey.Question;

import java.util.Collection;
import java.util.Set;

public class Filters {
    public enum LetterMap {
        A(1), B(2), C(3), D(4), E(5),
        F(6), G(7), H(8), I(9), J(10);

        final int index;
        LetterMap(int i) {
            this.index = i;
        }
        public int getIndex() {
            return index;
        }
    };

    public static Question byName(Set<Question> set, String text) {
        return set
                .stream()
                .filter(q -> q.question().equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow();
    }

    public static Question byId(Collection<Question> set, int id) {
        return id > 1 ? set.stream().skip(id-1).findFirst().orElseThrow() :
                set.stream().findFirst().orElseThrow();
    }

    public static Question byLetter(Collection<Question> set, String letter) {
        return byId(set, LetterMap.valueOf(letter.toUpperCase()).getIndex());
    }

}
