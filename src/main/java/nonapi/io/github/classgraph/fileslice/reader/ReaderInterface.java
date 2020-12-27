package nonapi.io.github.classgraph.fileslice.reader;

import java.io.IOException;
import java.nio.ByteBuffer;

/** Interface for random access to values in byte order. */
public interface ReaderInterface {
    
    public int read(long srcOffset, ByteBuffer dstBuf, int dstBufStart, int numBytes) throws IOException;

    
    public int read(long srcOffset, byte[] dstArr, int dstArrStart, int numBytes) throws IOException;

    
    public byte readByte(final long offset) throws IOException;

    
    public int readUnsignedByte(final long offset) throws IOException;

    
    public short readShort(final long offset) throws IOException;

    
    public int readUnsignedShort(final long offset) throws IOException;

    
    public int readInt(final long offset) throws IOException;

    
    public long readUnsignedInt(final long offset) throws IOException;

    
    public long readLong(final long offset) throws IOException;

    public String readString(final long offset, final int numBytes, final boolean replaceSlashWithDot,
            final boolean stripLSemicolon) throws IOException;

    
    public String readString(final long offset, final int numBytes) throws IOException;
}
