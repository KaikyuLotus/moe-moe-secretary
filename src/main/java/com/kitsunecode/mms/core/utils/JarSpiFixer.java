package com.kitsunecode.mms.core.utils;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;

/**
 * I fucking hate javazoom.spi
 * Diocane. :)
 */
public class JarSpiFixer {

    private static final String JAR_FILE           = "target/moe-moe-secretary.jar";
    private static final String CONV_PROVIDER_FILE = "META-INF/services/javax.sound.sampled.spi.FormatConversionProvider";
    private static final String READER_FILE        = "META-INF/services/javax.sound.sampled.spi.AudioFileReader";

    private static void writeFile(FileSystem fs, String file, String content) throws IOException {
        Path nf = fs.getPath(file);
        try (Writer writer = Files.newBufferedWriter(nf, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(content);
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("[INFO] Editing file inside built jar");

        String file1 = "javazoom.spi.mpeg.sampled.convert.MpegFormatConversionProvider\n" +
                       "javazoom.spi.vorbis.sampled.convert.VorbisFormatConversionProvider\n";
        String file2 = "javazoom.spi.mpeg.sampled.file.MpegAudioFileReader\n" +
                       "javazoom.spi.vorbis.sampled.file.VorbisAudioFileReader\n";

        URI uri = URI.create("jar:" + Paths.get(JAR_FILE).toUri());
        try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            writeFile(fs, CONV_PROVIDER_FILE, file1);
            writeFile(fs, READER_FILE, file2);
        }

        System.out.println("[INFO] javazoom.spi information updated correctly");
    }

}
