package com.sprint.mission.discodeit.enums;

import lombok.Getter;

@Getter
public enum MANAGE_MESSAGE {

    CREATE_MESSAGE(1, "메시지 등록"),
    FIND_MESSAGE(2, "메시지 정보 조회"),
    FIND_ALL_MESSAGE(3, "모든 메시지 조회"),
    UPDATE_MESSAGE(4, "메시지 수정"),
    DELETE_MESSAGE(5, "메시지 삭제"),
    END(9, "메시지 관리 종료");

    private final int number;
    private final String text;

    MANAGE_MESSAGE(int number, String text) {
        this.number = number;
        this.text = text;
    }

    public static MANAGE_MESSAGE findByNumber(int number) {
        for (MANAGE_MESSAGE option : MANAGE_MESSAGE.values()) {
            if (number == option.getNumber()) {
                return option;
            }
        }
        return null;
    }
}
