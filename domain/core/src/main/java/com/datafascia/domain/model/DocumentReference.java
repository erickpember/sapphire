// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a DocumentReference model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DocumentReference")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_DOCUMENT_REFERENCE_ID)
public class DocumentReference {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<DocumentReference> id;

  /** Who/what is the subject of the document. */
  @JsonProperty("subject")
  private DocumentReferenceSubject subject;

  /** Kind of document. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Categorization of document, Fhir name "class" is reserved keyword in Java. */
  @JsonProperty("documentReferenceClass")
  private CodeableConcept documentReferenceClass;

  /** Format/content rules for the document. */
  @JsonProperty("formats")
  private List<URI> formats;

  /** Who and/or what authored the document. */
  @JsonProperty("authors")
  private List<DocumentReferenceAuthor> authors;

  /** Org which maintains the document. */
  @JsonProperty("custodianId")
  private Id<Organization> custodianId;

  /** Who/what authenticated the document. */
  @JsonProperty("authenticator")
  private DocumentReferenceAuthenticator authenticator;

  /** Document creation time. */
  @JsonProperty("created") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant created;

  /** When this document reference (was) created. */
  @JsonProperty("indexed") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant indexed;

  /** Current | superceded | entered-in-error. */
  @JsonProperty("status")
  private DocumentReferenceStatus status;

  /** Preliminary | final | appended | amended | entered-in-error. */
  @JsonProperty("docStatus")
  private ReferredDocumentStatus docStatus;

  /** Relationships to other documents. */
  @JsonProperty("relatesTo")
  private List<DocumentReferenceRelatesTo> relatesTo;

  /** Human-readable description (title). */
  @JsonProperty("description")
  private String description;

  /** Document security-tags. */
  @JsonProperty("confidentiality")
  private CodeableConcept confidentiality;

  /** Where to access the document. */
  @JsonProperty("contents")
  private List<Attachment> contents;

  /** Clinical context of document. */
  @JsonProperty("context")
  private DocumentReferenceContext context;
}
