package programas

import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import org.json.simple.JSONObject;
import SensorHumo.activeMqUrl

object Rociadores {

  def main(args: Array[String]): Unit = {
    val cFactory = new ActiveMQConnectionFactory(activeMqUrl)
    val connection = cFactory.createConnection
    connection.start

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val rq = session.createQueue("RociadoresQueue")

    val consumidor = session.createConsumer(rq)

    val listener = new MessageListener {
      def onMessage(message: Message): Unit ={
        message match {
          case objeto: ObjectMessage => {
            val alerta : JSONObject = objeto.getObject()

            val emergencia : bool =  alerta.getBoolean("emergencia")
            if(emergencia){
                println("Activando rociadores!")
            }
            
          }
          case _ => {
            throw new Exception("Error desconocido")
          }
        }
      }
    }
    consumidor.setMessageListener(listener)
  }
}