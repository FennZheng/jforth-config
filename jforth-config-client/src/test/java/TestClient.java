import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.xforth.config.client.ConfigBundle;

import java.util.concurrent.CountDownLatch;


@ContextConfiguration(locations = { "classpath*:configBundle.xml" })
public class TestClient extends AbstractJUnit4SpringContextTests {
    @Autowired
    private ConfigBundle configBundle;
    private CuratorFramework zkClient;
    @Test
    public void testLocalConfigRead() throws Exception {
        //local config
        Assert.assertEquals("local.config.local.value1",configBundle.get("local.config.local.key1"));
        Assert.assertEquals("local.config.local.value2",configBundle.get("local.config.local.key2"));
    }
    @Test
    public void testRemoteConfigRead() throws Exception {
        //remote config
        Assert.assertEquals("local.config.remote.value3",configBundle.get("local.config.remote.key3"));
        Assert.assertEquals("local.config.remote.value4",configBundle.get("local.config.remote.key4"));
    }
    @Test
    public void testLocalOverrideRemoteConfig() throws Exception {
        //override config
        Assert.assertEquals("local.config.override.local.value1",configBundle.get("local.config.override.key1"));
    }
    @Test
    public void testDyanmicLocalConfig() throws Exception {
        //dynamic local config change
        //assertEquals("local.config.local.value1",configBundle.get("local.config.local.key1"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String oldVal = configBundle.get("local.config.local.key1");
                while (true) {
                    long start = System.nanoTime();
                    //modify config.properties value during this time
                    System.out.println("get cost:" + (System.nanoTime() - start) + configBundle.get("local.config.local.key1"));
                    if (!oldVal.equals(configBundle.get("local.config.local.key1"))) {
                        //assertEquals("local.config.local.value1.dynamic",configBundle.get("local.config.local.key1"));
                        //break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                       // e.printStackTrace();
                    }
                    //use no sleep to simulate concurrent read/modify
                    //Thread.sleep(5000);
                }
            }
        }).start();
    }
    @Test
    public void testDynamicRemoteConfig() throws Exception {
        //dynamic remote config change
        Assert.assertEquals("local.config.remote.value3",configBundle.get("local.config.remote.key3"));
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(new Runnable() {
            @Override
            public void run() {
                countDownLatch.countDown();
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    logger.error("modify dynamic remote config InterruptedException error:{}",e);
                }
                modifyDataInZookeeper();
            }
        }).start();
        String oldVal = configBundle.get("local.config.remote.key3");
        while(true){
            String newVal = configBundle.get("local.config.remote.key3");
            System.out.println(newVal);
            if(!oldVal.equals(newVal)){

                break;
            }
            countDownLatch.countDown();
        }
    }
    @Before
    public void initZkClient(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.newClient("172.16.3.27:2181", retryPolicy);
        zkClient.start();
    }
    public TestClient initZkClientAndReturn(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        zkClient = CuratorFrameworkFactory.newClient("172.16.3.27:2181", retryPolicy);
        zkClient.start();
        return this;
    }
    @After
    public void stopZkClient(){
        zkClient.getZookeeperClient().close();
    }
   /* public static void main(String args[]) throws Exception {
        new TestClient().initZkClientAndReturn().mockDataInZookeeper();
    }*/
    public void mockDataInZookeeper() {
        try {
            //zkClient.delete().forPath("/config-center/app1");
            String serviceCode = "/config-center/app1";
            //zkClient.create().withMode(CreateMode.PERSISTENT).forPath(serviceCode);
            zkClient.create().withMode(CreateMode.PERSISTENT).forPath(serviceCode+"/local.config.remote.key3","local.config.remote.value3".getBytes("utf-8"));
            zkClient.create().withMode(CreateMode.PERSISTENT).forPath(serviceCode+"/local.config.remote.key4","local.config.remote.value4".getBytes("utf-8"));
            zkClient.create().withMode(CreateMode.PERSISTENT).forPath(serviceCode+"/local.config.override.key1","local.config.override.remote.value1".getBytes("utf-8"));
        }catch (Exception e ){
            logger.error("modifyDataInZookeeper error:{}",e);
        }
    }
    @Test
    public void modifyDataInZookeeper() {
        try {
            String serviceCode = "/config-center/app1";
            zkClient.setData().forPath(serviceCode+"/local.config.remote.key3",
                    "local.config.remote.value3.dynamicChange4".getBytes("utf-8"));
        }catch (Exception e ){
            logger.error("modifyDataInZookeeper error:{}",e);
        }
    }

    public void resetDataInZookeeper() {
        try {
            //zkClient.delete().forPath("/config-center/app1");
            String serviceCode = "/config-center/app1";
            //zkClient.create().withMode(CreateMode.PERSISTENT).forPath(serviceCode);
            zkClient.setData().forPath(serviceCode+"/local.config.remote.key3","local.config.remote.value3".getBytes("utf-8"));
            zkClient.setData().forPath(serviceCode+"/local.config.remote.key4","local.config.remote.value4".getBytes("utf-8"));
            zkClient.setData().forPath(serviceCode+"/local.config.override.key1","local.config.override.remote.value1".getBytes("utf-8"));
        }catch (Exception e ){
            logger.error("modifyDataInZookeeper error:{}",e);
        }
    }
    @Test
    public void deleteMockData() throws Exception {
        //zkClient.delete().forPath("/config-center/app1");
        //zkClient.delete().forPath("/config-center");
    }
}
