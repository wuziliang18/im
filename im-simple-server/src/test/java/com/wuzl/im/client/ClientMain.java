package com.wuzl.im.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.wuzl.im.common.message.Message.Header;
import com.wuzl.im.common.message.Message.Type;

/**
 * 类ClientMain.java的实现描述：测试im服务的客户端
 * 
 * @author ziliang.wu 2017年2月28日 下午4:05:44
 */
public class ClientMain {

    private static final Logger logger = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] args) throws IOException {
        // System.out.println(getAdminMessageByFile());
        // System.out.println("消息长度:"+getAdminMessageByFile().length());
//         String tcpUrl = "172.17.4.90:10010";// 测试
//         String tcpUrl = "47.95.65.171:10010";// 公共环境
         String tcpUrl = "localhost:10010";// 本地
        // String tcpUrl = "106.14.51.42:10010";// 预发/
        // String tcpUrl = "172.18.100.26:10010";//预发
        // String tcpUrl = "106.14.51.42:10010";
//        String tcpUrl = "106.14.55.215:10010";// 线上
        //
//        String loginKey = "admin_1000_xitongtz";
         String loginKey = "10010";// 测试
        new TestServerSubscribeMessage(tcpUrl, loginKey).startSub();
        System.out.println("><<");
    }

    private static class TestServerSubscribeMessage extends ReceiveMessage {

        public TestServerSubscribeMessage(String tcpUrl, String loginKey) throws IOException{
            super(tcpUrl, loginKey);
        }

        @Override
        protected void init() {
            logger.info("loginKey:" + loginKey + "启动成功");
            super.init();
        }

        @Override
        protected void messageArrived(Type messageType, String json, Header requestHeader) {

            try {
                System.out.println(messageType);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println(sdf.format(new Date()));
                switch (messageType) {
                    case PUBLIC_KEY_RESPONSE:
                        System.out.println(json);
                        setKey(json);
                        sendLoginRequest();
                        break;
                    case LOGIN_RESPONSE:
                         String memberId = "10086";
                        for (int i = 0; i < 100000; i++) {
                            Map<String, Object> message = new HashMap<>();
                            message.put("content", "我是联通你是移动吗? 发几个好看的番号给你 番号id:" + i);
                             sendMessage(memberId, JSON.toJSONString(message));
//                             try {
//                                Thread.sleep(1000l);
//                            } catch (Exception e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                            sendMessage(memberId, getAdminMessage());
                        }
                        // String memberIds = "4lv80190,4l9wczc7";
                        // String[] receiveMemberId = memberIds.split(",");
                        // List<String> receiveMemberIdList = getAdminMessageReceiveMemberIdList();
                        // int count = 1;
                        // for (String memberId : receiveMemberIdList) {
                        // if (StringUtils.isEmpty(memberId)) {
                        // break;
                        // }
                        // memberId = memberId.trim();
                        // try {
                        // sendAdminMessage(memberId, getAdminMessage());
                        // } catch (Exception e) {
                        // System.out.println("发送失败:" + memberId);
                        // e.printStackTrace();
                        // }
                        // count++;
                        // if (count % 50 == 0) {
                        // System.out.println("当前数" + count);
                        // }
                        // }
                        break;
                    case COMMON_RESPONSE:
                        System.out.println(json);
                        break;
                    case MESSAGE_NOTIFICATION:
                        System.out.println(json);
                        break;

                    default:
                        System.out.println(json);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void outJson(Object obj) {
        System.out.println(JSON.toJSON(obj));
    }


    @SuppressWarnings("finally")
    public static String getFileContent(String filePath) {
        String FILE_SEPARATOR = System.getProperty("line.separator");
        StringBuilder content = new StringBuilder();
        File file = new File(filePath);
        if (!file.exists()) {
            throw new RuntimeException("文件【" + filePath + "】不存在!");
        }
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String data = br.readLine();
            while (data != null) {
                content.append(data + FILE_SEPARATOR);
                data = br.readLine(); // 接着读下一行
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return content.toString();
        }
    }

    private static List<String> getAdminMessageReceiveMemberIdList() throws IOException {
        List<String> contentList = Files.readAllLines(Paths.get("D:/wuzl/data/接受系统消息.csv"));
        return contentList;
    }
}
