package com.tiger.yunda.data.internet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResult {
    private String token;
    private String expires;
    private String tokenType;
}
