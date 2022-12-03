package io.taig.cover;

public class Options {
    enum Mode {
        CONTAIN, COVER, FILL
    }

    public final int width;
    public final int height;
    public final Mode mode;
    public final boolean scaleUp;

    public Options(int width, int height, Mode mode, boolean scaleUp) {
        this.width = width;
        this.height = height;
        this.mode = mode;
        this.scaleUp = scaleUp;
    }

    public Options(int width, int height) {
        this(width, height, Mode.CONTAIN, false);
    }

    public Options() {
        this(500, 500);
    }

    /**
     * Set width and height independently
     *
     * @param width maximum width of the rendered image (default: 500)
     * @param height maximum height of the rendered image (default: 500)
     * @see #size(int)
     */
    public Options size(int width, int height) {
        return new Options(width, height, mode, scaleUp);
    }

    /**
     * Set width and height to the same dimension to render a square image
     *
     * @param dimension equal width and height (default: 500)
     * @see #size(int, int)
     */
    public Options size(int dimension) {
        return size(dimension, dimension);
    }

    public Options mode(Mode mode) {
        return new Options(width, height, mode, scaleUp);
    }

    public Options scaleUp(boolean enabled) {
        return new Options(width, height, mode, enabled);
    }

    public Options scaleUp() {
        return scaleUp(true);
    }
}
