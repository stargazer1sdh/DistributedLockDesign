title client2
echo on
e:
cd E:\eclipse-workspace\DistributedLockDesign


java -cp gson-2.6.2.jar;hamcrest-core-1.3.jar;junit-4.12.jar;.\bin com.sjtu.sdh.Client 192.168.201.128 4447

pause