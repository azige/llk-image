/*
 * Created 2019-1-22 18:15:57
 */
package io.github.azige.llkimage.client.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import io.github.azige.llkimage.format.StoreImage;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @author Azige
 */
public class ViewImageFrame extends JFrame {

    private static final JFileChooser SHARED_FILE_CHOOSER = createFileChooser();
    private JFileChooser fileChooser;
    private StoreImage storeImage;
    private JPopupMenu popupMenu;
    private Image rawImage;
    private JLabel imageLabel;

    public ViewImageFrame(String title, StoreImage storeImage, JFileChooser fileChooser) throws IOException {
        super(title);
        this.fileChooser = fileChooser;
        this.storeImage = storeImage;

        popupMenu = new JPopupMenu();
        JMenuItem saveToMenuItem = new JMenuItem("另存为...");
        saveToMenuItem.addActionListener(event -> saveTo());
        popupMenu.add(saveToMenuItem);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                tryShowPopupMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                tryShowPopupMenu(e);
            }

            private void tryShowPopupMenu(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        rawImage = ImageIO.read(new ByteArrayInputStream(storeImage.getData()));
        Image image = rawImage;
        Dimension screenSize = getToolkit().getScreenSize();
        double widthLimit = screenSize.getWidth() * 0.9;
        double heightLimit = screenSize.getHeight() * 0.9;
        if (image.getWidth(null) > widthLimit || image.getHeight(null) > heightLimit) {
            image = resizeImage(image, widthLimit, heightLimit);
        }
        imageLabel = new JLabel(new ImageIcon(image));
        add(imageLabel);
        pack();

        Observable.<ComponentEvent>create(emitter -> {
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    emitter.onNext(e);
                }
            });
        })
            .debounce(200, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.from(SwingUtilities::invokeLater))
            .subscribe(e -> updateImage());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void updateImage() {
        if (getContentPane().getWidth() != 0 && getContentPane().getHeight() != 0) {
            imageLabel.setIcon(new ImageIcon(resizeImage(rawImage, getContentPane().getWidth(), getContentPane().getHeight())));
        }
    }

    public static Image resizeImage(Image image, double maxWidth, double maxHeight) {
        double limitRatio = maxWidth / maxHeight;
        double width = image.getWidth(null);
        double height = image.getHeight(null);
        double ratio = width / height;
        if (ratio > limitRatio) {
            width = maxWidth;
            height = maxWidth / ratio;
        } else {
            height = maxHeight;
            width = maxHeight * ratio;
        }
        return image.getScaledInstance((int) width, (int) height, Image.SCALE_SMOOTH);
    }

    public void saveTo() {
        fileChooser.setSelectedFile(new File(storeImage.getFileName()));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            Path toSavePath = fileChooser.getSelectedFile().toPath();
            try {
                Files.write(toSavePath, storeImage.getData());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static JFileChooser createFileChooser() {
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("图片文件", "jpg", "png");
        fc.setFileFilter(filter);
        return fc;
    }

    public static void showImage(String title, StoreImage storeImage) throws IOException {
        ViewImageFrame f = new ViewImageFrame(title, storeImage, SHARED_FILE_CHOOSER);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
