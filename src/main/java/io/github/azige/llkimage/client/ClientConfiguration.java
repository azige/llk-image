/*
 * Created 2019-2-12 17:27:57
 */
package io.github.azige.llkimage.client;

import lombok.Data;

/**
 *
 * @author Azige
 */
@Data
public class ClientConfiguration {

    private String storageLocation = "images";
    private String defaultServerHost = "127.0.0.1";
    private int defaultServerPort = 23333;
    private String password = "wtmsb";

}
