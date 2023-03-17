# Since I was not provided with API key by recruiter ¯\_(ツ)_/¯
# the only interesting way to simulate token validation and sending request
# is to write Flask app serving as JWT token endpoint

from flask import Flask, render_template, request
from flask_jwt_extended import create_access_token
from flask import jsonify
from flask_jwt_extended import JWTManager
from datetime import timedelta

ACCESS_EXPIRES = timedelta(hours=1)
API_KEY = '00000000-0000-0000-0000-000000000000'

app = Flask(__name__)
app.config["JWT_SECRET_KEY"] = "12345"  
app.config["JWT_ACCESS_TOKEN_EXPIRES"] = ACCESS_EXPIRES
app.config["JWT_TOKEN_LOCATION"] = "json"
jwt = JWTManager(app)


@app.route('/', methods = ['GET'])
def hello_world():
    return render_template('index.html')

@app.route('/token', methods = ['POST'])
def token():
    headers = request.headers
    auth = headers.get("X-Api-Key")
    if auth == API_KEY:
        access_token = create_access_token(identity="user")
        return jsonify({'token': access_token, 'access_exp_seconds': ACCESS_EXPIRES.seconds}), 200
    else:
        return jsonify({"message": "Unauthorized"}), 401
    
app.run()
