/*
 * Created 2019-1-19 16:43:30
 */
package io.github.azige.llkimage.client;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.github.azige.llkimage.client.ui.FileDropDialog;
import io.github.azige.llkimage.client.ui.LongTimeTaskDialog;
import io.github.azige.llkimage.client.ui.ViewImageFrame;
import io.github.azige.llkimage.crypto.AesCipher;
import io.github.azige.llkimage.format.StoreImage;
import io.github.azige.llkimage.storage.Storage;
import io.netty.channel.nio.NioEventLoopGroup;
import io.reactivex.disposables.Disposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Azige
 */
public class ClientApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ClientApplication.class);
    private static final List<Image> icons = Arrays.asList(createIconImage(2), createIconImage(4), createIconImage(8));
    private static FileDropDialog fileDropDialog;
    private static Client client;

    public static void main(String[] args) throws Exception {
        ClientConfiguration config = loadOrCreateConfig();
        ClientContext clientContext = new ClientContext(
            new InetSocketAddress(config.getDefaultServerHost(), config.getDefaultServerPort()),
            new Storage(Paths.get(config.getStorageLocation())),
            new AesCipher(config.getPassword())
        );
        client = new Client(clientContext);

        configLookAndFeel();

        fileDropDialog = new FileDropDialog();

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LOG.error("发生异常", e);
            JOptionPane.showMessageDialog(null, "发生异常：" + e.getMessage());
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            client.close();
        }));

        PopupMenu menu = new PopupMenu();

        MenuItem uploadMenuItem = new MenuItem("上传");
        uploadMenuItem.addActionListener(event -> {
            uploadImage();
        });
        menu.add(uploadMenuItem);

        MenuItem downloadMenuItem = new MenuItem("下载");
        downloadMenuItem.addActionListener(event -> {
            downloadImage();
        });
        menu.add(downloadMenuItem);

        menu.addSeparator();

        MenuItem exitMenuItem = new MenuItem("退出");
        exitMenuItem.addActionListener(event -> System.exit(0));
        menu.add(exitMenuItem);

        TrayIcon trayIcon = new TrayIcon(icons.get(0));
        trayIcon.setPopupMenu(menu);
        SystemTray.getSystemTray().add(trayIcon);
    }

    private static ClientConfiguration loadOrCreateConfig() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        Path configPath = Paths.get("client-config.json");
        ClientConfiguration config;
        if (Files.exists(configPath)) {
            try {
                config = objectMapper.readValue(configPath.toFile(), ClientConfiguration.class);
            } catch (Exception ex) {
                LOG.warn("配置文件不正确，将使用默认配置", ex);
                config = new ClientConfiguration();
            }
        } else {
            config = new ClientConfiguration();
            try {
                objectMapper.writeValue(configPath.toFile(), config);
            } catch (Exception ex) {
                LOG.warn("无法创建配置文件");
            }
        }
        return config;
    }

    private static void configLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            LOG.info("设置外观风格时发生异常", ex);
        }

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                WindowEvent windowEvent = (WindowEvent) event;
                if (windowEvent.getID() == WindowEvent.WINDOW_OPENED) {
                    Window window = windowEvent.getWindow();
                    window.setIconImages(icons);
                }
            }
        }, AWTEvent.WINDOW_EVENT_MASK);
    }

    /**
     *
     * @param base 基准值，16x16的图标基准值是2
     * @return
     */
    private static BufferedImage createIconImage(int base) {
        BufferedImage img = new BufferedImage(base * 8, base * 8, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setColor(new Color(0x66ccff));
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, base * 5));
        g.drawString("llk", 0, base * 6);
        return img;
    }

    public static void uploadImage() {
        List<File> filesToUpload = fileDropDialog.requestDrop();

        if (!filesToUpload.isEmpty()) {
            Disposable dialogDisposable = LongTimeTaskDialog.delayedShow("正在上传图片");
            SwingWorker<String, Void> task = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    ClientConnection connection = client.newConnection();
                    String uploadedUris = connection.uploadImage(filesToUpload);
                    client.close();
                    return uploadedUris;
                }

                @Override
                protected void done() {
                    try {
                        dialogDisposable.dispose();
                        showUploadedUris(get());
                    } catch (InterruptedException | ExecutionException ex) {
                        throw new RuntimeException(ex.getCause());
                    }
                }
            };
            task.execute();
        }
    }

    public static void showUploadedUris(String uploadedUris) {
        JFrame f = new JFrame("已上传的资源");
        JTextArea textArea = new JTextArea();
        textArea.setText(uploadedUris);
        f.add(textArea);

        JButton copyButton = new JButton("复制");
        copyButton.addActionListener(event -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(uploadedUris), null);
        });
        f.add(copyButton, BorderLayout.SOUTH);

        f.setSize(350, 500);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);
    }

    public static void downloadImage() {
        String uri = JOptionPane.showInputDialog("输入 URI");
        if (uri != null) {
            String trimedUri = uri.trim();

            Disposable dialogDisposable = LongTimeTaskDialog.delayedShow("正在下载图片");
            SwingWorker<StoreImage, Void> task = new SwingWorker<StoreImage, Void>() {
                @Override
                protected StoreImage doInBackground() throws Exception {
                    ClientConnection connection = client.newConnection();
                    StoreImage storeImage = connection.downloadImage(trimedUri);
                    client.close();
                    return storeImage;
                }

                @Override
                protected void done() {
                    dialogDisposable.dispose();
                    try {
                        StoreImage storeImage = get();
                        if (storeImage == null) {
                            JOptionPane.showMessageDialog(null, "图片不存在");
                        } else {
                            ViewImageFrame.showImage(trimedUri, storeImage);
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            };
            task.execute();
        }
    }
}
