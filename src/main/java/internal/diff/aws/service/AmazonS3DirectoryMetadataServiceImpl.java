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


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import internal.diff.common.domain.ChecksumType;
import internal.diff.common.domain.DirectoryMetadata;
import internal.diff.common.domain.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Emerson Farrugia
 */
@Service
public class AmazonS3DirectoryMetadataServiceImpl implements AmazonS3DirectoryMetadataService {

    @Autowired
    private AmazonS3Client amazonS3Client;


    @Override
    public DirectoryMetadata getMetadata(String bucketName, String prefix) {

        DirectoryMetadata directoryMetadata = new DirectoryMetadata();
        directoryMetadata.setName("");

        populateMetadata(directoryMetadata, bucketName, prefix);

        return directoryMetadata;
    }


    public void populateMetadata(DirectoryMetadata directoryMetadata, String bucketName, String prefix) {

        String marker = null;

        do {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName, prefix, marker, "/", null);

            ObjectListing objectListing = this.amazonS3Client.listObjects(listObjectsRequest);

            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {

                if (objectSummary.getKey().equals(prefix)) {
                    continue;
                }

                FileMetadata file = new FileMetadata();

                file.setName(objectSummary.getKey().substring(prefix.length()));
                file.setSizeInBytes(objectSummary.getSize());

                if (objectSummary.getETag().contains("-")) {
                    file.addChecksum(ChecksumType.S3_MULTIPART_ETAG, objectSummary.getETag());
                } else {
                    file.addChecksum(ChecksumType.MD5, objectSummary.getETag());
                }

                directoryMetadata.addFile(file);
            }

            for (String subdirectoryPath : objectListing.getCommonPrefixes()) {

                DirectoryMetadata subdirectory = new DirectoryMetadata();
                subdirectory.setName(subdirectoryPath.substring(prefix.length(), subdirectoryPath.length() - 1));

                directoryMetadata.addSubdirectory(subdirectory);

                populateMetadata(subdirectory, bucketName, subdirectoryPath);
            }

            marker = objectListing.getNextMarker();
        }
        while (marker != null);
    }
}
