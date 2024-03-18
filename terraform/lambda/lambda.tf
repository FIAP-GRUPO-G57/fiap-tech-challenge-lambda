resource "aws_lambda_function" "my_function" {
  function_name = "my_function"
  handler       = "com.yourcompany.CPFVerificationLambda"  
  role          = "arn:aws:iam::389985004788:role/lambda_execution_role"
  runtime       = "java21"

  filename = "../../target/function-bin.zip"
}