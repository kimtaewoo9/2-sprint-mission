package com.sprint.mission.discodeit.enums;

import lombok.Getter;

@Getter
public enum MANAGE_USER {

    CREATE_USER(1, "사용자 등록"),
    FIND_USER(2, "사용자 정보 조회"),
    FIND_ALL_USER(3, "모든 사용자 정보 조회"),
    UPDATE_USER(4, "사용자 이름 수정"),
    DELETE_USER(5, "사용자 정보 삭제"),
    END(9, "사용자 관리 종료");

    private final int number;
    private final String text;


    MANAGE_USER(int number, String text){
        this.number = number;
        this.text = text;
    }

    public static MANAGE_USER findByNumber(int number){
        for(MANAGE_USER option : MANAGE_USER.values()){
            if(number == option.number){
                return option;
            }
        }
        return null;
    }
}
