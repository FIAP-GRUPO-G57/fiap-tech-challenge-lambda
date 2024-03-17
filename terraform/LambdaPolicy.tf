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
