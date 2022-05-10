package programas

import javax.jms._
import org.apache.activemq.ActiveMQConnectionFactory
import org.json.simple.JSONObject;
import SensorHumo.activeMqUrl

object Bomberos {


  def main(args: Array[String]): Unit = {
    val cFactory = new ActiveMQConnectionFactory(activeMqUrl)
    val connection = cFactory.createConnection
    connection.start

    val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val bq = session.createQueue("BomberosQueue")

    val consumidor = session.createConsumer(bq)

    val listener = new MessageListener {
      def onMessage(message: Message): Unit ={
        message match {
          case objeto: ObjectMessage => {
            val alerta : JSONObject = objeto.getObject()

            val emergencia : bool =  alerta.getBoolean("emergencia")
            if(emergencia){
                println("Enviando SMS a los bomberos!")
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