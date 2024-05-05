Запустить 2 OSD, указав порты, на портах 11000 и 11001

java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.OSD.Main 11000
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.OSD.Main 11001

Запустить MDS 
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.MDS.Main

Запустить клиента
java -cp target/ceph-1.0-SNAPSHOT.jar ulyana.Client.Main