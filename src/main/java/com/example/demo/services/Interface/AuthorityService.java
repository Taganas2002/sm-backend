package com.example.demo.services.Interface;

import java.util.Set;

public interface AuthorityService {
  Set<String> computeEffectiveAuthorities(Long userId);
}
