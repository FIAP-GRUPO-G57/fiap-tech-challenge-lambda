resource "aws_lambda_function" "my_function" {
  function_name = "my_function"
  handler       = "com.example.CPFVerificationLambda"  
  role          = aws_iam_role.lambda_execution_role.arn
  runtime       = "java21"

  filename = "function-bin.zip"
}