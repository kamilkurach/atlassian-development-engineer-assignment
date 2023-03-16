import groovy.json.JsonOutput
import java.nio.file.Files
import groovy.json.JsonSlurper

class Listener {

    def payload
    def token_json = JsonOutput.toJson([token: 'test', user: 'adam.nowak', created: '', exp: ''])
    def test_token
    String token_directory = 'tmp'
    int loop_number = 5
    String url = 'https://webhook.site/76660e37-06fb-48bb-9ce6-5de86bbb73ea'

    def createTokenDir() {
        File f = new File(token_directory)
        if (!f.exists()) {
            def tree = new FileTreeBuilder()
            tree.dir(token_directory)
        }
    }

    def saveToken(json) {
        File f = new File(token_directory)
        if (f.exists()) {
            new File('tmp/token.json').write(json)
        }
    }

    def readTokenFromFile() {
        File f = new File(token_directory)
        if (f.exists()) {
            def jsonSlurper = new JsonSlurper()
            def data = jsonSlurper.parse(new File('tmp/token.json'))
            test_token = JsonOutput.toJson(data)
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
        l.createTokenDir()
        l.saveToken(l.token_json)
        l.readTokenFromFile()
        for (int i = 0; i < l.loop_number; i++) {
            l.updatePayload(i + 1, l.currentDateAndTime())
            l.get()
        }
    }

}
