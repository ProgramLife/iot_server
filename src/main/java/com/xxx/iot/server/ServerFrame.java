package com.xxx.iot.server;


import com.xxx.iot.common.observer.EventObserver;
import com.xxx.iot.common.observer.EventSubject;
import com.xxx.iot.common.observer.EventType;
import com.xxx.iot.common.observer.ThreadType;
import com.xxx.iot.common.util.TimeUtil;
import com.xxx.iot.tcp.TcpServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * Created by chen on 2019/2/15
 */
//@Component
public class ServerFrame extends JFrame implements EventObserver {

    @Autowired
    private TcpServer tcpServer;

    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static boolean isHeartLog = true;

    /**
     * 字体样式
     **/
    private SimpleAttributeSet server_style = new SimpleAttributeSet();
    private SimpleAttributeSet client_style = new SimpleAttributeSet();

    /**
     * 按钮
     **/
    private JButton start_bt = new JButton("开启服务");
    private JButton stop_bt = new JButton("关闭服务");
    private JButton clear_log_bt = new JButton("清除日志");
    private JCheckBox heart_check = new JCheckBox("显示心跳信息");

    /**
     * 面板
     **/
    private JScrollPane scrollPane = null;
    private JTextPane textPane = null;
    private Box box = null;
    private StyledDocument logDoc = null;

    /**
     * 文本
     **/
    private JLabel address_lb = new JLabel("地址：");
    private JLabel port_lb = new JLabel("端口：");

    /**
     * 数据输入框
     **/
    private JTextField address_field = new JTextField("localhost");
    private JTextField port_field = new JTextField("8899");
    private JTextField send_field = new JTextField("{\"name\":\"netty\", \"msg\":\"hello world!\"}");

    /**
     * 显示主界面
     */
    public void launchFrame() {
        this.setTitle("tcp服务端");                        //设置程序标题

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent arg0) {
                System.exit(0);
            }

        });
        initFont();
        initView();
        listenerInit();

        observerRegister();

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
        box_1.add(port_lb);
        box_1.add(port_field);
        heart_check.setSelected(isHeartLog);
        box_1.add(heart_check);
        box_1.add(start_bt);
        stop_bt.setVisible(false);
        box_1.add(stop_bt);
        box_1.add(clear_log_bt);

        this.getContentPane().add(scrollPane);
        this.getContentPane().add(box, BorderLayout.NORTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * 初始化字体
     **/
    private void initFont() {
        StyleConstants.setForeground(server_style, Color.BLACK);
        StyleConstants.setFontSize(server_style, 16);
        StyleConstants.setFontFamily(server_style, "微软雅黑");

        StyleConstants.setForeground(client_style, Color.BLUE);
        StyleConstants.setFontSize(client_style, 16);
        StyleConstants.setFontFamily(client_style, "微软雅黑");
    }

    /**
     * 观察者注册
     **/
    private void observerRegister() {
        EventSubject.getInstance().registerObserver(EventType.ON_START, this);
        EventSubject.getInstance().registerObserver(EventType.ON_CLOSE, this);

        EventSubject.getInstance().registerObserver(EventType.ON_CONNECT, this);
        EventSubject.getInstance().registerObserver(EventType.ON_READ, this);
        EventSubject.getInstance().registerObserver(EventType.ON_DISCONNECT, this);
        EventSubject.getInstance().registerObserver(EventType.ON_SEND, this);

    }

    /**
     * 监听器初始化
     **/
    private void listenerInit() {
        startBtListener();
        stopBtListener();
        clearLogBtListener();
        heartCheckListener();
    }


    private void startBtListener() {
        start_bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                start_bt.setVisible(false);
                stop_bt.setVisible(true);
                tcpServer.startTcpServer();
            }
        });
    }

    private void stopBtListener() {
        stop_bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                start_bt.setVisible(true);
                stop_bt.setVisible(false);
                tcpServer.stopTcpServer();
            }
        });
    }

    private void heartCheckListener() {
        heart_check.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isHeartLog = heart_check.isSelected();
            }
        });
    }


    private void clearLogBtListener() {
        clear_log_bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearLog();
            }
        });
    }

    /**
     * 消息处理 相关
     ***/
    @Override
    public void dispatchChange(EventType eventType, ThreadType threadType, String data) {
        switch (eventType) {
            case ON_START:
                serverMsgLog("", data);
                break;
            case ON_CLOSE:
                serverMsgLog("", data);
                break;
            case ON_CONNECT:
                serverMsgLog("", data);
                break;
            case ON_DISCONNECT:
                break;
            case ON_SEND:
                break;
            default:
                break;
        }
    }

    private void serverMsgLog(String msgType, String data) {
        try {
            logDoc.insertString(logDoc.getLength(),
                    TimeUtil.getTime() + "  " + msgType + "\n" + data + "\n",
                    server_style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void clearLog() {
        textPane.setText("");
    }

    public static void main(String[] args) {
        new ServerFrame().launchFrame();
    }
}
