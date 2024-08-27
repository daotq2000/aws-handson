variable "aws_region" {
  type = string
  default = "us-east-1"
}
variable "s3_bucket" {
  type = string
  default = "s3-resize-daotq1"
}
variable "jar_path" {
  type = string
  default = "../target/aws-lambda-resize-image-0.0.1-SNAPSHOT.jar"
}