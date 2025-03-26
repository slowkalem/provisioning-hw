package com.voxloud.provisioning.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OverrideFragment {
    public String domain;
    public String port;
    public int timeout;
}
