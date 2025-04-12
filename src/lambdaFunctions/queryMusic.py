import boto3

def lambda_handler(event, context):
    title = event.get('title', '').lower()
    artist = event.get('artist', '').lower()
    album = event.get('album', '').lower()
    year = event.get('year', '').lower()

    dynamodb = boto3.resource('dynamodb')
    table = dynamodb.Table('music')

    try:
        # Get all items (not ideal for huge datasets, but okay for this assignment)
        response = table.scan()
        items = response['Items']

        # Apply filters (AND logic)
        filtered = []
        for item in items:
            if title and title not in item.get('title', '').lower():
                continue
            if artist and artist not in item.get('artist', '').lower():
                continue
            if album and album not in item.get('album', '').lower():
                continue
            if year and year not in str(item.get('year', '')).lower():
                continue
            filtered.append(item)

        return {
            'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': '*'
            },
            'body': {
                'results': filtered
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
