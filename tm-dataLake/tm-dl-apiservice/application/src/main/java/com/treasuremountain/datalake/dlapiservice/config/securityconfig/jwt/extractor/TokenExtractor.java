/**
* Copyright © 2016-2017 The Polarisboard Authors
 */
package com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.extractor;

import javax.servlet.http.HttpServletRequest;

public interface TokenExtractor {
    public String extract(HttpServletRequest request);
}