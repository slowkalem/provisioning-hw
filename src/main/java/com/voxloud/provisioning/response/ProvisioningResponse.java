package com.voxloud.provisioning.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ProvisioningResponse {
    private String username;
    private String password;
    private String domain;
    private String port;
    private List<String> codecs;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int timeout;
}
