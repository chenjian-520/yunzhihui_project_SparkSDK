# coding:UTF-8


def main(dPSparkSdk):
    appJpype = dPSparkSdk.appJpype
    print("start the spark session" + dPSparkSdk.DPSparkApp.getSession().toString())
    # 实例化 scan
    scanClass = appJpype.JClass("org.apache.hadoop.hbase.client.Scan")
    scan = scanClass()
    # 实例化Calendar
    calendarClass = appJpype.JClass("java.util.GregorianCalendar")
    calendar = calendarClass()
    calendar.setTime(appJpype.JClass("java.util.Date")())
    calendar.add(calendarClass.DATE, -1)
    beginDate = appJpype.JClass('java.lang.Long').toString(calendar.getTimeInMillis())
    calendar.set(calendarClass.HOUR, 23)
    endDate = appJpype.JClass('java.lang.Long').toString(calendar.getTimeInMillis())
    # 获得JavaPairRDD
    useractionlogResultRdd = dPSparkSdk.DPHbase.rddRead('sys_re_tt_a20190819112335', scan)
    print("count", str(useractionlogResultRdd.count()))
    # 关闭java进程
    dPSparkSdk.stop()
