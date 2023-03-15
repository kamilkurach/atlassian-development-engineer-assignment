import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class Listener {

    String payload = '''
    {
        "requestID":"14",
        "reporter":"adam.nowak",
        "title":"Megacorp rate limit",
        "attempt":"1",
        "details": {
            "requestDate":"12/12/2023-1623",
            "requestor":"webhook-user"
        }
    }'''

    int loop_number = 5

    def currentDateAndTime() {
        Date date = new Date()
        return date.format('dd/MM/YYYY-hhmm')
    }

    def update_json(payload, attempt, date) {
        def slurped = new JsonSlurper().parseText(payload)
        def builder = new JsonBuilder(slurped)
        builder.content.attempt = attempt
        builder.content.details.requestDate = date
        return builder.toPrettyString()
    }

    def get() {
        try {
            def webhookGet = new URL('https://webhook.site/76660e37-06fb-48bb-9ce6-5de86bbb73ea')
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
            l.payload = l.update_json(l.payload, i + 1, l.currentDateAndTime())
            l.get()
        }
    }

}
