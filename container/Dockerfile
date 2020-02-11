FROM openjdk:12

### installing PostgreSQL Client ###

RUN yum -y install https://download.postgresql.org/pub/repos/yum/reporpms/EL-7-x86_64/pgdg-redhat-repo-latest.noarch.rpm && yum -y install postgresql96

ENV TNA_HOME /tna

COPY . /tna

### Execute Permission to shell scripts ###

RUN ["/bin/bash", "-c", "chmod 755 /tna/bin/*"]
RUN ["/bin/bash", "-c", "chmod -R 755 /tna/conf/"]

EXPOSE 8006

ENTRYPOINT ["/tna/bin/run.sh"]