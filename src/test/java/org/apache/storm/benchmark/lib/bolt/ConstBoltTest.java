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

package org.apache.storm.benchmark.lib.bolt;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.apache.storm.benchmark.util.MockTupleHelpers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class ConstBoltTest {

    private ConstBolt bolt;
    private Tuple tuple;
    private BasicOutputCollector collector;
    private OutputFieldsDeclarer declarer;

    @BeforeMethod
    public void setUp() {
        bolt = new ConstBolt();
        tuple = MockTupleHelpers.mockAnyTuple();
        collector = mock(BasicOutputCollector.class);
        declarer = mock(OutputFieldsDeclarer.class);
    }

    @Test
    public void shouldDeclareOutputFields() {
        bolt.declareOutputFields(declarer);

        verify(declarer, times(1)).declare(any(Fields.class));
    }

    @Test
    public void shouldEmitFirstFieldOfTuple() {
        bolt.execute(tuple, collector);

        verify(tuple, times(1)).getValue(0);
        verify(collector, times(1)).emit(any(Values.class));
    }
}
