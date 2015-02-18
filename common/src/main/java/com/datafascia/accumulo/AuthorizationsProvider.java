// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import javax.inject.Provider;
import org.apache.accumulo.core.security.Authorizations;

/**
 * Gets authorizations for performing Accumulo operations.
 */
public interface AuthorizationsProvider extends Provider<Authorizations> {
}
