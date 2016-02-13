FROM 	resin/rpi-raspbian:wheezy
ENV     JAVA_HOME   	/usr/local/apps/jdk1.8.0_65
ENV     GLASSFISH_HOME  /usr/local/apps/glassfish4
ENV		RMP_HOME		/usr/local/apps/rmp
ENV     PATH        	$PATH:$RMP_HOME:$JAVA_HOME/bin:$GLASSFISH_HOME/bin

# Tools needed for glassfish
RUN         apt-get update && \
            apt-get install -y curl unzip zip inotify-tools && \
            rm -rf /var/lib/apt/lists/*
            
RUN		mkdir -p $RMP_HOME 

# Java
COPY	jdk-8u65-linux-arm32-vfp-hflt.gz /tmp

RUN         tar xzf /tmp/jdk-8u65-linux-arm32-vfp-hflt.gz -C /usr/local/apps && \
            rm -f /tmp/jdk-8u65-linux-arm32-vfp-hflt.gz
            
#Glassfish         
COPY	glassfish-4.1.zip	/tmp
RUN		unzip /tmp/glassfish-4.1.zip -d /usr/local/apps && \
        rm -f /tmp/glassfish-4.1.zip
        
EXPOSE  8080 4848 8181

#RMP   
COPY	rmp.war $GLASSFISH_HOME/glassfish/domains/domain1/autodeploy

#Elastic Search
COPY	elasticsearch-1.7.2.zip /tmp
RUN     unzip /tmp/elasticsearch-1.7.2.zip -d /usr/local/apps && \
        rm -f /tmp/elasticsearch-1.7.2.zip       
EXPOSE	9200 9300

COPY	startall.sh $RMP_HOME
RUN		chmod 755 $RMP_HOME/startall.sh

CMD /usr/local/apps/rmp/startall.sh
#Invoke using  docker run -v //c/Users/dev/SomeMusic:/music -P=true rmp 
#That will mount /c/users/dev/SomeMusic to /music on the container. the // is needed for some Git bash issue which interprets paths wrong

#on rpi
#docker run -v /media/music:/music -P=true rmp