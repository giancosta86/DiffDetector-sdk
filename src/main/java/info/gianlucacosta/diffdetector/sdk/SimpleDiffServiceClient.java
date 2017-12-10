/*^
  ===========================================================================
  Diff Detector - SDK
  ===========================================================================
  Copyright (C) 2017 Gianluca Costa
  ===========================================================================
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ===========================================================================
*/

package info.gianlucacosta.diffdetector.sdk;

import info.gianlucacosta.diffdetector.core.ComparisonResult;
import org.apache.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

/**
 * High-level client for contacting Diff Detector's RESTful web service.
 */
public class SimpleDiffServiceClient {
    private final DiffServiceClient diffServiceClient;


    /**
     * Creates the client, but still does not open any connection to the service
     *
     * @param endpoint A string in the form "{protocol}://{host}:{port}"
     */
    public SimpleDiffServiceClient(String endpoint) {
        diffServiceClient =
                new DiffServiceClient(endpoint);
    }


    /**
     * Compares the 2 given operands, then removes them from the server.
     * This method throws no network exceptions.
     * <p>
     * Removal is performed at-most-once.
     *
     * @param left  The left operand
     * @param right The right operand
     * @return The comparison result, or an empty Optional in case of exceptions
     */
    public Optional<ComparisonResult> compare(byte[] left, byte[] right) {
        String id =
                UUID.randomUUID().toString();

        try {
            if (diffServiceClient.setLeft(id, left) != HttpStatus.SC_CREATED) {
                return Optional.empty();
            }

            try {
                if (diffServiceClient.setRight(id, right) != HttpStatus.SC_CREATED) {
                    return Optional.empty();
                }

                return diffServiceClient.compare(id);
            } finally {
                diffServiceClient.remove(id);
            }
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
