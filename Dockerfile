FROM maven:3.6.2-jdk-11
WORKDIR /workspace

COPY . ./
ARG repo_username
ARG repo_password
ENV REPO_USERNAME=$repo_username
ENV REPO_PASSWORD=$repo_password

ARG goal=package
RUN mvn $goal -s build/settings.xml