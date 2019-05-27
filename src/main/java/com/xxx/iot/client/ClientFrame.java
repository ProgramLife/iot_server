package com.xxx.iot.client;

import com.alibaba.fastjson.JSONObject;
import com.xxx.iot.common.util.ConvertUtil;
import com.xxx.iot.common.util.TimeUtil;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @Author: cdp
 * @Date: 2019/5/27 15:21
 * @Version 1.0
 */
public class ClientFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static boolean isHeartLog = true;
    private static final String tcpIp = "127.0.0.1";
    private static final int tcpPort = 9988;
    private TcpClientManager tcpClientManager;

    /** 字体样式 **/
    private SimpleAttributeSet server_style = new SimpleAttributeSet();
    private SimpleAttributeSet client_style = new SimpleAttributeSet();
    private SimpleAttributeSet warn_style = new SimpleAttributeSet();
    /** 按钮 **/
    private JButton start_bt = new JButton("打开连接");
    private JButton stop_bt = new JButton("关闭连接");
    private JButton send_bt = new JButton("发送");
    private JButton clear_log_bt = new JButton("清除日志");
    private JCheckBox heart_check = new JCheckBox("启用心跳");
    /** 面板 **/
    private JScrollPane scrollPane = null;
    private JTextPane textPane = null;
    private Box box = null;
    private StyledDocument logDoc = null;
    /** 文本 **/
    private JLabel address_lb = new JLabel("地址：");
    private JLabel port_lb = new JLabel("端口：");
    private JLabel send_lb = new JLabel("发送数据：");
    /** 数据输入框 **/
    private JTextField address_field = new JTextField("localhost");
    private JTextField port_field = new JTextField("8899");
    private JTextField send_field = new JTextField("{\"name\":\"netty\", \"msg\":\"hello world!\"}");

    /**
     * 显示主界面
     */
    public void launchFrame() {
        this.setTitle("tcp客户端");                        //设置程序标题

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent arg0) {
                System.exit(0);
            }

        });
        initFont();
        initView();
        initListener();
    }

    /** 初始化字体 **/
    private void initFont() {
        StyleConstants.setForeground(server_style, Color.BLACK);
        StyleConstants.setFontSize(server_style, 16);
        StyleConstants.setFontFamily(server_style, "微软雅黑");

        StyleConstants.setForeground(client_style, Color.BLUE);
        StyleConstants.setFontSize(client_style, 16);
        StyleConstants.setFontFamily(client_style, "微软雅黑");

        StyleConstants.setForeground(warn_style, Color.RED);
        StyleConstants.setFontSize(warn_style, 16);
        StyleConstants.setFontFamily(warn_style, "微软雅黑");
    }

    /**
     * 初始化UI
     **/
    private void initView() {
        textPane = new JTextPane();
        textPane.setEditable(false);
        logDoc = textPane.getStyledDocument();
        scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        box = Box.createVerticalBox();               // 竖结构
        Box box_1 = Box.createHorizontalBox();       // 横结构
        Box box_2 = Box.createHorizontalBox();       // 横结构
        box.add(box_1);
        box.add(Box.createVerticalStrut(20)); // 两行的间距
        box.add(box_2);
        box.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        box_1.add(address_lb);
        box_1.add(address_field);
        box_1.add(new JLabel("    "));
        box_1.add(port_lb);
        box_1.add(port_field);
        heart_check.setSelected(isHeartLog);
        box_1.add(heart_check);
        box_1.add(start_bt);
        stop_bt.setVisible(false);
        box_1.add(stop_bt);
        box_1.add(new JLabel("    "));
        box_1.add(clear_log_bt);
        box_2.add(send_lb);
        box_2.add(send_field);
        box_2.add(new JLabel("    "));
        box_2.add(send_bt);

        this.getContentPane().add(scrollPane);
        this.getContentPane().add(box, BorderLayout.NORTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void initListener() {
        startListener();
        stopListener();
        clearLogListener();
        sendDataListener();
    }
    private void startListener() {
        start_bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tcpConnect();
                start_bt.setVisible(false);
                stop_bt.setVisible(true);
            }
        });
    }

    private void stopListener() {
        stop_bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tcpClientManager.stopClient();
                start_bt.setVisible(true);
                stop_bt.setVisible(false);
            }
        });
    }

    private void clearLogListener() {
        clear_log_bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearLog();
            }
        });
    }

    private void sendDataListener() {

    }

    private void tcpConnect() {
        if (tcpClientManager != null) {
            tcpClientManager.stopClient();
            tcpClientManager = null;
        }

        tcpClientManager = new TcpClientManager(tcpIp, tcpPort, tcpClientManagerListener);

        if (tcpClientManager != null && !tcpClientManager.isConnected()) {
            tcpClientManager.startClient();
        }
    }
    private TcpClientManager.TcpClientManagerListener tcpClientManagerListener = new TcpClientManager.TcpClientManagerListener() {
        @Override
        public void onConnected() {
            printMsgLog("", "连接成功", client_style);
        }

        @Override
        public void onReceived(int bodyLen, byte[] buff) {
            System.out.println(bodyLen);
            System.out.println(buff[0]);
            byte[] msgLenArr = new byte[4];
            byte[] body = new byte[bodyLen - 13];
            byte[] sessionIdArr = new byte[4];
            // 把接收到信息拷贝到receiData数组中
            // head(1)+ msgBodyLen(4) + version(1) + flags(1) + cc(2) + sessionId(4) + body(n)
            System.arraycopy(buff, 13, body, 0, bodyLen - 13);
            System.arraycopy(buff, 9, sessionIdArr, 0, 4);
            System.arraycopy(buff, 1, msgLenArr, 0, 4);
            byte head = buff[0];
            int version = buff[5];
            int flags = buff[6];
            byte[] ccArr = {buff[7], buff[8]};
            System.out.println(buff[7]);
            System.out.println(buff[8]);
            short cc;
            if (buff[7] == 1) {
                cc = (short) buff[8];
            } else {
                cc = (short) ConvertUtil.byteArray2int(ccArr);
            }

            int sessionId = ConvertUtil.byteArray2int(sessionIdArr);
            int msglen = ConvertUtil.byteArray2int(msgLenArr);

            String bodyStr = new String(body);
            JSONObject obj = new JSONObject();
            obj.put("head", head);
            obj.put("msglen", msglen);
            obj.put("version", version);
            obj.put("flags", flags);
            obj.put("cc", cc);
            obj.put("sessionId", sessionId);
            obj.put("body", bodyStr);

            printMsgLog("收到消息", obj.toJSONString(), server_style);
        }

        @Override
        public void onDisconnected() {
            printMsgLog("", "连接断开", warn_style);
        }

        @Override
        public void onConnectFailed() {
            System.out.println("连接失败");
            printMsgLog("", "连接失败", warn_style);
        }

        @Override
        public void onSend(int bodyLen, byte[] buff) {

        }
    };

    private void printMsgLog(String msgType, String data, SimpleAttributeSet font_style) {
        try {
            logDoc.insertString(logDoc.getLength(),
                    TimeUtil.getTime() + "  " + msgType + "\n" + data + "\n",
                    font_style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void clearLog() {
        textPane.setText("");
    }

    public static void main(String[] args) {
        new ClientFrame().launchFrame();
    }
}
