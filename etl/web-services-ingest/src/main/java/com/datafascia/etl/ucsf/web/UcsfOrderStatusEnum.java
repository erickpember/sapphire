// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact

package com.datafascia.etl.ucsf.web;

/**
 * A list of statuses for med and nursing orders. Comments by USCF's nursing and medication teams
 * are included.
 */
public enum UcsfOrderStatusEnum {
  /**
   * A null/invalid status. Not officially defined.
   */
  NULL,

  /**
   * Medication use: Meds that are signed and held don’t have ORD 90 populated. Don’t think this
   * applies to meds. Same as Aparna ORD 500 and 505 designate pending and held orders.
   *
   * Nursing use: This order status in ORD 90 is not used for non-med orders, to my knowledge. When
   * an order is signed and held, it is reflected in other items in ORD (such as ORD 500 and 505),
   * but NOT in ORD 90.
   */
  PENDING,

  /**
   *  Medication use: Yes. Med orders that have been signed and gone to pharmacy but not yet
   *  verified.
   *
   *  Nursing use: Yes. Correct, any order that has been signed/activated, and has gone through
   *  system logic to route it somewhere gets this order status of SENT.
   */
  SENT,

  /**
   * Medication use: Not for meds.
   *
   * Nursing use: Again, I don’t think this order status is used for non-med orders. When a lab
   * order is resulted, it is reflected in ORD 90 as “completed”.
   */
  RESULTED,

  /**
   * Medication use: Not for meds.
   *
   * Nursing use: Canceled is used for non-med orders, while discontinued is used for med orders.
   */
  CANCELED,

  /**
   * Medication use: Yes.  A med order with a defined end criteria (number of doses or number of
   * days) gets this status when the criteria is met.
   *
   * Nursing use: This is the status an order gets when it has been resulted (labs, imaging studies,
   * any studies that have interpretations tied to them), or manually completed (like a one-time
   * order completed by the nurse). This is NOT the same as expired. Expired orders today do not
   * automatically change the order status, at least for UCSF. They may have the functionality to
   * automatically cancel the order, in which case I would imagine the order status changing to
   * “Canceled” (for non-meds) or ‘Discontinued’ (for meds).
   */
  COMPLETED,

  /**
   * Medication use: No.
   *
   * Nursing use: Not sure about this one, Epic may need to chime in. I think this might have to
   * do with outpatient orders that automatically generate a referral record behind the scenes.
   */
  HOLDING,

  /**
   * Medication use: No.
   *
   * Nursing use: Not sure about this one, Epic may need to chime in.
   */
  DENIED,

  /**
   * Medication use: Yes. A PTA med that is not ordered for inpatient use gets this status (provider
   * clicks ‘don’t order’ button in admission med rec).
   *
   * Nursing use: Not sure about this one. Epic/Willow may need to chime in. Is this when a med is
   * put on MAR hold?
   */
  SUSPEND,

  /**
   * Medication use: Yes. A med order that has been discontinued by a provider.
   *
   * Nursing use: No, as mentioned above, non-med orders get ‘canceled’ status only.
   */
  DISCONTINUED,

  /**
   * Medication use: Yes once a med is verified but not yet dispensed.
   *
   * Nursing use: Not used for non-meds.
   */
  VERIFIED,

  /**
   * Medication use: Yes once a med is dispensed.
   *
   * Nursing use: Not used for non-meds.
   */
  DISPENSED,

  /**
   * Medication use: Yes.  A med order that requires two pharmacist verification gets this status
   * when the 1st verification is done but the 2nd verification is not yet complete.
   *
   * Nursing use: Not used for non-meds.
   */
  PENDINGVERIFY;

}
