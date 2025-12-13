package org.resq.firepulseapi.detectionservice.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Conversion {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> jsonNodeToMap(JsonNode jsonNode) {
        if (jsonNode == null || jsonNode.isNull() || jsonNode.isMissingNode()) {
            return Map.of();
        }

        if (!jsonNode.isObject()) {
            return Map.of();
        }

        return mapper.convertValue(jsonNode, new TypeReference<>() {});
    }

    public static JsonNode mapToJsonNode(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return mapper.createObjectNode();
        }

        return mapper.valueToTree(map);
    }
}
