package com.panda.demo.criteria;

import lombok.Builder;
import lombok.Data;

/**
 * @author Panda
 * @date 4/21/2020
 */
@Data
@Builder
public class OauthCriteria {
    private String code;
    private String client_id;
    private String scope;
    private String redirect_uri;
    private String client_secret;
    private String grant_type;
}
