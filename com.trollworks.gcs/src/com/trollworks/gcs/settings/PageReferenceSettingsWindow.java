/*
 * Copyright ©1998-2021 by Richard A. Wilkes. All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, version 2.0. If a copy of the MPL was not distributed with
 * this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, version 2.0.
 */

package com.trollworks.gcs.settings;

import com.trollworks.gcs.menu.file.CloseHandler;
import com.trollworks.gcs.pdfview.PDFRef;
import com.trollworks.gcs.preferences.Preferences;
import com.trollworks.gcs.ui.UIUtilities;
import com.trollworks.gcs.ui.border.EmptyBorder;
import com.trollworks.gcs.ui.border.LineBorder;
import com.trollworks.gcs.ui.layout.PrecisionLayout;
import com.trollworks.gcs.ui.layout.PrecisionLayoutData;
import com.trollworks.gcs.ui.widget.BandedPanel;
import com.trollworks.gcs.ui.widget.BaseWindow;
import com.trollworks.gcs.ui.widget.EditorField;
import com.trollworks.gcs.ui.widget.FontAwesomeButton;
import com.trollworks.gcs.ui.widget.WindowUtils;
import com.trollworks.gcs.utility.I18n;
import com.trollworks.gcs.utility.text.IntegerFormatter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.nio.file.Path;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.text.DefaultFormatterFactory;

/** A window for editing page reference lookup settings. */
public class PageReferenceSettingsWindow extends BaseWindow implements CloseHandler {
    private static PageReferenceSettingsWindow INSTANCE;
    private        BandedPanel                 mPanel;

    /** Displays the page reference lookup settings window. */
    public static void display() {
        if (!UIUtilities.inModalState()) {
            PageReferenceSettingsWindow wnd;
            synchronized (PageReferenceSettingsWindow.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PageReferenceSettingsWindow();
                }
                wnd = INSTANCE;
            }
            wnd.setVisible(true);
        }
    }

    public static void rebuild() {
        if (INSTANCE != null) {
            INSTANCE.mPanel.removeAll();
            INSTANCE.buildPanel();
        }
    }

    private PageReferenceSettingsWindow() {
        super(I18n.Text("Page Reference Settings"));
        setLayout(new BorderLayout());
        mPanel = new BandedPanel("");
        mPanel.setLayout(new PrecisionLayout().setColumns(5).setMargins(2, 5, 2, 5).setVerticalSpacing(0));
        buildPanel();
        Container   content  = getContentPane();
        JScrollPane scroller = new JScrollPane(mPanel);
        scroller.setBorder(null);
        content.add(scroller, BorderLayout.CENTER);
        WindowUtils.packAndCenterWindowOn(this, null);
    }

    private void buildPanel() {
        Preferences prefs      = Preferences.getInstance();
        Color       background = new Color(255, 255, 224);
        for (PDFRef ref : prefs.allPdfRefs(false)) {
            FontAwesomeButton removeButton = new FontAwesomeButton("\uf1f8", I18n.Text("Remove"), null);
            removeButton.setClickFunction(() -> {
                String   cancel  = I18n.Text("Cancel");
                Object[] options = {I18n.Text("Delete"), cancel};
                if (WindowUtils.showConfirmDialog(this, String.format(I18n.Text("""
                                Are you sure you want to delete this page reference
                                mapping from %s to "%s"?"""), ref.getID(), ref.getPath().getFileName().toString()),
                        "", JOptionPane.YES_NO_OPTION, options, cancel) == JOptionPane.YES_OPTION) {
                    Preferences.getInstance().removePdfRef(ref);
                    Component[] children = mPanel.getComponents();
                    int         length   = children.length;
                    for (int i = 0; i < length; i++) {
                        if (children[i] == removeButton) {
                            for (int j = i + 5; --j >= i; ) {
                                mPanel.remove(j);
                            }
                            break;
                        }
                    }
                    mPanel.revalidate();
                    mPanel.repaint();
                }
            });
            removeButton.setBorder(new EmptyBorder(4));
            mPanel.add(removeButton);
            JLabel idLabel = new JLabel(ref.getID(), SwingConstants.CENTER);
            idLabel.setBorder(new CompoundBorder(new LineBorder(), new EmptyBorder(1, 4, 1, 4)));
            idLabel.setOpaque(true);
            idLabel.setBackground(background);
            mPanel.add(idLabel, new PrecisionLayoutData().setFillHorizontalAlignment());
            EditorField field = new EditorField(new DefaultFormatterFactory(new IntegerFormatter(-9999, 9999, true)),
                    (evt) -> ref.setPageToIndexOffset(((Integer) evt.getNewValue()).intValue()),
                    SwingConstants.RIGHT, Integer.valueOf(ref.getPageToIndexOffset()),
                    Integer.valueOf(-9999),
                    I18n.Text("If your PDF is opening up to the wrong page when opening page references, enter an offset here to compensate."));
            mPanel.add(field);
            Path path = ref.getPath().normalize().toAbsolutePath();
            mPanel.add(new JLabel(path.getFileName().toString()));
            JLabel dirLabel = new JLabel(path.getParent().toString());
            Font   font     = dirLabel.getFont();
            dirLabel.setFont(font.deriveFont((float) (font.getSize() * 8) / 10));
            mPanel.add(dirLabel);
        }
        mPanel.revalidate();
        mPanel.repaint();
    }

    @Override
    public boolean mayAttemptClose() {
        return true;
    }

    @Override
    public boolean attemptClose() {
        windowClosing(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        return true;
    }

    @Override
    public void dispose() {
        synchronized (PageReferenceSettingsWindow.class) {
            INSTANCE = null;
        }
        super.dispose();
    }
}