package com.sprint.mission.discodeit.enums;

import lombok.Getter;

@Getter
public enum MANAGE_OPTIONS {

    USER(1, "사용자 관리"),
    CHANNEL(2, "채널 관리"),
    MESSAGE(3,"메시지 관리"),
    END(9, "프로그램 종료");

    private final int number;
    private final String text;

    MANAGE_OPTIONS(int number, String text){
        this.number = number;
        this.text = text;
    }

    public static MANAGE_OPTIONS findByNumber(int number){
        for(MANAGE_OPTIONS option : MANAGE_OPTIONS.values()){
            if(number == option.number){
                return option;
            }
        }
        return null;
    }

}
