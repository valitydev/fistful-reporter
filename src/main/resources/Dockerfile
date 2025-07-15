# @dockerfile.Template@
FROM ghcr.io/valitydev/temurin-jemalloc:sha-14c0b34

RUN apt-get update \
      && apt-get install -y \
      --no-install-recommends \
      perl=5.38.2-3.2ubuntu0.1 \
      fontconfig=2.15.0-1.1ubuntu2 \
      libfreetype6=2.13.2+dfsg-1build3 \
      && rm -rf /var/lib/apt/lists/*

RUN useradd -s /bin/false -U -u 20000 vality

COPY --chown=vality:vality ${artifactId}-${version}.jar /opt/${artifactId}/${artifactId}.jar

USER vality:vality

CMD ["java", "-jar","/opt/${artifactId}/${artifactId}.jar"]

EXPOSE ${exposed.ports}

WORKDIR /opt/${artifactId}
