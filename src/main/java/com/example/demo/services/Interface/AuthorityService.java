package com.example.demo.services.Interface;

import java.util.Set;

public interface AuthorityService {
	Set<String> computeEffectiveAuthorities(Long userId, Long schoolId);

	/** Keep existing signature used by your code. Baseline = global roles + overrides. */
	Set<String> computeEffectiveAuthorities(Long userId);
}
