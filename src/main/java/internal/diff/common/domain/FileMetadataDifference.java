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


/**
 * A bean representing the difference between the metadata of two files.
 *
 * @author Emerson Farrugia
 */
public class FileMetadataDifference {

    private Long expectedSizeInBytes;
    private Long actualSizeInBytes;
    private String expectedChecksum;
    private String actualChecksum;


    public Long getExpectedSizeInBytes() {
        return expectedSizeInBytes;
    }


    public void setExpectedSizeInBytes(Long expectedSizeInBytes) {
        this.expectedSizeInBytes = expectedSizeInBytes;
    }


    public Long getActualSizeInBytes() {
        return actualSizeInBytes;
    }


    public void setActualSizeInBytes(Long actualSizeInBytes) {
        this.actualSizeInBytes = actualSizeInBytes;
    }


    public String getExpectedChecksum() {
        return expectedChecksum;
    }


    public void setExpectedChecksum(String expectedChecksum) {
        this.expectedChecksum = expectedChecksum;
    }


    public String getActualChecksum() {
        return actualChecksum;
    }


    public void setActualChecksum(String actualChecksum) {
        this.actualChecksum = actualChecksum;
    }
}
