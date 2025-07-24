# File: base-with-ansible.Dockerfile
FROM hyperflex/jre21ansible:latest

COPY backend/ansible /ansible
COPY server/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]