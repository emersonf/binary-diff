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

package internal.diff.fs.service;


import internal.diff.common.domain.ChecksumType;
import internal.diff.common.domain.DirectoryMetadata;
import internal.diff.common.domain.FileMetadata;
import internal.diff.common.service.FileChecksumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.Deque;


/**
 * @author Emerson Farrugia
 */
@Service
public class FileSystemDirectoryMetadataServiceImpl implements FileSystemDirectoryMetadataService {

    private static final Logger log = LoggerFactory.getLogger(FileSystemDirectoryMetadataServiceImpl.class);

    @Autowired
    @Qualifier("amazonS3ETagFileChecksumServiceImpl")
    private FileChecksumService amazonS3ETagFileChecksumService;

    @Autowired
    @Qualifier("md5FileChecksumServiceImpl")
    private FileChecksumService md5FileChecksumService;


    @Override
    public DirectoryMetadata getMetadata(String path) {

        Path startPath = Paths.get(path);
        DirectoryMetadataBuildingFileVisitor visitor = new DirectoryMetadataBuildingFileVisitor();

        try {
            Files.walkFileTree(startPath, visitor);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return visitor.getRootDirectory();
    }


    /**
     * A file visitor that builds the metadata of a directory as it is traversed.
     */
    class DirectoryMetadataBuildingFileVisitor implements FileVisitor<Path> {

        private Deque<DirectoryMetadata> directoryStack = new ArrayDeque<>();
        private DirectoryMetadata rootDirectory;


        @Override
        public FileVisitResult preVisitDirectory(Path directoryPath, BasicFileAttributes attributes) throws IOException {

            log.debug("Entering directory '{}'.", directoryPath);

            DirectoryMetadata directory = new DirectoryMetadata();
            directory.setName(directoryPath.getName(directoryPath.getNameCount() - 1).toString());

            directoryStack.push(directory);

            return FileVisitResult.CONTINUE;
        }


        @Override
        public FileVisitResult visitFile(Path filePath, BasicFileAttributes attributes) throws IOException {

            log.debug("Visiting file '{}'.", filePath);

            FileMetadata file = new FileMetadata();

            file.setName(filePath.getName(filePath.getNameCount() - 1).toString());
            file.setSizeInBytes(Files.size(filePath));

            String s3ETag = amazonS3ETagFileChecksumService.calculateChecksum(filePath);

            file.addChecksum(ChecksumType.S3_MULTIPART_ETAG, s3ETag);

            // since S3 ETags are just MD5s when not using multi-part requests, avoid additional checksum computations if the ETag isn't multi-part
            if (!s3ETag.contains("-")) {
                file.addChecksum(ChecksumType.MD5, s3ETag);
            } else {
                file.addChecksum(ChecksumType.MD5, md5FileChecksumService.calculateChecksum(filePath));
            }

            directoryStack.getFirst().addFile(file);

            return FileVisitResult.CONTINUE;
        }


        @Override
        public FileVisitResult visitFileFailed(Path filePath, IOException exc) throws IOException {

            log.error("The file '{}' can't be visited.", filePath);

            return FileVisitResult.TERMINATE;
        }


        @Override
        public FileVisitResult postVisitDirectory(Path directoryPath, IOException exc) throws IOException {

            DirectoryMetadata directory = directoryStack.pop();

            if (directoryStack.isEmpty()) {
                rootDirectory = directory;
            } else {
                directoryStack.getFirst().addSubdirectory(directory);
            }

            log.debug("Leaving directory '{}'.", directoryPath);

            return FileVisitResult.CONTINUE;
        }


        public DirectoryMetadata getRootDirectory() {
            return rootDirectory;
        }
    }
}
