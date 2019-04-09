/*
 * Created 2019-1-18 11:10:19
 */
package io.github.azige.llkimage.server;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
public class ServerConfiguration {

    private int port = 23333;
    private String storageLocation = "images";
}
