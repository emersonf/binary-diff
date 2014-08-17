/*
 * Copyright 2014 Emerson Farrugia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package internal.diff.aws.service;


import internal.diff.common.service.FileChecksumService;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * A service that computes S3 object ETags. The part size limit is currently fixed to 10MB.
 *
 * @author Emerson Farrugia
 */
@Service
public class AmazonS3ETagFileChecksumServiceImpl implements FileChecksumService {

    private static final int S3_MULTIPART_SIZE_LIMIT_IN_BYTES = 10 * 1024 * 1024;


    @Override
    public String calculateChecksum(Path file) throws IOException {

        long fileSize = Files.size(file);

        int parts = (int) (fileSize / S3_MULTIPART_SIZE_LIMIT_IN_BYTES);
        parts += fileSize % S3_MULTIPART_SIZE_LIMIT_IN_BYTES > 0 ? 1 : 0;

        ByteBuffer checksumBuffer = ByteBuffer.allocate(parts * 16);

        SeekableByteChannel byteChannel = Files.newByteChannel(file);

        for (int part = 0; part < parts; part++) {

            int partSizeInBytes;

            if (part < parts - 1 || fileSize % S3_MULTIPART_SIZE_LIMIT_IN_BYTES == 0) {
                partSizeInBytes = S3_MULTIPART_SIZE_LIMIT_IN_BYTES;
            } else {
                partSizeInBytes = (int) (fileSize % S3_MULTIPART_SIZE_LIMIT_IN_BYTES);
            }

            ByteBuffer partBuffer = ByteBuffer.allocate(partSizeInBytes);

            boolean endOfFile;
            do {
                endOfFile = byteChannel.read(partBuffer) == -1;
            }
            while (!endOfFile && partBuffer.hasRemaining());

            checksumBuffer.put(DigestUtils.md5(partBuffer.array()));
        }

        if (parts > 1) {
            return DigestUtils.md5Hex(checksumBuffer.array()) + "-" + parts;
        } else {
            return Hex.encodeHexString(checksumBuffer.array());
        }
    }
}
