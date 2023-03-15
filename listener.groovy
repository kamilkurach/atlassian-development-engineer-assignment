import groovy.json.JsonOutput

class Listener {

    def payload
    int loop_number = 5
    String url = 'https://webhook.site/76660e37-06fb-48bb-9ce6-5de86bbb73ea'

    def currentDateAndTime() {
        Date date = new Date()
        return date.format('dd/MM/YYYY-hhmm')
    }

    def update_json(attempt, date) {
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
        for (int i = 0; i < l.loop_number; i++) {
            l.update_json(i + 1, l.currentDateAndTime())
            l.get()
        }
    }

}
