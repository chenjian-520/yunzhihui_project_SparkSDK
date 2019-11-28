# coding:UTF-8

# *****************************************************************************
#   Description:  python for DPSpark sdk,
#   use the variable DPSparkApp,DPHdfs,DPHbase ... the same as javaDpsparkSdk
#   use other class or method in Java with appJpype
#   Created by FL on 2019/4/16.
#
# *****************************************************************************
import sys, json, jpype, os.path
from pyspark.sql import SparkSession


class DPSparkSdk:
    DPSparkApp = 0
    DPHdfs = 0
    DPHbase = 0
    DPEs = 0
    BigdataPermission = 0
    DPSqlServer = 0
    appJpype = 0
    __initTag = False
    __params = []
    __dprunclass = ''
    __dpappName = 'PythonProxyApp'
    __sparkSession = 0

    def __init__(self):
        self.__initTag = True
        argv = sys.argv
        print("got params json is ", argv[1])

        self.__params = json.loads(argv[1])
        self.__dprunclass = self.__params['dprunclass']  # runclass为模块名
        self.__dpappName = self.__params['dpappName']
        self.__params['callSource'] = "python"
        self.__sparkSession = SparkSession.builder.appName(self.__dpappName).getOrCreate()
        #
        self.appJpype = jpype
        jarpath = os.path.join(os.path.abspath('.') + "\\jars\\", 'dpsparksdk-1.0-SNAPSHOT-jar-with-dependencies.jar') # 本地調試
        # jarpath = "/usr/local/dp/python_jars/dpsparksdk-1.0-SNAPSHOT-jar-with-dependencies.jar" # 正式環境
        print("=========jvm path", self.appJpype.get_default_jvm_path())
        self.appJpype.startJVM(self.appJpype.get_default_jvm_path(), "-Djava.class.path=%s" % jarpath)
        # 公共成员变量赋值
        self.DPSparkApp = self.appJpype.JClass('com.tm.dl.javasdk.dpspark.DPSparkApp')
        self.DPHdfs = self.appJpype.JClass('com.tm.dl.javasdk.dpspark.hdfs.DPHdfs')
        self.DPHbase = self.appJpype.JClass('com.tm.dl.javasdk.dpspark.hbase.DPHbase')
        self.DPEs = self.appJpype.JClass('com.tm.dl.javasdk.dpspark.es.DPEs')
        self.BigdataPermission = self.appJpype.JClass('com.tm.dl.javasdk.dpspark.common.BigdataPermission')
        self.DPSqlServer = self.appJpype.JClass('com.tm.dl.javasdk.dpspark.sqlserver.DPSqlServer')

    def main(self):
        # 初始化sparkContext
        self.DPSparkApp.main([json.dumps(self.__params)]) # run java sdk main
        # 执行用户任务脚本
        userObj = __import__(self.__dprunclass)
        if hasattr(userObj, 'main'):
            getattr(userObj, 'main')(self)
        else:
            print('User define function \'main\' is not exist.')
            self.stop()
        self.__sparkSession.stop()

    def stop(self):
        if self.__initTag:
            self.DPSparkApp.stop()
            self.appJpype.shutdownJVM()
        else:
            print('Execute the init method first.')


if __name__ == '__main__':
    DPSparkSdk().main()
