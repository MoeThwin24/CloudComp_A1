import boto3
from boto3.dynamodb.conditions import Key

def lambda_handler(event, context):
    # Retrieve the user email from the POST body
    email = event.get('email')

    if not email:
        return {
            'statusCode': 400,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'error': 'Missing email in request'
            }
        }

    # Connect to DynamoDB
    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('subscriptions')

    try:
        # Query subscriptions by email (partition key)
        response = table.query(
            KeyConditionExpression=Key('email').eq(email)
        )

        return {
            'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'subscriptions': response.get('Items', [])
            }
        }

    except Exception as e:
        return {
            'statusCode': 500,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'error': str(e)
            }
        }
