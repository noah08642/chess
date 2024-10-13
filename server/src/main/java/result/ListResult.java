package result;

import dataaccess.IntegerID;
import model.GameData;

import java.util.Map;

public record ListResult(Map<IntegerID, GameData> games) {
    // not sure what kind of data object they would like here...
}
