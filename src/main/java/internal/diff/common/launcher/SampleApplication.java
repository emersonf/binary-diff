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

package internal.diff.common.launcher;


import com.fasterxml.jackson.databind.ObjectMapper;
import internal.diff.aws.service.AmazonS3DirectoryMetadataService;
import internal.diff.common.domain.DirectoryMetadata;
import internal.diff.common.domain.DirectoryMetadataDifference;
import internal.diff.common.service.DirectoryMetadataDifferenceService;
import internal.diff.fs.service.FileSystemDirectoryMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;


/**
 * A sample application that compares the contents of a filesystem directory with the contents of an S3 bucket.
 *
 * @author Emerson Farrugia
 */
@Configuration
@ComponentScan("internal.diff")
@PropertySource("classpath:application.properties")
@PropertySource(value = "classpath:secret.properties")
public class SampleApplication {

    private static final Logger log = LoggerFactory.getLogger(SampleApplication.class);

    public static final String FILE_SYSTEM_DIRECTORY = "/Users/foo/bar";
    public static final String S3_BUCKET = "my-bucket";
    public static final String S3_PREFIX = "bar/";  // set this to "" to compare all keys in the bucket

    public static void main(String[] args) throws IOException {

        ApplicationContext context = new AnnotationConfigApplicationContext(SampleApplication.class);

        AmazonS3DirectoryMetadataService s3DirectoryMetadataService = context.getBean(AmazonS3DirectoryMetadataService.class);
        FileSystemDirectoryMetadataService fileSystemDirectoryMetadataService = context.getBean(FileSystemDirectoryMetadataService.class);
        DirectoryMetadataDifferenceService differenceService = context.getBean(DirectoryMetadataDifferenceService.class);

        log.info("The metadata of S3 bucket '{}' with prefix '{}' is going to be read.", S3_BUCKET, S3_PREFIX);
        DirectoryMetadata directory2 = s3DirectoryMetadataService.getMetadata(S3_BUCKET, S3_PREFIX);

        log.info("The metadata of file system directory '{}' is going to be read.", FILE_SYSTEM_DIRECTORY);
        DirectoryMetadata directory1 = fileSystemDirectoryMetadataService.getMetadata(FILE_SYSTEM_DIRECTORY);

        log.info("The differences between the metadata is being calculated.");
        DirectoryMetadataDifference difference = differenceService.getDifference(directory1, directory2);

        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, difference);
    }
}