package programas

import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory

object SensorHumo {
  val activeMqUrl: String = "tcp://localhost:61616"

  def main(args: Array[String]): Unit = {
    val cFactory = new ActiveMQConnectionFactory(activeMqUrl)
    val connection = cFactory.createConnection()
    connection.start()

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val hq = session.createQueue("HumoQueue")

    val productor = session.createProducer(hq)
    val uuid = java.util.UUID.randomUUID.toString

    //Crear ciclo while infinito (?) que envie numeros random
    //{
    //Creacion objeto con data
    JSONObject obj = new JSONObject()
    obj.put("uuid", uuid)
    obj.put("nivel", datoHumo) //la variable datoHumo va a almacenar los valores
    val objMessage = session.createObjectMessage(obj)

    productor.send(objMessage)
    println("Mensaje enviado")
    //}
    connection.close()

    var thread = new MainThread(uuid)
    thread.start()
  }
}
