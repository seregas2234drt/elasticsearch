/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.client.ml;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.client.ml.job.config.Job;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Request object to get {@link Job} objects with the matching {@code jobId}s or
 * {@code groupName}s.
 *
 * {@code _all} explicitly gets all the jobs in the cluster
 * An empty request (no {@code jobId}s) implicitly gets all the jobs in the cluster
 */
public class GetJobRequest extends ActionRequest implements ToXContentObject {

    public static final ParseField JOB_IDS = new ParseField("job_ids");
    public static final ParseField ALLOW_NO_MATCH = new ParseField("allow_no_match");
    public static final String EXCLUDE_GENERATED = "exclude_generated";

    private static final String ALL_JOBS = "_all";
    private final List<String> jobIds;
    private Boolean allowNoMatch;
    private Boolean excludeGenerated;

    @SuppressWarnings("unchecked")
    public static final ConstructingObjectParser<GetJobRequest, Void> PARSER = new ConstructingObjectParser<>(
        "get_job_request",
        true, a -> new GetJobRequest(a[0] == null ? new ArrayList<>() : (List<String>) a[0]));

    static {
        PARSER.declareStringArray(ConstructingObjectParser.optionalConstructorArg(), JOB_IDS);
        PARSER.declareBoolean(GetJobRequest::setAllowNoMatch, ALLOW_NO_MATCH);
    }

    /**
     * Helper method to create a query that will get ALL jobs
     * @return new {@link GetJobRequest} object searching for the jobId "_all"
     */
    public static GetJobRequest getAllJobsRequest() {
        return new GetJobRequest(ALL_JOBS);
    }

    /**
     * Get the specified {@link Job} configurations via their unique jobIds
     * @param jobIds must not contain any null values
     */
    public GetJobRequest(String... jobIds) {
        this(Arrays.asList(jobIds));
    }

    GetJobRequest(List<String> jobIds) {
        if (jobIds.stream().anyMatch(Objects::isNull)) {
            throw new NullPointerException("jobIds must not contain null values");
        }
        this.jobIds = new ArrayList<>(jobIds);
    }

    /**
     * All the jobIds for which to get configuration information
     */
    public List<String> getJobIds() {
        return jobIds;
    }

    /**
     * Whether to ignore if a wildcard expression matches no jobs.
     *
     * @param allowNoMatch If this is {@code false}, then an error is returned when a wildcard (or {@code _all}) does not match any jobs
     */
    public void setAllowNoMatch(boolean allowNoMatch) {
        this.allowNoMatch = allowNoMatch;
    }

    public Boolean getAllowNoMatch() {
        return allowNoMatch;
    }

    /**
     * Setting this flag to `true` removes certain fields from the configuration on retrieval.
     *
     * This is useful when getting the configuration and wanting to put it in another cluster.
     *
     * Default value is false.
     * @param excludeGenerated Boolean value indicating if certain fields should be removed
     */
    public void setExcludeGenerated(boolean excludeGenerated) {
        this.excludeGenerated = excludeGenerated;
    }

    public Boolean getExcludeGenerated() {
        return excludeGenerated;
    }

    @Override
    public ActionRequestValidationException validate() {
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobIds, excludeGenerated, allowNoMatch);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        GetJobRequest that = (GetJobRequest) other;
        return Objects.equals(jobIds, that.jobIds) &&
            Objects.equals(excludeGenerated, that.excludeGenerated) &&
            Objects.equals(allowNoMatch, that.allowNoMatch);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();

        if (jobIds.isEmpty() == false) {
            builder.field(JOB_IDS.getPreferredName(), jobIds);
        }

        if (allowNoMatch != null) {
            builder.field(ALLOW_NO_MATCH.getPreferredName(), allowNoMatch);
        }

        builder.endObject();
        return builder;
    }
}
