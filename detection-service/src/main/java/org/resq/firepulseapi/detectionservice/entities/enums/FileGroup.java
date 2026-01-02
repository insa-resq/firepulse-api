package org.resq.firepulseapi.detectionservice.entities.enums;

import lombok.Getter;

@Getter
public enum FileGroup {
    LIVE("live"),
    RAW("raw");

    private final String value;

    FileGroup(String value) {
        this.value = value;
    }
}
