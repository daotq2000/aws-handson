# IAM Role for Lambda
# S3 Bucket

resource "aws_s3_bucket" "s3_bucket" {
  bucket = var.s3_bucket # Ensure the bucket name is globally unique
  # Optional: Add tags to the bucket
  tags = {
    Name        = "example-bucket"
    Environment = "dev"
  }
}

# Upload JAR file to S3
resource "aws_s3_object" "lambda_jar" {
  bucket = aws_s3_bucket.s3_bucket.bucket
  key    = "lambda/function.jar"  # Path where the JAR will be stored in the S3 bucket
  source = var.jar_path  # Local path to the JAR file
  acl    = "private"
}



resource "aws_iam_role" "lambda_role" {
  name = "lambda_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

# IAM Policy Attachment for Lambda Role
resource "aws_iam_role_policy_attachment" "lambda_policy_attachment" {
  role       = aws_iam_role.lambda_role.name
  policy_arn  = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# Lambda Function
resource "aws_lambda_function" "lambda-resize-image" {
  function_name = "lambda-function-resize"

  s3_bucket = aws_s3_bucket.s3_bucket.bucket
  s3_key    = aws_s3_object.lambda_jar.key

  handler = "org.aws.resize.LambdaResizeImage::handleRequest"
  runtime = "java11"

  role = aws_iam_role.lambda_role.arn

  # Environment variables, memory size, timeout, etc.
  memory_size = 512
  timeout     = 30
}

# API Gateway Rest API
resource "aws_api_gateway_rest_api" "api" {
  name        = "api-gw"
  description = "API Gateway for Lambda"
}

# API Gateway Resource
resource "aws_api_gateway_resource" "resource" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_rest_api.api.root_resource_id
  path_part    = "lambda-resize"
}

# API Gateway Method
resource "aws_api_gateway_method" "method" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.resource.id
  http_method   = "POST"
  authorization = "NONE"
}

# Integration between API Gateway and Lambda
resource "aws_api_gateway_integration" "integration" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.resource.id
  http_method = aws_api_gateway_method.method.http_method
  integration_http_method = "POST"
  type = "AWS_PROXY"
  uri  = "arn:aws:apigateway:${var.aws_region}:lambda:path/2015-03-31/functions/${aws_lambda_function.lambda-resize-image.arn}/invocations"
}

# Lambda Permissions for API Gateway
resource "aws_lambda_permission" "api_gateway_permission" {
  statement_id  = "AllowExecutionFromApiGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.lambda-resize-image.function_name
  principal     = "apigateway.amazonaws.com"

  # Source ARN to restrict API Gateway access
  source_arn = "${aws_api_gateway_rest_api.api.execution_arn}/*/*"
}

# Deploy API Gateway
resource "aws_api_gateway_deployment" "deployment" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  stage_name  = "prod"

  depends_on = [aws_api_gateway_integration.integration]
}
