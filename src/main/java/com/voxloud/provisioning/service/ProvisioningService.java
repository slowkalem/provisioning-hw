package com.voxloud.provisioning.service;

import org.springframework.http.ResponseEntity;

public interface ProvisioningService {

    ResponseEntity<?> getProvisioningFile(String macAddress);
}
