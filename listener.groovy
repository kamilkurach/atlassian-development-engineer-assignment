class Listener {

    String payload = '''{

        "RequestID":"14",

        "Reporter":"adam.nowak",

        "Title":"Megacorp rate limit",

        "Attempt":"1",

        "Details": {

            "RequestDate":"12/12/2023-1623",

            "Requestor":"webhook-user"

        }

    }'''

    int loop_number = 5

    def getCurrentDateAndTime() {
        Date date = new Date()
        return date.format("dd/MM/YYYY-hhmm")
    }

    def get() {
        try {
            def webhookGet = new URL('https://webhook.site/76660e37-06fb-48bb-9ce6-5de86bbb73ea')
            def connection = webhookGet.openConnection()
            connection.setRequestMethod("POST")
            connection.setDoOutput(true)
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("X-HTTP-Method-Override", "GET")
            connection.with {
                outputStream.withWriter { outputStreamWriter ->
                    outputStreamWriter << payload
                }
            }
            println("GET " + connection.responseCode);
        } catch(Exception ex) {
            println(ex);
        }
    }

    static void main(String[] args) {
        Listener l = new Listener()
        for(int i = 0; i < l.loop_number; i++) {
            l.get()
        }
    }
}