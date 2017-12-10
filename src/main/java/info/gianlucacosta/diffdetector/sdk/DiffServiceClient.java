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

import com.google.gson.Gson;
import info.gianlucacosta.diffdetector.core.ComparisonOperand;
import info.gianlucacosta.diffdetector.core.ComparisonResult;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Optional;


/**
 * Connects to Diff Detector's RESTful web service in order to
 * detect diffs between 2 operands.
 * <p>
 * The protocol to follow  is fairly straightforward:
 * <ol>
 * <li>Call setLeft() and ensure it returns 201</li>
 * <li>Call setRight() and ensure it returns 201</li>
 * <li>Call compare() and ensure it returns a non-empty Optional</li>
 * <li>In a finally block, call remove(), which should return 204</li>
 * </ol>
 * <p>
 * Beware of RuntimeException - as it may be anytime thrown
 * due to connectivity errors.
 * <p>
 * This client does not need to be closed, as its connections exist only
 * within its methods' execution span.
 */
public class DiffServiceClient {
    private static final Gson gson =
            new Gson();

    private static final String rootPath =
            "v1/diff";

    private final String endpoint;


    /**
     * Creates the client, but still does not open any connection to the service
     *
     * @param endpoint A string in the form "{protocol}://{host}:{port}"
     */
    public DiffServiceClient(String endpoint) {
        this.endpoint =
                endpoint.endsWith("/") ?
                        endpoint.substring(
                                0,
                                endpoint.length() - 1
                        )
                        :
                        endpoint;
    }


    /**
     * Sets the left operand for the comparison having the given id
     *
     * @param id   The id of the comparison
     * @param data The data of the left operand
     * @return The HTTP status code
     * @throws RuntimeException In case of connectivity errors
     */
    public int setLeft(String id, byte[] data) {
        return setComparisonOperand(
                id,
                "left",
                data
        );
    }


    private int setComparisonOperand(String id, String operandName, byte[] data) {
        String jsonMessage =
                gson.toJson(
                        new ComparisonOperand(data)
                );


        String url =
                String.format(
                        "%s/%s/%s/%s",
                        endpoint,
                        rootPath,
                        id,
                        operandName
                );

        try {
            Response response =
                    Request
                            .Post(url)
                            .bodyString(
                                    jsonMessage,
                                    ContentType.APPLICATION_JSON
                            )
                            .execute();

            return response.returnResponse().getStatusLine().getStatusCode();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * Sets the right operand for the comparison having the given id
     *
     * @param id   The id of the comparison
     * @param data The data of the right operand
     * @return The HTTP status code
     * @throws RuntimeException In case of connectivity errors
     */
    public int setRight(String id, byte[] data) {
        return setComparisonOperand(
                id,
                "right",
                data
        );
    }


    /**
     * Compares the left and right operand associated with the given id
     * and returns a non-empty Optional of ComparisonResult if everything
     * was successful
     *
     * @param id The id related to the current comparison
     * @return The HTTP status code
     * @throws RuntimeException In case of connectivity errors
     */
    public Optional<ComparisonResult> compare(String id) {
        String url =
                String.format(
                        "%s/%s/%s",
                        endpoint,
                        rootPath,
                        id
                );

        try {
            HttpResponse response =
                    Request
                            .Get(url)
                            .execute()
                            .returnResponse();

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return Optional.empty();
            }

            String diffResultString =
                    EntityUtils.toString(response.getEntity());

            ComparisonResult diffResult =
                    gson.fromJson(
                            diffResultString,
                            ComparisonResult.class
                    );

            return Optional.of(diffResult);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * Removes the left and right element having the given ID
     *
     * @param id The id to remove from the server
     * @return The HTTP status code
     */
    public int remove(String id) {
        String url =
                String.format(
                        "%s/%s/%s",
                        endpoint,
                        rootPath,
                        id
                );

        try {
            Response response =
                    Request
                            .Delete(url)
                            .execute();

            return response.returnResponse().getStatusLine().getStatusCode();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
