��������� 5 OSD, ������ �����, �� ������ 11001 11002 11003 11004 11005
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.OSD.Main 11000
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.OSD.Main 11001
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.OSD.Main 11002
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.OSD.Main 11003
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.OSD.Main 11004
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.OSD.Main 11005

��������� Monitor
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.Monitor.Main

��������� MDS
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.MDS.Main

��������� �������, ����� ����� ���� ����������� ���� ����� � �������� ��������� �������� ���
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.Client.Main