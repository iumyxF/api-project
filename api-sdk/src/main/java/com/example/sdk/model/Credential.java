package com.example.sdk.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fzy
 * @description: 凭证对象
 * @date 2023/4/19 9:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credential {

    private String accessKey;

    private String secretKey;
}
