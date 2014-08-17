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


import java.util.EnumMap;
import java.util.Map;


/**
 * A bean representing file metadata.
 *
 * @author Emerson Farrugia
 */
public class FileMetadata {

    private String name;
    private Map<ChecksumType, String> checksums = new EnumMap<>(ChecksumType.class);
    private long sizeInBytes;


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public boolean hasChecksum(ChecksumType checksumType) {

        return checksums.containsKey(checksumType);
    }


    public String getChecksum(ChecksumType checksumType) {

        return checksums.get(checksumType);
    }


    public Map<ChecksumType, String> getChecksums() {
        return checksums;
    }


    public void addChecksum(ChecksumType checksumType, String checksum) {
        checksums.put(checksumType, checksum);
    }


    public long getSizeInBytes() {
        return sizeInBytes;
    }


    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }
}
