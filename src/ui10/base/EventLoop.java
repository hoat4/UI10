package ui10.nodes;

import ui10.geom.Fraction;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.time.Instant.now;

public class EventLoop {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(UIThread::new);

    public void runLater(Runnable runnable) {
        executorService.execute(() -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public ScheduledFuture<?> beginAnimation(Duration duration, Consumer<Fraction> f) {
        Animation animation = new Animation(duration, f);
        animation.scheduledFuture = executorService.scheduleAtFixedRate(animation,
                0, 16, TimeUnit.MILLISECONDS);
        return animation.scheduledFuture;
    }

    private static class Animation implements Runnable {

        private final Instant begin = now();
        private final Duration duration;
        private final Consumer<Fraction> consumer;
        private ScheduledFuture<?> scheduledFuture;
        private boolean done;

        public Animation(Duration duration, Consumer<Fraction> consumer) {
            this.duration = duration;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            try {
                Duration d = Duration.between(begin, now());
                Fraction t = new Fraction(Math.toIntExact(d.toMillis()), Math.toIntExact(duration.toMillis()));

                if (!done)
                    consumer.accept(t.isAboveOne() ? Fraction.WHOLE : t); // legyen a nevező ugyanaz, ne 1?

                if (t.isAboveOne()) {
                    done = true;
                    if (scheduledFuture != null)
                        scheduledFuture.cancel(false);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
