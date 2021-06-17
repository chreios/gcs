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

package com.trollworks.gcs.ui.widget;

import com.trollworks.gcs.ui.GraphicsUtilities;
import com.trollworks.gcs.ui.RetinaIcon;
import com.trollworks.gcs.ui.TextDrawing;
import com.trollworks.gcs.ui.ThemeFont;
import com.trollworks.gcs.ui.UIUtilities;
import com.trollworks.gcs.ui.scale.Scale;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

/** A simple label replacement that is scalable. */
public class Label extends Panel implements PropertyChangeListener {
    /** Pass this constant in to indicate wrapping should be done rather than truncation. */
    public static final  int    WRAP      = -1;
    private static final String ERROR_KEY = "error";
    private static final int    GAP       = 4;

    private RetinaIcon mIcon;
    private String     mText;
    private ThemeFont  mThemeFont;
    private int        mHAlign;
    private JComponent mRefersTo;
    private int        mTruncationPolicy;

    /**
     * Create a new label.
     *
     * @param text The text to use.
     */
    public Label(String text) {
        super(null, false);
        mText = "";
        mHAlign = SwingConstants.LEFT;
        mTruncationPolicy = SwingConstants.CENTER;
        setThemeFont(ThemeFont.LABEL_PRIMARY);
        setText(text);
    }

    /**
     * Create a new label.
     *
     * @param text   The text to use.
     * @param hAlign The horizontal alignment to use.
     */
    public Label(String text, int hAlign) {
        this(text);
        mHAlign = hAlign;
    }

    /**
     * Create a new right-aligned label that refers to another component.
     *
     * @param text     The text to use.
     * @param refersTo The {@link JComponent} to pair with.
     */
    public Label(String text, JComponent refersTo) {
        this(text, SwingConstants.RIGHT);
        setRefersTo(refersTo);
    }

    /**
     * Create a new label.
     *
     * @param icon The icon to use.
     * @param text The text to use.
     */
    public Label(RetinaIcon icon, String text) {
        this(text);
        mIcon = icon;
    }

    public final ThemeFont getThemeFont() {
        return mThemeFont;
    }

    public final void setThemeFont(ThemeFont font) {
        mThemeFont = font;
    }

    @Override
    public final Font getFont() {
        if (mThemeFont == null) {
            // If this happens, we are in the constructor and the look & feel is being inited, so
            // just return whatever was there by default.
            return super.getFont();
        }
        return mThemeFont.getFont();
    }

    /** @return The icon, or {@code null} if there is none. */
    public Icon getIcon() {
        return mIcon;
    }

    /** @param icon The {@link RetinaIcon} to use, or {@code null}. */
    public void setIcon(RetinaIcon icon) {
        if (mIcon != icon) {
            mIcon = icon;
            invalidate();
        }
    }

    /** @return The text. */
    public String getText() {
        return mText;
    }

    /** @param text The text to use. */
    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        if (!mText.equals(text)) {
            mText = text;
            invalidate();
        }
    }

    /** @return The horizontal alignment. */
    public int getHorizontalAlignment() {
        return mHAlign;
    }

    /** @param alignment The horizontal alignment to use. */
    public void setHorizontalAlignment(int alignment) {
        if (mHAlign != alignment) {
            mHAlign = alignment;
            repaint();
        }
    }

    public int getTruncationPolicy() {
        return mTruncationPolicy;
    }

    public void setTruncationPolicy(int truncationPolicy) {
        mTruncationPolicy = truncationPolicy;
    }

    @Override
    protected void paintComponent(Graphics g) {
        paintComponentWithText(g, mText);
    }

    // This is exposed to allow sub-classes to temporarily override the text used.
    protected void paintComponentWithText(Graphics g, String text) {
        Graphics2D gc = GraphicsUtilities.prepare(g);
        super.paintComponent(gc);
        if (mRefersTo != null && mRefersTo.getClientProperty(ERROR_KEY) != null) {
            gc.setColor(Color.RED); // TODO: Use themed error color
        }
        Rectangle bounds = UIUtilities.getLocalInsetBounds(this);
        Scale     scale  = Scale.get(this);
        Font      font   = scale.scale(getFont());
        gc.setFont(font);
        if (mIcon != null) {
            mIcon.paintIcon(this, gc, bounds.x, bounds.y + (bounds.height - scale.scale(mIcon.getIconHeight())) / 2);
            int amt = scale.scale(mIcon.getIconWidth()) + scale.scale(GAP);
            bounds.x += amt;
            bounds.width -= amt;
        }
        if (mTruncationPolicy != WRAP) {
            text = TextDrawing.truncateIfNecessary(font, text, bounds.width, mTruncationPolicy);
        }
        TextDrawing.draw(gc, bounds, text, mHAlign, SwingConstants.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        Insets    insets = getInsets();
        Scale     scale  = Scale.get(this);
        Dimension size   = TextDrawing.getPreferredSize(scale.scale(getFont()), mText);
        size.width += insets.left + insets.right;
        size.height += insets.top + insets.bottom;
        if (mIcon != null) {
            size.width += scale.scale(mIcon.getIconWidth()) + scale.scale(GAP);
            int height = scale.scale(mIcon.getIconHeight());
            if (height > size.height) {
                size.height = height;
            }
        }
        return size;
    }

    public int getPreferredHeight(int width) {
        Scale  scale  = Scale.get(this);
        Insets insets = getInsets();
        width -= insets.left + insets.right;
        if (mIcon != null) {
            width -= scale.scale(mIcon.getIconWidth()) + scale.scale(GAP);
        }
        Font      font = scale.scale(getFont());
        Dimension size = TextDrawing.getPreferredSize(font, TextDrawing.wrapToPixelWidth(font, mText, width));
        return size.height;
    }

    /** @return The {@link JComponent} that is being paired with. */
    public JComponent getRefersTo() {
        return mRefersTo;
    }

    /** @param refersTo The {@link JComponent} to pair with. */
    public void setRefersTo(JComponent refersTo) {
        if (mRefersTo != refersTo) {
            if (mRefersTo != null) {
                mRefersTo.removePropertyChangeListener(TOOL_TIP_TEXT_KEY, this);
                mRefersTo.removePropertyChangeListener(ERROR_KEY, this);
            }
            mRefersTo = refersTo;
            if (mRefersTo != null) {
                mRefersTo.addPropertyChangeListener(TOOL_TIP_TEXT_KEY, this);
                mRefersTo.addPropertyChangeListener(ERROR_KEY, this);
            }
            adjustToLink();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        adjustToLink();
    }

    private void adjustToLink() {
        String tooltip = null;
        if (mRefersTo != null) {
            tooltip = (String) mRefersTo.getClientProperty(ERROR_KEY);
            if (tooltip == null) {
                tooltip = mRefersTo.getToolTipText();
            }
        }
        setToolTipText(tooltip);
        repaint();
    }

    /**
     * Sets/clears the error message that a Label will respond to.
     *
     * @param comp The {@link JComponent} to set the message on.
     * @param msg  The error message or {@code null}.
     */
    public static void setErrorMessage(JComponent comp, String msg) {
        if (!Objects.equals(comp.getClientProperty(ERROR_KEY), msg)) {
            comp.putClientProperty(ERROR_KEY, msg);
        }
    }
}