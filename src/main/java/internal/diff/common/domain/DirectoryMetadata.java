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


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashMap;
import java.util.Map;


/**
 * A bean representing the metadata of a directory tree.
 *
 * @author Emerson Farrugia
 */
public class DirectoryMetadata {

    private String name;
    private Map<String, DirectoryMetadata> subdirectories = new HashMap<>();
    private Map<String, FileMetadata> files = new HashMap<>();


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Map<String, DirectoryMetadata> getSubdirectories() {
        return subdirectories;
    }


    public void addSubdirectory(DirectoryMetadata subdirectory) {
        this.subdirectories.put(subdirectory.getName(), subdirectory);
    }


    public Map<String, FileMetadata> getFiles() {
        return files;
    }


    public void addFile(FileMetadata file) {
        this.files.put(file.getName(), file);
    }


    @Override
    public String toString() {

        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
