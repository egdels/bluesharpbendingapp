package de.schliweb.bluesharpbendingapp.model.mircophone.android;
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

import static android.media.AudioRecord.STATE_INITIALIZED;

import android.media.AudioRecord;

import java.io.IOException;

import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;

/**
 * The type Android audio input stream.
 */
public class AndroidAudioInputStream implements TarsosDSPAudioInputStream {

    /**
     * The Underlying stream.
     */
    private final AudioRecord underlyingStream;
    /**
     * The Format.
     */
    private final TarsosDSPAudioFormat format;

    /**
     * Instantiates a new Android audio input stream.
     *
     * @param underlyingStream the underlying stream
     * @param format           the format
     */
    public AndroidAudioInputStream(AudioRecord underlyingStream, TarsosDSPAudioFormat format) {
        this.underlyingStream = underlyingStream;
        this.format = format;
    }

    /**
     * Skip long.
     *
     * @param bytesToSkip the bytes to skip
     * @return the long
     * @throws IOException the io exception
     */
    @Override
    public long skip(long bytesToSkip) throws IOException {
        throw new IOException("Can not skip in audio stream");
    }

    /**
     * Read int.
     *
     * @param b   the b
     * @param off the off
     * @param len the len
     * @return the int
     * @throws IOException the io exception
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (underlyingStream.getState() == STATE_INITIALIZED)
            return underlyingStream.read(b, off, len);
        throw new IOException("Can not read audio stream");
    }

    /**
     * Close.
     *
     * @throws IOException the io exception
     */
    @Override
    public void close() throws IOException {
        try {
            underlyingStream.stop();
            underlyingStream.release();
        } catch (java.lang.IllegalStateException e) {
            throw new IOException("Can not close audio stream");
        }
    }

    /**
     * Gets format.
     *
     * @return the format
     */
    @Override
    public TarsosDSPAudioFormat getFormat() {
        return format;
    }

    /**
     * Gets frame length.
     *
     * @return the frame length
     */
    @Override
    public long getFrameLength() {
        return -1;
    }

}