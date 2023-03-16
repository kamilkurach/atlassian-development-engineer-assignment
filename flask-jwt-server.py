# Since I was not provided with API key by recruiter ¯\_(ツ)_/¯
# the only interesting way to simulate token validation and sending request
# is to write Flask app serving as JWT token endpoint

from flask import Flask, render_template
app = Flask(__name__)

@app.route('/', methods = ['GET'])
def hello_world():
    return render_template('index.html')

app.run()
