import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.consumer.Whitelist;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

public class Main {

    public static void main(String[] args) throws  Exception{
        kafakTest2();

    }
    public static void kafakTest2() throws  Exception {
        KafkaProducer producerThread = new KafkaProducer(KafkaProperties.topic);
        producerThread.start();
        KafkaConsumer consumerThread = new KafkaConsumer(KafkaProperties.topic);
        consumerThread.start();
    }

    public static void kafakTest() throws  Exception{
        Properties properties = new Properties();
        properties.put("zookeeper.connect", "10.20.22.89:2181");
        properties.put("auto.commit.enable", "true");
        properties.put("auto.commit.interval.ms", "60000");
        properties.put("group.id", "test");

        ConsumerConfig consumerConfig = new ConsumerConfig(properties);

        ConsumerConnector javaConsumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);

        //topic的过滤器
        Whitelist whitelist = new Whitelist("test");
        List<KafkaStream<byte[], byte[]>> partitions = javaConsumerConnector.createMessageStreamsByFilter(whitelist);

        if (partitions==null) {
            System.out.println("empty!");
            TimeUnit.SECONDS.sleep(1);
        }

        //消费消息
        for (KafkaStream<byte[], byte[]> partition : partitions) {

            ConsumerIterator<byte[], byte[]> iterator = partition.iterator();
            while (iterator.hasNext()) {
                MessageAndMetadata<byte[], byte[]> next = iterator.next();
                System.out.println("partiton:" + next.partition());
                System.out.println("offset:" + next.offset());
                System.out.println("接收到message:" + new String(next.message(), "GB2312"));
            }
        }

    }
    public static void redisTest() {
        Jedis jedis = new Jedis("localhost");
        System.out.println("连接成功");
        //jedis.auth("redis");
        //查看服务是否运行
        System.out.println("服务正在运行: "+jedis.ping());
        jedis.set("runoobkey", "www.runoob.com");
        // 获取存储的数据并输出
        System.out.println("redis 存储的字符串为: "+ jedis.get("runoobkey"));
        //存储数据到列表中
        jedis.lpush("site-list", "Runoob");
        jedis.lpush("site-list", "Google");
        jedis.lpush("site-list", "Taobao");
        // 获取存储的数据并输出
        List<String> list = jedis.lrange("site-list", 0 ,2);
        for(int i=0; i<list.size(); i++) {
            System.out.println("列表项为: "+list.get(i));
        }

        // 获取数据并输出
        Set<String> keys = jedis.keys("*");
        Iterator<String> it=keys.iterator() ;
        while(it.hasNext()){
            String key = it.next();
            System.out.println(key);
        }
    }
}
