package ui10.node;

import ui10.geom.FloatingPointNumber;
import ui10.geom.Num;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.time.Instant.now;

public class EventLoop {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public void runLater(Runnable runnable) {
        executorService.execute(() -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    public void beginAnimation(Duration duration, Consumer<Num> f) {
        Animation animation = new Animation(duration, f);
        animation.scheduledFuture = executorService.scheduleAtFixedRate(animation,
                0, 16, TimeUnit.MILLISECONDS);
    }

    private static class Animation implements Runnable {

        private final Instant begin = now();
        private final Duration duration;
        private final Consumer<Num> consumer;
        private ScheduledFuture<?> scheduledFuture;
        private boolean done;

        public Animation(Duration duration, Consumer<Num> consumer) {
            this.duration = duration;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            try {
                Duration d = Duration.between(begin, now());
                double t = d.toMillis() * 1.0 / duration.toMillis();

                if (!done)
                    consumer.accept(new FloatingPointNumber(Math.min(1, t)));
                if (t >= 1) {
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
