
import lombok.Data;

@Data
public class DeviceUnitData {
    //业务Id
    private String businessId;
    //数据唯一标识（有序存储）Test:1000000001(唯一标记 :时间戳)
    private String rowkey;
    //设备单元ID
    private String DeviceUnitID;
    //组织
    private String Org;
    //状态
    private String Status;
    //故障代码
    private String Value;
    //描述
    private String Desc;
    //备用值（备用数据）
    private String ReserveValue;
    //值时间
    private String ValueTime;
    //值时间戳
    private String ValueTimeStamp;
    //值发送时间
    private String SendTime;
    //值发送时间戳（发送接口时）
    private String SendTimeStamp;
    //值接收时间（备用暂不启用）
    private String ReceiveTime;
    //值接收时间戳（备用暂不启用）
    private String ReceiveTimeStamp;
}
