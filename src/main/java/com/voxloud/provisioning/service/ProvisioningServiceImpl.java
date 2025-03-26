package com.voxloud.provisioning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voxloud.provisioning.dto.OverrideFragment;
import com.voxloud.provisioning.entity.Device;
import com.voxloud.provisioning.repository.DeviceRepository;
import com.voxloud.provisioning.response.ProvisioningResponse;

@Service
public class ProvisioningServiceImpl implements ProvisioningService {

    @Value("${provisioning.domain}")
    private String domain;

    @Value("${provisioning.port}")
    private String port;

    @Value("#{'${provisioning.codecs}'.split(',')}")
    private List<String> codecs;

    @Autowired
    DeviceRepository deviceRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<?> getProvisioningFile(String macAddress) {
        return deviceRepository.findById(macAddress)
                .map(device -> {
                    ProvisioningResponse response = new ProvisioningResponse()
                            .setUsername(device.getUsername())
                            .setPassword(device.getPassword())
                            .setDomain(domain)
                            .setPort(port)
                            .setCodecs(codecs);

                    if (device.getOverrideFragment() != null) {
                        try {
                            OverrideFragment overrideFragment = objectMapper.readValue(device.getOverrideFragment(),
                                    OverrideFragment.class);
                            response
                                    .setDomain(overrideFragment.getDomain())
                                    .setPort(overrideFragment.getPort())
                                    .setTimeout(overrideFragment.getTimeout());
                        } catch (Exception e) {
                            String[] lines = device.getOverrideFragment().split("\n");
                            for (String line : lines) {
                                String[] keyValue = line.split("=");
                                if (keyValue.length == 2) {
                                    String key = keyValue[0].trim();
                                    String value = keyValue[1].trim();

                                    switch (key) {
                                        case "domain":
                                            response.setDomain(value);
                                            break;
                                        case "port":
                                            response.setPort(value);
                                            break;
                                        case "timeout":
                                            response.setTimeout(Integer.parseInt(value));
                                            break;
                                    }
                                }
                            }
                        }
                    }

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}
