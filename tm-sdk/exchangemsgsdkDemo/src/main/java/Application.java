import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson; //json序列化
import com.treasuremountain.exchangemsgsdk.TMexchangeMsgSdk;


/**
 * Description:数据湖数据上传，数据订阅Demo
 * Created by xun-yu.she on 2019/4/25.
 */
public class Application {

    public static void main(String[] args) throws IOException, TimeoutException {

        //订
        TMexchangeMsgSdk.exchangeRevive("DeviceUnitDataCollection", 10, d ->
        {
            ArrayList<DeviceUnitData> deviceUnitDataList = GetDeviceUnitDataList(d);
            ArrayList<DeviceUnitData> deviceUnitDataByKepwareList = GetDeviceUnitDataByKepwareList(d);
            //对结果集做业务逻辑操作
            if (deviceUnitDataList.size() > 0) {
                deviceUnitDataList.forEach(item -> {
                    System.out.println(item.getRowkey());
                });
            }
            System.out.println(d);
        });
    }

    //对收到的数据进行反序列化
    public static Gson gson = new Gson();

    public static ArrayList<DeviceUnitData> GetDeviceUnitDataList(String data) {
        ReviveData reviveData = gson.fromJson(data, ReviveData.class);
        //region 对收到的数据进行反序列化
        ArrayList<DeviceUnitData> deviceUnitDataList = new ArrayList<DeviceUnitData>();
        if (reviveData != null && reviveData.businessId != null && reviveData.rowKvList != null) {
            ArrayList<RowKvList> rowKvList = reviveData.getRowKvList();
            for (RowKvList row : rowKvList) {
                DeviceUnitData deviceUnitData = new DeviceUnitData();
                deviceUnitData.setBusinessId(reviveData.getBusinessId());
                ArrayList<ColumnKvList> columnKvList = row.getColumnKvList();
                for (ColumnKvList column : columnKvList) {
                    //region 获取或设置字段的值
                    switch (column.key) {
                        case "rowkey":
                            deviceUnitData.setRowkey(column.value);
                            continue;
                        case "DeviceUnitID":
                            deviceUnitData.setDeviceUnitID(column.value);
                            continue;
                        case "Org":
                            deviceUnitData.setOrg(column.value);
                            continue;
                        case "Status":
                            deviceUnitData.setStatus(column.value);
                            continue;
                        case "Value":
                            deviceUnitData.setValue(column.value);
                            continue;
                        case "Desc":
                            deviceUnitData.setDesc(column.value);
                            continue;
                        case "ReserveValue":
                            deviceUnitData.setReserveValue(column.value);
                            continue;
                        case "ValueTime":
                            deviceUnitData.setValueTime(column.value);
                            continue;
                        case "ValueTimeStamp":
                            deviceUnitData.setValueTimeStamp(column.value);
                            continue;
                        case "SendTime":
                            deviceUnitData.setSendTime(column.value);
                            continue;
                        case "SendTimeStamp":
                            deviceUnitData.setSendTimeStamp(column.value);
                            continue;
                        case "ReceiveTime":
                            deviceUnitData.setReceiveTime(column.value);
                            continue;
                        case "ReceiveTimeStamp":
                            deviceUnitData.setReceiveTimeStamp(column.value);
                            continue;
                    }
                    //endregion
                }
                deviceUnitDataList.add(deviceUnitData);
            }
        }
        //endregion
        return deviceUnitDataList;
    }

    public static ArrayList<DeviceUnitData> GetDeviceUnitDataByKepwareList(String data) {
        ReviveData reviveData = gson.fromJson(data, ReviveData.class);
        //region 对收到的数据进行反序列化
        ArrayList<DeviceUnitData> deviceUnitDataList = new ArrayList<DeviceUnitData>();
        if (reviveData != null && reviveData.businessId != null && reviveData.rowKvList != null) {
            ArrayList<RowKvList> rowKvList = reviveData.getRowKvList();
            for (RowKvList row : rowKvList) {
                DeviceUnitData deviceUnitData = new DeviceUnitData();
                deviceUnitData.setBusinessId(reviveData.getBusinessId());
                ArrayList<ColumnKvList> columnKvList = row.getColumnKvList();
                for (ColumnKvList column : columnKvList) {
                    if (deviceUnitData.getRowkey() != null && deviceUnitData.getDeviceUnitID() != null && deviceUnitData.getOrg() != null && deviceUnitData.getValue() != null && deviceUnitData.getValueTimeStamp() != null) {
                        break;
                    }
                    //region 获取或设置字段的值
                    switch (column.key) {
                        case "rowkey":
                            deviceUnitData.setRowkey(column.value);
                            continue;
                        case "id":
                            deviceUnitData.setDeviceUnitID(column.value);
                            continue;
                        case "org":
                            deviceUnitData.setOrg(column.value);
                            continue;
                        case "v":
                            deviceUnitData.setValue(column.value);
                            continue;
                        case "reserveValue":
                            deviceUnitData.setReserveValue(column.value);
                            continue;
                        case "t":
                            deviceUnitData.setValueTimeStamp(column.value);
                            continue;
                    }

                    //endregion
                }
                deviceUnitDataList.add(deviceUnitData);
            }
        }
        //endregion
        return deviceUnitDataList;
    }

}
