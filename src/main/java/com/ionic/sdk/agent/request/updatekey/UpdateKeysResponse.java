package com.ionic.sdk.agent.request.updatekey;

import com.ionic.sdk.agent.key.AgentKey;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.agent.key.KeyObligationsMap;
import com.ionic.sdk.agent.request.base.AgentResponseBase;
import com.ionic.sdk.core.value.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the output for an Agent.updateKeys() request.
 */
public class UpdateKeysResponse extends AgentResponseBase {

    /**
     * Represents a list of Key objects for an Agent.updateKeys() response.
     */
    private final List<Key> keys;

    /**
     * Represents a list of Key objects for an Agent.updateKeys() response.
     */
    private final List<IonicError> errors;

    /**
     * Constructor.
     */
    public UpdateKeysResponse() {
        super();
        this.keys = new ArrayList<Key>();
        this.errors = new ArrayList<IonicError>();
    }

    /**
     * @return a list of Key objects for an Agent.updateKeys() response.
     */
    public final List<Key> getKeys() {
        return keys;
    }

    /**
     * @return a list of Error objects for an Agent.updateKeys() response.
     */
    public final List<IonicError> getErrors() {
        return errors;
    }

    /**
     * Add a key response object to the {@link UpdateKeysResponse}.
     *
     * @param key the object containing the parameters of the key response
     */
    public final void add(final Key key) {
        keys.add(key);
    }

    /**
     * Add a error response object to the {@link UpdateKeysResponse}.
     *
     * @param error the object containing the parameters of the key response
     */
    public final void add(final IonicError error) {
        errors.add(error);
    }

    /**
     * Retrieve the key response with the matching id.
     *
     * @param id an identifier to correlate the response
     * @return the matching key response
     */
    public final UpdateKeysResponse.Key getKey(final String id) {
        UpdateKeysResponse.Key key = null;
        for (UpdateKeysResponse.Key keyIt : keys) {
            if (id.equals(keyIt.getId())) {
                key = keyIt;
                break;
            }
        }
        return key;
    }

    /**
     * Retrieve the key error with the matching id.
     *
     * @param id an identifier to correlate the response
     * @return the matching error response
     */
    public final UpdateKeysResponse.IonicError getError(final String id) {
        UpdateKeysResponse.IonicError error = null;
        for (UpdateKeysResponse.IonicError errorIt : errors) {
            if (id.equals(errorIt.getKeyId())) {
                error = errorIt;
                break;
            }
        }
        return error;
    }

    /**
     * Represents a discrete key response object in the context of a {@link UpdateKeysResponse}.
     */
    public static class Key extends AgentKey {

        /**
         * The device id associated with the creation request.
         */
        private String deviceId;

        /**
         * The origin of the associated key.
         */
        private String origin;

        /**
         * Constructor.
         */
        public Key() {
            super();
            this.deviceId = "";
            this.origin = "";
        }

        /**
         * Constructor.
         *
         * @param agentKey the base key implementation
         * @param deviceId the associated Ionic device id
         * @param origin   the origin of the key
         */
        public Key(final AgentKey agentKey, final String deviceId, final String origin) {
            super(agentKey);
            this.deviceId = deviceId;
            this.origin = origin;
        }

        /**
         * Constructor.
         *
         * @param id       the specified key id to initialize with.
         * @param key      the specified key data to initialize with.
         * @param deviceId the associated Ionic device id
         * @throws NullPointerException When keyId or keyBytes are null.
         */
        public Key(final String id, final byte[] key, final String deviceId) {
            super(id, key);
            this.setDeviceId(deviceId);
        }

        /**
         * Constructor.
         *
         * @param id                the specified key id to initialize with.
         * @param key               the specified key data to initialize with.
         * @param deviceId          the associated Ionic device id
         * @param attributes        the specified key attributes to initialize with.
         * @param mutableAttributes the specified mutable attributes to initialize with.
         * @param obligations       the specified key obligations to initialize with.
         * @param keyOrigin         the origin of the key
         */
        public Key(final String id, final byte[] key, final String deviceId, final KeyAttributesMap attributes,
                   final KeyAttributesMap mutableAttributes, final KeyObligationsMap obligations,
                   final String keyOrigin) {
            super(id, key, attributes, mutableAttributes, obligations);
            this.setDeviceId(deviceId);
            this.setOrigin(keyOrigin);
        }

        /**
         * Constructor.
         *
         * @param id                                   the specified key id to initialize with.
         * @param key                                  the specified key data to initialize with.
         * @param deviceId                             the associated Ionic device id
         * @param attributes                           the specified key attributes to initialize with.
         * @param mutableAttributes                    the specified mutable attributes to initialize with.
         * @param obligations                          the specified key obligations to initialize with.
         * @param keyOrigin                            the origin of the key
         * @param attributesSigBase64FromServer        the server signature applied to the immutable attributes
         * @param mutableAttributesSigBase64FromServer the server signature applied to the mutable attributes
         */
        @SuppressWarnings({"checkstyle:parameternumber"})  // Java JNI SDK API compatibility
        public Key(final String id, final byte[] key, final String deviceId, final KeyAttributesMap attributes,
                   final KeyAttributesMap mutableAttributes, final KeyObligationsMap obligations,
                   final String keyOrigin,
                   final String attributesSigBase64FromServer, final String mutableAttributesSigBase64FromServer) {
            super(id, key, attributes, mutableAttributes, obligations);
            this.setDeviceId(deviceId);
            this.setOrigin(keyOrigin);
            this.setAttributesSigBase64FromServer(attributesSigBase64FromServer);
            this.setMutableAttributesSigBase64FromServer(mutableAttributesSigBase64FromServer);
        }

        /**
         * @return the device id associated with the creation request
         */
        public final String getDeviceId() {
            return deviceId;
        }

        /**
         * Set the device ID.
         *
         * @param deviceId the device identifier to associate with this key
         *                 The key attributes map.
         */
        public final void setDeviceId(final String deviceId) {
            this.deviceId = Value.defaultOnEmpty(deviceId, "");
        }

        /**
         * @return the origin of the associated key
         */
        public final String getOrigin() {
            return origin;
        }

        /**
         * Set the origin string for this cryptography key.
         *
         * @param origin the origin identifier (typically Agent.KEYORIGIN_IONIC_KEYSERVER)
         */
        public final void setOrigin(final String origin) {
            this.origin = Value.defaultOnEmpty(origin, "");
        }
    }

    /**
     * Represents a discrete error response object in the context of a {@link UpdateKeysResponse}.
     */
    public static class IonicError {

        /**
         * A String denoting the id of the key.
         */
        private String keyId;

        /**
         * The client error code (SDK client side error code).
         */
        private int clientError;

        /**
         * The server error code provided by an Ionic server.
         */
        private int serverError;

        /**
         * The server error message string provided by an Ionic server.
         */
        private String serverMessage;

        /**
         * Constructor.
         *
         * @param keyId         the key ID (also known as the key tag)
         * @param clientError   the client error code (SDK client side error code)
         * @param serverError   the server error code provided by an Ionic server
         * @param serverMessage the server error message string provided by an Ionic server
         */
        public IonicError(
                final String keyId, final int clientError, final int serverError, final String serverMessage) {
            this.keyId = keyId;
            this.clientError = clientError;
            this.serverError = serverError;
            this.serverMessage = serverMessage;
        }

        /**
         * @return The key ID (also known as the key tag).
         */
        public final String getKeyId() {
            return keyId;
        }

        /**
         * Set the key ID (key tag).
         *
         * @param keyId The key ID (also known as the key tag)
         */
        public final void setKeyId(final String keyId) {
            this.keyId = keyId;
        }

        /**
         * @return The client error code (SDK client side error code).
         */
        public final int getClientError() {
            return clientError;
        }

        /**
         * Set the client error code (SDK client side error code).
         *
         * @param clientError The client error code (SDK client side error code)
         */
        public final void setClientError(final int clientError) {
            this.clientError = clientError;
        }

        /**
         * @return The server error code provided by an Ionic server.
         */
        public final int getServerError() {
            return serverError;
        }

        /**
         * Set the server error code provided by an Ionic server.
         *
         * @param serverError the server error code provided by an Ionic server
         */
        public final void setServerError(final int serverError) {
            this.serverError = serverError;
        }

        /**
         * @return The server error message string provided by an Ionic server.
         */
        public final String getServerMessage() {
            return serverMessage;
        }

        /**
         * Set the server error message string provided by an Ionic server.
         *
         * @param serverMessage the server error message string provided by an Ionic server
         */
        public final void setServerMessage(final String serverMessage) {
            this.serverMessage = serverMessage;
        }
    }
}
