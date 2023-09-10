package com.akgarg.client.authclient.cache;

import com.akgarg.client.authclient.common.AuthToken;

import java.io.*;

/**
 * @author Akhilesh Garg
 * @since 10/09/23
 */
final class AuthTokenSerializerDeserializer {

    private AuthTokenSerializerDeserializer() {
        throw new IllegalStateException();
    }

    public static byte[] serializeToken(final AuthToken authToken) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(authToken);
        objectOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static AuthToken deserialize(final byte[] bytesData) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesData);
        final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        final AuthToken authToken = (AuthToken) objectInputStream.readObject();
        objectInputStream.close();
        return authToken;
    }

}
