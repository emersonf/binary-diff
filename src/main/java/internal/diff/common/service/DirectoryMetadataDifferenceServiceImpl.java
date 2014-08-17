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

package internal.diff.common.service;


import com.google.common.collect.Sets;
import internal.diff.common.domain.DirectoryMetadata;
import internal.diff.common.domain.DirectoryMetadataDifference;
import internal.diff.common.domain.FileMetadata;
import internal.diff.common.domain.FileMetadataDifference;
import org.springframework.stereotype.Service;

import java.util.Set;

import static internal.diff.common.domain.ChecksumType.MD5;
import static internal.diff.common.domain.ChecksumType.S3_MULTIPART_ETAG;


/**
 * @author Emerson Farrugia
 */
@Service
public class DirectoryMetadataDifferenceServiceImpl implements DirectoryMetadataDifferenceService {

    private Set<String> ignoredFiles = Sets.newHashSet(".DS_Store");


    public DirectoryMetadataDifference getDifference(DirectoryMetadata directory1, DirectoryMetadata directory2) {

        DirectoryMetadataDifference directoryDifference = new DirectoryMetadataDifference();

        for (FileMetadata directory1File : directory1.getFiles().values()) {

            if (ignoredFiles.contains(directory1File.getName())) {
                continue;
            }

            FileMetadata directory2File = directory2.getFiles().get(directory1File.getName());

            if (directory2File == null) {
                directoryDifference.addMissingFile(directory1File);
            }
            else {
                FileMetadataDifference fileDifference = new FileMetadataDifference();

                boolean different = false;

                if (directory1File.hasChecksum(S3_MULTIPART_ETAG) && directory2File.hasChecksum(S3_MULTIPART_ETAG)) {
                    if (!directory1File.getChecksum(S3_MULTIPART_ETAG).equals(directory2File.getChecksum(S3_MULTIPART_ETAG))) {
                        fileDifference.setExpectedChecksum(directory1File.getChecksum(S3_MULTIPART_ETAG));
                        fileDifference.setActualChecksum(directory2File.getChecksum(S3_MULTIPART_ETAG));
                        different = true;
                    }
                }
                else if (directory1File.hasChecksum(MD5) && directory2File.hasChecksum(MD5)) {
                    if (!directory1File.getChecksum(MD5).equals(directory2File.getChecksum(MD5))) {
                        fileDifference.setExpectedChecksum(directory1File.getChecksum(MD5));
                        fileDifference.setActualChecksum(directory2File.getChecksum(MD5));
                        different = true;
                    }
                }
                else {
                    throw new IllegalStateException("A common checksum type isn't available for a file pair.");
                }

                if (directory1File.getSizeInBytes() != directory2File.getSizeInBytes()) {
                    fileDifference.setExpectedSizeInBytes(directory1File.getSizeInBytes());
                    fileDifference.setActualSizeInBytes(directory2File.getSizeInBytes());
                    different = true;
                }

                if (different) {
                    directoryDifference.addFileDifference(directory1File.getName(), fileDifference);
                }
                else {
                    directoryDifference.incrementIdenticalFiles();
                }
            }
        }

        for (FileMetadata directory2File : directory2.getFiles().values()) {

            if (ignoredFiles.contains(directory2File.getName())) {
                continue;
            }

            if (!directory1.getFiles().containsKey(directory2File.getName())) {
                directoryDifference.addExtraFile(directory2File);
            }
        }

        for (DirectoryMetadata directory1Subdirectory : directory1.getSubdirectories().values()) {

            DirectoryMetadata directory2Subdirectory = directory2.getSubdirectories().get(directory1Subdirectory.getName());

            if (directory2Subdirectory == null) {
                directoryDifference.addMissingSubdirectory(directory1Subdirectory);
            }
            else {
                DirectoryMetadataDifference subdirectoryDifference = getDifference(directory1Subdirectory, directory2Subdirectory);

                if (!subdirectoryDifference.isEmpty()) {
                    directoryDifference.addSubdirectoryDifference(directory1Subdirectory.getName(), subdirectoryDifference);
                }
                else {
                    directoryDifference.incrementIdenticalSubdirectories();
                }
            }
        }

        for (DirectoryMetadata directory2Subdirectory : directory2.getSubdirectories().values()) {

            if (!directory1.getSubdirectories().containsKey(directory2Subdirectory.getName())) {
                directoryDifference.addExtraSubdirectory(directory2Subdirectory);
            }
        }

        return directoryDifference;
    }
}
