package com.frank.blog.vo.param;

import lombok.Data;

@Data
public class GithubTokenParam {
    private String access_token;
    private String  scope;
    private String token_type;
}
