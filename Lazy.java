import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Lazy<T> {

    private T v;
    private Supplier<? extends T> s;
    private boolean ev;

    private Lazy(T v) {
        this.v = v;
        this.s = () -> v;
        this.ev = true;
    }

    private Lazy(Supplier<? extends T> s) {
        this.v = null;
        this.s = s;
        this.ev = false;
    }

    public Optional<T> get() {
        if (!ev) {
            ev = true;
            this.v = s.get();
        }
        return Optional.ofNullable(this.v);
    }

    public static <T> Lazy<T> ofNullable(T v) {
        return new Lazy<>(v);
    }

    public static <T> Lazy<T> generate(Supplier<? extends T> supplier) {
        return new Lazy<>(supplier);
    }

    public <R> Lazy<R> map(Function<? super T, ? extends R> mapper) {
        return Lazy.generate( () -> this.get().map(mapper).orElse(null));
    }

    public Lazy<T> filter(Predicate<? super T> predicate) {
        return Lazy.generate( () -> this.get().filter(predicate).orElse(null));
    }

    @Override
    public String toString() {
        if (!this.ev) {
            return "?";
        } else {
            return this.v + "";
        }
    }
}