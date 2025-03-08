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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


/**
 * FragmentViewModel is a ViewModel class responsible for storing and managing
 * the UI-related data related to a selected fragment view in a lifecycle-aware way.
 * It allows communication of the selected fragment view between the UI components
 * such as fragments or activities while ensuring data consistency and separation
 * of concerns.
 */
public class FragmentViewModel extends ViewModel {

    /**
     * Represents a {@link MutableLiveData} object that holds the currently selected
     * fragment view data within the {@link FragmentViewModel}.
     * <p>
     * This variable is lifecycle-aware and designed to store and notify observers
     * about changes to the selected fragment view. It is primarily used by the
     * associated ViewModel to facilitate communication between UI components.
     */
    private final MutableLiveData<FragmentView> selectedFragmentView = new MutableLiveData<>();


    /**
     * Updates the selected fragment view in the ViewModel.
     * This method sets the specified fragment view as the current selected value
     * in the `selectedFragmentView` MutableLiveData, notifying any observers of
     * the change.
     *
     * @param item the FragmentView to set as the currently selected fragment view.
     */
    public void selectFragmentView(FragmentView item) {
        selectedFragmentView.setValue(item);
    }


    /**
     * Returns a LiveData object representing the currently selected fragment view.
     * This LiveData allows observing changes to the selected fragment view
     * in a lifecycle-aware manner.
     *
     * @return a LiveData object containing the selected FragmentView instance.
     */
    public LiveData<FragmentView> getSelectedFragmentView() {
        return selectedFragmentView;
    }

}
