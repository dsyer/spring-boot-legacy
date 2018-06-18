/*
 * Copyright (c) 2018 CUBRC, Inc.  - Unpublished Work - All rights reserved under the copyright laws of the United States.
 * CUBRC, Inc. does not grant permission to any party outside the United States Government to use, disclose, copy, or make derivative works of this software.
 */

package org.springframework.boot.legacy.context.web.servlet.support;

import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for {@link ErrorPageFilter}.
 *
 * NOTE: Original class {@link ErrorPageFilterConfiguration} is not accessible.
 *
 * @author Daniel Cruver
 * @author Andy Wilkinson
 */
@Configuration
public class ErrorPageFilterConfiguration {

	@Bean
	public ErrorPageFilter errorPageFilter() {
		return new ErrorPageFilter();
	}

}
