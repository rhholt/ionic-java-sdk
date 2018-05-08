package com.ionic.sdk.agent.key;

/**
 * Common metadata interface for all Ionic Keys.
 */
 public interface KeyMetadata {

     /**
      * Get the key attributes.
      *
      * @return KeyAttributesMap attributes.
      */
     KeyAttributesMap getAttributesMap();

     /**
      * Set the key attributes map.
      *
      * @param keyAttributes
      *      The key attributes map.
      */
     void setAttributesMap(KeyAttributesMap keyAttributes);

     /**
      * Get the key attributes.
      *
      * @return KeyAttributesMap attributes.
      */
     KeyAttributesMap getMutableAttributes();

     /**
      * Set the key attributes map.
      *
      * @param mutableAttributes The key attributes map.
      */
     void setMutableAttributes(KeyAttributesMap mutableAttributes);

     /**
      * Get the key obligations.
      *
      * @return KeyObligationsMap obligations.
      */
     KeyObligationsMap getObligationsMap();

     /**
      * Set the key obligations map.
      *
      * @param keyObligations
      *      The key obligations map.
      */
     void setObligationsMap(KeyObligationsMap keyObligations);
 }
