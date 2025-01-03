package de.schliweb.bluesharpbendingapp.view.android;
/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import de.schliweb.bluesharpbendingapp.view.HarpViewNoteElement;

import java.util.HashMap;

/**
 * The type Harp view note element android.
 */
public class HarpViewNoteElementAndroid implements HarpViewNoteElement {

    /**
     * The constant instances.
     */
    private static final HashMap<TextView, HarpViewNoteElementAndroid> instances = new HashMap<>();

    /**
     * The Text view.
     */
    private final TextView textView;

    /**
     * The Activity.
     */
    private final FragmentActivity activity;

    /**
     * Instantiates a new Harp view note element android.
     *
     * @param textView the text view
     */
    private HarpViewNoteElementAndroid(TextView textView) {
        this.textView = textView;
        this.activity = (FragmentActivity) textView.getContext();
    }

    /**
     * Gets instance.
     *
     * @param textView the text view
     * @return the instance
     */
    public static HarpViewNoteElement getInstance(TextView textView) {
        return instances.computeIfAbsent(textView, HarpViewNoteElementAndroid::new);
    }

    /**
     * Update.
     *
     * @param cents the cents
     */
    @Override
    public void update(double cents) {
        activity.runOnUiThread(
                () -> {
                    LayerDrawable layout = (LayerDrawable) textView.getBackground();

                    GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
                    line.setAlpha(255);

                    double height = textView.getHeight();

                    int lineWidth = Math.max((int) (textView.getHeight() / 10.0), 10);

                    line.setStroke(lineWidth, Color.rgb((int) (250.0 * Math.abs(cents / 50.0)), (int) (250.0 * (1.0 - Math.abs(cents / 50.0))), 0));

                    // Limit the cents values to the range -44 to 44
                    double limitedCents = Math.max(-44.0, Math.min(44.0, cents));
                    double position = height - height * (limitedCents / 50.0);

                    line.setBounds(line.getBounds().left, line.getBounds().top, line.getBounds().right, (int) position);
                }
        );
    }

    /**
     * Clear.
     */
    @Override
    public void clear() {
        activity.runOnUiThread(
                () -> {
                    LayerDrawable layout = (LayerDrawable) textView.getBackground();
                    GradientDrawable line = (GradientDrawable) layout.getDrawable(1);
                    line.setAlpha(0);
                });
    }
}
