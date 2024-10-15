package result;

import model.GameData;

import java.util.Map;

public record ListResult(Map<IntegerID, GameData> games) {
    // not sure what kind of data object they would like here...
    // Why is it red??  Does IntegerID need to be in shared?  I tried that and it didn't work...
}
