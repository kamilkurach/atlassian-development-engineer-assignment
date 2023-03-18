import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class Listener {

    def payload
    def token_json
    def api_key = '00000000-0000-0000-0000-000000000000'
    String token_directory = 'tmp'
    String token_path = 'tmp/token.json'
    int loop_number = 5
    String url = 'https://webhook.site/76660e37-06fb-48bb-9ce6-5de86bbb73ea'
    Boolean isTokenValid = false

    def createTokenDir() {
        File f = new File(token_directory)
        if (!f.exists()) {
            println 'Creating directory..'
            def tree = new FileTreeBuilder()
            tree.dir(token_directory)
        }
    }

    def saveToken(json) {
        if (new File(token_directory).exists()) {
            println 'Saving token..'
            if (json != null) {
                new File(token_path).write(json)
            } else {
                println 'Missing token data!'
            }
        }
    }

    def readTokenFromFile() {
        if (new File(token_directory).exists() && new File(token_path).exists()) {
            println 'Reading token..'
            def jsonSlurper = new JsonSlurper()
            def data = jsonSlurper.parse(new File(token_path))
            token_json = JsonOutput.toJson(data)
        } else {
            println 'Missing token data!'
        }
    }

    def requestNewToken() {
        println 'Requesting token..'
        try {
            def webhookGet = new URL('http://127.0.0.1:5000/token')
            def connection = webhookGet.openConnection()
            connection.setRequestMethod('POST')
            connection.setRequestProperty('Content-Type', 'application/json')
            connection.setRequestProperty('X-Api-Key', api_key)
            token_json = JsonOutput.prettyPrint(connection.getInputStream().getText())
            println('POST ' + connection.responseCode)
        } catch (Exception ex) {
            println(ex)
        }
    }

    def validateToken() {
        println 'Validating token..'
        def object
        try {
            def validateGet = new URL('http://127.0.0.1:5000/validate')
            def connection = validateGet.openConnection()
            if (token_json != null) {
                def jsonSlurper = new JsonSlurper()
                object = jsonSlurper.parseText(token_json)
                connection.setRequestProperty('Authorization', 'Bearer ' + object.token)
            }
            connection.setRequestProperty('Content-Type', 'application/json')
            connection.setRequestMethod('GET')
            println('GET ' + connection.responseCode)
            if (connection.responseCode == 200) {
                isTokenValid = true
            } else {
                isTokenValid = false
            }
        } catch (Exception ex) {
            println(ex)
        }
    }

    def currentDateAndTime() {
        Date date = new Date()
        return date.format('dd/MM/YYYY-hhmm')
    }

    def updatePayload(attempt, date) {
        def json = JsonOutput.toJson(
          [requestID:'14',
          reporter:'adam.nowak',
          title:'Megacorp rate limit',
          attempt: attempt,
          details: [requestDate: date, requestor: 'webhook-user']])
        payload = JsonOutput.prettyPrint(json)
    }

    def get() {
        try {
            def webhookGet = new URL(url)
            def connection = webhookGet.openConnection()
            connection.setRequestMethod('POST')
            connection.setDoOutput(true)
            connection.setRequestProperty('Content-Type', 'application/json')
            connection.setRequestProperty('X-HTTP-Method-Override', 'GET')
            connection.with {
                outputStream.withWriter { outputStreamWriter ->
                    outputStreamWriter << payload
                }
            }
            println('GET ' + connection.responseCode)
        } catch (Exception ex) {
            println(ex)
        }
    }

    static void main(String[] args) {
        Listener l = new Listener()
        l.readTokenFromFile()
        l.validateToken()
        if (l.isTokenValid == false) {
            l.requestNewToken()
            l.createTokenDir()
            l.saveToken(l.token_json)
            l.readTokenFromFile()
            l.validateToken()
        }
        if (l.isTokenValid == true) {
            for (int i = 0; i < l.loop_number; i++) {
                l.updatePayload(i + 1, l.currentDateAndTime())
                l.get()
            }
        } else {
            println 'Invalid Token..'
        }
    }

}
