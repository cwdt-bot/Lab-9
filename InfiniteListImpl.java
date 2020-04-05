import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class InfiniteListImpl<T> implements InfiniteList<T> {
    private final Lazy<T> head;
    private final Supplier<InfiniteListImpl<T>> tail;

    protected InfiniteListImpl(Lazy<T> head, Supplier<InfiniteListImpl<T>> tail) {
        this.head = head;
        this.tail = tail;
    }

    public static <T> InfiniteListImpl<T> generate(Supplier<? extends T> s) {
        return new InfiniteListImpl<T>(Lazy.generate(s), 
        () -> InfiniteListImpl.generate(s));
    }

    public static <T> InfiniteListImpl<T> iterate(T seed, UnaryOperator<T> next) {
        return new InfiniteListImpl<>(Lazy.ofNullable(seed), () -> InfiniteListImpl.iterate(next.apply(seed), next));
    }

    public InfiniteList<T> peek() {
        this.head.get().ifPresent(System.out::println);
        return this.tail.get();
    }

    public <R> InfiniteListImpl<R> map(Function<? super T, ? extends R> mapper) {
        Lazy<R> newHead = this.head.map(mapper);
        Supplier<InfiniteListImpl<R>> newTail = () -> this.tail.get().map(mapper);
        return new InfiniteListImpl<>(newHead, newTail);
    }

    public InfiniteListImpl<T> filter(Predicate<? super T> predicate) {
        Lazy<T> newHead = this.head.filter(predicate);
        Supplier<InfiniteListImpl<T>> newTail = () -> this.tail.get().filter(predicate);
        return new InfiniteListImpl<>(newHead, newTail);
    }

    public boolean isEmpty() {
        return false;
    }

    public InfiniteListImpl<T> limit(long n) {
        if (n <= 0) {
            return new EmptyList<>();
        } else if (n == 1) {
            Supplier<InfiniteListImpl<T>> newTail = () -> {
                if (this.head.get().isPresent()) {
                    return new EmptyList<>();
                } else {
                    return this.tail.get().limit(n);
                }
            };
            return new InfiniteListImpl<>(this.head, newTail);
        } else {
            Supplier<InfiniteListImpl<T>> newTail = () -> {
                if (this.head.get().isPresent()) {
                    return this.tail.get().limit(n - 1);
                } else {
                    return this.tail.get().limit(n);
                }
            };
            return new InfiniteListImpl<>(this.head, newTail);
        }
    }
            


    public InfiniteListImpl<T> takeWhile(Predicate<? super T> predicate) {
        Lazy<T> newHead = this.head.filter(predicate);
        return new InfiniteListImpl<>(newHead, 
        () -> {
            if (this.head.get().isPresent() && newHead.get().isEmpty()) {
                return new EmptyList<>();
            } else {
                return this.tail.get().takeWhile(predicate);
            }
        });
    }

    public void forEach(Consumer<? super T> action) {
        InfiniteListImpl<T> curr = this;
        while (!curr.isEmpty()) {
            curr.head.get().ifPresent(action);
            curr = curr.tail.get();
        }
    }

    public Object[] toArray() {
        List<T> newList = new ArrayList<>();
        InfiniteListImpl<T> curr = this;
        while (!curr.isEmpty()) {
            if (curr.head.get().isPresent()) {
                newList.add(curr.head.get().get());
            }
            curr = curr.tail.get();
        }
        return newList.toArray();
    }
    
    public long count() {
        return this.toArray().length;
    }

    public <U> U reduce (U identity, BiFunction<U, ? super T, U> accumulator) {
        InfiniteListImpl<T> curr = this;
        while (!curr.isEmpty()) {
            if (curr.head.get().isPresent()) {
                identity = accumulator.apply(identity,curr.head.get().get());
            }
            curr = curr.tail.get();
        }
        return identity;
    }
}   