import boto3

def lambda_handler(event, context):
    email = event['email']
    password = event['password']

    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('login')

    try:
        response = table.get_item(Key={'email': email})
        item = response.get('Item')

        if item and item['password'] == password:
            return {
                'statusCode': 200,
                'headers': {
                    'Access-Control-Allow-Origin': '*',
                    'Access-Control-Allow-Methods': '*',
                    'Acecess-Control-Allow-Headers': 'Content-type'
                },
                'body': {
                    'result': 'valid',
                    'user_name': item['user_name']
                }
            }
        else:
            return{
                'statusCode': 401,
                'headers': {
                    'Access-Control-Allow-Origin': '*',
                    'Access-Control-Allow-Methods': '*',
                    'Acecess-Control-Allow-Headers': 'Content-type'
                },
                'body': {
                    'result': 'invalid',
                }
            }
    
    except Exception as e:
        return {
            'statusCode': 500,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': '*',
                'Acecess-Control-Allow-Headers': 'Content-type'
            },
            'body': {
                'result': 'error',
                'message': str(e)
            }
        }