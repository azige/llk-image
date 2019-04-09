/*
 * Created 2019-3-26 21:35:19
 */
package io.github.azige.llkimage.client;

import java.net.SocketAddress;

import io.github.azige.llkimage.crypto.AesCipher;
import io.github.azige.llkimage.storage.Storage;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class ClientContext {

    private SocketAddress defaultServerAddress;
    private Storage storage;
    private AesCipher cipher;
}
