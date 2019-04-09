/*
 * Created 2019-1-22 16:26:56
 */
package io.github.azige.llkimage.client.ui;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Azige
 */
public class FileDropDialog extends JDialog {

    private final DropTarget dropTarget;
    private List<File> droppedFiles = Collections.emptyList();
    private JFileChooser fileChooser = new JFileChooser();

    public FileDropDialog() {
        dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                onDrop(event);
            }
        });
        fileChooser.setFileFilter(new FileNameExtensionFilter("图像文件", "jpg", "jpeg", "png"));
        fileChooser.setMultiSelectionEnabled(true);

        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        JLabel label = new JLabel("把文件拖到此处，或者点击打开文件选择", JLabel.CENTER);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onLabelClicked();
            }
        });
        add(label, BorderLayout.CENTER);

        setTitle("上传文件");
        setSize(300, 100);
        setLocationRelativeTo(null);
        setResizable(false);
        setAlwaysOnTop(true);
        setModalityType(ModalityType.TOOLKIT_MODAL);
    }

    private void onDrop(DropTargetDropEvent event) {
        if (event.getCurrentDataFlavorsAsList().stream()
            .anyMatch(it -> it.isFlavorJavaFileListType())) {
            event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            try {
                droppedFiles = (List<File>) event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                setVisible(false);
            } catch (UnsupportedFlavorException | IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
                return;
            }
        }
    }

    private void onLabelClicked() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            droppedFiles = Arrays.asList(fileChooser.getSelectedFiles());
            setVisible(false);
        }
    }

    public List<File> requestDrop() {
        setVisible(true);
        List<File> result = droppedFiles;
        droppedFiles = Collections.emptyList();
        return result;
    }
}
