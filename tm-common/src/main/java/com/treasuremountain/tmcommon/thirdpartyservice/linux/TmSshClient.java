package com.treasuremountain.tmcommon.thirdpartyservice.linux;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Function;

/**
 * Description: ssh连接服务请，发送控制指令
 * <p>
 * Created by xun-yu.she on 2019/6/03.
 * Company: Foxconn
 * Project: MaxIoT
 */
public class TmSshClient
{

    private static final Logger LOG = LoggerFactory.getLogger(TmSshClient.class);

    private Connection conn;
    private String ip;
    private String usr;
    private String psword;
    private String charset = Charset.defaultCharset().toString();
    private String outResult;

    private static final int TIME_OUT = 1000 * 5 * 60;

    public TmSshClient(String ip, String usr, String ps) {
        this.ip = ip;
        this.usr = usr;
        this.psword = ps;
    }
    /**
     * 登录
     *
     * @return 返回True/False
     ***/
    public boolean login() throws IOException {
        if (conn == null) {
            conn = new Connection(ip);
            conn.connect();
            return conn.authenticateWithPassword(usr, psword);
        } else {
            return true;
        }
    }

    /**
     * 执行命令
     *
     * @return 回调命令信息
     ***/
    public void exec(String cmds, Function<String,String> msg,Function<String,String> errmsg) throws IOException {
        InputStream stdOut = null;
        InputStream stdErr = null;
        String outStr = "";
        String outErr = "";
        int ret = -1;

        try {
            if (login()) {
                Session session = conn.openSession();
                session.execCommand(cmds);
                stdOut = new StreamGobbler(session.getStdout());
                processStream(stdOut, charset,msg);
                LOG.info("caijl:[INFO] outStr=" + outStr);
                stdErr = new StreamGobbler(session.getStderr());
                processStream(stdErr, charset,errmsg);
                LOG.info("caijl:[INFO] outErr=" + outErr);
                session.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
                ret = session.getExitStatus();

            } else {
                LOG.error("caijl:[INFO] ssh2 login failure:" + ip);
                throw new IOException("SSH2_ERR");
            }

        } finally {
            if (stdOut != null)
                stdOut.close();
            if (stdErr != null)
                stdErr.close();
        }
        this.outResult = outStr;
    }

    /**
     * 处理流
     *
     * @return 逐行回调数据信息
     ***/
    private void processStream(InputStream in, String charset,Function<String,String> msg) throws IOException {
        byte[] buf = new byte[1024];
        while (in.read(buf) != -1) {
            msg.apply(new String(buf, charset));
        }
    }

    /**
     * 退出
     *
     * @return 退出
     ***/
    public String logout() {
        if (conn != null) {
            this.conn.close();
        }
        return "退出";
    }




}