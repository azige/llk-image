/*
 * Created 2019-3-26 18:39:22
 */
package io.github.azige.llkimage.server;

import io.github.azige.llkimage.storage.Storage;
import lombok.Value;

/**
 *
 * @author Azige
 */
@Value
public class ServerContext {

    private Storage storage;
}
