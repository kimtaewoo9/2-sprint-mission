package com.sprint.mission.discodeit.enums;

import lombok.Getter;

@Getter
public enum MANAGE_CHANNEL {
    
    CREATE_CHANNEL(1, "채널 등록"),
    FIND_CHANNEL(2, "채널 정보 조회"),
    FIND_ALL_CHANNEL(3, "모든 채널 조회"),
    UPDATE_CHANNEL(4, "채널 수정"),
    DELETE_CHANNEL(5, "채널 삭제"),
    END(9, "채널 관리 종료");

    private final int number;
    private final String text;

    MANAGE_CHANNEL(int number, String text) {
        this.number = number;
        this.text = text;
    }

    public static MANAGE_CHANNEL findByNumber(int number) {
        for (MANAGE_CHANNEL option : MANAGE_CHANNEL.values()) {
            if (number == option.number) {
                return option;
            }
        }
        return null;
    }
}
