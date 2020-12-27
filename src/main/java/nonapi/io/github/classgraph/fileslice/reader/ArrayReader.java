/*
 * This file is part of ClassGraph.
 *
 * Author: Luke Hutchison
 *
 * Hosted at: https://github.com/classgraph/classgraph
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Luke Hutchison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package nonapi.io.github.classgraph.fileslice.reader;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

import nonapi.io.github.classgraph.utils.StringUtils;


public class ArrayReader implements ReaderInterface{
    /** The array. */
    private final byte[] arr;

    /** The start index of the slice within the array. */
    private final int sliceStartPos;

    /** The length of the slice within the array. */
    private final int sliceLength;

    /**
     * Constructor for slicing an array.
     *
     * @param arr
     *            the array to slice.
     * @param sliceStartPos
     *            the start index of the slice within the array.
     * @param sliceLength
     *            the length of the slice within the array.
     */
    public ArrayReader(final byte[] arr, final int sliceStartPos, final int sliceLength) {
        this.arr = arr;
        this.sliceStartPos = sliceStartPos;
        this.sliceLength = sliceLength;
    }

    
    public int read(final long srcOffset, final byte[] dstArr, final int dstArrStart, final int numBytes)
            throws IOException {
        if (numBytes == 0) {
            return 0;
        }
        if (srcOffset < 0L || numBytes < 0 || numBytes > sliceLength - srcOffset) {
            throw new IOException("Read index out of bounds");
        }
        try {
            final int numBytesToRead = Math.max(Math.min(numBytes, dstArr.length - dstArrStart), 0);
            if (numBytesToRead == 0) {
                return -1;
            }
            final int srcStart = (int) (sliceStartPos + srcOffset);
            System.arraycopy(arr, srcStart, dstArr, dstArrStart, numBytesToRead);
            return numBytesToRead;
        } catch (final IndexOutOfBoundsException e) {
            throw new IOException("Read index out of bounds");
        }
    }

   
    public int read(final long srcOffset, final ByteBuffer dstBuf, final int dstBufStart, final int numBytes)
            throws IOException {
        if (numBytes == 0) {
            return 0;
        }
        if (srcOffset < 0L || numBytes < 0 || numBytes > sliceLength - srcOffset) {
            throw new IOException("Read index out of bounds");
        }
        try {
            final int numBytesToRead = Math.max(Math.min(numBytes, dstBuf.capacity() - dstBufStart), 0);
            if (numBytesToRead == 0) {
                return -1;
            }
            final int srcStart = (int) (sliceStartPos + srcOffset);
            ((Buffer) dstBuf).position(dstBufStart);
            ((Buffer) dstBuf).limit(dstBufStart + numBytesToRead);
            dstBuf.put(arr, srcStart, numBytesToRead);
            return numBytesToRead;
        } catch (BufferUnderflowException | IndexOutOfBoundsException | ReadOnlyBufferException e) {
            throw new IOException("Read index out of bounds");
        }
    }

   
    public byte readByte(final long offset) throws IOException {
        final int idx = sliceStartPos + (int) offset;
        return arr[idx];
    }

    
    public int readUnsignedByte(final long offset) throws IOException {
        final int idx = sliceStartPos + (int) offset;
        return arr[idx] & 0xff;
    }

    
    public short readShort(final long offset) throws IOException {
        return (short) readUnsignedShort(offset);
    }

    
    public int readUnsignedShort(final long offset) throws IOException {
        final int idx = sliceStartPos + (int) offset;
        return ((arr[idx + 1] & 0xff) << 8) //
                | (arr[idx] & 0xff);
    }

    
    public int readInt(final long offset) throws IOException {
        final int idx = sliceStartPos + (int) offset;
        return ((arr[idx + 3] & 0xff) << 24) //
                | ((arr[idx + 2] & 0xff) << 16) //
                | ((arr[idx + 1] & 0xff) << 8) //
                | (arr[idx] & 0xff);
    }

    
    public long readUnsignedInt(final long offset) throws IOException {
        return readInt(offset) & 0xffffffffL;
    }

    
    public long readLong(final long offset) throws IOException {
        final int idx = sliceStartPos + (int) offset;
        return ((arr[idx + 7] & 0xffL) << 56) //
                | ((arr[idx + 6] & 0xffL) << 48) //
                | ((arr[idx + 5] & 0xffL) << 40) //
                | ((arr[idx + 4] & 0xffL) << 32) //
                | ((arr[idx + 3] & 0xffL) << 24) //
                | ((arr[idx + 2] & 0xffL) << 16) //
                | ((arr[idx + 1] & 0xffL) << 8) //
                | (arr[idx] & 0xffL);
    }

    
    public String readString(final long offset, final int numBytes, final boolean replaceSlashWithDot,
            final boolean stripLSemicolon) throws IOException {
        final int idx = sliceStartPos + (int) offset;
        return StringUtils.readString(arr, idx, numBytes, replaceSlashWithDot, stripLSemicolon);
    }

    
    public String readString(final long offset, final int numBytes) throws IOException {
        return readString(offset, numBytes, false, false);
    }
}
