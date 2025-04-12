import boto3

def lambda_handler(event, context):
    email = event.get('email')
    title = event.get('title')
    artist = event.get('artist')
    album = event.get('album')
    year = event.get('year')
    image_url = event.get('image_url')

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
        # Put item into DynamoDB
        table.put_item(Item={
            'email': email,
            'title': title,
            'artist': artist,
            'album': album,
            'year': year,
            'image_url': image_url
        })

        return {
            'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'message': 'Subscription added'
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
