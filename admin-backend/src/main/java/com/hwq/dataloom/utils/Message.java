package com.hwq.dataloom.utils;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Message {
    private String role;
    private String content;
}