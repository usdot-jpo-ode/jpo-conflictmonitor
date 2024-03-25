package us.dot.its.jpo.deduplicator.deduplicator.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Pair<T> {

    public T message;
    public boolean shouldSend;

    public Pair(T message, boolean shouldSend){
        this.message = message;
        this.shouldSend = shouldSend;
    }
}
