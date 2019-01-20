package haumsweetohm.cbv;

public enum MusicStatus {
    PLAY("play"),
    PAUSE("pause"),
    STOP("stop");

    private final Object[] values;

    MusicStatus(Object... vals) {
        values = vals;
    }

    public String value() {
        return (String) values[0];
    }
}
