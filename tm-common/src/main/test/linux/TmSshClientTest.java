package linux;

import com.treasuremountain.tmcommon.thirdpartyservice.linux.TmSshClient;

import java.io.IOException;
import java.util.function.Function;

/**
 * Description:
 * <p>
 * Created by xun-yu.she on 2019/6/03.
 * Company: Foxconn
 * Project: MaxIoT
 * Remark:连接linux系统，提交命令，获取返回值
 */
public class TmSshClientTest {
    public static void main(String[] args) {
        String usr = "root";
        String password = "FoxconnP@ssw0rd";
        String serverIP = "10.60.136.132";
        TmSshClient exe = new TmSshClient(serverIP, usr, password);
        String outInf, outInf1, outInf2;
        try {
            exe.login();
            exe.exec("ps -ef", new Function<String, String>() {
                @Override
                public String apply(String s) {
                    System.out.println("----" + s);
                    return null;
                }
            }, new Function<String, String>() {
                @Override
                public String apply(String s) {
                    System.out.println("----" + s);
                    return null;
                }
            });
            exe.exec("ps -ef", new Function<String, String>() {
                @Override
                public String apply(String s) {
                    System.out.println("----" + s);
                    return null;
                }
            }, new Function<String, String>() {
                @Override
                public String apply(String s) {
                    System.out.println("----" + s);
                    return null;
                }
            });
            exe.exec("vmstat", new Function<String, String>() {
                @Override
                public String apply(String s) {
                    System.out.println("----" + s);
                    return null;
                }
            }, new Function<String, String>() {
                @Override
                public String apply(String s) {
                    System.out.println("----" + s);
                    return null;
                }
            });
            exe.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
