# Umbrella-alert
Check for daily rain/sleet and publish to SNS topic. Subscribers could be emails to alert you if there is a chance of 
precipitation (bring an umbrella!).

## Setup
1) Get api key from https://darksky.net/dev/register
2) Create SNS topic, add subscriber (email addresses), and confirm
3) Create Lambda Function: 
    - Java 8 runtime
    - 256 MB, 3 min timeout
    - handler = com.weather.alerts.Main::handleRequest
    - CloudWatch Event Trigger w/ custom role and `cron(19 12 * * ? *)`
    - Resource access to CloudWatch Logs & SNS
    - Upload env var, see `Configs` & `ForecastServiceConfig`
    
## Deploy
`sbt clean assembly` then upload jar to lambda

## Testing Lambda
1) Go to Lambda
2) Select Test & Configure
3) Search for "Amazon CloudWatch", this will bring up a template for a `ScheduledEvent`
4) Edit template to look like:
     ```json
    {
      "id": "cdc73f9d-aea9-11e3-9d5a-835b769c0d9c",
      "detail-type": "Scheduled Event",
      "source": "aws.events",
      "account": "{{account-id}}",
      "time": "1970-01-01T00:00:00Z",
      "region": "us-east-1",
      "resources": [
        "arn:aws:events:us-east-1:123456789012:rule/ExampleRule"
      ],
      "detail": {
          "test": true
      }
    }
    ```