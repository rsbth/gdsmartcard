package gdsmartcard.io;

import java.security.Provider;

public class GDProvider extends Provider {

    private static final long   serialVersionUID      = 1L;
    private static final String PROVIDER_NAME         = "gdsmartcardio";
    private static final double PROVIDER_VERSION      = 1.0d;
    private static final String PROVIDER_INFO         = "Giesecke & Devrient Smart Card Provider";

    public static final String  TERMINAL_FACTORY_NAME = "G&D";

    public GDProvider() {
        super(PROVIDER_NAME, PROVIDER_VERSION, PROVIDER_INFO);
        put("TerminalFactory." + TERMINAL_FACTORY_NAME, GDTerminalFactorySpi.class.getName());
    }

}
