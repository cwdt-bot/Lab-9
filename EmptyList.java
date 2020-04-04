
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class EmptyList<T> extends InfiniteListImpl<T> {

    public EmptyList() {
        super(Lazy.ofNullable(null), () -> new EmptyList<>());
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public <R> EmptyList<R> map(Function<? super T, ? extends R> mapper) {
        return new EmptyList<>();
    }

    @Override
    public InfiniteListImpl<T> filter(Predicate<? super T> predicate) {
        return new EmptyList<>();
    }
}