package com.ionic.sdk.agent.cipher.chunk;

import com.ionic.sdk.agent.cipher.chunk.data.ChunkCryptoChunkInfo;
import com.ionic.sdk.agent.cipher.chunk.data.ChunkCryptoDecryptAttributes;
import com.ionic.sdk.agent.cipher.chunk.data.ChunkCryptoEncryptAttributes;
import com.ionic.sdk.agent.key.AgentKey;
import com.ionic.sdk.agent.request.createkey.CreateKeysRequest;
import com.ionic.sdk.agent.request.createkey.CreateKeysResponse;
import com.ionic.sdk.agent.request.getkey.GetKeysRequest;
import com.ionic.sdk.agent.request.getkey.GetKeysResponse;
import com.ionic.sdk.core.codec.Transcoder;
import com.ionic.sdk.core.value.Value;
import com.ionic.sdk.error.IonicException;
import com.ionic.sdk.error.SdkError;
import com.ionic.sdk.key.KeyServices;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper object used to abstract Ionic cryptography operations.
 */
public abstract class ChunkCipherAbstract {

    /**
     * Key services implementation; used to broker key transactions and crypto operations.
     */
    private final KeyServices agent;

    /**
     * @return the key services implementation; used to broker key transactions and crypto operations
     */
    public final KeyServices getKeyServices() {
        return agent;
    }

    /**
     * Constructor.
     *
     * @param agent the key services implementation
     */
    public ChunkCipherAbstract(final KeyServices agent) {
        this.agent = agent;
    }

    /**
     * Encrypt some text, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param plainText         some text to be encrypted
     * @param encryptAttributes the attributes to pass along to the key created by the operation
     * @return the Ionic encoded encrypted representation of the input
     * @throws IonicException on cryptography errors
     */
    final String encryptInternal(
            final byte[] plainText, final ChunkCryptoEncryptAttributes encryptAttributes) throws IonicException {
        if (Value.isEmpty(plainText)) {
            throw new IonicException(SdkError.ISCHUNKCRYPTO_BAD_INPUT);
        }
        // create request
        final CreateKeysRequest createKeysRequest = new CreateKeysRequest();
        final String refId = getClass().getSimpleName();
        createKeysRequest.add(new CreateKeysRequest.Key(refId, 1,
                encryptAttributes.getKeyAttributes(), encryptAttributes.getMutableKeyAttributes()));
        // execute request
        createKeysRequest.setMetadata(encryptAttributes.getMetadata());
        final CreateKeysResponse createKeysResponse = agent.createKeys(createKeysRequest);
        // capture response
        encryptAttributes.setCipherId(getId());
        if (createKeysResponse.getServerErrorCode() != SdkError.ISAGENT_OK) {
            encryptAttributes.setServerErrorResponse(createKeysResponse);
        }
        final List<CreateKeysResponse.Key> createKeys = createKeysResponse.getKeys();
        if (createKeys.isEmpty()) {
            throw new IonicException(SdkError.ISAGENT_KEY_DENIED,
                    SdkError.getErrorString(SdkError.ISAGENT_KEY_DENIED));
        }
        // capture response key
        final CreateKeysResponse.Key createKey = createKeys.iterator().next();
        encryptAttributes.setKey(createKey);
        final String keyId = createKey.getId();
        // perform crypto operation
        final String cipherText = normalize(encryptInternal(createKey, plainText));
        // format output
        final StringBuilder buffer = new StringBuilder();
        buffer.append(getDelimiterKeyTagStart()).append(keyId)
                .append(getDelimiterCiphertextStart()).append(cipherText).append(getDelimiterCiphertextEnd());
        return buffer.toString();
    }

    /**
     * Decrypt some text, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param cipherText        some text (previously encrypted with an instance of this agent) to be decrypted
     * @param decryptAttributes the attributes to pass along from the key fetched by the operation
     * @return the plainText representation of the input
     * @throws IonicException on cryptography errors
     */
    final byte[] decryptInternal(
            final String cipherText, final ChunkCryptoDecryptAttributes decryptAttributes) throws IonicException {
        final String delimiterKeyTagStart = getDelimiterKeyTagStart();
        final String delimiterCiphertextStart = getDelimiterCiphertextStart();
        final String delimiterCiphertextEnd = getDelimiterCiphertextEnd();
        final int keyTagDelimStart = cipherText.indexOf(delimiterKeyTagStart);
        final int keyTagDelimEnd = keyTagDelimStart + delimiterKeyTagStart.length();
        final int cipherTextDelimStart = cipherText.indexOf(delimiterCiphertextStart, keyTagDelimEnd);
        final int cipherTextStart = cipherTextDelimStart + delimiterCiphertextStart.length();
        final int cipherTextEnd = cipherText.indexOf(delimiterCiphertextEnd, cipherTextStart);
        final int cipherTextDelimEnd = cipherTextEnd + delimiterCiphertextEnd.length();
        if (keyTagDelimStart >= 0 && cipherTextDelimStart > 0 && cipherTextDelimEnd > 0) {
            final String keyId = cipherText.substring(keyTagDelimEnd, cipherTextDelimStart);
            final String cipherTextBase64 = denormalize(cipherText.substring(cipherTextStart, cipherTextEnd));
            return decryptInternal(keyId, cipherTextBase64, decryptAttributes);
        } else {
            throw new IonicException(SdkError.ISAGENT_INVALIDVALUE, new IOException(cipherText));
        }
    }

    /**
     * Decrypt the input cipherText, using the key associated with the input Ionic keyId.
     *
     * @param keyIdQ            the Ionic keyId associated with the crypto key to be used to decrypt
     * @param cipherTextBase64  some text (previously encrypted with an instance of this agent) to be decrypted
     * @param decryptAttributes the attributes to pass along from the key fetched by the operation
     * @return the plainText representation of the input
     * @throws IonicException on cryptography errors
     */
    private byte[] decryptInternal(final String keyIdQ, final String cipherTextBase64,
                                   final ChunkCryptoDecryptAttributes decryptAttributes) throws IonicException {
        // create request
        final GetKeysRequest getKeysRequest = new GetKeysRequest();
        getKeysRequest.add(keyIdQ);
        // execute request
        getKeysRequest.setMetadata(decryptAttributes.getMetadata());
        final GetKeysResponse getKeysResponse = agent.getKeys(getKeysRequest);
        // capture response
        decryptAttributes.setCipherId(getId());
        if (getKeysResponse.getServerErrorCode() != SdkError.ISAGENT_OK) {
            decryptAttributes.setServerErrorResponse(getKeysResponse);
        }
        final List<GetKeysResponse.Key> getKeys = getKeysResponse.getKeys();
        if (getKeys.isEmpty()) {
            throw new IonicException(SdkError.ISAGENT_KEY_DENIED, new IOException(cipherTextBase64));
        }
        // capture response key
        final GetKeysResponse.Key getKey = getKeys.iterator().next();
        decryptAttributes.setKey(getKey);
        decryptAttributes.setKeyAttributes(getKey.getAttributesMap());
        decryptAttributes.setMutableAttributes(getKey.getMutableAttributesMap());
        final String keyId = getKey.getId();
        if (!keyIdQ.equals(keyId)) {
            throw new IonicException(SdkError.ISAGENT_BADRESPONSE, new IOException(cipherTextBase64));
        }
        return decryptInternal(getKey, cipherTextBase64);
    }

    /**
     * Inspect the parameter data to determine the relevant Ionic chunk cipher used to encrypt it.
     *
     * @param data the text data to be inspected
     * @return an info object which may be used to decrypt the parameter data; or null if the data is not understood
     */
    final ChunkCryptoChunkInfo getChunkInfoInternal(final String data) {
        final String delimKeyTagStart = getDelimiterKeyTagStart();
        final String delimCiphertextStart = getDelimiterCiphertextStart();
        final String delimCiphertextEnd = getDelimiterCiphertextEnd();
        final String delimAll = delimKeyTagStart + delimCiphertextStart + delimCiphertextEnd;
        if (data == null) {
            return null;
        } else if (data.length() < delimAll.length()) {
            return null;
        }
        if (!data.startsWith(delimKeyTagStart)) {
            return null;
        } else if (!data.endsWith(delimCiphertextEnd)) {
            return null;
        }
        final int indexOf = data.indexOf(delimCiphertextStart, delimKeyTagStart.length());
        if (indexOf < 0) {
            return null;
        }
        final int cipherTextStart = indexOf + delimCiphertextStart.length();
        final int cipherTextEnd = data.indexOf(delimCiphertextEnd, cipherTextStart);
        final int keyIdLength = indexOf - delimKeyTagStart.length();
        final int beginIndex = delimKeyTagStart.length();
        final int endIndex = beginIndex + keyIdLength;
        final String keyId = data.substring(beginIndex, endIndex);
        return new ChunkCryptoChunkInfo(true, keyId, getId(), cipherTextStart, cipherTextEnd - cipherTextStart);
    }

    /**
     * @return The text id of this cipher.
     */
    public abstract String getId();

    /**
     * @return The text label of this cipher.
     */
    public abstract String getLabel();

    /**
     * @return The token used to mark the start of the key tag for a ChunkCipherAbstract encrypted string.
     */
    protected abstract String getDelimiterKeyTagStart();

    /**
     * @return The token used to mark the start of the ciphertext for a ChunkCipherAbstract encrypted string.
     */
    protected abstract String getDelimiterCiphertextStart();

    /**
     * @return The token used to mark the end of the ciphertext for a ChunkCipherAbstract encrypted string.
     */
    protected abstract String getDelimiterCiphertextEnd();

    /**
     * Inspect the parameter data to determine the relevant Ionic chunk cipher used to encrypt it.
     *
     * @param data the text data to be inspected
     * @return an info object which may be used to decrypt the parameter data; or null if the data is not understood
     */
    public abstract ChunkCryptoChunkInfo getChunkInfo(final String data);

    /**
     * Encrypt some text, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param plainText some text to be encrypted
     * @return the Ionic encoded encrypted representation of the input
     * @throws IonicException on cryptography errors
     */
    public abstract String encrypt(final String plainText) throws IonicException;

    /**
     * Encrypt some text, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param plainText         some text to be encrypted
     * @param encryptAttributes the attributes to pass along to the key created by the operation
     * @return the Ionic encoded encrypted representation of the input
     * @throws IonicException on cryptography errors
     */
    public abstract String encrypt(
            final String plainText, final ChunkCryptoEncryptAttributes encryptAttributes) throws IonicException;

    /**
     * Encrypt some bytes, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param plainText some bytes to be encrypted
     * @return the Ionic encoded encrypted representation of the input
     * @throws IonicException on cryptography errors
     */
    public abstract String encrypt(final byte[] plainText) throws IonicException;

    /**
     * Encrypt some bytes, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param plainText         some bytes to be encrypted
     * @param encryptAttributes the attributes to pass along to the key created by the operation
     * @return the Ionic encoded encrypted representation of the input
     * @throws IonicException on cryptography errors
     */
    public abstract String encrypt(
            final byte[] plainText, final ChunkCryptoEncryptAttributes encryptAttributes) throws IonicException;

    /**
     * Encrypt some text, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param key       the Ionic key associated with the ciphertext
     * @param plainText some data to be encrypted
     * @return the Ionic encoded encrypted representation of the input
     * @throws IonicException on cryptography errors
     */
    protected abstract String encryptInternal(final AgentKey key, final byte[] plainText) throws IonicException;

    /**
     * Decrypt some text, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param cipherText some text (previously encrypted with an instance of this agent) to be decrypted
     * @return the plainText representation of the input
     * @throws IonicException on cryptography errors
     */
    public abstract String decrypt(final String cipherText) throws IonicException;

    /**
     * Decrypt some text, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param cipherText        some text (previously encrypted with an instance of this agent) to be decrypted
     * @param decryptAttributes the attributes to pass along from the key fetched by the operation
     * @return the plainText representation of the input
     * @throws IonicException on cryptography errors
     */
    public abstract String decrypt(
            final String cipherText, final ChunkCryptoDecryptAttributes decryptAttributes) throws IonicException;

    /**
     * Decrypt some bytes, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param cipherText some bytes (previously encrypted with an instance of this agent) to be decrypted
     * @return the plainText representation of the input
     * @throws IonicException on cryptography errors
     */
    public abstract String decrypt(final byte[] cipherText) throws IonicException;

    /**
     * Decrypt some bytes, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param cipherText        some bytes (previously encrypted with an instance of this agent) to be decrypted
     * @param decryptAttributes the attributes to pass along from the key fetched by the operation
     * @return the plainText representation of the input
     * @throws IonicException on cryptography errors
     */
    public abstract String decrypt(
            final byte[] cipherText, final ChunkCryptoDecryptAttributes decryptAttributes) throws IonicException;

    /**
     * Decrypt some text, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param cipherText some text (previously encrypted with an instance of this agent) to be decrypted
     * @return the plainText representation of the input
     * @throws IonicException on cryptography errors
     */
    public abstract byte[] decryptToBytes(final String cipherText) throws IonicException;

    /**
     * Decrypt some text, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param cipherText        some text (previously encrypted with an instance of this agent) to be decrypted
     * @param decryptAttributes the container for the key attributes from the Ionic server
     * @return the plainText representation of the input
     * @throws IonicException on cryptography errors
     */
    public abstract byte[] decryptToBytes(
            final String cipherText, final ChunkCryptoDecryptAttributes decryptAttributes) throws IonicException;

    /**
     * Decrypt some text, using Ionic infrastructure to abstract away the key management and cryptography.
     *
     * @param key              the Ionic key associated with the ciphertext
     * @param cipherTextBase64 some text (previously encrypted with an instance of this agent) to be decrypted
     * @return the plainText representation of the input
     * @throws IonicException on cryptography errors
     */
    protected abstract byte[] decryptInternal(final AgentKey key, final String cipherTextBase64) throws IonicException;

    /**
     * ChunkCipher strips base64 padding ('=') from ciphertext embedded in a formatted ChunkCipher string.  This
     * function takes (padded) base64, and strips the padding for inclusion in a ChunkCipher string.
     *
     * @param base64 input text to normalize to ChunkCipher format
     * @return base64 text with trailing base64 padding characters removed
     */
    private String normalize(final String base64) {
        return base64.replace(Transcoder.BASE64_PAD, "");
    }

    /**
     * ChunkCipher strips base64 padding ('=') from ciphertext embedded in a formatted ChunkCipher string.  This
     * function takes a ChunkCipher ciphertext string, and restores the base64 padding.
     *
     * @param cipherText input text to normalize to base64 format
     * @return base64 text with trailing base64 padding characters added
     */
    private String denormalize(final String cipherText) {
        final int base64BlockSize = 4;
        final int lastBlockSize = cipherText.length() % base64BlockSize;
        if (lastBlockSize == 0) {
            return cipherText;
        } else {
            final char[] padding = new char[base64BlockSize - lastBlockSize];
            Arrays.fill(padding, '=');
            return cipherText + new String(padding);
        }
    }
}
