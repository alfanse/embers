package adf.embers.configuration;


import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class DbiFactory {
    private final DBI dbi;

    public DbiFactory(DBI dbi) {
        this.dbi = dbi;
    }

    public Handle getHandle() {
        return dbi.open();
    }
}
