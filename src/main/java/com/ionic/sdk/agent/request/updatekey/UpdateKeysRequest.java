package com.ionic.sdk.agent.request.updatekey;

import com.ionic.sdk.agent.key.AgentKey;
import com.ionic.sdk.agent.request.base.AgentRequestBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the input for an Agent.createKeys() request.
 */
public class UpdateKeysRequest extends AgentRequestBase {

    /**
     * Represents a list of Key objects for an Agent.createKeys() request.
     */
    private final List<Key> keyRequests;

    /**
     * Constructor.
     */
    public UpdateKeysRequest() {
        this.keyRequests = new ArrayList<Key>();
    }

    /**
     * @return a list of Key objects for an Agent.createKeys() request.
     */
    public final List<Key> getKeys() {
        return keyRequests;
    }

    /**
     * Add a key request object to the {@link UpdateKeysRequest}.
     *
     * @param key the object containing the parameters of the key request
     * @deprecated
     *      Please migrate usages to the replacement {@link #addKey(Key)}
     *      method (Ionic SDK 1.x API compatibility).
     */
    @Deprecated
    public final void add(final Key key) {
        keyRequests.add(key);
    }

    /**
     * Add a key request object to the {@link UpdateKeysRequest}.
     *
     * @param key the object containing the parameters of the key request
     */
    public final void addKey(final Key key) {
        keyRequests.add(key);
    }

    /**
     * Add a key request object to the {@link UpdateKeysRequest}.
     *
     * @param key         the object containing the parameters of the key request
     * @param forceUpdate instruction for server to unconditionally overwrite the existing mutable attributes
     */
    public final void addKey(final Key key, final boolean forceUpdate) {
        keyRequests.add(new Key(key, forceUpdate));
    }

    /**
     * Retrieve the key request with the matching keyId.
     *
     * @param keyId an identifier to correlate the request
     * @return the matching key request
     */
    public final Key findKey(final String keyId) {
        Key key = null;
        for (Key keyRequest : keyRequests) {
            if (keyId.equals(keyRequest.getId())) {
                key = keyRequest;
                break;
            }
        }
        return key;
    }

    /**
     * Retrieve the key request with the matching keyId.
     *
     * @param keyId an identifier to correlate the request
     * @return the matching key request
     */
    public final Key getKey(final String keyId) {
        Key key = null;
        for (Key keyRequest : keyRequests) {
            if (keyId.equals(keyRequest.getId())) {
                key = keyRequest;
                break;
            }
        }
        return key;
    }

    /**
     * Represents a discrete key request object in the context of a {@link UpdateKeysRequest}.
     */
    public static class Key extends AgentKey {

        /**
         * Flag indicating desire to force write of updated mutable attributes in the context of this request.
         */
        private final boolean forceUpdate;

        /**
         * Constructor.
         */
        public Key() {
            super();
            this.forceUpdate = false;
        }

        /**
         * Constructor.
         *
         * @param key from which to copy attributes
         */
        public Key(final AgentKey key) {
            super(key);
            this.forceUpdate = false;
        }

        /**
         * Constructor.
         *
         * @param key         from which to copy attributes
         * @param forceUpdate instruction for server to unconditionally overwrite the existing mutable attributes
         */
        public Key(final AgentKey key, final boolean forceUpdate) {
            super(key);
            this.forceUpdate = forceUpdate;
        }

        /**
         * @return server instruction to overwrite existing mutable attributes
         * @deprecated
         *      Please migrate usages to the replacement {@link #getForceUpdate()}
         *      method (Ionic SDK 1.x API compatibility).
         */
        @Deprecated
        public final boolean isForceUpdate() {
            return forceUpdate;
        }

        /**
         * @return server instruction to overwrite existing mutable attributes
         */
        public final boolean getForceUpdate() {
            return forceUpdate;
        }
    }
}
