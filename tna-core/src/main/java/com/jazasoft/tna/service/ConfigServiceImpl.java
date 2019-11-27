package com.jazasoft.tna.service;

import com.jazasoft.mtdb.service.AbstractConfigService;
import com.jazasoft.mtdb.service.TenantService;
import org.springframework.stereotype.Service;

/**
 * Created by mdzahidraza on 04/09/17.
 */
@Service
public class ConfigServiceImpl extends AbstractConfigService {

    public ConfigServiceImpl(TenantService tenantService) {
        super(tenantService);
    }
}
