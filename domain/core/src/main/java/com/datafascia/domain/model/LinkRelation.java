// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Enumerates a list of link relation types
 *
 * @see <a href="http://www.iana.org/assignments/link-relations/link-relations.xhtml">IANA Spec</a>
 */
public enum LinkRelation {
  About("about"),
  Alternate("alternate"),
  Appendix("appendix"),
  Archives("archives"),
  Author("author"),
  Bookmark("bookmark"),
  Canonical("canonical"),
  Chapter("chapter"),
  Collection("collection"),
  Contents("contents"),
  Copyright("copyright"),
  CreateForm("create-form"),
  Current("current"),
  Derivedfrom("derivedfrom"),
  Describedby("describedby"),
  Describes("describes"),
  Disclosure("disclosure"),
  Duplicate("duplicate"),
  Edit("edit"),
  EditForm("edit-form"),
  EditMedia("edit-media"),
  Enclosure("enclosure"),
  First("first"),
  Glossary("glossary"),
  Help("help"),
  Hosts("hosts"),
  Hub("hub"),
  Icon("icon"),
  Index("index"),
  Item("item"),
  Last("last"),
  LatestVersion("latest-version"),
  License("license"),
  Lrdd("lrdd"),
  Memento("memento"),
  Monitor("monitor"),
  MonitorGroup("monitor-group"),
  Next("next"),
  NextArchive("next-archive"),
  Nofollow("nofollow"),
  Noreferrer("noreferrer"),
  Original("original"),
  Payment("payment"),
  PredecessorVersion("predecessor-version"),
  Prefetch("prefetch"),
  Prev("prev"),
  Preview("preview"),
  Previous("previous"),
  PrevArchive("prev-archive"),
  PrivacyPolicy("privacy-policy"),
  Profile("profile"),
  Related("related"),
  Replies("replies"),
  Search("search"),
  Section("section"),
  Self("self"),
  Service("service"),
  Start("start"),
  Stylesheet("stylesheet"),
  Subsection("subsection"),
  SuccessorVersion("successor-version"),
  Tag("tag"),
  TermsOfService("terms-of-service"),
  Timegate("timegate"),
  Timemap("timemap"),
  Type("type"),
  Up("up"),
  VersionHistory("version-history"),
  Via("via"),
  WorkingCopy("working-copy"),
  WorkingCopyOf("working-copy-of");

  private String name;

  /*
   * private constructor
   */
  private LinkRelation(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
