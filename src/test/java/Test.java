import com.sjw.sjw.redis.client.client.SjwRedisClient;
import com.sjw.sjw.redis.client.client.SjwRedisClientPool;
import com.sjw.sjw.redis.client.client.SjwRedisClientPoolImpl;
import com.sjw.sjw.redis.client.exception.SjwRedisClientTryException;

import java.util.List;

/**
 * @author shijiawei
 * @version Test.java, v 0.1
 * @date 2018/11/19
 */
public class Test {
    public static void main(String[] args) throws SjwRedisClientTryException {
        SjwRedisClientPoolImpl.Build build = new SjwRedisClientPoolImpl.Build();
        build.setHost("47.100.118.214");
        build.setPort(6379);
        build.setPassword("Shijiawei#110");
        build.setPoolSize(1);
        build.setOutTimeMills(5000L);
        build.setWorkerGroupSize(1);
        SjwRedisClientPool pool = build.build();
//        SjwRedisClientPool pool = SjwRedisClientPoolImpl.simplePool("47.100.118.214", 6379, "Shijiawei#110");
        SjwRedisClient client = pool.getClient();
//        SjwRedisClient client2 = pool.getClient();
        try {
            long start = System.currentTimeMillis();
            client.stringSet("test", "ddd", 100);
            String res = client.stringGet("test");

            printRes(start, res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
            pool.closePool();
        }
//        ExecutorService executorService = Executors.newCachedThreadPool();
//        for (int i = 0; i < 5; i++) {
//            int num = i;
//            executorService.execute(() -> {
//                String res = client.sendRequest("PING\r\n");
//                System.out.println("回复" + num + " = " + res);
//            });
//        }
    }

    private static void stringGetTest(SjwRedisClient client) {
        System.out.println("->开始执行【string get】测试");
        boolean check = false;
        long start = System.currentTimeMillis();
        String response = client.stringGet("a1");
        printRes(start, response);
        if (response.equals("1a")) {
            check = true;
        }
        System.out.println("->结束执行【string get】测试  -----> 验证结果 : " + check);
    }


    private static void printRes(long start, String res) {
        long dua = System.currentTimeMillis() - start;
        System.out.println("res = " + res + " ,time = " + dua + "ms");
    }

    private static void printRes(long start, int res) {
        long dua = System.currentTimeMillis() - start;
        System.out.println("res = " + res + " ,time = " + dua + "ms");
    }


    private static void printRes(long start, Object[] res) {
        long dua = System.currentTimeMillis() - start;
        String response = "";
        for (Object re : res) {
            response = response + re + ",";
        }
        System.out.println("res = " + response + " ,time = " + dua + "ms");
    }

    private static void printRes(long start, List<String> res) {
        long dua = System.currentTimeMillis() - start;
        String response = "";
        for (Object re : res) {
            response = response + re + ",";
        }
        System.out.println("res = " + response + " ,time = " + dua + "ms");
    }

    private static void printResObj(long start, List<Book> res) {
        long dua = System.currentTimeMillis() - start;
        String response = "";
        for (Book re : res) {
            response = response + re + ",";
        }
        System.out.println("res = " + response + " ,time = " + dua + "ms");
    }


}
