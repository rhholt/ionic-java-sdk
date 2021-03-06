package com.ionic.sdk.agent.request.getkey;

import com.ionic.sdk.agent.Agent;
import com.ionic.sdk.agent.key.KeyAttributesMap;
import com.ionic.sdk.agent.request.base.AgentRequestBase;
import com.ionic.sdk.agent.request.base.MessageBase;
import com.ionic.sdk.agent.service.IDC;
import com.ionic.sdk.agent.transaction.AgentTransactionUtil;
import com.ionic.sdk.core.value.Value;
import com.ionic.sdk.error.IonicException;
import com.ionic.sdk.json.JsonIO;
import com.ionic.sdk.json.JsonSource;
import com.ionic.sdk.json.JsonTarget;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Encapsulation of helper logic associated with GetKeys SDK API.  Includes state associated with request, and
 * conversion of request object into json representation, for submission to IDC.
 */
public class GetKeysMessage extends MessageBase {

    /**
     * Constructor.
     *
     * @param agent the {@link com.ionic.sdk.key.KeyServices} implementation
     * @throws IonicException on random number generation failure
     */
    public GetKeysMessage(final Agent agent) throws IonicException {
        super(agent, AgentTransactionUtil.generateConversationIdV(agent.getActiveProfile().getDeviceId()));
    }

    /**
     * Assemble the "data" json associated with the request.
     *
     * @param requestBase the user-generated object containing the attributes of the request
     * @return a {@link JsonObject} to be incorporated into the request payload
     * @throws IonicException on cryptography errors (used by protected attributes feature)
     */
    @Override
    protected final JsonObject getJsonData(final AgentRequestBase requestBase) throws IonicException {
        GetKeysRequest getKeysRequest = (GetKeysRequest) requestBase;
        final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        JsonTarget.add(objectBuilder, IDC.Payload.PROTECTION_KEYS, JsonTarget.toJsonArray(getKeysRequest.getKeyIds()));
        // external-id
        final JsonObjectBuilder protectionKeyQueries = Json.createObjectBuilder();
        for (final String id : getKeysRequest.getExternalIds()) {
            final JsonObjectBuilder externalId = Json.createObjectBuilder();
            JsonTarget.addNotNull(externalId, IDC.Payload.IONIC_EXTERNAL_ID, id);
            JsonTarget.addNotNull(protectionKeyQueries, id, externalId.build());
        }
        JsonTarget.add(objectBuilder, IDC.Payload.PROTECTION_KEY_QUERIES, protectionKeyQueries.build());
        return objectBuilder.build();
    }

    /**
     * Extract the attributes from the json structure in the parameter string.
     *
     * @param attrs    the json-serialized attributes string in the payload
     * @param keyId    the key id used as AAD
     * @param keyBytes if using encrypted attributes, we need a key to decrypt
     * @return the map of attribute keys to the list of values associated with the key
     * @throws IonicException on failure parsing the attribute json, or on cryptography errors
     *                        (used by protected attributes feature)
     */
    public final KeyAttributesMap getJsonAttrs(final String attrs, final String keyId,
                                               final byte[] keyBytes) throws IonicException {
        final KeyAttributesMap keyAttributes = new KeyAttributesMap();
        if (!Value.isEmpty(attrs)) {
            final JsonObject jsonObject = JsonIO.readObject(attrs);
            final Iterator<Map.Entry<String, JsonValue>> iterator = JsonSource.getIterator(jsonObject);
            while (iterator.hasNext()) {
                final Map.Entry<String, JsonValue> entry = iterator.next();
                final String key = entry.getKey();
                final JsonValue value = super.isIonicProtect(key)
                        ? super.decryptIonicAttrs(entry.getValue(), keyId, keyBytes) : entry.getValue();
                final JsonArray jsonArray = JsonSource.toJsonArray(value, key);
                final List<String> values = new ArrayList<String>();
                for (final JsonValue jsonValue : jsonArray) {
                    values.add(JsonSource.toString(jsonValue));
                }
                keyAttributes.put(key, values);
            }
        }
        return keyAttributes;
    }
}
