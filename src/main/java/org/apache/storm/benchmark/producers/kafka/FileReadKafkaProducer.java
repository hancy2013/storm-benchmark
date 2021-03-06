/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package org.apache.storm.benchmark.producers.kafka;

import backtype.storm.Config;
import backtype.storm.generated.StormTopology;
import org.apache.storm.benchmark.util.FileReader;

public class FileReadKafkaProducer extends KafkaProducer {

    public static final String FILE = "/resources/A_Tale_of_Two_City.txt";

    @Override
    public StormTopology getTopology(Config config) {
        spout = new FileReadSpout(FILE);
        return super.getTopology(config);
    }

    static class FileReadSpout extends KafkaProducerSpout {

        private static final long serialVersionUID = -7503987913879480348L;
        private final FileReader reader;

        public FileReadSpout(String file) {
            this.reader = new FileReader(file);
        }

        public FileReadSpout(FileReader reader) {
            this.reader = reader;
        }

        @Override
        public void nextTuple() {
            nextMessage(reader.nextLine());
        }
    }
}
