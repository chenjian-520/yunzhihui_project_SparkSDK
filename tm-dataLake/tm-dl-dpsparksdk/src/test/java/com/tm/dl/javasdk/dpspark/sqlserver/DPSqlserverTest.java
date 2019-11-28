package com.tm.dl.javasdk.dpspark.sqlserver;

import com.google.gson.Gson;
import com.tm.dl.javasdk.dpspark.DPSparkApp;
import com.tm.dl.javasdk.dpspark.DPSparkAppTest;
import com.tm.dl.javasdk.dpspark.common.DPSparkBase;
import com.tm.dl.javasdk.dpspark.streaming.DPStreaming;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/5/27.
 * Company: Foxconn
 * Project: TreasureMountain
 */
@Slf4j
public class DPSqlserverTest extends DPSparkBase implements Serializable {

    private static Gson gson = new Gson();


    public void scheduling(Map<String, Object> map) throws Exception {
        log.info("===============");
        HashMap<String, StructField> fieldHashMap = new HashMap<>();
        List<StructField> schemalist = new ArrayList<>();
        fieldHashMap.putIfAbsent("usercode", DataTypes.createStructField("usercode", DataTypes.StringType, true));
        fieldHashMap.putIfAbsent("menuid", DataTypes.createStructField("menuid", DataTypes.StringType, true));
        fieldHashMap.putIfAbsent("menuname", DataTypes.createStructField("menuname", DataTypes.StringType, true));
        fieldHashMap.putIfAbsent("dtime", DataTypes.createStructField("dtime", DataTypes.StringType, true));

        schemalist.add(DataTypes.createStructField("usercode", DataTypes.StringType, true));
        schemalist.add( DataTypes.createStructField("menuid", DataTypes.StringType, true));
        schemalist.add(DataTypes.createStructField("menuname", DataTypes.StringType, true));
        schemalist.add(DataTypes.createStructField("dtime", DataTypes.StringType, true));

        StructType schema = DataTypes.createStructType(schemalist);
        Intodashboard intodashboard= new Intodashboard();
        long time = Long.parseLong("1558883880001");
        Date dt = new Date(time);
        SimpleDateFormat sf =new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        intodashboard.setDtime(sf.format(dt));
        intodashboard.setMenuid("44444");
        intodashboard.setMenuname("ceshi");
        intodashboard.setUsercode("pod");
        List<String> intodashboards=new ArrayList<>();
        intodashboards.add(gson.toJson(intodashboard));
        JavaRDD<String> s= DPSparkApp.getContext().parallelize(intodashboards);
        Dataset<Row> intodashboardDs= DPSparkApp.getSession().read().schema(schema).json(s);

        intodashboardDs.show();
        DPSqlServer.commonOdbcWriteBatch("jdbc:sqlserver://10.60.136.172:1433;databaseName=HGS_DW;loginTimeout=90;",
                "HGS_DC", "Foxconn1@#", "sys_tm_intodashboard_log",
                intodashboardDs.toJavaRDD(), fieldHashMap,schema);
        DPSparkApp.stop();
    }

    @Override
    public void streaming(Map<String, Object> arrm, DPStreaming dpStreaming) throws Exception {

    }

    private static class Intodashboard {

        private String usercode;
        private String menuid;
        private String menuname;
        private String dtime;

        public void setUsercode(String usercode) {
            this.usercode = usercode;
        }

        public String getUsercode() {
            return this.usercode;
        }

        public void setMenuid(String menuid) {
            this.menuid = menuid
            ;
        }

        public String getMenuid() {
            return this.menuid;
        }

        public void setMenuname(String menuname) {
            this.menuname = menuname;
        }

        public String getMenuname(){
            return this.menuname;
        }

        public void setDtime(String dtime){
            this.dtime=dtime;
        }

        public String getDtime(){
            return this.dtime;
        }

    }
}
