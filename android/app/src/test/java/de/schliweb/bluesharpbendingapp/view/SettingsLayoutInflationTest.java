package de.schliweb.bluesharpbendingapp.view;

/*
 * MIT License
 *
 * Copyright (c) 2025 Christian Kierdorf
 */

import static org.junit.Assert.*;

import android.content.Context;
import android.content.res.Resources;
import androidx.test.core.app.ApplicationProvider;
import de.schliweb.bluesharpbendingapp.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Simple Robolectric test to ensure the settings layout inflates without throwing InflateException
 * due to resource/type issues. This guards against missing or mis-typed dimension resources
 * referenced from the layout.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class SettingsLayoutInflationTest {

  @Test
  public void settingsDimensArePresentAndValid() {
    Context ctx = ApplicationProvider.getApplicationContext();
    Resources res = ctx.getResources();
    // Validate that each referenced settings dimen resolves to a positive pixel size
    assertTrue(res.getDimensionPixelSize(R.dimen.settings_padding) > 0);
    assertTrue(res.getDimensionPixelSize(R.dimen.settings_group_margin) > 0);
    assertTrue(res.getDimensionPixelSize(R.dimen.settings_item_margin) > 0);
    assertTrue(res.getDimensionPixelSize(R.dimen.settings_card_corner_radius) > 0);
    assertTrue(res.getDimensionPixelSize(R.dimen.settings_card_elevation) >= 0);
    assertTrue(res.getDimensionPixelSize(R.dimen.settings_header_margin) > 0);
    assertTrue(res.getDimensionPixelSize(R.dimen.settings_divider_height) >= 0);
  }
}
