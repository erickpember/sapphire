// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.accumulo;

import java.util.function.Supplier;
import org.apache.accumulo.core.security.Authorizations;

/**
 * Gets authorizations for reading entries from Accumulo.
 */
public interface AuthorizationsSupplier extends Supplier<Authorizations> {
}
