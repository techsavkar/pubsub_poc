#Pubsub POC

### Steps for reproducing the issue.
1. Create a topic in GCP console.
2. Create a subscription with name **deltasub** with acknowledge deadline of **120** seconds
3. Build the project from the root of the project directory
   ```sh
    ./gradlew clean build
    ```
4. Set the Google Credentials environment variable and start the application.
   ```sh
    GOOGLE_APPLICATION_CREDENTIALS=<PATH_TO_CREDENTIALS_FILE>.json ./gradlew bootRun
    ```
5. Publish a sample message to the topic created in step 1.
6. Verify that you see the below log statement in terminal.
    ```shell
    received message is : <your_sample_message_posted_in_step5>
    ```
7. After the acknowledge deadline (120 seconds), you will notice the above log statement getting printed again 
   proving that the ```spring.cloud.gcp.pubsub.subscriber.max-ack-extension-period``` defined in **application.yml** is
   not having any effect