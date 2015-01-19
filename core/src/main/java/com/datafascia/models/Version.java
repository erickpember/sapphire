// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Retrieves the relevant information from the manifest file and makes it available to others for
 * processing
 */
@JsonAutoDetect @Slf4j @NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName("Version")
public class Version {
  @Getter @Setter
  private long id;
  @Getter @Setter
  private String revision;
  @Getter @Setter
  private String vendor;
  @Getter @Setter
  private String title;

  /**
   * Create version details for passed package name
   *
   * @param id the identifer with which this model is created
   * @param packageName the packageName
   */
  public Version(long id, String packageName) {
    Package pkg = Package.getPackage(packageName);
    title = pkg.getImplementationTitle();
    vendor = pkg.getImplementationVendor();
    revision = pkg.getImplementationVersion();
    this.id = id;
  }
}
