public class Main{
	public static void main(String[] args){
		String kafkaServer = "localhost:9092";
		String topic = "cpu";
		String groupId = "flink";
		//StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
		KafkaSource<String> kafkaSource = createStringConsumerForTopic(kafkaServer, topic, groupId);
	}
}
