// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Represents status of an item Element in the DiagnosticOrder Model.
 */
public enum DiagnosticOrderItemStatus {
  PROPOSED,
  DRAFT,
  PLANNED,
  REQUESTED,
  RECEIVED,
  ACCEPTED,
  IN_PROGRESS,
  REVIEW,
  COMPLETED,
  CANCELLED,
  SUSPENDED,
  REJECTED,
  FAILED
}
