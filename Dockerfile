FROM reg.yondervision.com.cn:8081/rdgrp/java:8u161-k8s
#ADD target/plat-batlist.jar /var/k8s/plat-batlist.ja
#ADD target/peer-to-peer.jar /var/k8s/peer-to-peer.ja
#ADD target/plat-peer2peer.jar /var/k8s/plat-peer2peer.jar
#ADD plat-peer2peer-service/target/plat-peer2peer-service.jar /var/k8s/plat-peer2peer-service.jar
ADD p2p-service/target/plat-peer2peer-service.jar /var/k8s/plat-peer2peer-service.jar
USER root
#RUN chown 1000:1000 /var/k8s/peer-to-peer.jar
RUN chown 1000:1000 /var/k8s/plat-peer2peer-service.jar
USER k8s 