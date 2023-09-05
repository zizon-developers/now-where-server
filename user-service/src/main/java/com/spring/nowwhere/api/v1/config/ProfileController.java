package com.spring.nowwhere.api.v1.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final Environment env;
    private final int DEFAULT_PROFILE_INDEX = 0;

    @GetMapping("/profile")
    public String profile() {
        List<String> profiles = Arrays.asList(env.getActiveProfiles());

        List<String> realProfile = Arrays.asList("real", "real1", "real2");
        String defaultProfile = profiles.isEmpty() ? "default" : profiles.get(DEFAULT_PROFILE_INDEX);

        return profiles.stream().filter(realProfile::contains)
                                .findAny()
                                .orElse(defaultProfile);
    }
}
