data "aws_iam_role" "existing" {
  name = "lambda_execution_role"
}


resource "aws_iam_role" "lambda_execution_role" {
  count = data.aws_iam_role.existing.id != null ? 0 : 1
  name = "lambda_execution_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      },
    ]
  })
}

resource "aws_lambda_function" "my_function" {
  function_name = "my_function"
  handler       = "com.example.CPFVerificationLambda"  
  role          = aws_iam_role.lambda_execution_role.arn
  runtime       = "java21"

  filename = "function-bin.zip"
}

resource "aws_api_gateway_rest_api" "api" {
  name        = "MyAPI"
  description = "This is my API for demonstration purposes"
}

resource "aws_api_gateway_resource" "resource" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_rest_api.api.root_resource_id
  path_part   = "cpfVerify"
}


resource "aws_api_gateway_method" "method" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.resource.id
  http_method   = "GET"
  authorization = "NONE"
  request_parameters = {
    "method.request.querystring.cpf" = true
  }
}

resource "aws_api_gateway_integration" "integration" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_resource.resource.id
  http_method = aws_api_gateway_method.method.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.my_function.invoke_arn
  request_parameters = {
    "integration.request.querystring.cpf" = "method.request.querystring.cpf"
  }
}


resource "aws_lambda_permission" "permission" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.my_function.function_name
  principal     = "apigateway.amazonaws.com"

  source_arn = "${aws_api_gateway_rest_api.api.execution_arn}/*/${aws_api_gateway_method.method.http_method}${aws_api_gateway_resource.resource.path}"
}









resource "aws_iam_role_policy" "lambda_policy" {
  name = "LambdaDynamoDBCognitoAccess"
  role = aws_iam_role.lambda_execution_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "dynamodb:GetItem",
          "dynamodb:Scan",
          "dynamodb:Query",
          "dynamodb:PutItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem"
        ]
        Effect = "Allow"
        Resource = "arn:aws:dynamodb:*:*:table/cliente"
      },
      {
        Action = [
          "cognito-idp:AdminInitiateAuth",
          "cognito-idp:AdminUserGlobalSignOut",
          "cognito-idp:ListUsers"
        ]
        Effect = "Allow"
        Resource = "arn:aws:cognito-idp:*:*:userpool/us-east-1_Ny9FfbR8y"
      }
    ]
  })
}
