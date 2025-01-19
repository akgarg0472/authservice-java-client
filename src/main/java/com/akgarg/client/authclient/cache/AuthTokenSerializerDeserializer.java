package com.akgarg.client.authclient.cache;

import com.akgarg.client.authclient.common.AuthToken;

import java.io.*;

/**
 * Utility class for serializing and deserializing {@link AuthToken} objects.
 * This class provides methods to convert {@link AuthToken} instances to byte arrays
 * and vice versa for storage and transmission.
 *
 * <p>This class is not meant to be instantiated.</p>
 *
 * @author Akhilesh Garg
 * @since 10/09/23
 */
final class AuthTokenSerializerDeserializer {

    private AuthTokenSerializerDeserializer() {
        throw new IllegalStateException();
    }

    /**
     * Serializes an {@link AuthToken} into a byte array.
     *
     * @param authToken the {@link AuthToken} to serialize
     * @return a byte array representing the serialized {@link AuthToken}
     * @throws IOException if an I/O error occurs during serialization
     */
    public static byte[] serializeToken(final AuthToken authToken) throws IOException {
        final var byteArrayOutputStream = new ByteArrayOutputStream();
        final var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(authToken);
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Deserializes a byte array into an {@link AuthToken} object.
     *
     * @param bytesData the byte array representing a serialized {@link AuthToken}
     * @return the deserialized {@link AuthToken}
     * @throws IOException            if an I/O error occurs during deserialization
     * @throws ClassNotFoundException if the {@link AuthToken} class cannot be found
     */
    public static AuthToken deserialize(final byte[] bytesData) throws IOException, ClassNotFoundException {
        final var byteArrayInputStream = new ByteArrayInputStream(bytesData);
        final var objectInputStream = new ObjectInputStream(byteArrayInputStream);
        final var authToken = (AuthToken) objectInputStream.readObject();
        objectInputStream.close();
        return authToken;
    }

}
