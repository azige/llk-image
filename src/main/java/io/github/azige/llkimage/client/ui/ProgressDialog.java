/*
 * Created 2019-2-12 17:57:15
 */
package io.github.azige.llkimage.client.ui;

import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @author Azige
 */
public class ProgressDialog extends JDialog {

    private JProgressBar progressBar;

    public ProgressDialog(int maxValue) {
        progressBar = new JProgressBar();
        progressBar.setMaximum(maxValue);
        progressBar.setStringPainted(true);
        add(progressBar);

        pack();
        setTitle("进度条");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
    }

    public void setValue(int value) {
        progressBar.setValue(value);
    }
}
