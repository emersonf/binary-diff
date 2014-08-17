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

package internal.diff.common.domain;


import java.util.HashMap;
import java.util.Map;


/**
 * A bean representing the difference between the metadata of two directory trees.
 *
 * @author Emerson Farrugia
 */
public class DirectoryMetadataDifference {

    private int identicalSubdirectories = 0;
    private int identicalFiles = 0;
    private Map<String, DirectoryMetadata> missingSubdirectories = new HashMap<>();
    private Map<String, DirectoryMetadata> extraSubdirectories = new HashMap<>();
    private Map<String, FileMetadata> missingFiles = new HashMap<>();
    private Map<String, FileMetadata> extraFiles = new HashMap<>();
    private Map<String, FileMetadataDifference> fileDifferences = new HashMap<>();
    private Map<String, DirectoryMetadataDifference> subdirectoryDifferences = new HashMap<>();


    public boolean isEmpty() {

        return missingSubdirectories.isEmpty()
                && extraSubdirectories.isEmpty()
                && missingFiles.isEmpty()
                && extraFiles.isEmpty()
                && fileDifferences.isEmpty()
                && subdirectoryDifferences.isEmpty();
    }


    public int getIdenticalSubdirectories() {
        return identicalSubdirectories;
    }


    public void incrementIdenticalSubdirectories() {
        identicalSubdirectories++;
    }


    public int getIdenticalFiles() {
        return identicalFiles;
    }


    public void incrementIdenticalFiles() {
        identicalFiles++;
    }


    public Map<String, DirectoryMetadata> getMissingSubdirectories() {
        return missingSubdirectories;
    }


    public void addMissingSubdirectory(DirectoryMetadata subdirectory) {
        this.missingSubdirectories.put(subdirectory.getName(), subdirectory);
    }


    public Map<String, DirectoryMetadata> getExtraSubdirectories() {
        return extraSubdirectories;
    }


    public void addExtraSubdirectory(DirectoryMetadata subdirectory) {
        this.extraSubdirectories.put(subdirectory.getName(), subdirectory);
    }


    public Map<String, FileMetadata> getMissingFiles() {
        return missingFiles;
    }


    public void addMissingFile(FileMetadata file) {
        this.missingFiles.put(file.getName(), file);
    }


    public Map<String, FileMetadata> getExtraFiles() {
        return extraFiles;
    }


    public void addExtraFile(FileMetadata file) {
        this.extraFiles.put(file.getName(), file);
    }


    public Map<String, FileMetadataDifference> getFileDifferences() {
        return fileDifferences;
    }


    public void addFileDifference(String name, FileMetadataDifference fileDifference) {
        this.fileDifferences.put(name, fileDifference);
    }


    public Map<String, DirectoryMetadataDifference> getSubdirectoryDifferences() {
        return subdirectoryDifferences;
    }


    public void addSubdirectoryDifference(String name, DirectoryMetadataDifference subdirectoryDifference) {
        this.subdirectoryDifferences.put(name, subdirectoryDifference);
    }
}
