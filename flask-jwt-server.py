# Since I was not provided with API key by recruiter ¯\_(ツ)_/¯
# the only interesting way to simulate token validation and sending request
# is to write Flask app serving as JWT token endpoint

from flask import Flask, render_template, request
from flask_jwt_extended import create_access_token, jwt_required, get_jwt_identity
from flask import jsonify
from flask_jwt_extended import JWTManager
from datetime import timedelta

ACCESS_EXPIRES = timedelta(minutes=1)
API_KEY = '00000000-0000-0000-0000-000000000000'
user = "adam.nowak"

app = Flask(__name__)
app.config["JWT_SECRET_KEY"] = "12345"
app.config["JWT_ACCESS_TOKEN_EXPIRES"] = ACCESS_EXPIRES
jwt = JWTManager(app)


@app.route('/', methods=['GET'])
def hello_world():
    return render_template('index.html')


@app.route('/validate', methods=['GET'])
@jwt_required()
def validate():
    current_user = get_jwt_identity()
    if current_user == user:
        return jsonify({"user":  current_user}), 200


@app.route('/token', methods=['POST'])
def token():
    headers = request.headers
    auth = headers.get("X-Api-Key")
    if auth == API_KEY:
        access_token = create_access_token(identity=user)
        return jsonify({'token': access_token, "user_id": user}), 200
    else:
        return jsonify({"message": "Unauthorized"}), 401


if __name__ == "__main__":
    app.run()
