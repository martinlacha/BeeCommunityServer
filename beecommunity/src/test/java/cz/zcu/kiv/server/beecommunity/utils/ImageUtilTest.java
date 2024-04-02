package cz.zcu.kiv.server.beecommunity.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImageUtilTest {
    @Test
    void testCompressAndDecompressImage() {
        byte[] originalData = "This is a test image data".getBytes();
        byte[] compressedData = ImageUtil.compressImage(originalData);
        byte[] decompressedData = ImageUtil.decompressImage(compressedData);
        assertArrayEquals(originalData, decompressedData);
    }

    @Test
    void testCompressAndDecompressEmptyImage() {
        byte[] originalData = new byte[0];
        byte[] compressedData = ImageUtil.compressImage(originalData);
        byte[] decompressedData = ImageUtil.decompressImage(compressedData);
        assertArrayEquals(originalData, decompressedData);
    }

    @Test
    void testCompressAndDecompressNullImage() {
        byte[] originalData = null;
        byte[] compressedData = ImageUtil.compressImage(originalData);
        byte[] decompressedData = ImageUtil.decompressImage(compressedData);
        assertNull(decompressedData);
    }
}
