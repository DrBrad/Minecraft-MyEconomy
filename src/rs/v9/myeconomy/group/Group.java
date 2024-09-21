package rs.v9.myeconomy.group;

import java.util.UUID;

public interface Group {

    boolean canClaim(UUID uuid);

    UUID getKey();

    String getName();

    int getType();

    int getColor();
}
