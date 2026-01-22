package it.unibo.mvc;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Application entry point for DrawNumber game.
 */
public final class DrawNumberApp implements DrawNumberViewObserver {

    private final DrawNumber model;
    private final List<DrawNumberView> views;

    /**
     * Constructor.
     *
     * @param views
     *            the views to attach
     */
    public DrawNumberApp(final String configurationFile, final DrawNumberView... views) {
        /*
         * Side-effect proof
         */
        this.views = Arrays.asList(Arrays.copyOf(views, views.length));
        for (final DrawNumberView view: views) {
            view.setObserver(this);
            view.start();
        }
        
        /**
         * Configuration
         */
        final Configuration.Builder configurationBuilder = new Configuration.Builder();
        try (BufferedReader contents = new BufferedReader(
                new InputStreamReader(
                    Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(configurationFile)),
                        StandardCharsets.UTF_8))) {
            
            String fileLine = contents.readLine();
            while (fileLine != null) {
                final String[] lineElements = fileLine.split(":");
                if (lineElements.length == 2) {
                    final int value = Integer.parseInt(lineElements[1].trim());
                    switch (lineElements[0]) {
                        case "min": 
                            configurationBuilder.withMin(value);
                            break;
                        case "max":
                            configurationBuilder.withMax(value);
                            break;
                        case "attempts":
                            configurationBuilder.withAttempts(value);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown configuration key: " + lineElements[0]);
                    }
                } else {
                    throw new IllegalArgumentException("Cannot parse line: " + fileLine);
                }
                fileLine = contents.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading configuration: " + e.getMessage(), e);
        }

        final Configuration configuration = configurationBuilder.build();
        if (!configuration.isConsistent()) {
            throw new IllegalStateException();
        }
        this.model = new DrawNumberImpl(new Configuration.Builder().build());
    }

    @Override
    public void newAttempt(final int n) {
        try {
            final DrawResult result = model.attempt(n);
            for (final DrawNumberView view: views) {
                view.result(result);
            }
        } catch (final IllegalArgumentException e) {
            for (final DrawNumberView view: views) {
                view.numberIncorrect();
            }
        }
    }

    @Override
    public void resetGame() {
        this.model.reset();
    }

    @Override
    @SuppressFBWarnings(
        value = "DM_EXIT",
        justification = "Acceptable for exercising purposes."
    )
    public void quit() {
        /*
         * A bit harsh. A good application should configure the graphics to exit by
         * natural termination when closing is hit. To do things more cleanly, attention
         * should be paid to alive threads, as the application would continue to persist
         * until the last thread terminates.
         */
        System.exit(0);
    }

    /**
     * Application entry point.
     *
     * @param args
     *            ignored
     * @throws FileNotFoundException if the configuration file cannot be fetched
     */
    public static void main(final String... args) throws FileNotFoundException {
        new DrawNumberApp(
            "config.yml",
            new DrawNumberViewImpl(),
            new DrawNumberViewImpl(),
            new PrintStreamView(System.out),
            new PrintStreamView("output.log")
        );
    }

}
