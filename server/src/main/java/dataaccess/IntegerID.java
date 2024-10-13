package dataaccess;

import java.util.Objects;

public final class IntegerID {
    private final int id;

    public IntegerID(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (IntegerID) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "IntegerId[" +
                "id=" + id + ']';
    }

}
