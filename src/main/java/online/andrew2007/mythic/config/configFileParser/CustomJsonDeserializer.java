package online.andrew2007.mythic.config.configFileParser;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import online.andrew2007.mythic.config.ConfigLoader;

import java.util.HashSet;
import java.util.Set;

public interface CustomJsonDeserializer<T> extends JsonDeserializer<T> {
    static int getCharCount(String string, char character) {
        return string.split(String.valueOf(character), -1).length - 1;
    }

    default boolean readBoolean(JsonElement jsonElement) throws JsonParseException {
        JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
        if (!jsonPrimitive.isBoolean()) {
            throw new JsonParseException("Wrong type: non boolean value!");
        }
        return jsonPrimitive.getAsBoolean();
    }

    default Identifier parseIdentifier(JsonElement jsonElement) throws JsonParseException {
        String rawIdentity = jsonElement.getAsString();
        if (rawIdentity.isBlank()) {
            throw new JsonParseException(String.format("The identity \"%s\" is blank.", rawIdentity));
        }
        int colonCount = getCharCount(rawIdentity, ':');
        try {
            if (colonCount == 0) {
                return Identifier.ofVanilla(rawIdentity);
            } else if (colonCount == 1) {
                String[] segments = rawIdentity.split(":", -1);
                String nameSpace = segments[0];
                String path = segments[1];
                if (nameSpace.isBlank() || path.isBlank()) {
                    throw new JsonParseException(String.format("The identity \"%s\" has blank content.", rawIdentity));
                }
                return Identifier.of(nameSpace, path);
            } else {
                throw new JsonParseException(String.format("The identity \"%s\" has wrong format.", rawIdentity));
            }
        } catch (InvalidIdentifierException e) {
            throw new JsonParseException(e);
        }
    }

    default Item getItem(Identifier itemIdentifier) throws JsonParseException {
        Item item = ConfigLoader.getAllItems().get(itemIdentifier);
        if (item == null) {
            throw new JsonParseException(String.format("Item \"%s\" doesn't exist!", itemIdentifier));
        } else {
            return item;
        }
    }

    default void checkKeys(Set<String> expected, Set<String> present, boolean requireFull) throws JsonParseException {
        HashSet<String> excessKeys = new HashSet<>(present);
        excessKeys.removeAll(expected);
        HashSet<String> missingKeys = new HashSet<>(expected);
        missingKeys.removeAll(present);
        boolean error = false;
        StringBuilder errorMessage = new StringBuilder();
        if (!excessKeys.isEmpty()) {
            error = true;
            errorMessage.append(String.format("Excess keys: %s.", excessKeys));
        }
        if (!missingKeys.isEmpty() && requireFull) {
            error = true;
            errorMessage.append(String.format("Missing keys: %s.", missingKeys));
        }
        if (error) {
            throw new JsonParseException(String.format("Wrong config structure. %s", errorMessage));
        }
    }
}
