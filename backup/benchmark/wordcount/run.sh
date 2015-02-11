#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# load global configurations

CUR_DIR=`dirname "$0"`
BASE_DIR=$CUR_DIR/..
. $BASE_DIR/conf/config.sh
. $BASE_DIR/bin/functions.sh

# benchmarks configurations

TOPOLOGY_CLASS=storm.benchmark.benchmarks.FileReadWordCount
TOPOLOGY_NAME=WordCount

# 2 workers
# SPOUT_NUM=18
# SPLIT_NUM=10
# COUNT_NUM=30
# WORKERS=2
# ACKERS=6


# 3 workers
SPOUT_NUM=27
SPLIT_NUM=15
COUNT_NUM=45
WORKERS=3
ACKERS=9


PENDING=200


TOPOLOGY_CONF=topology.name=$TOPOLOGY_NAME,topology.workers=$WORKERS,topology.acker.executors=$ACKERS,topology.max.spout.pending=$PENDING,component.spout_num=$SPOUT_NUM,component.split_bolt_num=$SPLIT_NUM,component.count_bolt_num=$COUNT_NUM


echo "========== running WordCount =========="
run_benchmark
