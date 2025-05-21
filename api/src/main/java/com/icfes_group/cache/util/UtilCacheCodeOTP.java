package com.icfes_group.cache.util;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class UtilCacheCodeOTP {
    @CachePut(value = "securityCodes", key = "#email")
    public Integer createCode(String email,Integer code) {
        return code;
    }

    // Recupera el código OTP desde la caché
    @Cacheable(value = "securityCodes", key = "#email")
    public Integer getCode(String email) {
        return null;
    }

    @CacheEvict(value = "securityCodes", key = "#email")
    public void removeCode(String email) {
    }

}
