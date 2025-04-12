import boto3

def lambda_handler(event, context):
    email = event.get('email')
    title = event.get('title')

    if not email or not title:
        return {
            'statusCode': 400,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'error': 'Missing email or title'
            }
        }

    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('subscriptions')

    try:
        # Delete the item using email and title (composite key)
        table.delete_item(
            Key={
                'email': email,
                'title': title
            }
        )

        return {
            'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'message': 'Subscription removed'
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
