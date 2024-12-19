

#!/bin/bash

BROKER="localhost:9092"
TOPIC="benchmark-topic"
NUM_RECORDS=(1000 10000 100000 1000000 1600000)
OUTPUT_FILE_PROD="benchmark/kafka_prod_benchmark_results.txt"
OUTPUT_FILE_CONS="benchmark/kafka_cons_benchmark_results.txt"

run_benchmark() {
    local num_records=$1
    for i in {1..10}; do
        kafka-producer-perf-test.sh --topic $TOPIC --num-records $num_records --record-size 100 --throughput -1 --producer-props bootstrap.servers=$BROKER >> $OUTPUT_FILE_PROD 2>&1
        echo "" >> $OUTPUT_FILE_PROD

        kafka-consumer-perf-test.sh --broker-list $BROKER --topic $TOPIC --fetch-size 1048576 --messages $num_records --threads 1 >> $OUTPUT_FILE_CONS 2>&1
        echo "" >> $OUTPUT_FILE_CONS
    done
}

for num_records in "${NUM_RECORDS[@]}"; do
    run_benchmark $num_records
done

echo "Benchmarking completed. Results are saved in $OUTPUT_FILE."