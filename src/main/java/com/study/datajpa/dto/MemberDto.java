package com.study.datajpa.dto;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class MemberDto {
    private Long id;
    private String username;
    private String teamName;

    public MemberDto(Long id, String username, String teamName) {
        this.id = id;
        this.username = username;
        this.teamName = teamName;
    }
}
