# Step 1: Use a lightweight JDK 17 or 21 image
FROM eclipse-temurin:17-jdk-alpine

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the JAR file from your target folder to the container
# Note: Make sure you run 'mvn clean package' first!
COPY target/*.jar app.jar

# Step 4: Expose the port your app runs on
EXPOSE 8080

# Step 5: Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]