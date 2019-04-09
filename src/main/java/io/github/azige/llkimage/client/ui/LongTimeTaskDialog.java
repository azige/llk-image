/*
 * Created 2019-3-27 18:26:58
 */
package io.github.azige.llkimage.client.ui;

import java.awt.Dimension;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @author Azige
 */
public class LongTimeTaskDialog extends JDialog {

    public LongTimeTaskDialog(String content) {
        JLabel label = new JLabel(content);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setText(content);
        add(label);

        pack();
        setTitle("长时间任务");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
    }

    public static Disposable delayedShow(String content) {
        LongTimeTaskDialog[] ref = new LongTimeTaskDialog[1];
        return Completable.timer(1, TimeUnit.SECONDS)
            .observeOn(Schedulers.from(SwingUtilities::invokeLater))
            .doOnComplete(() -> {
                synchronized (ref) {
                    LongTimeTaskDialog dialog = new LongTimeTaskDialog(content);
                    dialog.setVisible(true);
                    ref[0] = dialog;
                }
            })
            .andThen(Completable.never())
            .doOnDispose(() -> {
                synchronized (ref) {
                    LongTimeTaskDialog dialog = ref[0];
                    if (dialog != null) {
                        dialog.dispose();
                    }
                }
            })
            .subscribe();
    }
}
