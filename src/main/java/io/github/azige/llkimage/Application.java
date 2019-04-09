/*
 * Created 2019-1-16 11:26:30
 */
package io.github.azige.llkimage;

import java.util.Arrays;

import io.github.azige.llkimage.client.ClientApplication;
import io.github.azige.llkimage.server.ServerApplication;

/**
 *
 * @author Azige
 */
public class Application {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            showHelp();
            return;
        }
        switch (args[0]) {
            case "server":
                ServerApplication.main(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "client":
                ClientApplication.main(Arrays.copyOfRange(args, 1, args.length));
                break;
            default:
                showHelp();
        }
    }

    public static void showHelp() {
        System.out.println("提供参数 server 或 client 来启动程序");
    }
}
