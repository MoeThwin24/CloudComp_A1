import boto3

def lambda_handler(event, context):
    email = event['email']
    password = event['password']
    username = event['user_name']

    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('login')

    try:
        response = table.get_item(Key={'email': email})
        if 'Item' in response:
            return {
                'statusCode': 200,
                'headers': {
                    'Access-Control-Allow-Origin': '*',
                    'Access-Control-Allow-Headers': 'Content-Type',
                    'Access-Control-Allow-Methods': '*'
                },
                'body': {
                    'result': 'exists',
                }
            }
        
        table.put_item(Item={
            'email': email,
            'password': password,
            'user_name': username
        })

        return {
            'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'result': 'registered',
            }
        }

    except Exception as e:
        return{
            'statusCode': 500,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'result': 'error',
                'message': str(e)
            }

        }

