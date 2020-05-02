FROM maven:3.6.2-jdk-11
WORKDIR /workspace

COPY . ./
ARG goal=package
RUN mvn $goal -s build/settings.xml