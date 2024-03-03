I. Create  API gate way

To create resources for your API using the AWS CLI, you would typically define these resources in your API definition JSON file and then import it into AWS API Gateway. Here's how you can do it:

Modify your API definition JSON file (api-definition.json) to include the resources you want to create.
Use the aws apigateway import-rest-api command to import your API definition into AWS API Gateway.
Here's an example of how you can define resources in your api-definition.json file:
//api-config.json
{
    "swagger": "1.0",
    "info": {
      "title": "Resource for note app",
      "version": "1.0"
    },
    "paths": {
      "/notes": {
        "get": {
          "summary": "Fetch notes records",
          "responses": {
            "200": {
              "description": "Successful operation"
            }
          }
        }
      },
      "/notes": {
        "post": {
          "summary": "Add notes records",
          "responses": {
            "200": {
              "description": "Successful operation"
            }
          }
        }
      }
    }
  }
  
In this example, I've defined two resources: get and post for notes endpoint

Now, you can import this API definition into AWS API Gateway using the following command:

<code>aws apigateway import-rest-api --body fileb://api-config.json --region us-east-1 > api-resouce-out-put.json</code>

This command will import the API definition into AWS API Gateway and create the specified resources. You can then proceed with deploying your API as explained earlier.
The response have been append into <b>api-resouce-out-put.json</b> in directory, let inspect it.
II. Create lambda function to attach api gateway
using command below to fetch list role for lamdafunction
<code>aws iam list-roles</code>
     {
            "Path": "/",
            "RoleName": "SystemAdminReadOnly",
            "RoleId": "AROA4KDNQH5RUZHAB5SSL",
            "Arn": "arn:aws:iam::846338211683:role/SystemAdminReadOnly",
            "CreateDate": "2024-02-19T17:29:30+00:00",
            "AssumeRolePolicyDocument": {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {
                            "Service": "ec2.amazonaws.com"
                        },
                        "Action": "sts:AssumeRole"
                    }
                ]
            },
            "Description": "Allows EC2 instances to call AWS services on your behalf.",
            "MaxSessionDuration": 3600
        }
First, create your Lambda function. Below is an example command to create a simple Lambda function using the AWS CLI:
using command below to fetch list buckets
<code>aws s3 ls</code>
2024-02-27 13:27:52 cf-templates-1ohbdpx0bstp8-us-east-1
<code>aws lambda create-function --function-name lambda-fetch-notes --runtime java11 --role arn:aws:iam::846338211683:role/LambdaFunctionRole --handler org.example.awsserverlessnoteapp.LambdaFetchDataFunction.handler --code S3Bucket=cf-templates-1ohbdpx0bstp8-us-east-1,S3Key=aws-serverless-note-app.zip --
region us-east-1</code>
