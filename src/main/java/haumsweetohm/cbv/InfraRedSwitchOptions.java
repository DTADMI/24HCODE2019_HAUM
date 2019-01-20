package haumsweetohm.cbv;

public enum InfraRedSwitchOptions {
    POW("power"),
    MODE("mode"),
    MUTE("mute"),
    PLAYP("playp"),
    PREV("prev"),
    NEXT("next"),
    EQ("eq"),
    MINUS("minus"),
    PLUS("plus"),
    ZERO("0"),
    CHG("chg"),
    U_SD("u_sd"),
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9");

    private final Object[] values;

    InfraRedSwitchOptions(Object... vals) {
        values = vals;
    }

    public String value() {
        return (String) values[0];
    }
}
