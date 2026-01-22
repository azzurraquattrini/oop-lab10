package it.unibo.mvc;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * This class implements a view that can write on any PrintStream.
 */
public final class PrintStreamView implements DrawNumberView {

    private final PrintStream out;

    /**
     * Builds a {@link PrintStreamView} that writes on the given {@link PrintStream}.
     *
     * @param stream the {@link PrintStream} where to write
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "PrintStream stored by purpose for output operations"
    )
    public PrintStreamView(final PrintStream stream) {
        out = stream;
    }

    /**
     * Builds a {@link PrintStreamView} that writes on file, given a path.
     *
     * @param path a file path
     * @throws FileNotFoundException if the file is not present
     */
    public PrintStreamView(final String path) throws FileNotFoundException {
        out = new PrintStream(new FileOutputStream(path), true, StandardCharsets.UTF_8);
    }

    @Override
    public void setObserver(final DrawNumberViewObserver observer) {
        /*
         * This UI is output only.
         */
    }

    @Override
    public void start() {
        /*
         * PrintStreams are always ready.
         */
    }

    @Override
    public void numberIncorrect() {
        out.println("You must enter a number");
    }

    @Override
    public void result(final DrawResult res) {
        out.println(res.getDescription());
    }

    @Override
    public void displayError(final String message) {
        out.println("ERROR: " + message);
    }

}
