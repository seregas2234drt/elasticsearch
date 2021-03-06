/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.client.transform.transforms.latest;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.common.xcontent.ConstructingObjectParser.constructorArg;

/**
 * Class describing how to compute latest doc for every unique key
 */
public class LatestConfig implements ToXContentObject {

    private static final String NAME = "latest_config";

    private static final ParseField UNIQUE_KEY = new ParseField("unique_key");
    private static final ParseField SORT = new ParseField("sort");

    private final List<String> uniqueKey;
    private final String sort;

    @SuppressWarnings("unchecked")
    private static final ConstructingObjectParser<LatestConfig, Void> PARSER =
        new ConstructingObjectParser<>(NAME, true, args -> new LatestConfig((List<String>) args[0], (String) args[1]));

    static {
        PARSER.declareStringArray(constructorArg(), UNIQUE_KEY);
        PARSER.declareString(constructorArg(), SORT);
    }

    public static LatestConfig fromXContent(final XContentParser parser) {
        return PARSER.apply(parser, null);
    }

    LatestConfig(List<String> uniqueKey, String sort) {
        this.uniqueKey = uniqueKey;
        this.sort = sort;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(UNIQUE_KEY.getPreferredName(), uniqueKey);
        builder.field(SORT.getPreferredName(), sort);
        builder.endObject();
        return builder;
    }

    public List<String> getUniqueKey() {
        return uniqueKey;
    }

    public String getSort() {
        return sort;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        LatestConfig that = (LatestConfig) other;
        return Objects.equals(this.uniqueKey, that.uniqueKey) && Objects.equals(this.sort, that.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueKey, sort);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> uniqueKey;
        private String sort;

        /**
         * Set how to group the source data
         * @param uniqueKey The configuration describing how to group the source data
         * @return the {@link Builder} with the unique key set.
         */
        public Builder setUniqueKey(String... uniqueKey) {
            return setUniqueKey(Arrays.asList(uniqueKey));
        }

        public Builder setUniqueKey(List<String> uniqueKey) {
            this.uniqueKey = uniqueKey;
            return this;
        }

        public Builder setSort(String sort) {
            this.sort = sort;
            return this;
        }

        public LatestConfig build() {
            return new LatestConfig(uniqueKey, sort);
        }
    }
}
