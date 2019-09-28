FROM ubuntu:18.04

MAINTAINER Drew Hilton "adhilton@ee.duke.edu"

USER root

ENV DEBIAN_FRONTEND noninteractive
RUN apt-get update && apt-get -yq dist-upgrade \
  && apt-get install -yq --no-install-recommends \
     curl \
     wget \
     bzip2 \
     sudo \
     locales \
     ca-certificates \
     git \
     unzip \
     openjdk-11-jdk-headless \
     emacs25

RUN echo "en_US.UTF-8 UTF-8" > /etc/locale.gen && \
    locale-gen

ARG LOCAL_USER_ID=1001
ENV USER_ID ${LOCAL_USER_ID}
RUN adduser --uid ${USER_ID} juser
WORKDIR /home/juser

# Get the 651 emacs setup so we can report code coverage
# that matches what we will grade with
USER juser
RUN git clone https://gitlab.oit.duke.edu/adh39/ece651-emacs-setup.git
WORKDIR /home/juser/ece651-emacs-setup

RUN ./setup.sh



WORKDIR /home/juser
USER root
# we are going to do a bit of gradle first, just to speed
# up future builds
COPY build.gradle gradlew settings.gradle  ./
COPY gradle/wrapper gradle/wrapper
RUN chown -R juser.juser .
USER juser

# this will fetch gradle 5.4, and the packages we depend on
RUN ./gradlew resolveDependencies


# Now we copy all our source files in.  Note that
# if we change src, etc, but not our gradle setup,
# Docker can resume from this point
USER root
COPY ./ ./
RUN chown -R juser.juser .

USER juser
# compile the code
RUN ./gradlew  assemble
